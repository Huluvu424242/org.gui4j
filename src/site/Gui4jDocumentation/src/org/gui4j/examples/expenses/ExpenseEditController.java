package org.gui4j.examples.expenses;

import java.util.List;

import org.gui4j.Gui4j;
import org.gui4j.event.Gui4jEvent;
import org.gui4j.event.SimpleEvent;
import org.gui4j.examples.util.DialogController;

public class ExpenseEditController extends DialogController {

    public static final String RESOURCE_NAME = "expenseEdit.xml";

    private final SimpleEvent validationChanged = new SimpleEvent();
    private String validationMessage;

    public final ExpenseListController listController;
    public final ExpenseRecord editRecord;
    public final ExpenseRecord originalRecord;

    public ExpenseEditController(Gui4j gui4j, ExpenseListController listController, ExpenseRecord record) {
        super(gui4j, listController);

        this.listController = listController;
        this.originalRecord = record;
        editRecord = new ExpenseRecord();
        editRecord.copyValuesFrom(originalRecord);
        
    }

    public Gui4jEvent eValidationChanged() {
        return validationChanged;
    }

    protected String getResourceName() {
        return RESOURCE_NAME;
    }

    protected String getTitle() {
        return "Edit Expense Record";
    }

    public List getExpenseTypes() {
        return listController.getExpenseTypes();
    }

    public ExpenseRecord getRecord() {
        return editRecord;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public boolean showValidation() {
        return validationMessage != null;
    }
    
    public void actionSave() {

        if (!validate()) {
            validationChanged.fireEvent();
        } else {
            save();
            close();
        }
    }

    public void actionCancel() {
        close();
    }

    public boolean onWindowClosing() {
        actionCancel();
        return true;
    }

    public ResourceProvider res() {
        return ResourceProvider.getInstance();
    }

    private void save() {
        // The save/cancel problem can be solved in a variety of ways. In
        // this simple example we rely on a copyValuesFrom() method provided
        // by the domain object. When using Hibernate it is possible to use
        // its metadata to implement a generic copyValues()
        // method for all (persistent) domain objects.
        
        originalRecord.copyValuesFrom(editRecord);
        
    }
    
    private boolean validate() {
        validationMessage = null;

        // This is just a very simple example for validation.
        
        if (getRecord().getRecordId() == null || "".equals(getRecord().getRecordId().trim())) {
            validationMessage = "Record Id must not be empty.";
            return false;
        }

        return true;
    }

}
