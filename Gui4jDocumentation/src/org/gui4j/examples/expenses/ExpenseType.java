package org.gui4j.examples.expenses;

import java.util.HashSet;
import java.util.Set;

public class ExpenseType implements Comparable
{
    private static final Set elements = new HashSet();
    
    public static final ExpenseType TRAVEL = new ExpenseType("Travel Expenses");
    public static final ExpenseType ACCOMODATION = new ExpenseType("Accomodation");
    public static final ExpenseType FOOD = new ExpenseType("Food");
    
    private String text;
    
    public static Set getAllExpenseTypes()
    {
        return elements;
    }

    /**
     * Private constructor to prevent instantiation from outside.
     * @param text
     */
    private ExpenseType(String text)
    {
        this.text = text;
        elements.add(this);
    }
    
    public String getText()
    {
        return text;
    }

    public int compareTo(Object o) {
        return compareTo((ExpenseType)o);
    }
    
    public int compareTo(ExpenseType type) {
        return text.compareTo(type.text);
    }
}
