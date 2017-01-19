package org.gui4j.component;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.PlainDocument;

import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.listener.Gui4jListenerTextArea;
import org.gui4j.core.swing.MaxLengthDocumentFilter;


public final class Gui4jTextArea extends Gui4jJComponent
{
    public static final String PARAM_VALUE = "value";

    private Gui4jCall mFocusLost;
    private Gui4jCall mSetText;
    private int mVisibleRows;
    private boolean mLineWrap;
    private boolean mWrapStyleWord;
    private int mMaxLength = -1;

    /**
     * Constructor for Gui4jTextArea.
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jTextArea(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JTextArea.class, id);
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        JTextArea textArea = (JTextArea) gui4jComponentInstance.getComponent();
        if (mFocusLost != null)
        {
            Gui4jListenerTextArea gui4jListenerTextArea = new Gui4jListenerTextArea(gui4jComponentInstance);
            gui4jListenerTextArea.setActionPerformed(mFocusLost);
            textArea.addFocusListener(gui4jListenerTextArea);
        }
        if (mSetText != null)
        {
            Gui4jListenerTextArea gui4jListenerTextArea = new Gui4jListenerTextArea(gui4jComponentInstance);
            gui4jListenerTextArea.setActionPerformed(mSetText);
            textArea.addFocusListener(gui4jListenerTextArea);
        }
        {
            textArea.setRows(mVisibleRows);
        }
        if (mMaxLength >= 0)
        {
            PlainDocument doc = (PlainDocument) textArea.getDocument();
            doc.setDocumentFilter(new MaxLengthDocumentFilter(mMaxLength));
        }
        
        textArea.setLineWrap(mLineWrap);
        textArea.setWrapStyleWord(mWrapStyleWord);
    }

    public void setText(JTextArea textArea, String text)
    {
        textArea.setText(text);
    }

    public void setWidth(JTextArea textArea, int length)
    {
        textArea.setColumns(length);
    }

    public void setMaxLength(int maxLength)
    {
        mMaxLength = maxLength;
    }

    public void setEditable(JTextArea textArea, boolean editable)
    {
        if (editable)
        {
            textArea.setBackground(UIManager.getColor("TextField.background"));
        }
        else
        {
            textArea.setBackground(UIManager.getColor("TextField.inactiveBackground"));
        }

        textArea.setEditable(editable);
    }

    /**
     * Sets the focusLost.
     * @param focusLost The focusLost to set
     */
    public void setFocusLost(Gui4jCall focusLost)
    {
        mFocusLost = focusLost;
    }

    /**
     * Sets the setText.
     * @param setText The setText to set
     */
    public void setSetText(Gui4jCall setText)
    {
        mSetText = setText;
    }

    /**
     * Sets the visibleRows.
     * @param visibleRows The visibleRows to set
     */
    public void setVisibleRows(int visibleRows)
    {
        mVisibleRows = visibleRows;
    }

    public void setLineWrap(boolean lineWrap)
    {
        mLineWrap = lineWrap;
    }
    
    public void setWrapStyleWord(boolean wrapStyleWord)
    {
        mWrapStyleWord = wrapStyleWord;
    }
}