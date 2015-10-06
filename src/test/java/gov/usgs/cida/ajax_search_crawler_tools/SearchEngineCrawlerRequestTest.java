package gov.usgs.cida.ajax_search_crawler_tools;

import org.junit.Test;
import static org.junit.Assert.*;

public class SearchEngineCrawlerRequestTest {
	
	@Test
	public void testGetUrlWithoutContextPath() {
		String fullUrl = "http://cida.usgs.gov/nwc/blah";
		String contextPath = "/nwc";
		String expResult = "/blah";
		String result = SearchCrawlerRequest.getUrlWithoutContextPath(fullUrl, contextPath);
		assertEquals(expResult, result);
	}
	/**
	 * Test of getUrlWithoutContextPath method, of class SkeletonPageServlet.
	 */
	@Test
	public void testEmptyContextPath() {
		String fullUrl = "http://localhost:8080/";
		String contextPath = "";
		String result = SearchCrawlerRequest.getUrlWithoutContextPath(fullUrl, contextPath);
		String expected = "/";
		assertEquals(expected, result);
	}
	@Test
	public void testUrlWithManyPathComponents(){
		String fullUrl = "http://localhost:8080/gdp_web/client/?some_param=some_value";
		String contextPath = "/gdp_web";
		String result = SearchCrawlerRequest.getUrlWithoutContextPath(fullUrl, contextPath);
		String expected = "/client/?some_param=some_value";
		assertEquals(expected, result);
		
	}
}
