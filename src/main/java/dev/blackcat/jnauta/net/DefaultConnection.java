package dev.blackcat.jnauta.net;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class DefaultConnection extends Connection
{
    @Override
    protected Result http(Type type, String urlStr, Proxy proxy, String parameters)
    {
        List<String> output = new ArrayList<String>();
        final Connection.Result result = new Connection.Result();
        result.url = urlStr;

        try
        {
            if (type == Type.GET && parameters != null)
                urlStr += "?" + parameters;
            URL url = new URL(urlStr);
            HttpURLConnection connection;

            // SSL stuff

            TrustManager[] allCerts = new TrustManager[] {
                    new X509TrustManager()
                    {

                        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                        {}

                        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                        {}

                        public X509Certificate[] getAcceptedIssuers()
                        {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }
                    }
            };

            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, allCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = new HostnameVerifier()
            {
                public boolean verify(String arg0, SSLSession arg1)
                {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Connection

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Referer", "https://secure.etecsa.net:8443/");
            connection.setRequestProperty("Host", "secure.etecsa.net");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.addRequestProperty("Cookie", this.getCookies());

            if (type == Type.POST)
            {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                OutputStream outStream = connection.getOutputStream ();
                outStream.write(parameters.getBytes("UTF-8"));
                outStream.close();
            }

            InputStream inStream = connection.getInputStream();
            result.statusCode = connection.getResponseCode();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while ((line = reader.readLine()) != null)
                output.add(line);
            inStream.close();

            String cookies = "";
            List<String> requestCookies = connection.getHeaderFields().get("Set-Cookie");
            if (requestCookies != null)
            {
                for (String cookie: requestCookies)
                {
                    cookies += cookie.split(";", 1)[0] + "; ";
                }
            }
            this.setCookies(cookies);

            connection.disconnect();
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
