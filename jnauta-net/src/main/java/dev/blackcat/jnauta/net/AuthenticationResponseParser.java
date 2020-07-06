package dev.blackcat.jnauta.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationResponseParser
{

    public class LoginResult
    {
        public String session;
        public String loggerId;
        public String ssid;
        public String domain;
        public String username;
        public String wlanacname;
        public String wlanmac;
        public String wlanuserip;

        public boolean alreadyConnected;
        public boolean noMoney;
        public boolean badUsername;
        public boolean badPassword;
        public boolean isGoogle;

        public String getParamString() {
            StringBuilder stringBuilder = new StringBuilder();
            if (session != null) stringBuilder.append(session);
            if (loggerId != null) stringBuilder.append(loggerId);
            if (ssid != null) stringBuilder.append(ssid);
            if (domain != null) stringBuilder.append(domain);
            if (username != null) stringBuilder.append(username);
            if (wlanuserip != null) stringBuilder.append(wlanuserip);
            if (wlanmac != null) stringBuilder.append(wlanmac);
            if (wlanacname != null) stringBuilder.append(wlanacname);
            return stringBuilder.toString();
        }
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
        Pattern pattern3 = Pattern.compile("alert\\(\"Su tarjeta no tiene saldo disponible");
        Pattern pattern4 = Pattern.compile("alert\\(\"El usuario ya está conectado.\"\\);");
        Pattern pattern5 = Pattern.compile("alert\\(\"Entre el nombre de usuario y contraseña correctos");
        Pattern pattern6 = Pattern.compile("alert\\(\"No se pudo autorizar al usuario");

        Pattern pattern10 = Pattern.compile("var urlParam = \"([A-Za-z0-9=_&]*)\"");
        Pattern pattern11 = Pattern.compile("\\+ \"(&loggerId=[0-9]*\\+[A-Za-z0-9._@]*)\"");
        Pattern pattern12 = Pattern.compile("\\+ \"(&ssid=[A-Za-z0-9._@]*)\"");
        Pattern pattern13 = Pattern.compile("\\+ \"(&domain=[A-Za-z0-9._@]*)\"");
        Pattern pattern14 = Pattern.compile("\\+ \"(&username=[A-Za-z0-9._@]*)\"");
        Pattern pattern15 = Pattern.compile("\\+ \"(&wlanacname=[A-Za-z0-9._@]*)\"");
        Pattern pattern16 = Pattern.compile("\\+ \"(&wlanmac=[A-Za-z0-9._@]*)\"");
        Pattern pattern17 = Pattern.compile("\\+ \"(&wlanuserip=[A-Za-z0-9._@]*)\"");

        String session = null;
        String loggerId = null;
        String ssid = null;
        String domain = null;
        String username = null;
        String wlanacname = null;
        String wlanmac = null;
        String wlanuserip = null;

        boolean alreadyConnected = false;
        boolean noMoney = false;
        boolean badUsername = false;
        boolean badPassword = false;
        boolean isGoogle = false;

        Iterator iterator = lines.iterator();
        while (iterator.hasNext())
        {
            String line = (String)iterator.next();
            line = line.trim();

            System.out.println(line);

            if (line.contains("<title>Google</title>"))
                isGoogle = true;

            // Url parameters
            Matcher m10 = pattern10.matcher(line);
            session = (session == null && m10.find()) ? m10.group(1) : session;

            Matcher m11 = pattern11.matcher(line);
            loggerId = (loggerId == null && m11.find()) ? m11.group(1) : loggerId;

            Matcher m12 = pattern12.matcher(line);
            ssid = (ssid == null && m12.find()) ? m12.group(1) : ssid;

            Matcher m13 = pattern13.matcher(line);
            domain = (domain == null && m13.find()) ? m13.group(1) : domain;

            Matcher m14 = pattern14.matcher(line);
            username = (username == null && m14.find()) ? m14.group(1) : username;

            Matcher m15 = pattern15.matcher(line);
            wlanacname = (wlanacname == null && m15.find()) ? m15.group(1) : wlanacname;

            Matcher m16 = pattern16.matcher(line);
            wlanmac = (wlanmac == null && m16.find()) ? m16.group(1) : wlanmac;

            Matcher m17 = pattern17.matcher(line);
            wlanuserip = (wlanuserip == null && m17.find()) ? m17.group(1) : wlanuserip;

            // Boolean checks
            Matcher m3 = pattern3.matcher(line);
            noMoney = (!noMoney && m3.find()) ? true : noMoney;

            Matcher m4 = pattern4.matcher(line);
            alreadyConnected = (!alreadyConnected && m4.find()) ? true : alreadyConnected;

            Matcher m5 = pattern5.matcher(line);
            badPassword = (!badPassword && m5.find()) ? true : badPassword;

            Matcher m6 = pattern6.matcher(line);
            badUsername = (!badUsername && m6.find()) ? true : badUsername;
        }

        if (session == null && loggerId == null && !alreadyConnected && !noMoney && !badPassword && !badUsername && !isGoogle)
            return null;

        LoginResult result = new LoginResult();
        result.alreadyConnected = alreadyConnected;
        result.badPassword = badPassword;
        result.badUsername = badUsername;
        result.isGoogle = isGoogle;
        result.noMoney = noMoney;

        result.session = session;
        result.loggerId = loggerId;
        result.ssid = ssid;
        result.domain = domain;
        result.username = username;
        result.wlanacname = wlanacname;
        result.wlanmac = wlanmac;
        result.wlanuserip = wlanuserip;
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
