package dev.blackcat.jnauta.net;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class AuthenticationTest
{

    @Test
    public void loginAndLogout()
    {
        String username = "";
        String password = "";
        Authentication authentication = new Authentication(ConnectionBuilder.Method.OK_HTTP, username, password, null);

        AuthenticationResponseParser.LoginResult loginResult = authentication.login();
        Assert.assertNotNull(loginResult);
        Assert.assertFalse(loginResult.alreadyConnected);
        Assert.assertFalse(loginResult.noMoney);
        Assert.assertFalse(loginResult.badUsername);
        Assert.assertFalse(loginResult.badPassword);
        Assert.assertFalse(loginResult.isGoogle);
        Assert.assertNotNull(loginResult.session);
        Assert.assertNotEquals(loginResult.session, "");

        String loginParam = loginResult.getParamString();
        System.out.println(loginParam);

        /*
        AuthenticationResponseParser.LoginResult loginResult2 = authentication.accountStatus();
        Assert.assertNotNull(loginResult2);
        Assert.assertFalse(loginResult2.alreadyConnected);
        Assert.assertFalse(loginResult2.noMoney);
        Assert.assertFalse(loginResult2.badUsername);
        Assert.assertFalse(loginResult2.badPassword);
        Assert.assertFalse(loginResult2.isGoogle);
        Assert.assertNotNull(loginResult2.session);
        Assert.assertNotEquals(loginResult2.session, "");

        System.out.println(loginResult2.session);
         */

        String availTime = authentication.getAvailableTime(loginParam);
        System.out.println(availTime);

        boolean logoutResult = authentication.logout(loginParam);
        Assert.assertTrue(logoutResult);
    }

    @Test
    public void testParser()
    {
        String[] lines = new String[] {
            "\t\t             var urlParam = \"ATTRIBUTE_UUID=C2F258618FE0B593044705E708D948BA&CSRFHW=b0d0f8caa03fe819094ef0c953f2e7c5\"",
            "\t\t                     + \"&loggerId=20200603214242984+gildaguimeras@nauta.com.cu\""
        };

        AuthenticationResponseParser parser = new AuthenticationResponseParser();
        AuthenticationResponseParser.LoginResult loginResult = parser.parseLoginResponse(Arrays.asList(lines));
        Assert.assertNotNull(loginResult);
        System.out.println(loginResult.session);
        System.out.println(loginResult.loggerId);
    }

}
