package org.jnauta.mail;

/**
 * Created by noel on 2/18/17.
 */

public class NautaMailReceiver extends MailReceiverManager
{
    public NautaMailReceiver(String username, String password)
    {
        super("pop.nauta.cu",
              110,
              username.endsWith("@nauta.cu") ? username : username + "@nauta.cu",
              password,
              MailSecurityType.NONE,
              false);
    }
}
