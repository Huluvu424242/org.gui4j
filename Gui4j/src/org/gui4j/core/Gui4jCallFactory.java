package org.gui4j.core;

import java.util.Map;



/**
 * This interface is responsible for reflection. It parses a string for method, field occurences and primitive types.
 * The syntax is according to the following grammar:
 * <ul>
 *  <li>accessPath = ('{' (parseCallSequence ',')* parseCallSequence '}')? parseSeqSequence</li>
 *  <li>parseSeqSequence = (parseCallSequence ';')* parseCallSequence</li>
 *  <li>parseCallSequence = (reflection '.')* reflection</li>
 *  <li>reflection = (~T, ~F, [0-9]+ | %[A-Za-z] | \' string \'  | :aliasName | methodName(arg1,...,argn) | fieldName </li>
 * </ul>
 */
public interface Gui4jCallFactory
{
    /**
     * Method getInstance.
     * @param gui4jComponent
     * @param lineNumber
     * @param accessPath
     * @return Gui4jCall
     */
    Gui4jCall getInstance(Gui4jComponent gui4jComponent, int lineNumber, String accessPath);

    /**
     * Method getInstance.
     * @param gui4jComponent
     * @param lineNumber
     * @param valueClass
     * @param accessPath
     * @return Gui4jCall
     */
    Gui4jCall getInstance(
        Gui4jComponent gui4jComponent,
        int lineNumber,
        Class valueClass,
        String accessPath);

    /**
     * Method getInstance.
     * @param gui4jComponent
     * @param lineNumber
     * @param valueClassMap
     * @param accessPath
     * @return Gui4jCall
     */
    Gui4jCall getInstance(
        Gui4jComponent gui4jComponent,
        int lineNumber,
        Map valueClassMap,
        String accessPath);

}
