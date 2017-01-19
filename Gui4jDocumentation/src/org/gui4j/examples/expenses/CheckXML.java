package org.gui4j.examples.expenses;

import java.net.URL;

import org.gui4j.Gui4j;
import org.gui4j.Gui4jFactory;
import org.gui4j.Gui4jValidator;
import org.gui4j.exception.Gui4jUncheckedException;

/**
 * Run this class to check if the gui4j xml descriptions are valid.
 */
public class CheckXML {

    private Gui4jValidator gui4jValidator;
    private boolean success = true;

    public static void main(String[] args) {
        new CheckXML().checkAll();
    }

    public CheckXML() {
        super();
        initGui4jValidator();
    }

    protected void initGui4jValidator() {
        URL componentProperties = getClass().getResource("gui4jComponents.properties");
        Gui4j gui4j = Gui4jFactory.createGui4j(true, false, -1, componentProperties);
        gui4jValidator = gui4j.createValidator();
    }

    private void checkAll() {
        System.out.println("Checking gui4j XML files...");

        // check all resources used in the application
        checkResource(ExpenseListController.class, ExpenseListController.RESOURCE_NAME);
        checkResource(ExpenseEditController.class, ExpenseEditController.RESOURCE_NAME);

        if (success) {
            System.out.println("All files are OK.");
        } else {
            System.out.println("Some files have ERRORS !");
        }
    }

    private void checkResource(Class controller, String resource) {
        System.out.println("   Checking " + resource + " with " + controller);        
        boolean check = true;
        String message = "Validator returned false";
        try {
            check = gui4jValidator.validateResourceFile(controller, resource);
        } catch (Gui4jUncheckedException e) {
            check = false;
            message = e.getMessage();
        }
        if (!check) {
            System.out.println(message);
            success = false;
        }
    }

}
