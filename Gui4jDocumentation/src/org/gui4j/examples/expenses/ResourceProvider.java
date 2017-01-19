package org.gui4j.examples.expenses;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.border.Border;

/**
 * Provides methods to retrieve GUI resources like
 * Icons and Borders.
 */
public class ResourceProvider {

    private static final ResourceProvider instance = new ResourceProvider();

    public final Icon iconCreate;
    public final Icon iconDelete;
    public final Icon iconEdit;
    public final Icon iconError;
    
    public final Border borderEmpty;
    public final Border borderForm;
    
    public static ResourceProvider getInstance() {
        return instance;
    }
    
    private ResourceProvider() {
        super();
        
        iconCreate = createIcon("create.gif");
        iconDelete = createIcon("delete.gif");
        iconEdit = createIcon("edit.gif");
        iconError = createIcon("error.gif");
        
        borderEmpty = BorderFactory.createEmptyBorder();
        borderForm = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    }

    private Icon createIcon(String resourceName) {
        return new ImageIcon(createImageImpl(resourceName));
    }

    private Image createImageImpl(String resourceName) {
        if (resourceName == null || "".equals(resourceName)) {
            return null;
        }
        URL iconURL = getClass().getResource(resourceName);
        if (iconURL == null) {
            throw new RuntimeException("Missing resource: " + resourceName);
        }
        return Toolkit.getDefaultToolkit().createImage(iconURL);
    }
    
}
