package org.gui4j.component.factory;

import java.awt.Image;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.LElement;
import org.gui4j.component.Gui4jJComponent;
import org.gui4j.component.Gui4jPanel;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentContainerManager;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.definition.Attribute;
import org.gui4j.core.definition.AttributeTypeEnumeration;
import org.gui4j.core.definition.AttributeTypeID;
import org.gui4j.core.definition.AttributeTypeMethodCall;
import org.gui4j.core.definition.Param;
import org.gui4j.exception.Gui4jUncheckedException;
import org.gui4j.util.Filter;


public final class Gui4jPanelFactory extends Gui4jJComponentFactory 
{

    private static final String NAME = "panel";
    private static final String ID = "id";
    private static final String IMAGE = "image";
    private static final String IMAGEMODE = "imageMode";

    protected Gui4jJComponent defineGui4jJComponentBy(
        Gui4jComponentContainer gui4jComponentContainer,
        String id,
        LElement e)
    {
        String gui4jId = e.attributeValue(ID);
        Gui4jQualifiedComponent gui4jComponentInPath;
        if (gui4jId == null)
        {
            if (e.elements().isEmpty())
            {
                Object[] args = { getName()};
                throw new Gui4jUncheckedException.ResourceError(
                    gui4jComponentContainer.getConfigurationName(),
                    Gui4jComponentContainerManager.getLineNumber(e),
                    RESOURCE_ERROR_element_must_contain_gui4jComponent,
                    args);
            }
            LElement gui4jElement = (LElement) e.elements().iterator().next();
            gui4jComponentInPath = gui4jComponentContainer.extractGui4jComponent(gui4jElement);
        }
        else
        {
            gui4jComponentInPath = gui4jComponentContainer.getGui4jQualifiedComponent(gui4jId);
        }
        Gui4jPanel gui4jPanel = new Gui4jPanel(gui4jComponentContainer, id, gui4jComponentInPath);

        String imageMode = e.attributeValue(IMAGEMODE, "single");
        gui4jPanel.setImageMode(imageMode);

        return gui4jPanel;
    }

    protected void defineProperties(Gui4jJComponent gui4jJComponent, LElement e)
    {
        super.defineProperties(gui4jJComponent, e);
        gui4jJComponent.definePropertySetter(IMAGE, getGui4jAccessInstance(Image.class, gui4jJComponent, e, IMAGE));

    }

    public String getName()
    {
        return NAME;
    }

    public SubElement getSubElement(String elementName)
    {
        if (NAME.equals(elementName))
        {
            return SubElement.optional(SubElement.gui4jComponent());
        }
        return null;
    }
    
    public void addInnerAttributes(String elementName, List list)
    {
    }

    public void addToplevelAttributes(List attrList, Filter filter)
    {
        Set s = new HashSet();
        s.add(new Param("single"));
        s.add(new Param("scale"));
        s.add(new Param("tile"));
        
        super.addToplevelAttributes(attrList, filter);
        if (filter == null || filter.takeIt(Gui4jPanelFactory.class))
        {
            attrList.add(new Attribute(ID, new AttributeTypeID(), IMPLIED, false));
            attrList.add(new Attribute(IMAGE, new AttributeTypeMethodCall(Image.class), IMPLIED, false));
            attrList.add(new Attribute(IMAGEMODE, new AttributeTypeEnumeration(s), IMPLIED, false));
        }
    }

}
