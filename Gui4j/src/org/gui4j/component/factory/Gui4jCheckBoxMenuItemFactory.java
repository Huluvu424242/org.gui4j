package org.gui4j.component.factory;

import org.dom4j.LElement;

import org.gui4j.component.Gui4jCheckBoxMenuItem;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.core.Gui4jComponentContainer;


public final class Gui4jCheckBoxMenuItemFactory extends Gui4jMenuItemFactory
{
	public static final String CHECKBOX_MENUITEM_NAME = "checkBoxMenuItem";
	
	/* (non-Javadoc)
	 * @see org.gui4j.component.factory.Gui4jJComponentFactory#defineGui4jJComponentBy(org.gui4j.core.Gui4jComponentContainer, java.lang.String, org.dom4j.LElement)
	 */
	protected Gui4jJComponent defineGui4jJComponentBy(
		Gui4jComponentContainer gui4jComponentContainer,
		String id,
		LElement e)
	{
        return new Gui4jCheckBoxMenuItem(gui4jComponentContainer, id);
	}

	/**
	 * @see org.gui4j.core.Gui4jComponentFactory#getName()
	 */
	public String getName()
	{
		return CHECKBOX_MENUITEM_NAME;
	}

}
