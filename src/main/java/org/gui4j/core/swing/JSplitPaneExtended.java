/*
 * This class has been derived from work done by Karsten Lentzsch in his
 * examples for the JGoodies looks package. Modifications: Copyright (c) 2005
 * beck et al. projects GmbH
 */

/*
 * Copyright (c) 2001-2004 JGoodies Karsten Lentzsch. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * o Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * o Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * o Neither the name of JGoodies Karsten Lentzsch nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.gui4j.core.swing;

import java.awt.Component;

import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * A <code>JSplitPane</code> subclass that can try to remove the divider
 * border. Useful if the splitted components render their own borders. Note that
 * this feature is not supported by all look&amp;feels. Some look&amp;feel
 * implementation will always show a divider border, and conversely, others will
 * never show a divider border.
 * 
 * @author Karsten Lentzsch
 * @author Kay Krüger-Barvels
 * 
 * @see javax.swing.plaf.basic.BasicSplitPaneUI
 */
public class JSplitPaneExtended extends JSplitPane
{

    /**
     * Holds an empty border that is reused for the split pane itself and the
     * divider.
     */
    private static final Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);

    /**
     * Determines whether the divider border shall be removed when the UI is
     * updated.
     * 
     * @see #setDividerBorderVisible(boolean)
     */
    private Boolean dividerBorderVisible;

    public JSplitPaneExtended(int newOrientation, Component newLeftComponent, Component newRightComponent)
    {
        super(newOrientation, newLeftComponent, newRightComponent);
    }

    /**
     * Checks and answers whether the divider border shall be visible or
     * invisible. Note that this feature is not supported by all look&amp;feels.
     * Some look&amp;feel implementation will always show a divider border, and
     * conversely, others will never show a divider border.
     * 
     * @return the desired (but potentially inaccurate) divider border visiblity
     */
    private Boolean isDividerBorderVisible()
    {        
        return dividerBorderVisible;
    }

    /**
     * Makes the divider border visible or invisible. Note that this feature is
     * not supported by all look&amp;feels. Some look&amp;feel implementation
     * will always show a divider border, and conversely, others will never show
     * a divider border.
     * 
     * @param newVisibility
     *            true for visible, false for invisible
     */
    public void setDividerBorderVisible(boolean newVisibility)
    {   
        if (isDividerBorderVisible() == null || isDividerBorderVisible().booleanValue() != newVisibility) {
            dividerBorderVisible = new Boolean(newVisibility);
            updateUI();
        }
    }

    /**
     * Updates the UI and sets an empty divider border. The divider border may
     * be restored by a L&F at UI installation time. And so, we try to reset it
     * each time the UI is changed.
     */
    public void updateUI()
    {
        super.updateUI();
        if (isDividerBorderVisible() != null && !(isDividerBorderVisible().booleanValue()))
            setEmptyDividerBorder();
    }

    /**
     * Sets an empty divider border if and only if the UI is an instance of
     * <code>BasicSplitPaneUI</code>.
     */
    private void setEmptyDividerBorder()
    {
        SplitPaneUI splitPaneUI = getUI();
        if (splitPaneUI instanceof BasicSplitPaneUI)
        {
            BasicSplitPaneUI basicUI = (BasicSplitPaneUI) splitPaneUI;
            basicUI.getDivider().setBorder(EMPTY_BORDER);
        }
    }

}