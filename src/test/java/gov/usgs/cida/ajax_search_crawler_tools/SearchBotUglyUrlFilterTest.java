package gov.usgs.cida.ajax_search_crawler_tools;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SearchBotUglyUrlFilterTest {
	SearchCrawlerUglyUrlFilter filter;
	MockSearchCrawlerServicer mockServlet;
	MockFilterChain filterChain;
	HttpServletRequest req;
	HttpServletResponse res;
	
	class MockFilterChain implements FilterChain {
		public boolean wasCalled = false;
		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			wasCalled = true;
		}
	}
	
	class MockSearchCrawlerServicer implements ISearchCrawlerServicer{
		public boolean wasCalled = false;
		@Override
		public void service(SearchCrawlerRequest request, HttpServletResponse response) {
			wasCalled = true;
		}


	}

	public void assertDelegateServletWasCalled(){
		assertFalse("filter chain was not called", filterChain.wasCalled);
		assertTrue("delegate servlet was called", mockServlet.wasCalled);
	}
	
	public void assertDelegateServletWasNotCalled(){
		assertTrue("filter chain was called", filterChain.wasCalled);
		assertFalse("delegate servlet was not called", mockServlet.wasCalled);
	}
	
	@Before
	public void setUp() {
		filter = new SearchCrawlerUglyUrlFilter();
		mockServlet = new MockSearchCrawlerServicer();
		filter.setSearchCrawlerServicer(mockServlet);
		filterChain = new MockFilterChain();
		req = mock(HttpServletRequest.class);
		res = mock(HttpServletResponse.class);
	
	}
	
	private static Enumeration<String> enumOf(String... strings){
		return Collections.enumeration(Arrays.asList(strings));
	}

	/**
	 * Test of doFilter method, of class SearchBotUglyUrlFilter.
	 */
	@Test
	public void assertRequestWithoutParamsDoesNotCallDelegate() throws Exception {
		filter.doFilter(req, res, filterChain);
		assertDelegateServletWasNotCalled();
	}
	
	/**
	 * Test of doFilter method, of class SearchBotUglyUrlFilter.
	 */
	@Test
	public void assertRequestWithIrrelevantParamsDoesNotCallDelegate() throws Exception {
		String irrelevantParamName = "somethingIrrelevant" + SearchCrawlerUglyUrlFilter.SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME;
		when(req.getParameterNames()).thenReturn(enumOf(irrelevantParamName));
		filter.doFilter(req, res, filterChain);
		assertDelegateServletWasNotCalled();
	}

	/**
	 * Test of doFilter method, of class SearchBotUglyUrlFilter.
	 */
	@Test
	public void assertRequestWithTheLowerCaseParamCallsTheDelegate() throws Exception {
		when(req.getParameterNames()).thenReturn(enumOf(SearchCrawlerUglyUrlFilter.SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME));
		filter.doFilter(req, res, filterChain);
		assertDelegateServletWasCalled();
	}
	
	/**
	 * Test of doFilter method, of class SearchBotUglyUrlFilter.
	 */
	@Test
	public void assertRequestWithTheUpperCaseParamCallsTheDelegate() throws Exception {
		when(req.getParameterNames()).thenReturn(enumOf(SearchCrawlerUglyUrlFilter.SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME.toUpperCase(Locale.ENGLISH)));
		filter.doFilter(req, res, filterChain);
		assertDelegateServletWasCalled();
	}

	/**
	 * Test of doFilter method, of class SearchBotUglyUrlFilter.
	 */
	@Test
	public void assertRequestWithTheParamAndIrrelevantParamsCallsTheDelegate() throws Exception {
		when(req.getParameterNames()).thenReturn(enumOf(
			"somethingIrrelevant" + SearchCrawlerUglyUrlFilter.SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME,
			SearchCrawlerUglyUrlFilter.SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME,
			"anotherSomethingIrrelevant" + SearchCrawlerUglyUrlFilter.SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME
		));
		filter.doFilter(req, res, filterChain);
		assertDelegateServletWasCalled();
	}

}
