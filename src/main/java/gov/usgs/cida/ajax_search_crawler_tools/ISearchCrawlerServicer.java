package gov.usgs.cida.ajax_search_crawler_tools;

import javax.servlet.http.HttpServletResponse;

public interface ISearchCrawlerServicer {
	public void service(SearchCrawlerRequest request, HttpServletResponse response);
}
