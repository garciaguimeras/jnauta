package dev.blackcat.jnauta.net;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationResponseParser
{

    public class LoginResult
    {
        public String session;
        public String timeParams;
        public boolean alreadyConnected;
        public boolean noMoney;
    }

    public HashMap<String, String> parseHomeResponse(List<String> lines)
    {
        Pattern pattern1 = Pattern.compile("<input type=\"hidden\" name=\"([A-Za-z0-9./]*)\" id=\"[A-Za-z0-9./]*\" value=\"([A-Za-z0-9./]*)\"");
        Pattern pattern2 = Pattern.compile("<input type='hidden' name='CSRFHW' value='([A-Za-z0-9]*)'");

        HashMap<String, String> result = new HashMap<String, String>();
        for (String line: lines)
        {
            line = line.trim();

            Matcher m1 = pattern1.matcher(line);
            if (m1.find())
            {
                result.put(m1.group(1), m1.group(2));
            }

            Matcher m2 = pattern2.matcher(line);
            if (m2.find() && !result.containsKey("CSRFHW"))
            {
                result.put("CSRFHW", m1.group(1));
            }
        }
        return result;
    }

    public LoginResult parseLoginResponse(List<String> lines)
    {
        Pattern pattern1 = Pattern.compile("var urlParam = \"([A-Za-z0-9=_&]*)\"");
        Pattern pattern2 = Pattern.compile("alert\\(\"El usuario ya está conectado.\"\\);");
        Pattern pattern3 = Pattern.compile("alert\\(\"Su tarjeta no tiene saldo disponible");
        Pattern pattern4 = Pattern.compile("g_httpRequest.open\\(\"post\", \"/EtecsaQueryServlet\\?([A-Za-z0-9=_&@.]*)\", true\\);");

        String session = null;
        String timeParams = "";
        boolean alreadyConnected = false;
        boolean noMoney = false;

        for (String line: lines)
        {
            line = line.trim();

            Matcher m1 = pattern1.matcher(line);
            if (m1.find())
            {
                session = m1.group(1);
            }

            Matcher m2 = pattern2.matcher(line);
            if (m2.find())
            {
                alreadyConnected = true;
            }

            Matcher m3 = pattern3.matcher(line);
            if (m3.find())
            {
                noMoney = true;
            }

            Matcher m4 = pattern4.matcher(line);
            if (m4.find())
            {
                timeParams = m4.group(1);
            }
        }

        if (session == null && !alreadyConnected && !noMoney)
            return null;

        LoginResult result = new LoginResult();
        result.alreadyConnected = alreadyConnected;
        result.noMoney = noMoney;
        result.session = session;
        result.timeParams = timeParams;
        return result;
    }

    public boolean parseLogoutResponse(List<String> lines)
    {
        if (lines == null || lines.size() == 0)
            return false;

        String line = lines.get(0).trim();
        return line.equals("logoutcallback('SUCCESS');");
    }

}
