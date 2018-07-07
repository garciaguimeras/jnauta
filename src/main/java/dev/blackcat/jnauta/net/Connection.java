package dev.blackcat.jnauta.net;

import okhttp3.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class Connection
{

    public enum Type
    {
        GET,
        POST
    }

    // new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort))

    protected Request.Builder GETRequestBuilder(String url, String parameters)
    {
        if (parameters != null)
            url = url + "?" + parameters;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        return requestBuilder;
    }

    protected Request.Builder POSTRequestBuilder(String url, String parameters)
    {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, parameters);

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.post(body);
        return requestBuilder;
    }

    protected List<String> http(Type type, String url, Proxy proxy, String parameters)
    {
        List<String> output = new ArrayList<String>();

        try
        {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            if (proxy != null)
                clientBuilder.proxy(proxy);
            OkHttpClient client = clientBuilder.build();

            Request.Builder requestBuilder;
            if (type == Type.GET)
                requestBuilder = GETRequestBuilder(url, parameters);
            else
                requestBuilder = POSTRequestBuilder(url, parameters);
            Request request = requestBuilder.build();

            Call call = client.newCall(request);
            Response response = call.execute();
            InputStream isContent = response.body().byteStream();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(isContent));
            String s = "";
            while ((s = buffer.readLine()) != null)
                output.add(s);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return output;
    }

    public List<String> get(String url, Proxy proxy, String parameters)
    {
        return http(Type.GET, url, proxy, parameters);
    }

    public List<String> get(String url, String parameters)
    {
        return http(Type.GET, url, null, parameters);
    }

    public List<String> get(String url)
    {
        return http(Type.GET, url, null, null);
    }

    public List<String> post(String url, Proxy proxy, String parameters)
    {
        return http(Type.POST, url, proxy, parameters);
    }

    public List<String> post(String url, String parameters)
    {
        return http(Type.POST, url, null, parameters);
    }

}
