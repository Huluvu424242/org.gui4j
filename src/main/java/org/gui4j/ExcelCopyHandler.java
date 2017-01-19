package org.gui4j;

import javax.swing.KeyStroke;

public interface ExcelCopyHandler
{
    /**
     * init is called before adding anything
     */
    void init();
    
    /**
     * Called at the end. Method has to copy the stored content to the clipboard.
     */
    void copyToClipboard();
    
    /**
     * Next row
     */
    void nextLine();

    /**
     * Adds the next column claue
     * @param value
     */
    void addColumn(String value);

    /**
     * @return KeyStroke for copying the table content excluding units
     */
    KeyStroke getKeyStrokeCopyRemoveUnits();

    /**
     * @return KeyStroke for copying the table content
     */
    KeyStroke getKeyStrokeCopy();
    
    /**
     * When copying a table with excluding units, this method has the task to
     * remove the unit
     * @param str
     * @return String
     */
    public String removeUnits(String str);

}
