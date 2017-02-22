package org.jnauta.mail;

import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.pop3.POP3SSLStore;
import com.sun.mail.pop3.POP3Store;

import javax.mail.*;
import java.util.Properties;

public class ImapSession
{
	public static boolean debug = false;
	public static int DefaultPOP3SocketOperationTimeout = 20000; // 20seg

	public static Store createStore(String host, int port, final String username, final String password, MailSecurityType securityType, boolean acceptInvalidCertificates)
	{
		Properties popProps = new Properties();

		// server setting
		popProps.setProperty("mail.imap.host", host);
		popProps.setProperty("mail.imap.port", String.valueOf(port));
		popProps.setProperty("mail.imap.username", username);
		popProps.setProperty("mail.imap.password", password);

		// useful for cases like when talking plain to SSL enabled ports
		popProps.setProperty("mail.imap.connectiontimeout", String.valueOf(DefaultPOP3SocketOperationTimeout));
		popProps.setProperty("mail.imap.timeout", String.valueOf(DefaultPOP3SocketOperationTimeout));
		popProps.setProperty("mail.imap.writetimeout", String.valueOf(DefaultPOP3SocketOperationTimeout));
		popProps.setProperty("mail.imap.rsetbeforequit", "true"); // avoid msg del in servers that auto-mark as del

		popProps.put("mail.debug", String.valueOf(debug));

		if (securityType == MailSecurityType.SSL)
		{
			popProps.setProperty("mail.imap.socketFactory.fallback", "false");
			popProps.setProperty("mail.imap.socketFactory.port", String.valueOf(port));
			popProps.setProperty("mail.imap.ssl.socketFactory.port", String.valueOf(port));
			popProps.setProperty("mail.imap.ssl.enable", "true");
			popProps.setProperty("mail.imaps.starttls.enable", "true");
			popProps.setProperty("mail.imaps.starttls.required", "false");
		}

		if (securityType == MailSecurityType.TLS)
		{
			popProps.setProperty("mail.imap.socketFactory.fallback", "false");
			popProps.setProperty("mail.imap.socketFactory.port", String.valueOf(port));
			popProps.setProperty("mail.imap.starttls.enable", "true");
			popProps.setProperty("mail.imap.starttls.required", "true");
			popProps.setProperty("mail.imaps.starttls.enable", "true");
			popProps.setProperty("mail.imaps.starttls.required", "true");
		}

		if (acceptInvalidCertificates)
		{
			popProps.setProperty("mail.imap.ssl.socketFactory.class", "org.acme.pocs.jmail.AlwaysTrustSSLContextFactory");

			popProps.setProperty("mail.imaps.ssl.checkserveridentity", "false");
			popProps.setProperty("mail.imaps.ssl.trust", "*");
		}

		URLName popUrl = new URLName(securityType == MailSecurityType.SSL ? "imaps" : "imap", host, port, "", username, password);

		Session session = Session.getInstance(popProps, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, password);
			}
		});

		// connects to the pop3 message store
		if (securityType == MailSecurityType.SSL)
		{
			return new IMAPSSLStore(session, popUrl);
		}

		return new IMAPStore(session, popUrl);
	}

	public static int getDefaultPort(MailSecurityType securityType)
	{
		switch (securityType)
		{
			case SSL:
				return 993;
			case TLS:
			case NONE:
			default:
				return 143;
		}
	}

	public static int getPort(MailSecurityType securityType, int port)
	{
		return port <= 0 ? getDefaultPort(securityType) : port;
	}
}
