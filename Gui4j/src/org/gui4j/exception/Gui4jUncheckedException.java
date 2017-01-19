package org.gui4j.exception;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.util.DefaultTranslator;
import org.gui4j.util.Translator;

public abstract class Gui4jUncheckedException extends RuntimeException implements ErrorTags
{
    private final String mErrorTag;
    private final Object[] mErrorArgs;

    protected static final Log mLogger = LogFactory.getLog(Gui4jUncheckedException.class);
    protected static final Translator defaultTranslator = new DefaultTranslator(Locale.US, Gui4jUncheckedException.class,
            "errorMessages");

    public String getMessage()
    {
        return getMessage(defaultTranslator);
    }

    public String getMessage(Locale locale)
    {
        Translator translator = new DefaultTranslator(locale, getClass(), "errorMessages");
        return getMessage(translator);
    }

    public String getMessage(Translator translator)
    {
        return getContext(translator) + translator.translate(mErrorTag, mErrorArgs);
    }

    public String getContext(Translator translator)
    {
        return "";
    }

    protected Gui4jUncheckedException(String errorTag, Object[] errorArgs, Throwable e)
    {
        super(errorTag, e);
        mErrorTag = errorTag;
        mErrorArgs = errorArgs;
    }

    public static class InternalError extends Gui4jUncheckedException
    {
        protected InternalError(String errorTag, Object[] errorArgs, Throwable e)
        {
            super(errorTag, errorArgs, e);
        }

        protected void printLogMessage()
        {
            mLogger.warn("Exception created: " + getMessage());
        }
    }

    public static class ErrorList extends InternalError
    {
        private final List errorList;

        public ErrorList(List errorList)
        {
            super("error_list", new String[] { getMessage(errorList) }, null);
            this.errorList = new ArrayList(errorList);
        }

        public List getErrorList()
        {
            return errorList;
        }

        private static String getMessage(List errorList)
        {
            StringBuffer str = new StringBuffer();
            Set alreadyAdded = new HashSet();
            add(str, errorList, alreadyAdded);
            return str.toString();
        }

        private static void add(StringBuffer str, List errorList, Set alreadyAdded)
        {
            for (Iterator it = errorList.iterator(); it.hasNext();)
            {
                Throwable t = (Throwable) it.next();
                if (t instanceof ErrorList)
                {
                    ErrorList errorListExc = (ErrorList) t;
                    add(str, errorListExc.getErrorList(), alreadyAdded);
                }
                else
                {
                    String message = t.getMessage();
                    if (message == null) {
                        message = t.getClass().getName();
                    }
                    if (!alreadyAdded.contains(message))
                    {
                        str.append("\n --> ");
                        str.append(message);
                        alreadyAdded.add(message);
                    }
                }
            }
        }
    }

    public static class ProgrammingError extends InternalError
    {
        public ProgrammingError(String errorTag)
        {
            super(errorTag, null, null);
            printLogMessage();
        }

        public ProgrammingError(String errorTag, Throwable e)
        {
            super(errorTag, null, e);
            printLogMessage();
        }

        public ProgrammingError(String errorTag, Object[] args)
        {
            super(errorTag, args, null);
            printLogMessage();
        }

        public String getContext(Translator translator)
        {
            return translator.translate(PROGRAMMING_ERROR, null);
        }
    }

    public static class ResourceError extends InternalError
    {
        private final String mRessourceName;
        private final int mLineNumber;

        public ResourceError(String ressourceName, int lineNumber, String errorTag)
        {
            super(errorTag, null, null);
            mRessourceName = ressourceName;
            mLineNumber = lineNumber;
            printLogMessage();
        }

        public ResourceError(String ressourceName, int lineNumber, String errorTag, Object[] args)
        {
            super(errorTag, args, null);
            mRessourceName = ressourceName;
            mLineNumber = lineNumber;
            printLogMessage();
        }

        public ResourceError(String ressourceName, int lineNumber, String errorTag, Object[] args, Throwable e)
        {
            super(errorTag, args, e);
            mRessourceName = ressourceName;
            mLineNumber = lineNumber;
            printLogMessage();
        }

        public ResourceError(String ressourceName, int lineNumber, String errorTag, Throwable e)
        {
            super(errorTag, null, e);
            mRessourceName = ressourceName;
            mLineNumber = lineNumber;
            printLogMessage();
        }

        public String getContext(Translator translator)
        {
            if (mLineNumber == -1)
            {
                Object[] args = { mRessourceName };
                return translator.translate(RESOURCE_ERROR, args);
            }
            else
            {
                Object[] args = { mRessourceName + ":" + mLineNumber };
                return translator.translate(RESOURCE_ERROR, args);
            }
        }
    }
}