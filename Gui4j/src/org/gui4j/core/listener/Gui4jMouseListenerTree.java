package org.gui4j.core.listener;

import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMouseListener;


public class Gui4jMouseListenerTree extends Gui4jMouseListener
{

    public Gui4jMouseListenerTree(Gui4jComponentInstance gui4jComponentInstance)
    {
        super(gui4jComponentInstance);
    }

    public void mousePressed(MouseEvent e)
    {
        selectPathOnRightClick(e);
        super.mousePressed(e);
    }

    private void selectPathOnRightClick(MouseEvent e)
    {
        if (!SwingUtilities.isRightMouseButton(e))
        {
            return;
        }
        
        JTree tree = getJTree();        
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            tree.setSelectionPath(path);
        }
        
    }

    private JTree getJTree()
    {
        return (JTree) mGui4jComponentInstance.getSwingComponent();
    }

}
