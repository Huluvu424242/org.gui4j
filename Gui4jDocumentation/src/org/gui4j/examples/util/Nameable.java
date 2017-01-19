package org.gui4j.examples.util;

/**
 * Implementierende Klassen müssen eindeutige sog. Nametags für
 * alle ihre Instanzen liefern.
 * Diese Nametags können von anderen Komponenten beliebiger Schichten verwendet werden.
 * Hauptintention für die Einführung: die GUI Schicht benutzt den Nametag von
 * Objekten, um Übersetzungen bzw. Anzeigetexte zu definieren.
 */
public interface Nameable
{
	public String getNameTag();
    public String getShortTag();
}
