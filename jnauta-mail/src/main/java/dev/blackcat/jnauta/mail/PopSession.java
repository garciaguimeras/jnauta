package dev.blackcat.jnauta.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import com.sun.mail.pop3.POP3SSLStore;
import com.sun.mail.pop3.POP3Store;

public class PopSession
{
	public static boolean debug = false;
	public static int DefaultPOP3SocketOperationTimeout = 20000; // 20seg

	public static Store createStore(String host, int port, final String username, final String password, MailSecurityType securityType, boolean acceptInvalidCertificates)
	{
		Properties popProps = new Properties();

		// server setting
		popProps.setProperty("mail.pop3.host", host);
		popProps.setProperty("mail.pop3.port", String.valueOf(port));
		popProps.setProperty("mail.pop3.username", username);
		popProps.setProperty("mail.pop3.password", password);

		// useful for cases like when talking plain to SSL enabled ports
		popProps.setProperty("mail.pop3.connectiontimeout", String.valueOf(DefaultPOP3SocketOperationTimeout));
		popProps.setProperty("mail.pop3.timeout", String.valueOf(DefaultPOP3SocketOperationTimeout));
		popProps.setProperty("mail.pop3.writetimeout", String.valueOf(DefaultPOP3SocketOperationTimeout));
		popProps.setProperty("mail.pop3.rsetbeforequit", "true"); // avoid msg del in servers that auto-mark as del

		popProps.put("mail.debug", String.valueOf(debug));

		if (securityType == MailSecurityType.SSL)
		{
			popProps.setProperty("mail.pop3.socketFactory.fallback", "false");
			popProps.setProperty("mail.pop3.socketFactory.port", String.valueOf(port));
			popProps.setProperty("mail.pop3.ssl.socketFactory.port", String.valueOf(port));
			popProps.setProperty("mail.pop3.ssl.enable", "true");
			popProps.setProperty("mail.pop3s.starttls.enable", "true");
			popProps.setProperty("mail.pop3s.starttls.required", "false");
		}

		if (securityType == MailSecurityType.TLS)
		{
			popProps.setProperty("mail.pop3.socketFactory.fallback", "false");
			popProps.setProperty("mail.pop3.socketFactory.port", String.valueOf(port));
			popProps.setProperty("mail.pop3.starttls.enable", "true");
			popProps.setProperty("mail.pop3.starttls.required", "true");
			popProps.setProperty("mail.pop3s.starttls.enable", "true");
			popProps.setProperty("mail.pop3s.starttls.required", "true");
		}

		if (acceptInvalidCertificates)
		{
			popProps.setProperty("mail.pop3.ssl.socketFactory.class", "org.acme.pocs.jmail.AlwaysTrustSSLContextFactory");

			popProps.setProperty("mail.pop3s.ssl.checkserveridentity", "false");
			popProps.setProperty("mail.pop3s.ssl.trust", "*");
		}

		URLName popUrl = new URLName(securityType == MailSecurityType.SSL ? "pop3s" : "pop3", host, port, "", username, password);

		Session session = javax.mail.Session.getInstance(popProps, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, password);
			}
		});

		// connects to the pop3 message store
		if (securityType == MailSecurityType.SSL)
		{
			return new POP3SSLStore(session, popUrl);
		}

		return new POP3Store(session, popUrl);
	}

	public static int getDefaultPort(MailSecurityType securityType)
	{
		switch (securityType)
		{
			case SSL:
				return 995;
			case TLS:
			case NONE:
			default:
				return 110;
		}
	}

	public static int getPort(MailSecurityType securityType, int port)
	{
		return port <= 0 ? getDefaultPort(securityType) : port;
	}
}
