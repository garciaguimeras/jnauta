package dev.blackcat.jnauta.net;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

public class Authentication
{

    Proxy proxy;
    String username;
    String password;

    // this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer, proxyPort));

    public Authentication(String username, String password, Proxy proxy)
    {
        this.username = username;
        this.password = password;
        this.proxy = proxy;
    }

    public AuthenticationResponseParser.LoginResult login()
    {
        List<String> response;
        Connection connection = new Connection();
        AuthenticationResponseParser parser = new AuthenticationResponseParser();

        response = connection.get("https://secure.etecsa.net:8443", this.proxy, null);
        parser.parseHomeResponse(response);

        HashMap<String, String> dict = parser.parseHomeResponse(response);
        if (!dict.containsKey("gotopage"))
            dict.put("gotopage", "/nauta_hogar/LoginURL/pc_login.jsp");
        if (!dict.containsKey("successpage"))
            dict.put("successpage", "/nauta_hogar/OnlineURL/pc_index.jsp");
        if (!dict.containsKey("lang"))
            dict.put("lang", "es_ES");
        dict.put("ssid", "nauta_hogar");
        dict.put("username", this.username);
        dict.put("password", this.password);

        response = connection.post("https://secure.etecsa.net:8443/LoginServlet", this.proxy, Utils.toURLEncoded(dict));
        return parser.parseLoginResponse(response);
    }

    public String getAvailableTime(String timeParams)
    {
        Connection connection = new Connection();
        List<String> response = connection.get("https://secure.etecsa.net:8443/EtecsaQueryServlet", this.proxy, timeParams);
        return response != null && response.size() > 0 ? response.get(0) : null;
    }

    public boolean logout(String formContent)
    {
        Connection connection = new Connection();
        AuthenticationResponseParser parser = new AuthenticationResponseParser();
        List<String> response =connection.get("https://secure.etecsa.net:8443/LogoutServlet", this.proxy, formContent);
        return parser.parseLogoutResponse(response);
    }

}
