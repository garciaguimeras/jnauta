package org.jnauta.mail;

import java.io.InputStream;

/**
 * Created by noel on 2/21/17.
 */
public class MailAttachment
{

    InputStream inputStream;
    String fileName;
    String contentType;

    public MailAttachment(String fileName, String contentType, InputStream inputStream)
    {
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
}
