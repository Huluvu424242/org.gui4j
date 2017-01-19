package org.gui4j.component;

import javax.swing.JCheckBoxMenuItem;

import org.gui4j.core.Gui4jComponentContainer;


public final class Gui4jCheckBoxMenuItem extends Gui4jMenuItem
{

	/**
	 * Constructor for Gui4jCheckBoxMenuItem.
	 * @param gui4jComponentContainer
	 * @param id
	 */
	public Gui4jCheckBoxMenuItem(
		Gui4jComponentContainer gui4jComponentContainer,
		String id)
	{
		super(gui4jComponentContainer, JCheckBoxMenuItem.class, id);
	}

}
