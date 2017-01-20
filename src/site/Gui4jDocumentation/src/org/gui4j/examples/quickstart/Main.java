package org.gui4j.examples.quickstart;

import java.net.URL;

import org.gui4j.Gui4j;
import org.gui4j.Gui4jController;
import org.gui4j.Gui4jFactory;
import org.gui4j.Gui4jView;
import org.gui4j.exception.Gui4jExceptionHandler;

public class Main implements Gui4jController
{
    private Gui4j gui4j;
    private Gui4jView gui4jView;

    public static void main(String[] args)
    {
        Main main = new Main();
        main.run();
    }
    
    private void run()
    {
        // initializing
        URL url = Main.class.getResource("gui4jComponents.properties");
        int numberOfWorkerThreads = -1;
        boolean validateXML = true;
        boolean logInvoke = false;
        gui4j = Gui4jFactory.createGui4j(validateXML, logInvoke, numberOfWorkerThreads, url);
        
        // creating the view
		String resourceName = "main.xml";
		String title = "My first application";
		boolean readOnlyMode = false;
		Gui4jController controller = this;
        gui4jView = gui4j.createView(resourceName, controller, title, readOnlyMode);
        
        // displaying the view
        gui4jView.prepare();
        gui4jView.show();
    }
    
    
    public boolean onWindowClosing()
    {
        return true;
    }
    
    public void windowClosed()
    {
        System.exit(0);
    }

    public Gui4jExceptionHandler getExceptionHandler()
    {
        return null;
    }
    
    public Gui4j getGui4j()
    {
        return gui4j;
    }
}
