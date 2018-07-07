package dev.blackcat.jnauta.net;

import java.net.URLEncoder;
import java.util.HashMap;

public class Utils
{

    public static String toURLEncoded(HashMap<String, String> dict)
    {
        String params = "";
        for (String key: dict.keySet())
        {
            String value = dict.get(key);
            if (!params.equals(""))
                params += "&";
            try
            {
                params += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
            }
            catch (Exception e)
            {
                params += key + "=" + value;
            }
        }
        return params;
    }

}
