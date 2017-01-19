package org.gui4j.examples.expenses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gui4j.Gui4j;
import org.gui4j.event.Gui4jEvent;
import org.gui4j.event.SimpleEvent;
import org.gui4j.examples.util.WindowController;
import org.gui4j.util.Day;

public class ExpenseListController extends WindowController {

    public static final String RESOURCE_NAME = "expenseList.xml";

    private final SimpleEvent selectedRecordChanged = new SimpleEvent();
    private final SimpleEvent recordListModified = new SimpleEvent();
    private final SimpleEvent recordsChanged = new SimpleEvent();

    private final List expenseTypes = new ArrayList();
    private final List expenseRecords = new ArrayList();

    private ExpenseRecord selectedRecord;

    /**
     * @param gui4j
     */
    public ExpenseListController(Gui4j gui4j) {
        super(gui4j);

        initExpenseTypes();
        createTestData();
    }

    /**
     * Fired when the selected record was changed, i.e. a different record is
     * now selected than before.
     * @return
     */
    public Gui4jEvent eSelectedRecordChanged() {
        return selectedRecordChanged;
    }

    /**
     * Fired when the list of records was modified, i.e. records have been added
     * or removed.
     * @return
     */
    public Gui4jEvent eRecordListModified() {
        return recordListModified;
    }

    /**
     * Fired when the contents of one or more displayed records may
     * have changed.
     * @return
     */
    public Gui4jEvent eRecordsChanged() {
        return recordsChanged;
    }
    
    public List getExpenseTypes() {
        return expenseTypes;
    }

    public List getExpenseRecords() {
        return expenseRecords;
    }

    public ExpenseRecord getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(ExpenseRecord selectedRecord) {
        this.selectedRecord = selectedRecord;
        selectedRecordChanged.fireEvent();
    }

    public boolean isRecordSelected() {
        return selectedRecord != null;
    }

    public void actionCreateRecord() {
        ExpenseRecord record = new ExpenseRecord(Day.getToday(), "NewRecord", ExpenseType.TRAVEL, 0);
        expenseRecords.add(0, record); // insert at top
        recordListModified.fireEvent();
    }

    public void actionDeleteRecord(ExpenseRecord record) {
        expenseRecords.remove(record);
        setSelectedRecord(null);        
        recordListModified.fireEvent();
    }

    public void actionDeleteSelectedRecord() {
        if (selectedRecord == null) {
            return;
        }
        actionDeleteRecord(selectedRecord);
    }
    
    public void actionEditRecord(ExpenseRecord record) {
        ExpenseEditController controller = new ExpenseEditController(getGui4j(), this, record);
        controller.display(300, 200);
        recordsChanged.fireEvent();
    }

    public void actionEditSelectedRecord() {    
        if (selectedRecord == null) {
            return;
        }
        actionEditRecord(selectedRecord);
    }
    
    public void actionExit() {
        System.exit(0);
    }

    /* (non-Javadoc)
     * @see de.bea.gui4j.examples.util.WindowController#getTitle()
     */
    protected String getTitle() {
        return "Expense Report";
    }

    protected String getResourceName() {
        return RESOURCE_NAME;
    }

    public ResourceProvider res() {
        return ResourceProvider.getInstance();
    }

    private void initExpenseTypes() {
        expenseTypes.clear();
        expenseTypes.addAll(ExpenseType.getAllExpenseTypes());
        Collections.sort(expenseTypes);
    }

    private void createTestData() {
        expenseRecords.add(new ExpenseRecord(Day.getToday().nextDay(-100), "A-44-S", ExpenseType.ACCOMODATION, 120));
        expenseRecords.add(new ExpenseRecord(Day.getToday().nextDay(-100), "X-2323", ExpenseType.TRAVEL, 70));
        expenseRecords.add(new ExpenseRecord(Day.getToday().nextDay(-100), "K-22-L", ExpenseType.FOOD, 20));
        expenseRecords.add(new ExpenseRecord(Day.getToday().nextDay(-50), "B-11-D", ExpenseType.TRAVEL, 300));
        expenseRecords.add(new ExpenseRecord(Day.getToday().nextDay(-50), "B-33-D", ExpenseType.ACCOMODATION, 150));
        expenseRecords.add(new ExpenseRecord(Day.getToday().nextDay(-10), "C-44-K", ExpenseType.ACCOMODATION, 200));
        expenseRecords.add(new ExpenseRecord(Day.getToday().nextDay(-10), "A100-2", ExpenseType.TRAVEL, 150));
        expenseRecords.add(new ExpenseRecord(Day.getToday().nextDay(0), "3535-O", ExpenseType.TRAVEL, 80));
        expenseRecords.add(new ExpenseRecord(Day.getToday().nextDay(0), "4242-L", ExpenseType.FOOD, 20));
    }

}
