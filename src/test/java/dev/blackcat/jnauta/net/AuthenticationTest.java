package dev.blackcat.jnauta.net;

import org.junit.Assert;
import org.junit.Test;

public class AuthenticationTest
{

    @Test
    public void loginAndLogout()
    {
        String username = "USERNAME@nauta.com.cu";
        String password = "PASSWORD";
        Authentication authentication = new Authentication(ConnectionBuilder.Method.DEFAULT, username, password, null);

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

        boolean logoutResult = authentication.logout(loginResult.session);
        Assert.assertTrue(logoutResult);
    }

}
