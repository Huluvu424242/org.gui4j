package org.gui4j.core.call;

import java.io.Serializable;
import java.util.Map;

import org.gui4j.exception.ErrorTags;


abstract class Gui4jAccessImpl implements Serializable, ErrorTags
{
    /**
     * Returns <code>true</code> if the value returned by <code>getValue(Object)</code> never
     * changes
     * @return boolean
    */
    public abstract boolean isConstant();

    abstract Object getValue(Object baseInstance, Object thisInstance, Map paramMap);

    abstract Class getResultClass();

    protected int minIndex(String str, String chars, int startIdx)
    {
        int i = -1;
        for (int j = 0; j < chars.length(); j++)
        {
            char c = chars.charAt(j);
            int idx = str.indexOf(c, startIdx);
            if (idx != -1 && (idx < i || (i == -1)))
            {
                i = idx;
            }
        }
        return i;
    }

}
