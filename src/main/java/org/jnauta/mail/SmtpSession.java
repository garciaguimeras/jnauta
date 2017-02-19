package org.jnauta.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class SmtpSession
{
	public static boolean debug = false;
	public static int DefaultSMTPSocketOperationTimeout = 20000; // 20seg

	public static Session createSmtpSession(String host, int port, final String username, final String password, MailSecurityType securityType, boolean acceptInvalidCertificates)
	{
		port = getSmtpPort(securityType, port);

		final Properties properties = new Properties();

		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.port", String.valueOf(port));
		properties.setProperty("mail.debug", String.valueOf(debug));

		// useful for cases like when talking plain to SSL enabled ports
		properties.setProperty("mail.smtp.connectiontimeout", String.valueOf(DefaultSMTPSocketOperationTimeout));
		properties.setProperty("mail.smtp.timeout", String.valueOf(DefaultSMTPSocketOperationTimeout));
		properties.setProperty("mail.smtp.writetimeout", String.valueOf(DefaultSMTPSocketOperationTimeout));

		if (securityType == MailSecurityType.TLS)
		{
			properties.setProperty("mail.smtp.socketFactory.fallback", "false");
			properties.setProperty("mail.smtp.starttls.enable", "true");
			properties.setProperty("mail.smtp.starttls.required", "true");
		}

		if (securityType == MailSecurityType.SSL)
		{
			properties.setProperty("mail.smtp.socketFactory.fallback", "false");
			properties.setProperty("mail.smtp.socketFactory.port", String.valueOf(port));
			properties.setProperty("mail.smtp.ssl.enable", "true");
			properties.setProperty("mail.smtps.ssl.enable", "true");
			properties.setProperty("mail.smtp.starttls.enable", "true");
		}

		if (acceptInvalidCertificates)
		{
			properties.put("mail.smtp.ssl.socketFactory.class", "org.acme.pocs.jmail.AlwaysTrustSSLContextFactory");

			properties.put("mail.smtp.ssl.checkserveridentity", "false");
			properties.put("mail.smtp.ssl.trust", "*");
			properties.put("mail.smtp.tls.checkserveridentity", "false");
			properties.put("mail.smtp.tls.trust", "*");

			properties.setProperty("mail.smtps.ssl.checkserveridentity", "false");
			properties.setProperty("mail.smtps.ssl.trust", "*");
			properties.setProperty("mail.smtps.tls.checkserveridentity", "false");
			properties.setProperty("mail.smtps.tls.trust", "*");
		}

		Session session;
		Authenticator authenticator = null;

		if (username != null && password != null) // if smtp authentication requested
		{
			// enable SMTP authentication
			properties.setProperty("mail.smtp.auth", "true");
			properties.setProperty("mail.user", username);
			properties.setProperty("mail.password", password);

			authenticator = new javax.mail.Authenticator()
			{
				// override the getPasswordAuthentication method
				@Override
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(username, password);
				}
			};

			session = Session.getInstance(properties, authenticator);
		}
		else
			session = Session.getInstance(properties);

		return session;
	}

	public static int getSmtpPort(MailSecurityType securityType, int port)
	{
		return port <= 0 ? getDefaultSmtpPort(securityType) : port;
	}

	public static int getDefaultSmtpPort(MailSecurityType securityType)
	{
		switch (securityType)
		{
			case SSL:
				return 465;
			case TLS:
				return 587;
			case NONE:
			default:
				return 25;
		}
	}
}
