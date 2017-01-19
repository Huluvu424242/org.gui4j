package org.gui4j.core;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.Map;

import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.LElement;
import org.gui4j.Gui4jCallBase;
import org.gui4j.component.util.Gui4jAlignment;

/**
 * Encapsulates foreground color, background color, and font
 */
public final class Gui4jTextAttribute implements Serializable
{
    public static final String FOREGROUND = "foreground";
    public static final String BACKGROUND = "background";
    public static final String EVEN_BACKGROUND = "evenBackground";
    public static final String FONT = "font";
    public static final String ALIGNMENT = "alignment";

    private static final Log mLogger = LogFactory.getLog(Gui4jTextAttribute.class);

    private final Gui4jCall mForeground;
    private final Gui4jCall mBackground;
    private final Gui4jCall mEvenBackground;
    private final Gui4jCall mFont;
    private final int mAlignment;

    /**
     * Constructor for Gui4jTextAttribute.
     * 
     * @param foreground
     * @param background
     * @param evenBackground
     * @param font
     * @param alignment
     */
    private Gui4jTextAttribute(Gui4jCall foreground, Gui4jCall background, Gui4jCall evenBackground, Gui4jCall font,
            int alignment)
    {
        mForeground = foreground;
        mBackground = background;
        mEvenBackground = evenBackground;
        mFont = font;
        mAlignment = alignment;
    }

    public static Gui4jTextAttribute getInstance(Gui4jComponentFactory gui4jComponentFactory,
            Gui4jComponent gui4jComponent, LElement e)
    {
        return getInstance(gui4jComponentFactory, gui4jComponent, e, null);
    }

    public static Gui4jTextAttribute getInstance(Gui4jComponentFactory gui4jComponentFactory,
            Gui4jComponent gui4jComponent, LElement e, Map paramMap)
    {

        Gui4jCall foreground = gui4jComponentFactory.getGui4jAccessInstance(Color.class, paramMap, gui4jComponent, e,
                FOREGROUND);
        Gui4jCall background = gui4jComponentFactory.getGui4jAccessInstance(Color.class, paramMap, gui4jComponent, e,
                BACKGROUND);
        Gui4jCall evenBackground = gui4jComponentFactory.getGui4jAccessInstance(Color.class, paramMap, gui4jComponent,
                e, EVEN_BACKGROUND);
        Gui4jCall font = gui4jComponentFactory.getGui4jAccessInstance(Font.class, paramMap, gui4jComponent, e, FONT);
        int hAlign = -1;
        {
            String alignmentStr = gui4jComponent.getGui4jComponentContainer().getAttrValue(e, ALIGNMENT);
            if (alignmentStr != null)
            {
                Integer val = (Integer) Gui4jAlignment.mHorizontalAlign.get(alignmentStr);
                if (val != null)
                {
                    hAlign = val.intValue();
                }
                else
                {
                    mLogger.warn("Alignment " + alignmentStr + " nicht definiert");
                }
            }
        }

        if (foreground != null || background != null || font != null || evenBackground != null || hAlign != -1)
        {
            return new Gui4jTextAttribute(foreground, background, evenBackground, font, hAlign);
        }
        else
        {
            return null;
        }
    }

    public static Color getForeground(Gui4jCallBase gui4jController, Gui4jTextAttribute gui4jTextAttribute1,
            Gui4jTextAttribute gui4jTextAttribute2, Gui4jTextAttribute gui4jTextAttribute3)
    {
        Map nullMap = null;
        Gui4jCall gui4jValue;
        if (gui4jTextAttribute1 != null && ((gui4jValue = gui4jTextAttribute1.mForeground) != null))
        {
            return (Color) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        if (gui4jTextAttribute2 != null && ((gui4jValue = gui4jTextAttribute2.mForeground) != null))
        {
            return (Color) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        if (gui4jTextAttribute3 != null && ((gui4jValue = gui4jTextAttribute3.mForeground) != null))
        {
            return (Color) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        return null;
    }

    public static Color getBackground(Gui4jCallBase gui4jController, Gui4jTextAttribute gui4jTextAttribute1,
            Gui4jTextAttribute gui4jTextAttribute2, Gui4jTextAttribute gui4jTextAttribute3)
    {
        Map nullMap = null;
        Gui4jCall gui4jValue;
        if (gui4jTextAttribute1 != null && ((gui4jValue = gui4jTextAttribute1.mBackground) != null))
        {
            return (Color) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        if (gui4jTextAttribute2 != null && ((gui4jValue = gui4jTextAttribute2.mBackground) != null))
        {
            return (Color) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        if (gui4jTextAttribute3 != null && ((gui4jValue = gui4jTextAttribute3.mBackground) != null))
        {
            return (Color) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        return null;
    }

    /**
     * Returns the non-null call with the highest priority.  
     * @param gui4jTextAttribute1
     * @param gui4jTextAttribute2
     * @param gui4jTextAttribute3
     * @param provider
     * @return The non-null call of the provided attributes with the highest priority.
     */
    public static Gui4jCall getCall(Gui4jTextAttribute gui4jTextAttribute1,
            Gui4jTextAttribute gui4jTextAttribute2, Gui4jTextAttribute gui4jTextAttribute3, Gui4jCallProvider provider)
    {
        Gui4jCall gui4jValue;
        if (gui4jTextAttribute1 != null && ((gui4jValue = provider.retrieveGui4jCall(gui4jTextAttribute1)) != null))
        {
            return gui4jValue;
        }
        if (gui4jTextAttribute2 != null && ((gui4jValue = provider.retrieveGui4jCall(gui4jTextAttribute2)) != null))
        {
            return gui4jValue;
        }
        if (gui4jTextAttribute3 != null && ((gui4jValue = provider.retrieveGui4jCall(gui4jTextAttribute3)) != null))
        {
            return gui4jValue;
        }
        return null;
    }
    
    
    public static Color getColor(Gui4jCallBase callBase, Gui4jCall call)
    {
        Map nullMap = null;
        return (Color) call.getValue(callBase, nullMap, null);
    }

    public static Color getEvenBackground(Gui4jCallBase gui4jController, Gui4jTextAttribute gui4jTextAttribute1,
            Gui4jTextAttribute gui4jTextAttribute2, Gui4jTextAttribute gui4jTextAttribute3)
    {
        Map nullMap = null;
        Gui4jCall gui4jValue;
        if (gui4jTextAttribute1 != null && ((gui4jValue = gui4jTextAttribute1.mEvenBackground) != null))
        {
            return (Color) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        if (gui4jTextAttribute2 != null && ((gui4jValue = gui4jTextAttribute2.mEvenBackground) != null))
        {
            return (Color) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        if (gui4jTextAttribute3 != null && ((gui4jValue = gui4jTextAttribute3.mEvenBackground) != null))
        {
            return (Color) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        return null;
    }

    public static Font getFont(Gui4jCallBase gui4jController, Gui4jTextAttribute gui4jTextAttribute1,
            Gui4jTextAttribute gui4jTextAttribute2, Gui4jTextAttribute gui4jTextAttribute3)
    {
        Map nullMap = null;
        Gui4jCall gui4jValue;
        if (gui4jTextAttribute1 != null && ((gui4jValue = gui4jTextAttribute1.mFont) != null))
        {
            return (Font) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        if (gui4jTextAttribute2 != null && ((gui4jValue = gui4jTextAttribute2.mFont) != null))
        {
            return (Font) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        if (gui4jTextAttribute3 != null && ((gui4jValue = gui4jTextAttribute3.mFont) != null))
        {
            return (Font) gui4jValue.getValue(gui4jController, nullMap, null);
        }
        return null;
    }

    public static int getAlignment(Gui4jTextAttribute gui4jTextAttribute1, Gui4jTextAttribute gui4jTextAttribute2,
            Gui4jTextAttribute gui4jTextAttribute3)
    {
        if (gui4jTextAttribute1 != null && gui4jTextAttribute1.mAlignment != -1)
        {
            return gui4jTextAttribute1.mAlignment;
        }
        if (gui4jTextAttribute2 != null && gui4jTextAttribute2.mAlignment != -1)
        {
            return gui4jTextAttribute2.mAlignment;
        }
        if (gui4jTextAttribute3 != null && gui4jTextAttribute3.mAlignment != -1)
        {
            return gui4jTextAttribute3.mAlignment;
        }
        return SwingConstants.LEFT;
    }

    /**
     * Returns the background.
     * 
     * @return Gui4jCall
     */
    public Gui4jCall getBackground()
    {
        return mBackground;
    }

    /**
     * Returns the evenBackground.
     * 
     * @return Gui4jCall
     */
    public Gui4jCall getEvenBackground()
    {
        return mEvenBackground;
    }

    /**
     * Returns the font.
     * 
     * @return Gui4jCall
     */
    public Gui4jCall getFont()
    {
        return mFont;
    }

    /**
     * Returns the foreground.
     * 
     * @return Gui4jCall
     */
    public Gui4jCall getForeground()
    {
        return mForeground;
    }

    /**
     * Returns the alignment.
     * 
     * @return int
     */
    public int getAlignment()
    {
        return mAlignment;
    }

    // *************
    
    public static interface Gui4jCallProvider {
        Gui4jCall retrieveGui4jCall(Gui4jTextAttribute textAttribute);
    }
    
}