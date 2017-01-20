package org.gui4j.util;

/**
 * Allgemeines Interface zur Defintion von Filtern auf beliebigen Objekten.
 */
public interface Filter
{
    /**
     * 
     * @param object Objekt, für das die Filtereigenschaft abgefragt werden soll.
     * @return boolean <code>true</code>, falls das Objekt "genommen" werden soll,
     * d.h. wenn das Objekt nicht rausgefiltert wurde. <code>false</code>, falls das Objekt
     * nicht genommen werden soll, d.h. wenn es rausgefiltert wurde.
     */
    boolean takeIt(Object object);
}
