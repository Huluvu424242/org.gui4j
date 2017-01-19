package org.gui4j.examples.util;

import org.gui4j.Gui4j;

public class DialogController extends WindowController {

    private final WindowController parent;

    public DialogController(Gui4j gui4j, WindowController parent) {
        super(gui4j);
        this.parent = parent;
    }

    protected void createGui4jWindow() {
        gui4jWindow = getGui4j().createDialog(parent.getGui4jWindow(), getResourceName(), this, getTitle(), false);
    }

}
