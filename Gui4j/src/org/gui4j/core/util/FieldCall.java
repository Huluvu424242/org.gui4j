package org.gui4j.core.util;

import java.io.Serializable;

public interface FieldCall extends Serializable
{
    Class getType();

    Object get(Object base) throws IllegalAccessException;

}
