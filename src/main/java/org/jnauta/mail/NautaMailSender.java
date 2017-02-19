package org.jnauta.mail;

/**
 * Created by noel on 2/18/17.
 */

public class NautaMailSender extends MailSenderManager
{

    public NautaMailSender(String username, String password)
    {
        super("smtp.nauta.cu",
                25,
                username.endsWith("@nauta.cu") ? username : username + "@nauta.cu",
                password,
                MailSecurityType.NONE,
                false);
    }
}
