package de.hd.seegarten.pinyou;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by d062787 on 2/20/16.
 */
public class PinterestLogic {

    private static String getPinterestRequestUrl(List<String> keywords){
        String baseurl = "https://api.pinterest.com/v3/search/pins/?page_size=100&access_token=MTQzMTU5NDo1MDI3MTQ1MTQ1NjM4MDIzODU6OTIyMzM3MjAzNjg1NDc3NTgwN3wxNDU2MDI5NDgyOjI1OTIwMDAtLTU4ZWU4YTVmZTc0MzFmMzBlMTU3ZjBmYTBmOWZjOTY1";
        String url = baseurl+"&query=";
        for(String s : keywords){
            url+=s+" " ;
        }
        System.out.println(url);
        return url;
    }

    private static List<String> getPinIds(String respString) throws JSONException {
        JSONObject obj = new JSONObject(respString.toString());
        JSONArray obj2 = (JSONArray) obj.get("data");
        List<String> alist = new ArrayList<>();
        for (int i1 = 0; i1 < 3; i1++) {
            String x = (String)((JSONObject)obj2.get(i1)).get("id");
            alist.add(x);
        }

        return alist;
    }

    public static List<String> getListPins(List<String> keywords) throws IOException, JSONException {
        String pinterestSearchUrl = URLEncoder.encode(getPinterestRequestUrl(keywords));
        WebClient target = WebClient.create(pinterestSearchUrl);

        ResponseImpl resp = (ResponseImpl) target.get();

        InputStream inputStream = (InputStream) resp.getEntity();
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        String respString = writer.toString();
        return getPinIds(respString);
    }

    public static String convertToJSON(Object obj){
        Gson g = new Gson();
        return g.toJson(obj);
    }
}
