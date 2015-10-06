package gov.usgs.cida.ajax_search_crawler_tools.servicer;

import gov.usgs.cida.ajax_search_crawler_tools.SearchCrawlerRequest;
import javax.servlet.http.HttpServletResponse;

public interface ISearchCrawlerServicer {
	public void service(SearchCrawlerRequest request, HttpServletResponse response);
}
