package org.gui4j.examples.tree;

import java.util.ArrayList;
import java.util.List;

import org.gui4j.Gui4j;
import org.gui4j.Gui4jView;
import org.gui4j.event.SimpleEvent;

public class TreeMainController extends BaseController
{
    private final static String RESOUCE_NAME = "main.xml";
    
    public final SimpleEvent eSwitch = new SimpleEvent();

    private Gui4jView gui4jView;
    private boolean rootSelected;
    private boolean itemSelected;

    public TreeMainController(Gui4j gui4j)
    {
        super(gui4j);
    }

    public boolean onWindowClosing()
    {
        System.exit(0);
        return true;
    }

    public void display()
    {
        gui4jView = createGui4jView();
        gui4jView.maximize1024x768();
        gui4jView.center(true);
        gui4jView.prepare();
        gui4jView.show();
    }

    protected Gui4jView createGui4jView()
    {
        Gui4jView view = getGui4j().createView(RESOUCE_NAME, this, getTitle(), false);
        return view;
    }

    private String getTitle()
    {
        return "Tree Example";
    }
    
    public Root getTreeRoot()
    {
        return new Root();
    }
    
    public List getChildrenOfRoot(Root root)
    {
        List l = new ArrayList();
        List li1 = new ArrayList();
        li1.add(new Item("Sub Item 1", null));
        li1.add(new Item("Sub Item 2", null));
        Item i1 = new Item("Item i1",li1);
        Item i2 = new Item("Item i2",null);
        Item i3 = new Item("Item i3",null);
        l.add(i1);
        l.add(i2);
        l.add(i3);
        return l;
    }

    public void actionExit()
    {
        close();
        System.exit(0);
    }

    public void close()
    {
        if (gui4jView != null)
        {
            gui4jView.close();
            gui4jView = null;
        }
    }
    
    public void actionSelectRoot(Root root)
    {
        rootSelected = true;
        itemSelected = false;
        eSwitch.fireEvent();
    }
    
    public void actionSelectItem(Item item)
    {
        rootSelected = false;
        itemSelected = true;
        eSwitch.fireEvent();
    }
    
    public boolean displayRootLabel()
    {
        return rootSelected;
    }
    
    public boolean displayItemLabel()
    {
        return itemSelected;
    }
    
    public static class Root
    {
        
    }

    public static class Item
    {
        private final List children;
        private final String nodeText;
        
        public Item(String nodeText, List children)
        {
            this.nodeText = nodeText;
            this.children = children;
        }
        
        public List getChildren()
        {
            return children == null ? new ArrayList() : children;
        }
        
        public String getNodeText()
        {
            return nodeText;
        }
    }

}