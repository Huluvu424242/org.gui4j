package org.gui4j.core;

import java.awt.event.MouseEvent;

import org.gui4j.Gui4jCallBase;

public interface Gui4jComponent
{
    /**
     * One instance of Gui4jComponent represents one component used in a given a xml resource file
     * resolved for a given controller class. This means that several views of the same resource
     * file share the same instance of the corresponding Gui4jComponent. This is important, because
     * the Gui4jComponent instance must not contain any information dependant on a given swing instance.
    */

    /**
     * is called to create an instance of Gui4jComponentInstance
     * @param gui4jSwingContainer
     * @param gui4jCallBase
     * @param gui4jComponentInPath
     * @return Gui4jComponentInstance
    */
    public Gui4jComponentInstance createGui4jComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath);

    /**
     * @return the id of the given component or null if no id is present
    */
    public String getId();

    /**
     * @return the used instance of Gui4j
    */
    public Gui4jInternal getGui4j();

    /**
     * @return the container where this component is stored in
    */
    public Gui4jComponentContainer getGui4jComponentContainer();

    /**
     * @return the properties to be called by reflection
    */
    public Gui4jComponentProperty[] getGui4jComponentProperties();

    /**
     * Aktualisiert die Komponente.
     * @param gui4jComponentInstance
     */
    public void refreshComponent(Gui4jComponentInstance gui4jComponentInstance);

    /**
     * Gibt Speicher frei in Verbindung zu der angegebenen Instanz
     * @param gui4jComponentInstance
     */
    public void dispose(Gui4jComponentInstance gui4jComponentInstance);
    
    /**
     * Zeigt das PopUp Menü an der Komponente an (falls eines definiert ist).
     * @param gui4jComponentInstance
     * @param mouseEvent das mouse event, falls PopUp durch Mausaktion gefordert wurde,
     * <code>null</code> falls nicht (z.B. durch Tastatur)
     */
    public void showPopupMenu(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent);
    
    /**
     * Wird aufgerufen, wenn ein Setter ohne Exception ausgeführt wurde.
     * @param gui4jComponentInstance
     */
    public void handleSuccess(Gui4jComponentInstance gui4jComponentInstance);

    /**
     * Wird aufgerufen, wenn ein Setter mit Exception ausgeführt wurde.
     * @param gui4jComponentInstance
     * @param t
     */
    public void handleException(Gui4jComponentInstance gui4jComponentInstance, Throwable t);
    
    public Object evaluateContext(Gui4jCallBase gui4jCallBase);
}
