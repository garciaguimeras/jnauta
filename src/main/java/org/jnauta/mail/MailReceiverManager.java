package org.jnauta.mail;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.util.SharedByteArrayInputStream;

/**
 * Created by noel on 2/18/17.
 */

public class MailReceiverManager extends MailManager
{

    Store store;

    public MailReceiverManager(String host, int port, String username, String password, MailSecurityType securityType, boolean acceptInvalidCertificates) throws  MailException
    {
        super(host, port, username, password, securityType, acceptInvalidCertificates);
        try
        {
            store = PopSession.createStore(host, port, username, password, securityType, acceptInvalidCertificates);
            store.connect();
        }
        catch (Exception e)
        {
            store = null;
            throw new MailException(e);
        }
    }

    public Store getStore()
    {
        return store;
    }

    public void close() throws MailException
    {
        try
        {
            store.close();
            store = null;
        }
        catch (MessagingException e)
        {
            store = null;
            throw new MailException(e);
        }
    }

    public List<Folder> getFolders() throws MailException
    {
        try
        {
            Folder[] folders = store.getDefaultFolder().list();
            return Arrays.asList(folders);
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public Folder openFolder(String folderName, boolean readOnly) throws MailException
    {
        try
        {
            Folder folder = store.getFolder(folderName);
            if (folder == null || !folder.exists())
                return null;
            folder.open(readOnly ? Folder.READ_ONLY : Folder.READ_WRITE);
            return folder;
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public Folder createFolder(Folder parent, String folderName) throws MailException
    {
        try
        {
            Folder folder = parent.getFolder(folderName);
            if (folder == null)
                return null;
            boolean result = true;
            if (!folder.exists())
                result = folder.create(Folder.HOLDS_MESSAGES);
            if (!result)
                return null;
            folder.open(Folder.READ_WRITE);
            return folder;
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public void closeFolder(Folder folder) throws MailException
    {
        try
        {
            folder.close(false);
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public int getUnreadMessagesCount(Folder folder) throws MailException
    {
        try
        {
            return folder.getUnreadMessageCount();
        }
        catch (MessagingException e)
        {
            throw new MailException(e);
        }
    }


    public Message[] getUnreadMessages(Folder folder) throws MailException
    {
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        try
        {
            Message[] messages = folder.search(ft);
            return messages;
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public int getNewMessagesCount(Folder folder) throws MailException
    {
        try
        {
            return folder.getNewMessageCount();
        }
        catch (MessagingException e)
        {
            throw new MailException(e);
        }
    }

    public Message[] getNewMessages(Folder folder) throws MailException
    {
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.RECENT), true);
        try
        {
            Message[] messages = folder.search(ft);
            return messages;
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public void markMessagesAsRead(Folder folder, Message[] messages) throws MailException
    {
        try
        {
            folder.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public void markMessageAsRead(Folder folder, Message message) throws MailException
    {
        markMessagesAsRead(folder, new Message[] { message });
    }

    public void copyMessages(Folder fromFolder, Folder toFolder, Message[] messages) throws MailException
    {
        try
        {
            fromFolder.copyMessages(messages, toFolder);
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public void copyMessage(Folder fromFolder, Folder toFolder, Message message) throws MailException
    {
        copyMessages(fromFolder, toFolder, new Message[] { message });
    }

    public void deleteMessages(Folder folder, Message[] messages) throws MailException
    {
        try
        {
            folder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public void deleteMessage(Folder folder, Message message) throws MailException
    {
        deleteMessages(folder, new Message[] { message });
    }

    private String getMessageContentAsMultipart(Multipart content)
    {
        String result = null;

        try
        {
            int count = content.getCount();
            for (int i = 0; i < count; i++)
            {
                BodyPart part = content.getBodyPart(i);
                if (part.isMimeType("text/plain"))
                {
                    result = (String) part.getContent();
                    break;
                }
                else if (part.isMimeType("text/html"))
                {
                    String html = (String) part.getContent();
                    result = Jsoup.parse(html).text();
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    private String getUTF8Content(Object contentObject)
    {
        SharedByteArrayInputStream sbais = (SharedByteArrayInputStream) contentObject;
        InputStreamReader isr = new InputStreamReader(sbais, Charset.forName("UTF-8"));
        int charsRead = 0;
        StringBuilder content = new StringBuilder();
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];

        try
        {
            while ((charsRead = isr.read(buffer)) != -1)
            {
                content.append(Arrays.copyOf(buffer, charsRead));
            }
        }
        catch (Exception e)
        {}

        return content.toString();
    }

    private String getMessageContentAsInputStream(InputStream is)
    {
        String result = null;
        byte[] array = null;

        try
        {
            array = IOUtils.toByteArray(is);
        }
        catch (Exception e)
        {}

        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(array);
            MimeMultipart multipart = new MimeMultipart(new ByteArrayDataSource(bais, "multipart/mixed"));

            result = null;
            for (int i = 0; i < multipart.getCount(); i++)
            {
                BodyPart part = multipart.getBodyPart(i);
                if (part.getContentType().startsWith("text/plain"))
                {
                    result = getUTF8Content(part.getContent());
                    break;
                }
                if (part.getContentType().startsWith("text/html"))
                {
                    String html = getUTF8Content(part.getContent());
                    result = Jsoup.parse(html).text();
                    break;
                }
            }
        }
        catch (Exception e)
        {
            result = null;
        }

        try
        {
            if (result == null)
                result = IOUtils.toString(array, "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public String getMessageFrom(Message message) throws MailException
    {
        try
        {
            return message.getFrom()[0].toString();
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public String getMessageSubject(Message message) throws MailException
    {
        try
        {
            return message.getSubject();
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }
    }

    public String getMessageContent(Message message) throws MailException
    {
        String result = null;
        try
        {
            if (message instanceof MimeMessage)
            {
                MimeMessage m = (MimeMessage) message;
                Object contentObject = m.getContent();
                if (contentObject instanceof Multipart)
                {
                    result = getMessageContentAsMultipart((Multipart) contentObject);
                }
                else if (contentObject instanceof String) // a simple text message
                {
                    result = (String) contentObject;
                }
                else if (contentObject instanceof InputStream) // an input stream
                {
                    result = getMessageContentAsInputStream((InputStream) contentObject);
                }
            }
            else // not a mime message
            {
                result = null;
            }
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }

        return result != null ? result : "";
    }

    private List<MailAttachment> getAttachments(Multipart content)
    {
        ArrayList<MailAttachment> result = new ArrayList<MailAttachment>();

        try
        {
            int count = content.getCount();
            for (int i = 0; i < count; i++)
            {
                BodyPart part = content.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
                {
                    MailAttachment attachment = new MailAttachment(part.getFileName(), part.getContentType(), part.getInputStream());
                    result.add(attachment);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public List<MailAttachment> getMessageAttachments(Message message) throws MailException
    {
        List<MailAttachment> result = null;

        try
        {
            if (message instanceof MimeMessage)
            {
                MimeMessage m = (MimeMessage) message;
                Object contentObject = m.getContent();
                if (contentObject instanceof Multipart)
                {
                    result = getAttachments((Multipart) contentObject);
                }
            }
        }
        catch (Exception e)
        {
            throw new MailException(e);
        }

        return result;
    }

}
