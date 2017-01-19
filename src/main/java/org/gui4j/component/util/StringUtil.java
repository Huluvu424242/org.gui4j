package org.gui4j.component.util;

public class StringUtil
{

    /**
     * Constructor for StringUtil.
     */
    private StringUtil()
    {
        super();
    }

    /**
     * @param c zu kopierendes Zeichen
     * @param count Anzahl
     * @return String
     */
    public static String copy(char c, int count)
    {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < count; i++)
        {
            b.append(c);
        }
        return b.toString();
    }

}
