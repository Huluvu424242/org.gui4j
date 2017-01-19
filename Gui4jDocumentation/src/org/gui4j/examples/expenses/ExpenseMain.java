package org.gui4j.examples.expenses;

import java.awt.Toolkit;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.gui4j.Gui4j;
import org.gui4j.Gui4jFactory;

public class ExpenseMain {
    
    private final Gui4j gui4j;

    public ExpenseMain() {
        URL url = getClass().getResource("gui4jComponents.properties");
        gui4j = createGui4j(url);
    }

    public static void main(String[] args) {
        initLogging();
        
        ExpenseMain main = new ExpenseMain();
        main.run();
    }

    private void run() {
        prepareLookAndFeel();
        showExpenseList();
    }

    
    private void prepareLookAndFeel() {
        
        // here would be the place to initialize your desired Swing L&F...
        
        // activate dynamic resizing
        Toolkit.getDefaultToolkit().setDynamicLayout(true);

    }

    private void showExpenseList() {
        ExpenseListController controller = new ExpenseListController(gui4j);
        controller.display();
    }

    private Gui4j createGui4j(URL url) {
        int numberOfWorkerThreads = -1;  // -1 = no limit

        boolean validateXML = true;
        boolean logInvoke = false;

        return Gui4jFactory.createGui4j(validateXML, logInvoke, numberOfWorkerThreads, url);
    }

    private static void initLogging() {

        // choose your desired log level
        Level level = Level.INFO;
        //Level level = Level.DEBUG;
        
        // initialize concrete logging implementation (log4j)        
        BasicConfigurator.configure();        
        LogManager.getRootLogger().setLevel(level);
    }

}