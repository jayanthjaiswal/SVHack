package de.hd.seegarten.pinyou;

import de.hd.seegarten.pinyou.Entities.Pins;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.impl.ResponseImpl;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by d062787 on 2/20/16.
 */
public class PinterestLogic {

    private static String getPinterestRequestUrl(List<String> keywords){
        String baseurl = "https://api.pinterest.com/v3/search/pins/?page_size=100&query=cricket&access_token=MTQzMTU5NDo1MDI3MTQ1MTQ1NjM4MDIzODU6OTIyMzM3MjAzNjg1NDc3NTgwN3wxNDU2MDI5NDgyOjI1OTIwMDAtLTU4ZWU4YTVmZTc0MzFmMzBlMTU3ZjBmYTBmOWZjOTY1";
        String url = baseurl+"&query=";
        for(String s : keywords){
            url+=s+" " ;
        }
        System.out.println(url);
        return url;
    }

    public static void getListPins(List<String> keywords) throws IOException {
        String pinterestSearchUrl = URLEncoder.encode(getPinterestRequestUrl(keywords));
        WebClient target = WebClient.create(pinterestSearchUrl);

        ResponseImpl resp = (ResponseImpl) target.get();

        InputStream inputStream = (InputStream) resp.getEntity();
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        String respString = writer.toString();
        System.out.println(respString);
    }
}
