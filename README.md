# ajax_search_crawler_tool
AJAX Search Engine Crawler Tools - Respond to searchbots that implement the hashbang url convention

## Usage

This project automates the detection of search engine ajax crawling requests, and the mapping of the ugly "?\_escaped\_fragment\_=..." urls to pretty hashbang ("#!") urls. Since there are different ways to generate the content for the search engine crawler, and there is no one-size-fits-all solution, this project leaves crawler content generation strategy up to you.

 * Add this project as a dependency of your project.
   * Via maven, that would look like:
	```
	<dependency>
		<groupId>gov.usgs.cida.ajax_search_crawler_tools</groupId>
		<artifactId>ajax_search_crawler_tools</artifactId>
		<version>0.1.0-SNAPSHOT</version>
	</dependency>
	```
 * Create a class to respond to the search engine crawler.
	```
	package com.example;

	import gov.usgs.cida.ajax_search_crawler_tools.ISearchCrawlerServicer;
	import gov.usgs.cida.ajax_search_crawler_tools.SearchCrawlerRequest;
	import javax.servlet.http.HttpServletResponse;

	public class MyGreatSearchCrawlerServicer implements ISearchCrawlerServicer {


@Override
	public void service(SearchCrawlerRequest request, HttpServletResponse response) {
		//your response logic here
	}
	```
 * Edit your project's web.xml. Add the ugly url filter and a reference to your recently-created class
	```
    <filter>
        <filter-name>SearchBotUglyUrlFilter</filter-name>
        <filter-class>gov.usgs.cida.ajax_search_crawler_tools.SearchCrawlerUglyUrlFilter</filter-class>
        <init-param>
            <param-name>search-crawler-servicer</param-name>
            <param-value>com.example.MyGreatSearchCrawlerServicer</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>SearchBotUglyUrlFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
	```

