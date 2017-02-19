package org.jnauta.mail;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by noel on 2/18/17.
 */

public class MailSenderManager extends MailManager
{

    Session session;

    public MailSenderManager(String host, int port, String username, String password, MailSecurityType securityType, boolean acceptInvalidCertificates)
    {
        super(host, port, username, password, securityType, acceptInvalidCertificates);

        session = SmtpSession.createSmtpSession(host, port, username, password, securityType, acceptInvalidCertificates);
    }

    public Message createMessage(String subject, String from, String to)
    {
        return createMessage(subject, from, new String[] { to }, new String[] {}, new String[] {});
    }

    public Message createMessage(String subject, String from, String[] to)
    {
        return createMessage(subject, from, to, new String[] {}, new String[] {});
    }

    public Message createMessage(String subject, String from, String[] to, String[] cc)
    {
        return createMessage(subject, from, to, cc, new String[] {});
    }

    public Message createMessage(String subject, String from, String[] to, String[] cc, String[] bcc)
    {
        try
        {
            Message message = new MimeMessage(session);
            message.setSubject(subject);
            message.setFrom(new InternetAddress(from));
            for (String str: to)
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(str));
            for (String str: cc)
                message.setRecipient(Message.RecipientType.CC, new InternetAddress(str));
            for (String str: bcc)
                message.setRecipient(Message.RecipientType.BCC, new InternetAddress(str));
            Multipart multipart = new MimeMultipart();
            message.setContent(multipart);
            return message;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void addMessageBody(Message message, String body)
    {
        try
        {
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(body);

            Multipart multipart = (MimeMultipart) message.getContent();
            multipart.addBodyPart(bodyPart);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addMessageAttachment(Message message, String filename, String mimeType, Object attachment)
    {
        try
        {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(attachment, mimeType));
            attachmentPart.setFileName(filename);

            Multipart multipart = (Multipart) message.getContent();
            multipart.addBodyPart(attachmentPart);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(Message message)
    {
        try
        {
            Transport transport = session.getTransport("smtp");
            transport.connect(host, port, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

}
