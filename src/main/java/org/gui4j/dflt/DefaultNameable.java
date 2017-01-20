package org.gui4j.dflt;

import java.io.Serializable;
import java.util.Comparator;

import org.gui4j.core.util.Extract;
import org.gui4j.util.Nameable;
import org.gui4j.util.Translator;


/**
 * Default Implementierung für das Nameable Interface.
 */
public class DefaultNameable implements Nameable, Serializable
{
    protected final String nameTag;
    private final String shortTag;

	/**
	 * Method getTextComparator.
	 * @param translator
	 * @return Comparator
	 */
    public static Comparator getTextComparator(final Translator translator)
    {
        return new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                assert o1 != null && o2 != null;
                Nameable n1 = (Nameable) o1;
                Nameable n2 = (Nameable) o2;
                return translator.translate(n1.getNameTag(), null).compareTo(
                    translator.translate(n2.getNameTag(), null));
            }
        };
    }

    /**
     * Constructor for DefaultNameable.
     * @param shortTag
     */
    public DefaultNameable(String shortTag)
    {
        assert shortTag!=null;
        Class c = getTagClass();
        nameTag = createTag(shortTag, c);
        this.shortTag = shortTag;
    }
    
    protected Class getTagClass()
    {
    	return getClass();
    }

    /*
     * @see de.bea.util.Nameable#getNameTag()
     */
    public String getNameTag()
    {
        return nameTag;
    }

    /*
     * @see de.bea.util.Nameable#getShortTag()
     */
    public String getShortTag()
    {
        return shortTag;
    }

    /**
     * Method createTag.
     * @param tag
     * @param clazz
     * @return String
     */
    public static String createTag(String tag, Class clazz)
    {
        return clazz.getName() + "_" + tag;
    }

    /**
     * Method createShortTag.
     * @param tag
     * @param clazz
     * @return String
     */
    public static String createShortTag(String tag, Class clazz)
    {
        return Extract.getClassname(clazz) + "_" + tag;
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return Extract.getClassname(getClass()) + "[" + shortTag + "]";
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getNameTag().hashCode();
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof Nameable))
        {
            return false;
        }
        else
        {
            Nameable arg = (Nameable) o;
            return getNameTag().equals(arg.getNameTag());
        }
    }

}
