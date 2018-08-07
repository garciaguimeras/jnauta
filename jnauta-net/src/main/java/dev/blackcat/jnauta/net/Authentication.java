package dev.blackcat.jnauta.net;

import java.util.HashMap;

public class Authentication
{

    Connection.Proxy proxy;
    String username;
    String password;

    Connection connection;

    public Authentication(ConnectionBuilder.Method method, String username, String password, Connection.Proxy proxy)
    {
        this.username = username;
        this.password = password;
        this.proxy = proxy;
        this.connection = ConnectionBuilder.build(method);
    }

    public AuthenticationResponseParser.LoginResult login()
    {
        Connection.Result response;
        AuthenticationResponseParser parser = new AuthenticationResponseParser();

        response = connection.get("https://secure.etecsa.net:8443", this.proxy, null);
        HashMap<String, String> dict = parser.parseHomeResponse(response.content);
        if (!dict.containsKey("gotopage"))
            dict.put("gotopage", "/nauta_hogar/LoginURL/pc_login.jsp");
        if (!dict.containsKey("successpage"))
            dict.put("successpage", "/nauta_hogar/OnlineURL/pc_index.jsp");
        if (!dict.containsKey("lang"))
            dict.put("lang", "es_ES");
        dict.put("ssid", "nauta_hogar");
        dict.put("username", this.username);
        dict.put("password", this.password);

        response = connection.post("https://secure.etecsa.net:8443/LoginServlet", this.proxy, dict);
        //if (response.statusCode >= 300 && response.statusCode < 400)
        //    response = connection.get("https://secure.etecsa.net:8443/web/online.do", this.proxy, null);
        return parser.parseLoginResponse(response.content);
    }

    public AuthenticationResponseParser.LoginResult accountStatus()
    {
        AuthenticationResponseParser parser = new AuthenticationResponseParser();
        Connection.Result response = connection.get("https://secure.etecsa.net:8443/web/online.do", this.proxy, null);
        return parser.parseLoginResponse(response.content);
    }

    public String getAvailableTime(String timeParams)
    {
        Connection.Result response;
        response = connection.get("https://secure.etecsa.net:8443/EtecsaQueryServlet", this.proxy, timeParams);
        return response != null && response.content.size() > 0 ? response.content.get(0) : null;
    }

    public boolean logout(String formContent)
    {
        AuthenticationResponseParser parser = new AuthenticationResponseParser();
        Connection.Result response;
        response = connection.get("https://secure.etecsa.net:8443/LogoutServlet", this.proxy, formContent);
        return parser.parseLogoutResponse(response.content);
    }

}
