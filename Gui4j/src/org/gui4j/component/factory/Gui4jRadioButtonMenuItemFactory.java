package org.gui4j.component.factory;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jRadioButtonMenuItem;
import org.gui4j.core.Gui4jComponentContainer;


public final class Gui4jRadioButtonMenuItemFactory extends Gui4jMenuItemFactory
{
	public static final String RADIOBUTTON_MENUITEM_NAME = "radioButtonMenuItem";
	
	/* (non-Javadoc)
	 * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
	 */
	protected Gui4jJComponent defineGui4jJComponentBy(
		Gui4jComponentContainer gui4jComponentContainer,
		String id,
		LElement e)
	{
        return new Gui4jRadioButtonMenuItem(gui4jComponentContainer, id);
	}

	/**
	 * @see org.gui4j.core.Gui4jComponentFactory#getName()
	 */
	public String getName()
	{
		return RADIOBUTTON_MENUITEM_NAME;
	}


}
