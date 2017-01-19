package org.gui4j.component.util;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingConstants;

public class Gui4jAlignment
{
    public final static Map mHorizontalAlign = new HashMap();
    public final static Map mVerticalAlign = new HashMap();

    public final static String LEFT = "left";
    public final static String CENTER = "center";
    public final static String RIGHT = "right";
    public final static String LEADING = "leading";
    public final static String TRAILING = "trailing";
    
    public final static String TOP = "top";
    public final static String BOTTOM = "bottom";
    
    static
    {
        mHorizontalAlign.put(LEFT, new Integer(SwingConstants.LEFT));
        mHorizontalAlign.put(CENTER, new Integer(SwingConstants.CENTER));
        mHorizontalAlign.put(RIGHT, new Integer(SwingConstants.RIGHT));
        mHorizontalAlign.put(LEADING, new Integer(SwingConstants.LEADING));
        mHorizontalAlign.put(TRAILING, new Integer(SwingConstants.TRAILING));

        mVerticalAlign.put(TOP, new Integer(SwingConstants.TOP));
        mVerticalAlign.put(CENTER, new Integer(SwingConstants.CENTER));
        mVerticalAlign.put(BOTTOM, new Integer(SwingConstants.BOTTOM));
    }

}