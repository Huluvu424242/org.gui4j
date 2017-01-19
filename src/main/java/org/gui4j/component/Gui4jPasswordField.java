package org.gui4j.component;

import javax.swing.JPasswordField;

import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.listener.Gui4jActionListener;
import org.gui4j.core.listener.Gui4jListenerPasswordField;


public class Gui4jPasswordField extends Gui4jEdit
{

    public Gui4jPasswordField(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JPasswordField.class, id);
    }
    
    protected Gui4jActionListener createActionListener(Gui4jComponentInstance gui4jComponentInstance) {       
        return new Gui4jListenerPasswordField(gui4jComponentInstance);
    }

}
