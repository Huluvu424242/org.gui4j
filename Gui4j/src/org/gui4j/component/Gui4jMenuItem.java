package org.gui4j.component;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.core.Gui4jComponentContainer;


public class Gui4jMenuItem extends Gui4jAbstractButton
{
    private static final Log log = LogFactory.getLog(Gui4jMenuItem.class);

    /**
     * Constructor for Gui4jMenuItem.
     * @param gui4jComponentContainer
     * @param buttonClass
     * @param id
     */
    public Gui4jMenuItem(Gui4jComponentContainer gui4jComponentContainer, Class buttonClass, String id)
    {
        super(gui4jComponentContainer, buttonClass, id);
    }

    public void setAccelerator(JMenuItem menuItem, String key)
    {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        if (key == null)
        {
            log.warn(
                "Keystroke for key "
                    + key
                    + " not defined in component with id "
                    + getId()
                    + " in "
                    + getConfigurationName());
        }
        else
        {
            menuItem.setAccelerator(keyStroke);
        }
    }

}
