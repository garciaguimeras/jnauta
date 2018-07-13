package dev.blackcat.jnauta.net;

import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class OkHttpConnection extends Connection
{

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

    @Override
    protected Connection.Result http(Connection.Type type, String url, Proxy proxy, String parameters)
    {
        List<String> output = new ArrayList<String>();
        final Connection.Result result = new Connection.Result();
        result.url = url;

        try
        {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            if (proxy != null)
                clientBuilder.proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.hostname, proxy.port)));
            clientBuilder.addInterceptor(new Interceptor()
            {
                @Override
                public Response intercept(Chain chain) throws IOException
                {
                    result.url = chain.request().url().toString();
                    // System.out.println(result.url);
                    return chain.proceed(chain.request());
                }
            });
            OkHttpClient client = clientBuilder.build();

            Request.Builder requestBuilder;
            if (type == Connection.Type.GET)
                requestBuilder = GETRequestBuilder(url, parameters);
            else
                requestBuilder = POSTRequestBuilder(url, parameters);

            requestBuilder.addHeader("User-Agent", "Mozilla/5.0");
            requestBuilder.addHeader("Referer", "https://secure.etecsa.net:8443/");
            requestBuilder.addHeader("Host", "secure.etecsa.net");
            requestBuilder.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            requestBuilder.addHeader("Accept-Language", "en-US,en;q=0.5");
            requestBuilder.addHeader("Connection", "keep-alive");
            requestBuilder.addHeader("Cookie", this.getCookies());

            Request request = requestBuilder.build();
            Call call = client.newCall(request);
            Response response = call.execute();
            InputStream isContent = response.body().byteStream();
            result.statusCode = response.code();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(isContent));
            String s = "";
            while ((s = buffer.readLine()) != null)
                output.add(s);

            this.setCookies(response.header("Set-Cookie"));
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
