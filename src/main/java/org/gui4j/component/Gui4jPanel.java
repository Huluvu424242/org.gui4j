package org.gui4j.component;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.JComponent;

import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.swing.JPanelImage;


public final class Gui4jPanel extends Gui4jJComponent
{
    private final Gui4jQualifiedComponent mGui4jComponentInPath;
    private String imageMode;

    public Gui4jPanel(Gui4jComponentContainer gui4jComponentContainer, String id, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        super(gui4jComponentContainer, JPanelImage.class, id);
        mGui4jComponentInPath = gui4jComponentInPath;
    }

    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        super.setProperties(gui4jComponentInstance);

        Gui4jComponentInstance subInstance =
            gui4jComponentInstance.getGui4jSwingContainer().getGui4jComponentInstance(
                gui4jComponentInstance.getGui4jComponentInPath().getGui4jComponentPath(),
                mGui4jComponentInPath);

        JPanelImage panel = (JPanelImage) gui4jComponentInstance.getComponent();
        panel.setLayout(new BorderLayout());
        panel.add(subInstance.getComponent(), BorderLayout.CENTER);
        panel.setMode(imageMode);
    }

    public void setImage(JComponent component, Image image)
    {
        JPanelImage panel = (JPanelImage) component;
        panel.setImg(image);
    }

    public void setImageMode(String mode)
    {
        if (mode == null)
        {
            return;
        }

        if (mode.equals("tile"))
        {
            imageMode = JPanelImage.TILE;
        }
        else if (mode.equals("scale"))
        {
            imageMode = JPanelImage.SCALE;
        }
        else if (mode.equals("single"))
        {
            imageMode = JPanelImage.SINGLE;
        }
    }

}
