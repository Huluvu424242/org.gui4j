package org.gui4j.core.listener;

import java.awt.event.FocusListener;

import javax.swing.JPasswordField;

import org.gui4j.core.Gui4jComponentInstance;


public class Gui4jListenerPasswordField extends Gui4jListenerEdit implements FocusListener
{
    /**
     * Constructor for Gui4jListenerPasswordField.
     * @param gui4jComponentInstance
     */
    public Gui4jListenerPasswordField(Gui4jComponentInstance gui4jComponentInstance)
    {
        super(gui4jComponentInstance);
    }

    protected String getFieldValue()
    {
        JPasswordField passwordField = (JPasswordField) mGui4jComponentInstance.getComponent();
        return new String(passwordField.getPassword());
    }

}
