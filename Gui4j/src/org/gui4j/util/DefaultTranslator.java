package org.gui4j.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.util.Translator;

/**
 * Default implementation for the interface <code>Translator</code>.
 * The translation is taken from a property file. Optional it is
 * possible to delegate the translation process to another 
 * <code>Translator</code> if no entry was found in the property file.
 * 
 * @author Joachim Schmid
 * 
 */
public class DefaultTranslator implements Translator
{

    private static final Log logger = LogFactory.getLog(DefaultTranslator.class);

    private final ResourceBundle resourceBundle;
    private final Translator delegateTo;
    private final String bundleName;

    private static Translator idTranslator = new IdTranslator();

    /**
     * @param locale translation locale
     * @param clazz the classloader of this class will be used to locate the property file
     * @param bundleName the name of the property file
    */
    public DefaultTranslator(Locale locale, Class clazz, String bundleName)
    {
        this(locale, clazz, bundleName, null);
    }

    /**
     * @param locale translation locale
     * @param clazz the classloader of this class will be used to locate the property file
     * @param bundleName the name of the property file
     * @param delegateTo the translator where the translation is delegated to if a tag cannot be found in then given bundle
    */
    public DefaultTranslator(Locale locale, Class clazz, String bundleName, Translator delegateTo)
    {
        this.bundleName = bundleName;
        this.delegateTo = delegateTo;
        this.resourceBundle =
            ResourceBundle.getBundle(
                clazz.getPackage().getName() + "/" + bundleName,
                locale,
                clazz.getClassLoader());
    }

    /**
     * @return a translator which builds a simple string based on the tag and arguments
    */
    public static Translator getIdTranslator()
    {
        return idTranslator;
    }

    /**
     * Implementation of the interface method to translate a tag
     * @param errorTag
     * @param args
     * @return String
    */
    public String translate(String errorTag, Object[] args)
    {
        assert errorTag != null;
        
        if (errorTag == "")
        {
            return "";
        }

        String text = null;
        try
        {
            text = resourceBundle.getString(errorTag);
            if (text != null)
            {
                text = text.replaceAll("\\n", "\n");
            }
        }
        catch (MissingResourceException e)
        {
            // do nothing
        }
        
        if (text == null && delegateTo != null)
        {
            text = delegateTo.translate(errorTag, args);
            if (text != null)
            {
                return text;
            }
        }

        if (text == null)
        {
            logger.warn("Tag " + errorTag + " is not defined in bundle " + bundleName);
            return getIdTranslator().translate(errorTag, args);
        }
        else
        {
            MessageFormat msgFormat = new MessageFormat(text);
            return msgFormat.format(args);
        }
    }

    /**
     * @see org.gui4j.util.Translator#isDefined(java.lang.String)
     */
    public boolean isDefined(String errorTag)
    {
        assert errorTag != null;
        
        if (errorTag == "")
        {
            return true;
        }

        try
        {
            if (resourceBundle.getString(errorTag) != null)
            {
                return true;
            }
        }
        catch (MissingResourceException e)
        {
            // do nothing
        }
        
        if (delegateTo != null)
        {
            return delegateTo.isDefined(errorTag);
        }

        return false;
    }

    private static class IdTranslator implements Translator
    {
        public String translate(String tag, Object[] args)
        {
            if (args != null)
            {
                StringBuffer buffer = new StringBuffer(tag);
                buffer.append(": ");
                for (int i = 0; i < args.length; i++)
                {
                    if (i > 0)
                    {
                        buffer.append(", ");
                    }
                    buffer.append(args[i]);
                }
                return buffer.toString();
            }
            else
            {
                return tag;
            }
        }
        
        /**
         * @see org.gui4j.util.Translator#isDefined(java.lang.String)
         */
        public boolean isDefined(String tag)
        {
            return true;
        }

    }


}
