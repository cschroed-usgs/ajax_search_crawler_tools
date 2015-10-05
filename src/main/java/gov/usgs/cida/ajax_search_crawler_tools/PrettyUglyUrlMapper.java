package gov.usgs.cida.ajax_search_crawler_tools;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class performs bidirectional mapping between ugly and pretty urls as
 * specified in the Google specification for Making AJAX Applications Crawlable:
 * https://developers.google.com/webmasters/ajax-crawling/docs/specification?hl=en
 * 
 */
public class PrettyUglyUrlMapper {
	public static final String SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME = "_escaped_fragment_";
	public static final String BANG = "!";
	
	/**
	 * Convenience wrapper for converting string urls
	 * @param ugly the ugly url that the search engine crawler is requesting
	 * @return the pretty url
	 */
	public static String uglyToPretty(String ugly){
		try {
			String result = null;
			URI uri = uglyToPretty(new URI(ugly));
			if(null != uri){
				result = uri.toString();
			}
			return result;
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException(ex);
		}
	}
	
	/**
	 * Get Key-Value Pairs from a url's query string
	 * http://stackoverflow.com/questions/13592236/parse-the-uri-string-into-name-value-collection-in-java
	 * Small modification - the keys are all automatically lower-cased according to the English Locale
	 * 
	 * We're using this instead of the apache http client to avoid transitive dependency version conflicts
	 */
	static Map<String, List<String>> splitQuery(URI uri) {
		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = uri.getQuery().split("&");
		for (String pair : pairs) {
			try {
				final int idx = pair.indexOf("=");
				String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
				key = key.toLowerCase(Locale.ENGLISH);
				if (!query_pairs.containsKey(key)) {
					query_pairs.put(key, new LinkedList<String>());
				}
				final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
				query_pairs.get(key).add(value);
			} catch (UnsupportedEncodingException ex) {
				throw new IllegalArgumentException(ex);
			}
		}
		return query_pairs;
	}
	
	/**
	 * Maps ugly urls to pretty urls as specified in the Google 
	 * specification for Making AJAX Applications Crawlable:
	 * https://developers.google.com/webmasters/ajax-crawling/docs/specification?hl=en
	 * @param ugly the ugly url that the search engine crawler is requesting
	 * @return pretty url
	 */
	public static URI uglyToPretty(URI ugly){
		Map<String, List<String>> kvps = splitQuery(ugly);
		
		//building new query string that excludes the _escaped_fragment_ key and values
		//doing this instead of apache http client url builder to avoid transitive dependency conflicts
		
		String fragment = "";
		URI pretty = null;
		StringBuilder sb = new StringBuilder("?");
		for (String key : kvps.keySet()) {
			List<String> values = kvps.get(key);
			//presumes that all keys are already lower-case
			if (SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME.equals(key)) {
				String fragmentValue = values.get(0);
				if (!fragmentValue.isEmpty()) {
					fragment = BANG + fragmentValue;
				}
			} else {
				for (String value : values) {
					sb.append(key)
						.append("=")
						.append(value)
						.append("&");
				}
			}
		}
		
		String queryWithoutEscapedFragment = sb.toString();
		//remove the trailing ampersand, if any
		if("&".equals(queryWithoutEscapedFragment.charAt(queryWithoutEscapedFragment.length()))){
			queryWithoutEscapedFragment=queryWithoutEscapedFragment.substring(0, queryWithoutEscapedFragment.length());
		}
		try {
			pretty = new URI(
				ugly.getScheme(),
				ugly.getUserInfo(),
				ugly.getHost(),
				ugly.getPort(),
				ugly.getPath(),
				queryWithoutEscapedFragment,
				fragment
			);
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException(ex);
		}
		return pretty;
	}
//	/**
//	 * Convenience wrapper for converting string urls
//	 * @param pretty the pretty url
//	 * @return ugly url
//	 */
//	public static String prettyToUgly(String pretty){
//		try {
//			String result = null;
//			URI uri = prettyToUgly(new URI(pretty));
//			if(null != uri){
//				result = uri.toString();
//			}
//			return result;
//		} catch (URISyntaxException ex) {
//			throw new IllegalArgumentException(ex);
//		}
//	}
//	
//	/**
//	 * Maps pretty urls to ugly urls as specified in the Google 
//	 * specification for Making AJAX Applications Crawlable:
//	 * https://developers.google.com/webmasters/ajax-crawling/docs/specification?hl=en
//	 * 
//	 * @param pretty the pretty url
//	 * @return ugly url. If the pretty url does not contain a hashbang (#!),
//	 * then the pretty url is returned as is. In that case pretty.equals(ugly).
//	 */
//	public static URI prettyToUgly(URI pretty) {
//		URI ugly = null;
//		URIBuilder uriBuilder = new URIBuilder(pretty);
//		String fragment = pretty.getFragment();
//		if (null != fragment && !fragment.isEmpty()) {
//			if (fragment.startsWith(BANG)) {
//				//move the content of the escaped fragment param
//				//to the fragment. Exclude the initial "!"
//				uriBuilder.addParameter(SEARCHBOT_ESCAPED_FRAGMENT_PARAM_NAME, fragment.substring(1));
//				uriBuilder.setFragment(null);
//			}
//		}
//		try {
//			ugly = uriBuilder.build();
//
//		} catch (URISyntaxException ex) {
//			throw new IllegalArgumentException(ex);
//		}
//		return ugly;
//	}
}
