package dev.blackcat.jnauta.net;

public class ConnectionBuilder
{

    public enum Method
    {
        DEFAULT,
        OK_HTTP,
        HTTP_CLIENT
    }

    public static Connection build(Method method)
    {
        switch (method)
        {
            case DEFAULT:
                return new DefaultConnection();

            case OK_HTTP:
                return new OkHttpConnection();

            case HTTP_CLIENT:
                return new HttpClientConnection();
        }
        return null;
    }

}
