package org.gui4j;

import org.gui4j.exception.Gui4jExceptionHandler;

/**
 * Each gui controller must implement this interface. It is necessary to
 * execute the reflection calls.
 */
public interface Gui4jCallBase
{
    /**
     * Returns the gui instance.
     * @return Gui4j
     */
    Gui4j getGui4j();
    
    /**
     * Returns the exception handler used to handle exceptions.
     * @return Gui4jExceptionHandler
     */
    Gui4jExceptionHandler getExceptionHandler();
}
