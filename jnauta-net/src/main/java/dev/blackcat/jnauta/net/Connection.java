package dev.blackcat.jnauta.net;

import java.util.HashMap;
import java.util.List;

public abstract class Connection
{

    public enum Type
    {
        GET,
        POST
    }

    public class Proxy
    {
        public String hostname;
        public int port;
    }

    public class Result
    {
        public List<String> content;
        public String url;
        public int statusCode;
    }


    private String cookies = "";

    protected abstract Connection.Result http(Connection.Type type, String url, Proxy proxy, String parameters);

    public void setCookies(String cookies)
    {
        if (cookies == null)
            cookies = "";
        this.cookies = cookies;
    }

    public String getCookies()
    {
        return cookies;
    }

    public Connection.Result get(String url, Proxy proxy, String parameters)
    {
        return http(Connection.Type.GET, url, proxy, parameters);
    }

    public Connection.Result post(String url, Proxy proxy, HashMap<String, String> parameters)
    {
        return http(Connection.Type.POST, url, proxy, Utils.toURLEncoded(parameters));
    }
}
