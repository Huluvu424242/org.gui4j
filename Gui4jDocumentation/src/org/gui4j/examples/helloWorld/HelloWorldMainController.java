package org.gui4j.examples.helloWorld;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import org.gui4j.Gui4j;
import org.gui4j.Gui4jView;
import org.gui4j.Gui4jWindow;

public class HelloWorldMainController extends BaseController
{
    private final static String RESOUCE_NAME = "main.xml";

    private Gui4jWindow gui4jWindow;
    private Dimension dimension;

    public HelloWorldMainController(Gui4j gui4j, Dimension dimension)
    {
        super(gui4j);
        this.dimension = dimension;
    }

    public boolean onWindowClosing()
    {
        System.exit(0);
        return true;
    }

    public void display()
    {
        gui4jWindow = createGui4jWindow();

        if (dimension != null)
            gui4jWindow.maximize(dimension.width, dimension.height);
        gui4jWindow.center(true);
        gui4jWindow.prepare();
        gui4jWindow.show();
    }

    protected Gui4jWindow createGui4jWindow()
    {
        Gui4jView gui4jView = getGui4j().createView(RESOUCE_NAME, this, getTitle(), false);
        Image appImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("appIcon.gif"));
        if (appImage != null)
            gui4jView.setIconImage(appImage);
        return gui4jView;
    }

    public String getHelloWorldText()
    {
        return "Hello World!";
    }

    private String getTitle()
    {
        return "Hello World Example";
    }

    public void actionExit()
    {
        close();
        System.exit(0);
    }

    public void close()
    {
        if (gui4jWindow != null)
        {
            gui4jWindow.close();
            gui4jWindow = null;
        }
    }

}