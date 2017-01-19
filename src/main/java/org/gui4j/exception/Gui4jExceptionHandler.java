package org.gui4j.exception;

/**
 * Übernimmt die Fehlerbehandlung. Es werden drei unterschiedliche
 * Methodensignaturen unterstützt, wobei in jede dieser drei 
 * Möglichkeiten auch spezifischere Klassen verwendeten werden können.
 * 
 * Falls der errorContext gesetzt ist, wird zuerst nach einer
 * Methode mit beiden Argumenten gesucht. Anschließend erfolgt
 * die Suche ohne das Argument errorContext.
 * 
 * Der DelegationExceptionHandler dient dazu hierarchisch
 * Exception zu behandeln. Die Suche erfolgt Bottom-Up. Bezüglich
 * errorContext gilt, dass zuerst mit beiden Argumenten Bottom-Up
 * gesucht wird und falls keine passende Methode vorhanden war,
 * dann wird mit einem Argument nochmals Bottom-Up gesucht.
 * 
 */
public interface Gui4jExceptionHandler
{
	// void handleException(Throwable t)
	// void handleException(Object context, Throwable t)
	// void handleSuccess(Object context)
    
    Gui4jExceptionHandler getDelegationExceptionHandler();
}
