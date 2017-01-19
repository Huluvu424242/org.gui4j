package org.gui4j.component;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.SwingConstants;

import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.listener.Gui4jActionListener;
import org.gui4j.core.listener.Gui4jActionListenerButton;

public abstract class Gui4jAbstractButton extends Gui4jJComponent
{
    private Gui4jCall mActionCommand;
    private int mVTextPosition = SwingConstants.CENTER;
    private int mHTextPosition = SwingConstants.TRAILING; 

    /**
     * @param gui4jComponentContainer
     * @param buttonClass
     * @param id
     */
    public Gui4jAbstractButton(Gui4jComponentContainer gui4jComponentContainer, Class buttonClass, String id)
    {
        super(gui4jComponentContainer, buttonClass, id);
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        AbstractButton button = (AbstractButton) gui4jComponentInstance.getComponent();
        if (mActionCommand != null)
        {
            Gui4jActionListener gui4jActionListener = createActionListener(gui4jComponentInstance);
            gui4jComponentInstance.getGui4jSwingContainer().addDispose(gui4jActionListener);
            gui4jActionListener.setActionPerformed(mActionCommand);
            button.addActionListener(gui4jActionListener);
        }
        if (handleReadOnly() && gui4jComponentInstance.getGui4jSwingContainer().inReadOnlyMode())
        {
            button.setEnabled(false);
        }
        
        button.setHorizontalTextPosition(mHTextPosition);
        button.setVerticalTextPosition(mVTextPosition);
    }

    protected Gui4jActionListener createActionListener(Gui4jComponentInstance gui4jComponentInstance)    
    {
        return new Gui4jActionListenerButton(gui4jComponentInstance);
    }

	/**
	 * @param button
	 * @param text
	 */
	public void setText(AbstractButton button, String text)
	{
		button.setText(text);
	}

	/**
	 * @param button
	 * @param icon
	 */
	public void setIcon(AbstractButton button, Icon icon)
	{
		button.setIcon(icon);
	}

	/**
	 * @param button
	 * @param gap
	 */
	public void setIconTextGap(AbstractButton button, int gap)
	{
		button.setIconTextGap(gap);
	}

    /**
     * @param gui4jComponentInstance
     * @param groupName
     */
    public void setGroup(Gui4jComponentInstance gui4jComponentInstance, String groupName)
    {
        Gui4jSwingContainer gui4jSwingContainer = gui4jComponentInstance.getGui4jSwingContainer();
        ButtonGroup buttonGroup = (ButtonGroup) gui4jSwingContainer.getStorage(Gui4jAbstractButton.class, groupName);
        if (buttonGroup == null)
        {
            buttonGroup = new ButtonGroup();
            gui4jSwingContainer.putStorage(Gui4jAbstractButton.class, groupName, buttonGroup);
        }
        buttonGroup.add((AbstractButton) gui4jComponentInstance.getComponent());
    }

    /**
     * @param button
     * @param mnemonic
     */
    public void setMnemonic(AbstractButton button, char mnemonic)
    {
        button.setMnemonic(mnemonic);
    }

    /**
     * @param button
     * @param isSelected
     */
    public void setSelected(AbstractButton button, boolean isSelected)
    {
        button.setSelected(isSelected);
    }

    /** 
     * @param horizontalTextPosition
     */
    public void setHTextPosition(int horizontalTextPosition) {
        mHTextPosition = horizontalTextPosition;
    }
    
    /** 
     * @param verticalTextPosition
     */
    public void setVTextPosition(int verticalTextPosition) {
        mVTextPosition = verticalTextPosition;
    }
    
    /**
     * @param actionCommand
     */
    public void setActionCommand(Gui4jCall actionCommand)
    {
        mActionCommand = actionCommand;
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jAbstractComponent#setEnabled(org.gui4j.core.Gui4jComponentInstance, boolean)
     */
    public void setEnabled(Gui4jComponentInstance gui4jComponentInstance, boolean enabled)
    {
        if (handleReadOnly() && gui4jComponentInstance.getGui4jSwingContainer().inReadOnlyMode())
        {
            super.setEnabled(gui4jComponentInstance, false);
        }
        else
        {
            super.setEnabled(gui4jComponentInstance, enabled);
        }
    }

}
