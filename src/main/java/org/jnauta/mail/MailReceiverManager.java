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

    public MailReceiverManager(String host, int port, String username, String password, MailSecurityType securityType, boolean acceptInvalidCertificates)
    {
        super(host, port, username, password, securityType, acceptInvalidCertificates);
        try
        {
            store = PopSession.createStore(host, port, username, password, securityType, acceptInvalidCertificates);
            store.connect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            store = null;
        }
    }

    public Store getStore()
    {
        return store;
    }

    public void close()
    {
        try
        {
            store.close();
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }
        store = null;
    }

    public List<Folder> getFolders()
    {
        try
        {
            Folder[] folders = store.getDefaultFolder().list();
            return Arrays.asList(folders);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new ArrayList<Folder>();
    }

    public Folder openFolder(String folderName, boolean readOnly)
    {
        try
        {
            Folder folder = store.getFolder(folderName);
            if (folder == null)
                return null;
            folder.open(readOnly ? Folder.READ_ONLY : Folder.READ_WRITE);
            return folder;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Folder createFolder(String folderName)
    {
        try
        {
            Folder folder = store.getDefaultFolder().getFolder(folderName);
            if (!folder.exists())
                folder.create(Folder.HOLDS_MESSAGES);
            folder.open(Folder.READ_WRITE);
            return folder;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }

    public void closeFolder(Folder folder)
    {
        try
        {
            folder.close(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int getUnreadMessagesCount(Folder folder)
    {
        try
        {
            return folder.getUnreadMessageCount();
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }
        return 0;
    }


    public Message[] getUnreadMessages(Folder folder)
    {
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        try
        {
            Message[] messages = folder.search(ft);
            return messages;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Message[] {};
    }

    public int getNewMessagesCount(Folder folder)
    {
        try
        {
            return folder.getNewMessageCount();
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public Message[] getNewMessages(Folder folder)
    {
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.RECENT), true);
        try
        {
            Message[] messages = folder.search(ft);
            return messages;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Message[] {};
    }

    public void markMessagesAsRead(Folder folder, Message[] messages)
    {
        try
        {
            folder.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void markMessageAsRead(Folder folder, Message message)
    {
        markMessagesAsRead(folder, new Message[] { message });
    }

    public void copyMessages(Folder fromFolder, Folder toFolder, Message[] messages)
    {
        try
        {
            fromFolder.copyMessages(messages, toFolder);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void copyMessage(Folder fromFolder, Folder toFolder, Message message)
    {
        copyMessages(fromFolder, toFolder, new Message[] { message });
    }

    public void deleteMessages(Folder folder, Message[] messages)
    {
        try
        {
            folder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void deleteMessage(Folder folder, Message message)
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

    public String getMessageFrom(Message message)
    {
        try
        {
            return message.getFrom()[0].toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public String getMessageSubject(Message message)
    {
        try
        {
            return message.getSubject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public String getMessageContent(Message message)
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
            e.printStackTrace();
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

    public List<MailAttachment> getMessageAttachments(Message message)
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
            e.printStackTrace();
        }

        return result;
    }

}
