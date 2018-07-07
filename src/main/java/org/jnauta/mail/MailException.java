package org.jnauta.mail;

/**
 * Created by noel on 2/22/17.
 */
public class MailException extends Exception
{
    public MailException()
    {
    }

    public MailException(String message)
    {
        super(message);
    }

    public MailException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MailException(Throwable cause)
    {
        super(cause);
    }

    public MailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
