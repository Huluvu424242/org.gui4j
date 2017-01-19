package org.gui4j.component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;

public final class Gui4jLabel extends Gui4jJComponent
{
    private String mLabelForId;
    private int mHAlign = SwingConstants.LEADING;
    private int mVAlign = SwingConstants.CENTER;
    private int mVTextPosition = SwingConstants.CENTER;
    private int mHTextPosition = SwingConstants.TRAILING;
    private Gui4jCall mSuffixCall;

    /**
     * Constructor for Gui4jLabel.
     * 
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jLabel(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JLabel.class, id);
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        JLabel label = (JLabel) gui4jComponentInstance.getComponent();
        if (mLabelForId != null)
        {
            Gui4jQualifiedComponent gui4jComponentInPath = gui4jComponentInstance.getGui4jComponent()
                    .getGui4jComponentContainer().getGui4jQualifiedComponent(mLabelForId);
            Gui4jComponentInstance subInstance = gui4jComponentInstance.getGui4jComponentInstance(gui4jComponentInPath);
            label.setLabelFor(subInstance.getComponent());
        }
        label.setHorizontalAlignment(mHAlign);
        label.setVerticalAlignment(mVAlign);

        label.setHorizontalTextPosition(mHTextPosition);
        label.setVerticalTextPosition(mVTextPosition);
    }

    public void setMnemonic(JLabel label, char mnemonic)
    {
        label.setDisplayedMnemonic(mnemonic);
    }

    public void setIcon(JLabel label, Icon icon)
    {
        label.setIcon(icon);
    }

    public void setHAlignment(int halignment)
    {
        mHAlign = halignment;
    }

    public void setVAlignment(int valignment)
    {
        mVAlign = valignment;
    }

    public void setHTextPosition(int hTextPosition)
    {
        mHTextPosition = hTextPosition;
    }

    public void setVTextPosition(int vTextPosition)
    {
        mVTextPosition = vTextPosition;
    }

    public void setText(Gui4jComponentInstance gui4jComponentInstance, String text)
    {
        if (mSuffixCall != null)
        {
            String suffix = (String) mSuffixCall.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(), "");
            text = text + suffix;
        }

        JLabel label = (JLabel) gui4jComponentInstance.getSwingComponent();
        label.setText(text);
    }

    public void setSuffixCall(Gui4jCall suffixCall)
    {
        this.mSuffixCall = suffixCall;
    }

    /**
     * Sets the labelForId.
     * 
     * @param labelForId
     *            The labelForId to set
     */
    public void setLabelForId(String labelForId)
    {
        mLabelForId = labelForId;
    }

}
