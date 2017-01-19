package org.gui4j.component;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.PlainDocument;

import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.listener.Gui4jActionListener;
import org.gui4j.core.listener.Gui4jListenerEdit;
import org.gui4j.core.swing.MaxLengthDocumentFilter;


public class Gui4jEdit extends Gui4jJComponent
{
    public static final String PARAM_VALUE = "value";

    private Gui4jCall mActionCommand;
    private Gui4jCall mSetValue;
    private Gui4jCall mValue;

    private int mHAlign = SwingConstants.LEADING;
    private int mMaxLength = -1;

    /**
     * Constructor for Gui4jEdit.
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jEdit(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        this(gui4jComponentContainer, JTextField.class, id);
    }

    public Gui4jEdit(Gui4jComponentContainer gui4jComponentContainer, Class editClass, String id)
    {
        super(gui4jComponentContainer, editClass, id);
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);
        JTextField textField = (JTextField) gui4jComponentInstance.getComponent();
        if (mSetValue != null || mActionCommand != null)
        {
            Gui4jListenerEdit gui4jListenerEdit = (Gui4jListenerEdit) createActionListener(gui4jComponentInstance);
            gui4jListenerEdit.setActionPerformed(mActionCommand);
            gui4jListenerEdit.setSetValue(mSetValue);
            textField.addFocusListener(gui4jListenerEdit);
            textField.addActionListener(gui4jListenerEdit);
        }
        if (mSetValue == null)
        {
            textField.setEditable(false);
        }
        if (handleReadOnly() && gui4jComponentInstance.getGui4jSwingContainer().inReadOnlyMode())
        {
            textField.setEditable(false);
        }
        textField.setHorizontalAlignment(mHAlign);

        if (mMaxLength >= 0)
        {
            PlainDocument doc = (PlainDocument) textField.getDocument();
            doc.setDocumentFilter(new MaxLengthDocumentFilter(mMaxLength));
        }
    }

    public void setValue(final JTextField textField, final String value)
    {
        // follow Swing's single thread rule when modifying components
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                textField.setText(value);
                textField.selectAll();
            }
        });
    }

    /**
     * Sets the preferred size of the text field.
     * @param textField
     * @param length number of 'm' characters that should fit into the field
     */
    public void setWidth(JTextField textField, int length)
    {
        Dimension d = new Dimension(textField.getPreferredSize());
        JLabel l = new JLabel("M");
        l.setFont(textField.getFont());
        d.width = l.getPreferredSize().width * length + 2;
        textField.setMinimumSize(d);
        textField.setPreferredSize(d);
        textField.setMaximumSize(d);
        // textField.setColumns(length);
    }

    public void setHAlignment(int halignment)
    {
        mHAlign = halignment;
    }

    public void setMaxLength(int maxLength)
    {
        mMaxLength = maxLength;
    }


    public void setEditable(Gui4jComponentInstance gui4jComponentInstance, boolean editable)
    {
        JTextField textField = (JTextField) gui4jComponentInstance.getSwingComponent();
        if (handleReadOnly() && gui4jComponentInstance.getGui4jSwingContainer().inReadOnlyMode())
        {
            textField.setEditable(false);
        }
        else
        {
            textField.setEditable(editable);
        }
    }

    /**
     * Sets the actionCommand.
     * @param actionCommand The actionCommand to set
     */
    public void setActionCommand(Gui4jCall actionCommand)
    {
        mActionCommand = actionCommand;
    }

    /**
     * @param setValue
     */
    public void setSetValue(Gui4jCall setValue)
    {
        mSetValue = setValue;
    }

    /**
     * Sets the value.
     * @param value
     */
    public void setValue(Gui4jCall value)
    {
        mValue = value;
    }

    protected Gui4jActionListener createActionListener(Gui4jComponentInstance gui4jComponentInstance)
    {
        return new Gui4jListenerEdit(gui4jComponentInstance);
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jComponent#handleSuccess(org.gui4j.core.Gui4jComponentInstance)
     */
    public void handleSuccess(Gui4jComponentInstance gui4jComponentInstance)
    {
        // Nach dem der Setter erfolgreich aufgerufen worden ist, setzen
        // wir den Inhalt nochmals neu, um sicherzustellen, dass die
        // Formatierung stimmt.
        if (mValue != null)
        {
            String value = (String) mValue.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(), "");
            setValue((JTextField) gui4jComponentInstance.getComponent(), value);
        }
        super.handleSuccess(gui4jComponentInstance);
    }

}
