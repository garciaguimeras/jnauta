package dev.blackcat.jnauta.net;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OkHttpCookieJar implements CookieJar
{
    private static OkHttpCookieJar instance = null;

    private HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    private OkHttpCookieJar()
    {}

    public static OkHttpCookieJar getInstance()
    {
        if (instance == null)
            instance = new OkHttpCookieJar();
        return instance;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        cookieStore.put(url.host(), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url)
    {
        List<Cookie> cookies = cookieStore.get(url.host());
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
}
