package org.gui4j.examples.expenses;

import org.gui4j.util.Day;


public class ExpenseRecord {
    private String recordId;
    private Day day;
    private int amount; // in euros
    private ExpenseType expenseType;

    public ExpenseRecord() {
        super();
    }

    public ExpenseRecord(Day day, String recordId, ExpenseType expenseType, int amount) {
        this.day = day;
        this.recordId = recordId;
        this.expenseType = expenseType;
        this.amount = amount;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType belegArt) {
        this.expenseType = belegArt;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public void copyValuesFrom(ExpenseRecord editRecord) {
        this.recordId = editRecord.recordId;
        this.day = editRecord.day;
        this.expenseType = editRecord.expenseType;
        this.amount = editRecord.amount;
    }
}
