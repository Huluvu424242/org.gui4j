package org.gui4j.examples.tree;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.gui4j.Gui4j;
import org.gui4j.Gui4jFactory;

public class TreeMain
{
    public static void main(String[] args)
    {
        initLogging();
        
        final Log log = LogFactory.getLog(TreeMain.class);
        log.info("Creating Gui4j instance.");
        URL url = TreeMain.class.getResource("gui4jComponents.properties");
        Gui4j gui4j = createGui4j(url);
        log.info("Building controller.");
        TreeMainController controller = new TreeMainController(gui4j);
        log.info("Displaying GUI.");
        controller.display();
        log.info("Finished.");
    }
    
    private static Gui4j createGui4j(URL url)
    {
        int numberOfWorkerThreads = -1;
        boolean validateXML = true;
        boolean logInvoke = false;
        Gui4j myGui4j = Gui4jFactory.createGui4j(validateXML, logInvoke, numberOfWorkerThreads, url);
        return myGui4j;
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