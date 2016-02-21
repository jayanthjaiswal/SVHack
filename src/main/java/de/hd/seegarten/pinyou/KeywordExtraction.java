/**
 * (c) 2014 SAP AG or an SAP affiliate company.  All rights reserved.
 *
 * No part of this publication may be reproduced or transmitted in any form or for any purpose
 * without the express permission of SAP AG.  The information contained herein may be changed
 * without prior notice.
 */

package de.hd.seegarten.pinyou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeywordExtraction {
	private static final Logger logger = LoggerFactory.getLogger(RuleEngineActionHandlerImpl.class);

	public List<String> fetchKeywords(String url) {
		Document doc;
		boolean isVideo = false;
		if (url.contains("jpg") || url.contains("png") || url.contains("jpeg"))
			url = "https://www.google.com/searchbyimage?&image_url=" + url;
		else {
//			String id = url.substring(url.indexOf("?v=") + 3, url.length());
			String id = url;
			logger.debug("id" + id);
			url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + id
					+ "&key=AIzaSyDuzeh9J74iLqa9n1_AgMmQm7RZk5lHD9o";
			isVideo = true;
		}

		// http://api.theweek.com/sites/default/files/styles/tw_image_9_4/public/54168_article_full.jpg
		try {
			HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
//			HttpHost proxy = new HttpHost("proxy", 8080, "http");
			logger.debug("url " + url);
//			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			HttpGet request = new HttpGet(url);
//			request.setConfig(config);

			// add request header
			request.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
			HttpResponse response = client.execute(request);

			logger.debug("Response Code : " + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			// logger.debug(result.toString());
			if (isVideo) {
				JSONObject obj = new JSONObject(result.toString());
				JSONArray items = obj.getJSONArray("items");
				JSONObject item = items.getJSONObject(0);
				JSONObject snippet = item.getJSONObject("snippet");
				List<String> titleList = new ArrayList<String>();
				titleList.add(snippet.getString("title"));
				titleList.add(snippet.getString("description"));
				return titleList;
			}
			doc = Jsoup.parse(result.toString());

			List<String> titleList = new ArrayList<String>();
			Elements titles = doc.select("h3.r");
			Iterator it = titles.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				titleList.add(el.text());
			}
			// 139 for desc in soup.findAll('span', attrs={'class':'st'}):
			// 140 whole_array['description'].append(desc.get_text())
			// 141 all_text += desc.get_text().lower()
			List<String> descriptionList = new ArrayList<String>();
			Elements descriptions = doc.select("span.st");
			it = descriptions.iterator();
			while (it.hasNext()) {
				Element el = (Element) it.next();
				descriptionList.add(el.text());
			}

			titleList.addAll(descriptionList);
			return titleList;
		} catch (IOException e) {
			e.getStackTrace();
			return new ArrayList<String>();
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}
}
