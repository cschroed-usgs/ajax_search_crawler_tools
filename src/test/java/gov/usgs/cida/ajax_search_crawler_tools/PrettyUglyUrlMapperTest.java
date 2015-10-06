package gov.usgs.cida.ajax_search_crawler_tools;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class PrettyUglyUrlMapperTest {

	public static final List<PrettyUglyPair> prettyUglyPairs = Arrays.asList(
		//pretty url is a simple value
		new PrettyUglyPair("/path#!hashfragment", "/path?_escaped_fragment_=hashfragment"),
		//pretty url has ampersands and equals signs in the fragment:
		new PrettyUglyPair("/#!key1=value1&key2=value2", "/?_escaped_fragment_=key1%3Dvalue1%26key2%3Dvalue2"),
		//urls have query params besides the searchbot escaped fragment param
		new PrettyUglyPair("/path?queryparams#!hashfragment", "/path?queryparams&_escaped_fragment_=hashfragment")
	);
	
	/**
	 * Test of uglyToPretty method, of class PrettyUglyUrlMapper.
	 */
	@Test
	public void testUglyToPretty() throws UnsupportedEncodingException, URISyntaxException {
		for(PrettyUglyPair pair : prettyUglyPairs){
			String result = PrettyUglyUrlMapper.uglyToPretty(pair.ugly);
			assertEquals(pair.pretty, result);
		}
	}

	
	/**
	 * Test a unidirectional mapping.
	 * ugly urls with empty escaped fragment parameters
	 * map to a fragment-less pretty url, but fragment-less pretty
	 * urls map to themselves
	 */
	@Test
	public void testUnidirectionalMappingCase(){
		List<PrettyUglyPair> prettyUrlsWithoutFragments = Arrays.asList(
			//without additional query string args:
			new PrettyUglyPair("/path", "/path?_escaped_fragment_="),
			//with additional query string args:
			new PrettyUglyPair("/path?queryparams", "/path?queryparams&_escaped_fragment_=")
			
		);
		for(PrettyUglyPair pair : prettyUrlsWithoutFragments){
			String result = PrettyUglyUrlMapper.uglyToPretty(pair.ugly);
			assertEquals(pair.pretty, result);
		}
	}
	
}
