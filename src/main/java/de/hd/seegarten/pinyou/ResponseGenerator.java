package de.hd.seegarten.pinyou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResponseGenerator {
  
  final String ACCESS_KEY = "MTQzMTU5NDo1MDI3MTQ1MTQ1NjM4MDIzODU6OTIyMzM3MjAzNjg1NDc3NTgwN3wxNDU2MDMwOTA1OjI1OTIwMDAtLWRjODEzNTJiZWMyOTlhZjgxNThmM2MxODgyMjY4MWQ4";
  final Integer PAGE_SIZE = 100;
  public String kw_to_response(String[] kws) throws IOException {
    StringBuilder sb = new StringBuilder(); // Using default 16 character size
    int i;
    for(i = 0; i < kws.length - 1; i++) {
      sb.append(kws[0]);
      sb.append("+");
    }
    sb.append(kws[i]);
    String urlToRead = "https://api.pinterest.com/v3/search/pins/?page_size="+PAGE_SIZE+"&query="+sb.toString()+"&access_token="+ACCESS_KEY;
    StringBuilder result = new StringBuilder();
    URL url = new URL(urlToRead);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
       result.append(line);
    }
    rd.close();
    return result.toString();
  }
  
  public static void main(String[] args) throws IOException {
    String[] kws = {"test", "blood"};
    ResponseGenerator rg = new ResponseGenerator();
    System.out.println(rg.kw_to_response(kws));
  }

}
