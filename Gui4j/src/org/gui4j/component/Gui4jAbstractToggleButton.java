package org.gui4j.component;

import javax.swing.JToggleButton;

import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.listener.Gui4jActionListenerButton;

public abstract class Gui4jAbstractToggleButton extends Gui4jAbstractButton
{

    public static final String PARAM_VALUE = "value";

    private Gui4jCall mOnSelect;

    /**
     * @param gui4jComponentContainer
     * @param buttonClass
     * @param id
     */
    public Gui4jAbstractToggleButton(Gui4jComponentContainer gui4jComponentContainer, Class buttonClass, String id)
    {
        super(gui4jComponentContainer, buttonClass, id);
    }

    /**
     * Sets the onSelect.
     * 
     * @param onSelect
     *            The onSelect to set
     */
    public void setOnSelect(Gui4jCall onSelect)
    {
        mOnSelect = onSelect;
    }

    /**
     * @see org.gui4j.core.Gui4jAbstractComponent#setProperties(Gui4jComponentInstance)
     */
    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        JToggleButton button = (JToggleButton) gui4jComponentInstance.getSwingComponent();

        if (mOnSelect != null)
        {
            Gui4jActionListenerButton actionListener = new Gui4jActionListenerButton(gui4jComponentInstance);
            button.addActionListener(actionListener);
            actionListener.setActionPerformed(mOnSelect);
        }
    }

}
