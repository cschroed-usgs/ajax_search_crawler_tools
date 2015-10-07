package gov.usgs.cida.ajax_search_crawler_tools.servicer;

import gov.usgs.cida.ajax_search_crawler_tools.SearchCrawlerRequest;
import gov.usgs.cida.simplehash.SimpleHash;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.io.IOUtils;

/**
 * This class is a reference implementation of an ISearchCrawlerServicer.
 * This class assumes that your project has a jar on the classpath with a
 * "META-INF/resources/skeleton" directory. Further, it is assumed that
 * files in that directory have a ".html" extension and their names are the
 * SHA-1 hashes of a page's pretty url after the context path.
 */

public class SHA1SkeletonResourceServicer implements ISearchCrawlerServicer {
	public static final String RESOURCE_PREFIX = "/skeleton/";
	public static final String SKELETON_FILE_EXTENSION = ".html";
	
	public static final String TEXT_HTML_CONTENT_TYPE = "text/html";
	public static final int NOT_FOUND = 404;

	
	/**
	 * Given a request from a searchbot, serve up a cached page that is
	 * easily interpreted by the searchbot
	 * @param request from the search crawler
	 * @param response to the search crawler
	 */
	
	@Override
	public void service(SearchCrawlerRequest request, HttpServletResponse response) {
		String prettyUrlWithoutContextPath = request.getPrettyUrlWithoutContextPath();
		String resourceName = getResourceName(prettyUrlWithoutContextPath);
		response.setContentType(TEXT_HTML_CONTENT_TYPE);
		try (
			InputStream skeletonStream = this.getClass().getResourceAsStream(resourceName);
			OutputStream responseStream = response.getOutputStream();
		) {
			if(null == skeletonStream){
				response.sendError(NOT_FOUND);
			} else {
				IOUtils.copy(skeletonStream, responseStream);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Gets the name of the resource on the classpath based on the provided
	 * url.
	 * @param prettyUrlWithoutContextPath the url that the search bot wishes
	 * it could see if it could process javascript. Should not contain the
	 * protocol, host, port or context path
	 * @return the name of the resource
	 */
	public String getResourceName(String prettyUrlWithoutContextPath) {
		String resourceName = null;
		try {
			String prettyUrlFragment = "#" + new URI(prettyUrlWithoutContextPath).getFragment();

			String prettyUrlFragmentHash = SimpleHash.hash(prettyUrlFragment, "SHA-1");
			resourceName = RESOURCE_PREFIX + prettyUrlFragmentHash + SKELETON_FILE_EXTENSION;
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException(ex);
		}
		return resourceName;
	}
}