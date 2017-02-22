package org.jnauta.mail;

/**
 * Created by noel on 2/18/17.
 */

public class NautaMailReceiver extends MailReceiverManager
{
    public NautaMailReceiver(Protocol protocol, String username, String password) throws MailException
    {
        super(protocol,
              protocol == Protocol.POP ? "pop.nauta.cu" : "imap.nauta.cu",
              protocol == Protocol.POP ? 110 : 143,
              username.endsWith("@nauta.cu") ? username : username + "@nauta.cu",
              password,
              MailSecurityType.NONE,
              false);
    }
}
