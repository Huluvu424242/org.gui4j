package org.gui4j;

import java.io.InputStream;

public interface Gui4jResourceProvider
{
    /**
     * @param fullyQualifiedName
     * @return InputStream with XML content
     */
    InputStream getResource(String fullyQualifiedName);
}
