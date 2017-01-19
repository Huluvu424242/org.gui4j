package org.gui4j.component;

import java.awt.Component;
import java.util.Locale;

import javax.swing.text.JTextComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.swing.calendar.CalendarButton;

public final class Gui4jCalendarButton extends Gui4jAbstractButton
{
    private static final Log log = LogFactory.getLog(Gui4jCalendarButton.class);
    private final Gui4jQualifiedComponent mEditComponent;

    /**
     * Constructor for Gui4jButton.
     * @param gui4jComponentContainer
     * @param id
     * @param editComponent
     */
    public Gui4jCalendarButton(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        Gui4jQualifiedComponent editComponent)
    {
        super(gui4jComponentContainer, CalendarButton.class, id);
        mEditComponent = editComponent;
    }

    /* (non-Javadoc)
     * @see de.bea.gui4j.Gui4jAbstractComponent#setProperties(de.bea.gui4j.Gui4jComponentInstance)
     */
    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        Gui4jComponentInstance editInstance = gui4jComponentInstance.getGui4jComponentInstance(mEditComponent);
        CalendarButton calendarButton = (CalendarButton) gui4jComponentInstance.getSwingComponent();
        calendarButton.setLocale(Locale.GERMAN); // TODO: Locale dynamisch
        Component component = editInstance.getComponent();
        if (component instanceof JTextComponent)
        {
            calendarButton.setTextComponent((JTextComponent) component);
        }
        else
        {
            log.error(
                "CalendarButton element with id "
                    + getId()
                    + " in "
                    + getConfigurationName()
                    + " must have an instance of JTextComponent");
        }
    }

}
