package se.kth.castor;

import static org.junit.Assert.assertTrue;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @BeforeClass
    public static void init() {
        App.main(new String[]{});
    }
    /**
     * Rigorous Test :-)
     */
    @Test
    public void test() throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.setFollowRedirects(false);
        httpClient.start();

        String url = "http://localhost:" + App.SERVER_PORT + "/userJoin";
        System.out.println("url: " + url);
        Request request =  httpClient.POST(url);
        request.header(HttpHeader.CONTENT_TYPE, "application/json");
        request.content(new StringContentProvider("{\"roomId\":\"aksglfh\", \"user\":{\"id\":\"sdfs\", \"name\":\"Jean\"}}"));
        ContentResponse response = request.send();
        String res = new String(response.getContent());
        System.out.println(res);

        String url2 = "http://localhost:" + App.SERVER_PORT + "/usersRooms";
        System.out.println("url2: " + url2);
        ContentResponse response2 = httpClient.GET(url2);
        String res2 = new String(response2.getContent());
        System.out.println(res2);

        httpClient.stop();
    }
}
