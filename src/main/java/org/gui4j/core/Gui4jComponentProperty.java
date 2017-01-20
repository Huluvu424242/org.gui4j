package org.gui4j.core;

import org.gui4j.Gui4jCallBase;


/**
 * @author Joachim Schmid
 */
public interface Gui4jComponentProperty
{
	void apply(
		Gui4jComponentInstance gui4jComponentInstance,
		Object sourceClass,
		Gui4jCallBase gui4jController,
		boolean handleThreads);

	void apply(
		Gui4jComponentInstance gui4jComponentInstance,
		Gui4jCallBase gui4jController,
		boolean handleThreads);
	Gui4jCall getGui4jAccess();

	boolean applyInitially();
    
    boolean usesSwingComponent();
}
