package org.gui4j.core.call;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jCallFactory;
import org.gui4j.core.Gui4jComponent;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jTypeCheck;
import org.gui4j.event.Gui4jEvent;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;


final public class Gui4jCallParser implements Gui4jCallFactory, ErrorTags, Serializable
{

    /**
     * Constructor for Gui4jCallParser.
     */
    public Gui4jCallParser()
    {
        super();
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jCallFactory#getInstance(org.gui4j.core.Gui4jComponent, int, java.lang.String)
     */
    public Gui4jCall getInstance(Gui4jComponent gui4jComponent, int lineNumber, String accessPath)
    {
        Map m = null;
        return getInstance(gui4jComponent, lineNumber, m, accessPath);
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jCallFactory#getInstance(org.gui4j.core.Gui4jComponent, int, java.lang.Class, java.lang.String)
     */
    public Gui4jCall getInstance(
        Gui4jComponent gui4jComponent,
        int lineNumber,
        Class valueClass,
        String accessPath)
    {
        Map m = new Gui4jMap1("", valueClass);
        return getInstance(gui4jComponent, lineNumber, m, accessPath);
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jCallFactory#getInstance(org.gui4j.core.Gui4jComponent, int, java.util.Map, java.lang.String)
     */
    public Gui4jCall getInstance(
        Gui4jComponent gui4jComponent,
        int lineNumber,
        Map valueClassMap,
        String accessPath)
    {
        Class baseClass = gui4jComponent.getGui4jComponentContainer().getGui4jControllerClass();
        if (accessPath == null)
        {
            return null;
        }

        Gui4jAccessImpl baseAccess = new Gui4jAccessBase(baseClass);

        ParseCtx parseCtx =
            new ParseCtx(this, gui4jComponent, lineNumber, valueClassMap, baseAccess, accessPath);

        List dependantProps = null;
        if (accessPath.length() > 0 && accessPath.charAt(0) == '{')
        {
            dependantProps = parseDependantProperties(parseCtx, accessPath);
        }

        if (parseCtx.i == accessPath.length())
        {
            return new Gui4jDefaultCall(gui4jComponent, null, dependantProps, valueClassMap);
        }

        // support for "internal action" calls,
        // e.g. maximization of portlets
        if (accessPath.charAt(parseCtx.i) == '!') {
            return new Gui4jInternalActionCall(gui4jComponent, accessPath.substring(parseCtx.i + 1));
        }
        
        Gui4jAccessImpl result = parseSeqSequence(parseCtx, accessPath);
        if (parseCtx.i < accessPath.length())
        {
            Object[] args = { accessPath, new Integer(parseCtx.i)};
            throw new Gui4jUncheckedException.ResourceError(
                parseCtx.getConfigurationName(),
                parseCtx.getLineNumber(),
                RESOURCE_ERROR_access_unexpected_character,
                args);
        }

        return new Gui4jDefaultCall(gui4jComponent, result, dependantProps, valueClassMap, parseCtx.getUsedParams());
    }

    private Gui4jAccessImpl getInstance(ParseCtx parseCtx, Gui4jAccessImpl thisAccess, String accessPath)
    {
        int pos = parseCtx.i;
        int len = accessPath.length();
        if (pos >= len)
        {
            Object[] args = { accessPath, new Integer(pos)};
            throw new Gui4jUncheckedException.ResourceError(
                parseCtx.getConfigurationName(),
                parseCtx.getLineNumber(),
                RESOURCE_ERROR_access_unexpected_end,
                args);
        }

        switch (accessPath.charAt(pos))
        {
            case '\'' :
                return new Gui4jAccessConstant(parseCtx, accessPath);
            case '{' :
            case '}' :
            case ')' :
            case ',' :
            case '.' :
            case ';' :
                {
                    Object[] args = { accessPath, new Integer(pos)};
                    throw new Gui4jUncheckedException.ResourceError(
                        parseCtx.getConfigurationName(),
                        parseCtx.getLineNumber(),
                        RESOURCE_ERROR_access_unexpected_end,
                        args);
                }
            case '(' :
                parseCtx.i = parseCtx.i + 1;
                Gui4jAccessImpl gui4jAccess = getInstance(parseCtx, thisAccess, accessPath);
                if (len >= parseCtx.i || accessPath.charAt(parseCtx.i) != ')')
                {
                    Object[] args = { accessPath, new Integer(pos)};
                    throw new Gui4jUncheckedException.ResourceError(
                        parseCtx.getConfigurationName(),
                        parseCtx.getLineNumber(),
                        RESOURCE_ERROR_access_unexpected_character,
                        args);
                }
                else
                {
                    parseCtx.i = parseCtx.i + 1; // skip ')'
                }
                return gui4jAccess;
            case ':' :
                return new Gui4jAccessStatic(parseCtx, accessPath);
            case '~' :
                return new Gui4jAccessBoolean(parseCtx, accessPath);
            case '0' :
            case '1' :
            case '2' :
            case '3' :
            case '4' :
            case '5' :
            case '6' :
            case '7' :
            case '8' :
            case '9' :
                {
                    boolean isDouble = false;
                    int i = pos;
                    while (i < len)
                    {
                        char c = accessPath.charAt(i);
                        if (c == '.')
                        {
                            isDouble = true;
                            break;
                        }
                        else if (c >= '0' && c <= '9')
                        {
                            // go to next character
                            i++;
                        }
                        else
                        {
                            break;
                        }
                    }
                    return isDouble
                        ? (Gui4jAccessImpl) new Gui4jAccessDouble(parseCtx, accessPath)
                        : (Gui4jAccessImpl) new Gui4jAccessInteger(parseCtx, accessPath);
                }
            case '%' :
                return new Gui4jAccessCharacter(parseCtx, accessPath);
            case '$' :
                parseCtx.i = parseCtx.i+1;
                return parseCtx.getBaseAccess();
            case '?' :
                return new Gui4jAccessSetValue(parseCtx, accessPath);
            default :
                return new Gui4jAccessMethod(parseCtx, thisAccess, accessPath);
        }
    }

    Gui4jAccessImpl parseCallSequence(ParseCtx parseCtx, String accessPath)
    {
        Gui4jAccessImpl gui4jAccess = parseCtx.getBaseAccess();
        int len = accessPath.length();
        do
        {
            gui4jAccess = getInstance(parseCtx, gui4jAccess, accessPath);
            if (parseCtx.i < len && accessPath.charAt(parseCtx.i) == '.')
            {
                parseCtx.i = parseCtx.i + 1;
            }
            else
            {
                return gui4jAccess;
            }
        }
        while (true);
    }

    static void skipSpaces(ParseCtx parseCtx, int len)
    {
        while (parseCtx.i < len && parseCtx.mAccessPath.charAt(parseCtx.i) == ' ')
        {
            parseCtx.i++;
        }
    }

    private Gui4jAccessImpl createSeq(Gui4jAccessImpl first, Gui4jAccessImpl second)
    {
        if (first == null)
        {
            return second;
        }
        return new Gui4jAccessSeq(first, second);
    }

    private Gui4jAccessImpl parseSeqSequence(ParseCtx parseCtx, String accessPath)
    {
        Gui4jAccessImpl gui4jAccess = null;
        int len = accessPath.length();
        do
        {
            gui4jAccess = createSeq(gui4jAccess, parseCallSequence(parseCtx, accessPath));
            skipSpaces(parseCtx, len);
            if (parseCtx.i < len && accessPath.charAt(parseCtx.i) == ';')
            {
                parseCtx.i = parseCtx.i + 1;
            }
            else
            {
                return gui4jAccess;
            }
        }
        while (true);
    }

    private List parseDependantProperties(ParseCtx parseCtx, String accessPath)
    {
        List dependantProps = new ArrayList();
        int len = accessPath.length();
        parseCtx.i++; // skip '{'
        loop : do
        {
            Gui4jAccessImpl property = parseCallSequence(parseCtx, accessPath);
            Gui4jTypeCheck.ensureType(
                property.getResultClass(),
                Gui4jEvent.class,
                parseCtx.getConfigurationName(),
                accessPath);
            dependantProps.add(new Gui4jDefaultCall(parseCtx.getGui4jComponent(), property, null, null));
            skipSpaces(parseCtx, len);
            if (parseCtx.i < len)
            {
                switch (accessPath.charAt(parseCtx.i))
                {
                    case '}' :
                        parseCtx.i++;
                        return dependantProps;
                    case ',' :
                        parseCtx.i++;
                        break;
                    default :
                        Object[] args = { accessPath, new Integer(parseCtx.i)};
                        throw new Gui4jUncheckedException.ResourceError(
                            parseCtx.getConfigurationName(),
                            parseCtx.getLineNumber(),
                            RESOURCE_ERROR_access_unexpected_character,
                            args);
                }
            }
            else
            {
                Object[] args = { accessPath, new Integer(parseCtx.i)};
                throw new Gui4jUncheckedException.ResourceError(
                    parseCtx.getConfigurationName(),
                    parseCtx.getLineNumber(),
                    RESOURCE_ERROR_access_unexpected_end,
                    args);
            }
        }
        while (true);
    }

}
