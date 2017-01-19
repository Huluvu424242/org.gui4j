package org.gui4j.core.impl;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.Gui4jResourceProvider;

/**
 * Default implementation for providing ressources. This implementation uses the class loader to
 * retrieve ressources.
 */
public class Gui4jResourceProviderImpl implements Gui4jResourceProvider
{
    private static Log log = LogFactory.getLog(Gui4jResourceProviderImpl.class);
    
    public InputStream getResource(String fullyQualifiedName)
    {
        log.debug("Retrieving resource from classpath: " + fullyQualifiedName);
        return getClass().getResourceAsStream(fullyQualifiedName);
    }

}
