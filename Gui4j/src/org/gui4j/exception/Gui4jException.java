package org.gui4j.exception;

/**
 * Base class for all exceptions in this package
 * @author Joachim Schmid
 */
public class Gui4jException extends Exception
{
    public Gui4jException(Throwable e)
    {
        super(e);
    }
    
    public Gui4jException(String str)
    {
        super(str);
    }
}

