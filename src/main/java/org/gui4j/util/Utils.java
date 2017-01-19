package org.gui4j.util;

import java.util.Arrays;

/**
 * Utility class holding useful static methods.
 */
public final class Utils
{

    // private constructor to prevent instantiation
    private Utils() {        
    }
    
    /**
     * Returns a String consisting of <code>count</code> spaces.
     * Useful for producing indented output, e.g. in debugging statements.
     * @param count
     */
    public static String indent(int count) {
        // TODO: shouldn't we depend on Apache's Commons Lang and
        // use org.apache.commons.lang.StringUtils.repeat() instead?

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < count; i++) {
            buf.append(' ');
        }
        return buf.toString();
    }
    
    public static String arrayToString(Object[] array) {
        if (array == null) {
            return "null";
        }
        return Arrays.asList(array).toString();
    }
}
