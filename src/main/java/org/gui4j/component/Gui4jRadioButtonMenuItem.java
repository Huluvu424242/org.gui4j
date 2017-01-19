package org.gui4j.component;

import javax.swing.JRadioButtonMenuItem;

import org.gui4j.core.Gui4jComponentContainer;


public final class Gui4jRadioButtonMenuItem extends Gui4jMenuItem
{

	/**
	 * Constructor for Gui4jRadioButtonMenuItem.
	 * @param gui4jComponentContainer
	 * @param id
	 */
	public Gui4jRadioButtonMenuItem(
		Gui4jComponentContainer gui4jComponentContainer,
		String id)
	{
		super(gui4jComponentContainer, JRadioButtonMenuItem.class, id);
	}

}
