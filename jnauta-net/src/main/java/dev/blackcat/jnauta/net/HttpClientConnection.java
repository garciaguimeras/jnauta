package dev.blackcat.jnauta.net;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HttpClientConnection extends Connection
{

    protected HttpGet GETRequestBuilder(String url, String parameters)
    {
        if (parameters != null)
            url = url + "?" + parameters;
        HttpGet request = new HttpGet(url);
        return request;
    }

    protected HttpPost POSTRequestBuilder(String url, String parameters)
    {
        HttpPost request = new HttpPost(url);
        EntityBuilder entityBuilder = EntityBuilder.create();
        entityBuilder.setText(parameters);
        entityBuilder.setContentType(ContentType.APPLICATION_FORM_URLENCODED);
        request.setEntity(entityBuilder.build());
        return request;
    }

    @Override
    protected Connection.Result http(Connection.Type type, String url, Proxy proxy, String parameters)
    {
        List<String> output = new ArrayList<String>();
        final Connection.Result result = new Connection.Result();
        result.url = url;

        try
        {
            HttpClientBuilder clientBuilder = HttpClientBuilder.create();
            if (proxy != null)
                clientBuilder.setProxy(new HttpHost(proxy.hostname, proxy.port));
            HttpClient client = clientBuilder.build();

            HttpRequestBase request;
            if (type == Type.GET)
                request = GETRequestBuilder(url, parameters);
            else
                request = POSTRequestBuilder(url, parameters);

            request.setHeader("User-Agent", "Mozilla/5.0");
            request.setHeader("Referer", "https://secure.etecsa.net:8443/");
            request.setHeader("Host", "secure.etecsa.net");
            request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            request.setHeader("Accept-Language", "en-US,en;q=0.5");
            request.setHeader("Connection", "keep-alive");
            request.setHeader("Cookie", this.getCookies());

            HttpResponse response = client.execute(request);
            InputStream isContent = response.getEntity().getContent();
            result.statusCode = response.getStatusLine().getStatusCode();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(isContent));
            String s = "";
            while ((s = buffer.readLine()) != null)
                output.add(s);

            // this.setCookies(response.getFirstHeader("Set-Cookie").getValue());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.statusCode = 500;
            result.content = new ArrayList<String>();
            return result;
        }

        result.content = output;
        return result;
    }

}
