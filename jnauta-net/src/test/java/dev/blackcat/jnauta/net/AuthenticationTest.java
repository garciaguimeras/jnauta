package dev.blackcat.jnauta.net;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertNotNull(loginResult.timeParams);
        Assert.assertNotEquals(loginResult.timeParams, "");

        System.out.println(loginResult.session);
        System.out.println(loginResult.timeParams);

        AuthenticationResponseParser.LoginResult loginResult2 = authentication.accountStatus();
        Assert.assertNotNull(loginResult2);
        Assert.assertFalse(loginResult2.alreadyConnected);
        Assert.assertFalse(loginResult2.noMoney);
        Assert.assertFalse(loginResult2.badUsername);
        Assert.assertFalse(loginResult2.badPassword);
        Assert.assertFalse(loginResult2.isGoogle);
        Assert.assertNotNull(loginResult2.session);
        Assert.assertNotEquals(loginResult2.session, "");
        Assert.assertNotNull(loginResult2.timeParams);
        Assert.assertNotEquals(loginResult2.timeParams, "");

        System.out.println(loginResult2.session);
        System.out.println(loginResult2.timeParams);

        boolean logoutResult = authentication.logout(loginResult2.session);
        Assert.assertTrue(logoutResult);
    }

}
