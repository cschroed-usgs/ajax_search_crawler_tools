package gov.usgs.cida.ajax_search_crawler_tools;

import gov.usgs.cida.ajax_search_crawler_tools.servicer.ISearchCrawlerServicer;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SearchCrawlerUglyUrlFilter implements Filter{
	private ISearchCrawlerServicer searchCrawlerServicer;
	
	//http GET parameter
	public static final String SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME = PrettyUglyUrlMapper.SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME;
	
	//filter config parameter definied in web.xml
	public static final String SEARCH_CRAWLER_SERVICER = "search-crawler-servicer";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String searchCrawlerServletName = filterConfig.getInitParameter(SEARCH_CRAWLER_SERVICER);
		if(null == searchCrawlerServletName || searchCrawlerServletName.isEmpty()){
			throw new IllegalArgumentException(
				"an init config value must be specified for the '" +
				SEARCH_CRAWLER_SERVICER +"' " +
				"parameter of the '" + this.getClass().getSimpleName() +
				"' filter."
			);
		} else{
			searchCrawlerServletName = searchCrawlerServletName.trim();
			try {
				Class<?> clazz = Class.forName(searchCrawlerServletName);
				ISearchCrawlerServicer instance = (ISearchCrawlerServicer) clazz.newInstance();
				this.setSearchCrawlerServicer(instance);
			} catch (ClassNotFoundException ex){
				throw new IllegalArgumentException(
					"Could not find class '" + 
					searchCrawlerServletName + "' "+
					", the init config value specified for the '" +
					SEARCH_CRAWLER_SERVICER +"' " +
					"parameter of the '" + this.getClass().getSimpleName() +
					"' filter."
				);
			}
			catch (InstantiationException | IllegalAccessException ex) {
				throw new RuntimeException(ex);
			}
		}
		
	}

	/**
	 * If the request contains the searchbot escaped fragment parameter, then
	 * handle the request via this instance's delegate servlet and continue
	 * down the filter chain. If the request does not contain the 
	 * searchbot parameter, then continue through the filter chain.
	 * The detection of the searchbot escaped fragment parameter is
	 * case-insensitive
	 * @param request the request being made by the client
	 * @param response the response to the client's request
	 * @param chain the chain of all filters
	 * @throws IOException when FilterChain#doFilter throws one
	 * @throws ServletException when FilterChain#doFilter throws one
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
		Enumeration<String> paramNames = request.getParameterNames();
		Set<String> lowerCaseParamNames = new HashSet<>();
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if(null != paramNames){
			while(paramNames.hasMoreElements()){
				lowerCaseParamNames.add(paramNames.nextElement().toLowerCase(Locale.ENGLISH));
			}
		}
		
		if(lowerCaseParamNames.contains(SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME)){
			//bypass any other defined filters, delegate to the servlet
			SearchCrawlerRequest crawlerRequest = new SearchCrawlerRequest((HttpServletRequest) request);
			this.getSearchCrawlerServicer().service(crawlerRequest, httpServletResponse);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		
	}

	/**
	 * @return the searchCrawlerServicer
	 */
	public ISearchCrawlerServicer getSearchCrawlerServicer() {
		return searchCrawlerServicer;
	}

	/**
	 * @param searchCrawlerServicer the searchCrawlerServicer to set
	 */
	public void setSearchCrawlerServicer(ISearchCrawlerServicer searchCrawlerServicer) {
		this.searchCrawlerServicer = searchCrawlerServicer;
	}

}
