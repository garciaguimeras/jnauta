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
        public boolean badUsername;
        public boolean badPassword;
        public boolean isGoogle;
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
                result.put("CSRFHW", m2.group(1));
            }
        }
        return result;
    }

    public LoginResult parseLoginResponse(List<String> lines)
    {
        Pattern pattern1 = Pattern.compile("var urlParam = \"([A-Za-z0-9=_&]*)\"");
        Pattern pattern2 = Pattern.compile("g_httpRequest.open\\(\"post\", \"/EtecsaQueryServlet\\?([A-Za-z0-9=_&@.]*)\", true\\);");
        Pattern pattern3 = Pattern.compile("alert\\(\"Su tarjeta no tiene saldo disponible");
        Pattern pattern4 = Pattern.compile("alert\\(\"El usuario ya está conectado.\"\\);");
        Pattern pattern5 = Pattern.compile("alert\\(\"Entre el nombre de usuario y contraseña correctos");
        Pattern pattern6 = Pattern.compile("alert\\(\"No se pudo autorizar al usuario");

        String session = null;
        String timeParams = null;
        boolean alreadyConnected = false;
        boolean noMoney = false;
        boolean badUsername = false;
        boolean badPassword = false;
        boolean isGoogle = false;

        for (String line: lines)
        {
            line = line.trim();

            if (line.contains("<title>Google</title>"))
                isGoogle = true;

            Matcher m1 = pattern1.matcher(line);
            if (m1.find())
            {
                session = m1.group(1);
            }

            Matcher m2 = pattern2.matcher(line);
            if (m2.find())
            {
                timeParams = m2.group(1);
            }

            Matcher m3 = pattern3.matcher(line);
            if (m3.find())
            {
                noMoney = true;
            }

            Matcher m4 = pattern4.matcher(line);
            if (m4.find())
            {
                alreadyConnected = true;
            }

            Matcher m5 = pattern5.matcher(line);
            if (m5.find())
            {
                badPassword = true;
            }

            Matcher m6 = pattern6.matcher(line);
            if (m6.find())
            {
                badUsername = true;
            }
        }

        if (session == null && timeParams == null && !alreadyConnected && !noMoney && !badPassword && !badUsername && !isGoogle)
            return null;

        LoginResult result = new LoginResult();
        result.alreadyConnected = alreadyConnected;
        result.badPassword = badPassword;
        result.badUsername = badUsername;
        result.isGoogle = isGoogle;
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
