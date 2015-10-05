package gov.usgs.cida.ajax_search_crawler_tools;

import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class SearchCrawlerRequest extends HttpServletRequestWrapper{

	public SearchCrawlerRequest(HttpServletRequest request) {
		super(request);
	}
	/**
	 * Get this request's full ugly url, including query string
	 * Inspired by: http://stackoverflow.com/a/2222268
	 * @param request
	 * @return full Url
	 */
	String getUglyUrl() {
		StringBuffer requestURL = this.getRequestURL();
		String queryString = this.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}
	
	/**
	 * Get this request's full pretty url.
	 * @return full Url
	 */
	public String getPrettyUrl(){
		String uglyUrl = this.getUglyUrl();
		String prettyUrl = PrettyUglyUrlMapper.uglyToPretty(uglyUrl);
		return prettyUrl;
	}
	
	/**
	 * Returns the pretty url of the request after the context path. This excludes:
	 *	* protocol
	 *	* host
	 *	* port
	 *	* context path
	 * and includes:
	 *	* non-context path 
	 *	* the query string
	 *	* fragment
	 * @return the pretty url
	 */
	public String getPrettyUrlWithoutContextPath(){
		return getUrlWithoutContextPath(this.getPrettyUrl(), this.getContextPath());
	}
	
	/**
	 * Returns the url of the request after the context path. This excludes:
	 *	* protocol
	 *	* host
	 *	* port
	 *	* context path
	 * and includes:
	 *	* non-context path 
	 *	* the query string
	 *	* fragment
	 * @param request
	 * @return 
	 */
	static String getUrlWithoutContextPath(String fullUrl, String contextPath){
		URI fullUri;
		String urlWithoutContextPath = null;
		try {
			fullUri = new URI(fullUrl);
			URIBuilder builder = new URIBuilder(fullUri);
			builder.setHost(null)
			.setScheme(null);
			if(null != contextPath && !contextPath.isEmpty()){
				builder.setPath(fullUri.getPath().replaceFirst(".*" + contextPath, ""));
			}
			urlWithoutContextPath = builder.build().toString();
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException(ex);
		}
		return urlWithoutContextPath;
	}

}
