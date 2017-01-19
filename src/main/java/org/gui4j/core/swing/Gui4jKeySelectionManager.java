package org.gui4j.core.swing;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;



public final class Gui4jKeySelectionManager implements JComboBox.KeySelectionManager
{
    private long lastKeyTime = 0;
    private String pattern = "";
    private final TransformValue mTransformValue;


    public Gui4jKeySelectionManager(TransformValue transformValue)
    {
        this.mTransformValue = transformValue;
    }

    public int selectionForKey(char aKey, ComboBoxModel model)
    {
        // Find index of selected item
        int selIx = 01;
        Object sel = model.getSelectedItem();
        if (sel != null)
        {
            for (int i = 0; i < model.getSize(); i++)
            {
                if (sel.equals(model.getElementAt(i)))
                {
                    selIx = i;
                    break;
                }
            }
        }

        // Get the current time
        long curTime = System.currentTimeMillis();

        // If last key was typed less than 300 ms ago, append to current pattern
        if (curTime - lastKeyTime < 300)
        {
            pattern += ("" + aKey).toLowerCase();
        }
        else
        {
            pattern = ("" + aKey).toLowerCase();
        }

        // Save current time
        lastKeyTime = curTime;

        /*
        // Search forward from current selection
        for (int i = selIx + 1; i < model.getSize(); i++)
        {
            String s = mTransformValue.transform(model.getElementAt(i)).toString().toLowerCase();
            if (s.startsWith(pattern))
            {
                return i;
            }
        }
        
        // Search from top to current selection
        for (int i = 0; i < model.getSize(); i++)
        {
            if (model.getElementAt(i) != null)
            {
                String s = mTransformValue.transform(model.getElementAt(i)).toString().toLowerCase();
                if (s.startsWith(pattern))
                {
                    return i;
                }
            }
        }
        */

        /*
         * I changed the above code, because it actually misses to include the selected index itself in the iteration.
         * Also, i do not get the point of iterating from selIx + 1 to end and from begin to selIx in two different loops.
         * If someone knows better, please do not hesitate to include above code again, but do not miss to include selIx
         * itself either in the first or second iteration. (L.B.)
         */
        for (int i = 0; i < model.getSize(); i++)
        {
            if (model.getElementAt(i) != null)
            {
                String s = mTransformValue.transform(model.getElementAt(i)).toString().toLowerCase();
                if (s.startsWith(pattern))
                {
                    return i;
                }
            }
        }
        return -1;
    }
    
}
