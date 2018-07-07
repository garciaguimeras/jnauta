package dev.blackcat.jnauta.mail;

/**
 * Created by noel on 2/18/17.
 */

public abstract class MailManager
{
    String host;
    int port;
    String username;
    String password;
    MailSecurityType securityType;
    boolean acceptInvalidCertificates;

    public MailManager(String host, int port, String username, String password, MailSecurityType securityType, boolean acceptInvalidCertificates)
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.securityType = securityType;
        this.acceptInvalidCertificates = acceptInvalidCertificates;
    }

}
