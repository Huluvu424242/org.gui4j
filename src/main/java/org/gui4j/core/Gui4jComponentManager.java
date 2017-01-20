package org.gui4j.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.LElement;
import org.dom4j.io.LNSAXReader;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jException;
import org.gui4j.exception.Gui4jUncheckedException;


/**
 * This class manages the different Gui4jComponents. The known components are
 * defined by factory classes in an XML configuration file. 
 * The Gui4jComponentManager read an XML configuration file and instantiates
 * the defined factory classes. Theses classes are used to extract the 
 * Gui4jComponent definition in given XML resource files.
 */
public final class Gui4jComponentManager implements Serializable, ErrorTags
{
    private static final Log mLogger = LogFactory.getLog(Gui4jComponentManager.class);

    public final static String ELEMENT_Gui4jView = "View";
    public final static String ELEMENT_Gui4jRef = "Ref";
    public final static String ELEMENT_Gui4jInclude = "Include";
    public final static String ELEMENT_Gui4jClassAlias = "ClassAlias";
    public final static String ELEMENT_Gui4jDef = "Def";
    public final static String ELEMENT_Gui4jStyle = "Style";
    public final static String ELEMENT_Gui4jStyleBegin = "StyleBegin";
    public final static String ELEMENT_Gui4jStyleEnd = "StyleEnd";
    public final static String ELEMENT_Gui4jParam = "Param";
    public final static String ELEMENT_Gui4jArg = "Arg";

    public final static String FIELD_Gui4jViewDefaultButton = "defaultButton";
    public final static String FIELD_Gui4jViewTopComponent = "top";
    public final static String FIELD_Gui4jViewTitle = "title";
    public final static String FIELD_Gui4jViewName = "name";

    public final static String FIELD_Gui4jDefName = "name";
    public final static String FIELD_Gui4jDefValue = "value";

    public final static String FIELD_Gui4jStyleName = "name";
    public final static String FIELD_Gui4jStyleExtends = "extends";

    public final static String FIELD_Gui4jStyleBeginName = "name";

    public final static String FIELD_Gui4jIncludeAlias = "alias";
    public final static String FIELD_Gui4jIncludeURL = "url";
    public final static String FIELD_Gui4jIncludeController = "controller";
    public final static String FIELD_Gui4jIncludeRefresh = "refresh";

    public final static String FIELD_Gui4jParamId = "id";
    public final static String FIELD_Gui4jArgParam = "param";
    public final static String FIELD_Gui4jArgId = "id";

    public final static String FIELD_Gui4jClassAliasName = "name";
    public final static String FIELD_Gui4jClassAliasClass = "class";

    private final static String ELEMENT_Gui4jComponents = "Components";
    private final static String ELEMENT_Gui4jComponent = "Component";
    private final static String ATTR_FactoryClass = "factoryClass";

    private final boolean VALIDATE = true;
    private final Gui4jInternal mGui4j;
    private final Gui4jCallFactory mGui4jCallFactory;
    private final Map mGui4jComponentMap = new HashMap();

    private Gui4jComponentManager(Gui4jInternal gui4j)
    {
        mGui4j = gui4j;
        mGui4jCallFactory = gui4j.createCallFactory();
    }
    
    public void dispose()
    {
        mGui4jComponentMap.clear();
    }

    /**
     * Returns always the same instance
     * @param gui4j
     * @return Gui4jComponentManager
    */
    public static Gui4jComponentManager getNewInstance(Gui4jInternal gui4j)
    {
        return new Gui4jComponentManager(gui4j);
    }

    /**
     * Reads an XML file containing the set of defined Gui4jComponents
     * @param configurationSource URL for XML configuration file
    */
    public void configure(URL configurationSource)
    {
        mLogger.debug("Configuring Gui4jComponentManager from " + configurationSource);
        if (configurationSource.toString().endsWith(".properties"))
        {
            try
            {
                Properties props = new Properties();
                InputStream in = configurationSource.openStream();
                props.load(in);
                in.close();
                analyzeDocument(props, configurationSource);
            }
            catch (IOException e)
            {
                String[] args = { e.getMessage()};
                throw new Gui4jUncheckedException.ResourceError(
                    configurationSource.toString(),
                    -1,
                    RESOURCE_ERROR_jdom_exception,
                    args,
                    e);
            }
        }
        else
        {
            try
            {
                LNSAXReader builder = new LNSAXReader(VALIDATE);
                Document doc = builder.read(configurationSource);
                analyzeDocument(doc, configurationSource);
            }
            catch (DocumentException e)
            {
                String[] args = { e.getMessage()};
                throw new Gui4jUncheckedException.ResourceError(
                    configurationSource.toString(),
                    -1,
                    RESOURCE_ERROR_jdom_exception,
                    args,
                    e);
            }
        }
    }

    /**
     * Writes the DTD for the set of configured Gui4jComponents to the
     * specified OutputStream
     * @param out
    */
    public void writeDTD(PrintWriter out)
    {
        out.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>");
        out.println();

        out.print("<!ENTITY % Component \"(");
        boolean first = true;
        for (Iterator it = mGui4jComponentMap.keySet().iterator(); it.hasNext();)
        {
            String name = (String) it.next();
            if (first)
            {
                out.print(name);
                first = false;
            }
            else
            {
                out.print("|" + name);
            }
        }
        out.println(")\">");

        out.print("<!ENTITY % " + ELEMENT_Gui4jStyle + "s \"(");
        first = true;
        loadAll();
        for (Iterator it = mGui4jComponentMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            Gui4jComponentFactory gui4jComponentFactory = (Gui4jComponentFactory) entry.getValue();
            if (first)
            {
                out.print(ELEMENT_Gui4jStyle + "_" + name);
                first = false;
            }
            else
            {
                out.print("|" + ELEMENT_Gui4jStyle + "_" + name);
            }
            String[] innerElements = gui4jComponentFactory.getInnerElements();
            if (innerElements != null)
            {
                for (int i = 0; i < innerElements.length; i++)
                {
                    out.print("|" + ELEMENT_Gui4jStyle + "_" + innerElements[i]);
                }
            }
        }
        out.println(")\">");
        out.println(
            "<!ELEMENT "
                + ELEMENT_Gui4jView
                + " ("
                + ELEMENT_Gui4jParam
                + "*,"
                + ELEMENT_Gui4jInclude
                + "*,"
                + ELEMENT_Gui4jClassAlias
                + "*,"
                + ELEMENT_Gui4jStyle
                + "*,"
                + "(%Component;|"
                + ELEMENT_Gui4jDef
                + "|"
                + ELEMENT_Gui4jStyleBegin
                + "|"
                + ELEMENT_Gui4jStyleEnd
                + ")*)>");
        out.println("<!ATTLIST " + ELEMENT_Gui4jView);
        out.println("  " + FIELD_Gui4jViewDefaultButton + " CDATA #IMPLIED");
        out.println("  " + FIELD_Gui4jViewTopComponent + " CDATA #IMPLIED");
        out.println("  " + FIELD_Gui4jViewTitle + " CDATA #IMPLIED");
        out.println("  " + FIELD_Gui4jViewName + " CDATA #IMPLIED");
        out.println(">");
        out.println("<!ELEMENT " + ELEMENT_Gui4jRef + " EMPTY>");
        out.println("<!ELEMENT " + ELEMENT_Gui4jInclude + " (" + ELEMENT_Gui4jArg + "*)>");
        out.println("<!ELEMENT " + ELEMENT_Gui4jDef + " EMPTY>");
        out.println("<!ELEMENT " + ELEMENT_Gui4jClassAlias + " EMPTY>");
        out.println("<!ELEMENT " + ELEMENT_Gui4jStyleBegin + " EMPTY>");
        out.println("<!ELEMENT " + ELEMENT_Gui4jStyleEnd + " EMPTY>");
        out.println("<!ELEMENT " + ELEMENT_Gui4jParam + " EMPTY>");
        out.println("<!ELEMENT " + ELEMENT_Gui4jArg + " EMPTY>");
        out.println(
            "<!ATTLIST " + ELEMENT_Gui4jRef + " " + Gui4jComponentFactory.FIELDGui4jRef_Id + " CDATA #REQUIRED>");
        out.println("<!ELEMENT " + ELEMENT_Gui4jStyle + " ((%" + ELEMENT_Gui4jStyle + "s;)*)>");
        out.println("<!ATTLIST " + ELEMENT_Gui4jStyle);
        out.println("  " + FIELD_Gui4jStyleName + " CDATA #REQUIRED");
        out.println("  " + FIELD_Gui4jStyleExtends + " CDATA #IMPLIED");
        out.println(">");
        out.println("<!ATTLIST " + ELEMENT_Gui4jStyleBegin);
        out.println("  " + FIELD_Gui4jStyleBeginName + " CDATA #REQUIRED");
        out.println(">");
        out.println("<!ATTLIST " + ELEMENT_Gui4jDef);
        out.println("  " + FIELD_Gui4jDefName + " CDATA #REQUIRED");
        out.println("  " + FIELD_Gui4jDefValue + " CDATA #REQUIRED");
        out.println(">");
        out.println("<!ATTLIST " + ELEMENT_Gui4jInclude);
        out.println("  " + FIELD_Gui4jIncludeAlias + " CDATA #REQUIRED");
        out.println("  " + FIELD_Gui4jIncludeURL + " CDATA #REQUIRED");
        out.println("  " + FIELD_Gui4jIncludeController + " CDATA #IMPLIED");
        out.println("  " + FIELD_Gui4jIncludeRefresh + " CDATA #IMPLIED");
        out.println(">");
        out.println("<!ATTLIST " + ELEMENT_Gui4jClassAlias);
        out.println("  " + FIELD_Gui4jClassAliasName + " CDATA #REQUIRED");
        out.println("  " + FIELD_Gui4jClassAliasClass + " CDATA #REQUIRED");
        out.println(">");
        out.println("<!ATTLIST " + ELEMENT_Gui4jParam);
        out.println("  " + FIELD_Gui4jParamId + " CDATA #REQUIRED");
        out.println(">");
        out.println("<!ATTLIST " + ELEMENT_Gui4jArg);
        out.println("  " + FIELD_Gui4jArgParam + " CDATA #REQUIRED");
        out.println("  " + FIELD_Gui4jArgId + " CDATA #REQUIRED");
        out.println(">");
        loadAll();
        for (Iterator it = mGui4jComponentMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            Gui4jComponentFactory gui4jComponent = (Gui4jComponentFactory) entry.getValue();
            gui4jComponent.writeDTD(out);
        }
    }

    /**	
     * Writes the DTD for the set of configured Gui4jComponents to the
     * specified file
     * @param outputFile
     * @throws Gui4jException
    */
    public void writeDTD(File outputFile) throws Gui4jException
    {
        try
        {
            FileOutputStream out = new FileOutputStream(outputFile);
            PrintWriter writer = new PrintWriter(out);
            try
            {
                writeDTD(writer);
                writer.flush();
            }
            finally
            {
                out.close();
            }
        }
        catch (IOException e)
        {
            throw new Gui4jException(e);
        }
    }

    private void loadAll()
    {
        for (Iterator it = mGui4jComponentMap.keySet().iterator(); it.hasNext();)
        {
            String tag = (String) it.next();
            getGui4jComponentFactory(tag);
        }
    }

    /**
     * Returns the factory for the given component name
     * @param name
     * @return Gui4jComponentFactory
    */
    public Gui4jComponentFactory getGui4jComponentFactory(String name)
    {
        Object object = mGui4jComponentMap.get(name);
        if (object instanceof Gui4jComponentFactory)
        {
            return (Gui4jComponentFactory) object;
        }
        if (object instanceof String)
        {
            try
            {
                Class gui4jComponentClass = Class.forName((String) object);
                Gui4jComponentFactory gui4jComponentFactory =
                    (Gui4jComponentFactory) gui4jComponentClass.newInstance();
                gui4jComponentFactory.setGui4j(mGui4j);
                gui4jComponentFactory.setGui4jCallFactory(mGui4jCallFactory);
				mLogger.debug("Installing Gui4jComponent: " + name);
                mGui4jComponentMap.put(name, gui4jComponentFactory);
                assert name.equals(gui4jComponentFactory.getName()) : name;
                return gui4jComponentFactory;
            }
            catch (Exception e)
            {
            	throw new RuntimeException(e);
            }
        }
        Object[] args = { name };
        throw new Gui4jUncheckedException.ProgrammingError(RESOURCE_ERROR_gui4jComponent_not_registered, args);
    }

    /**
     * Checks if given component name is registered
     * @param name
     * @return boolean
    */
    public boolean isRegistered(String name)
    {
        return mGui4jComponentMap.containsKey(name);
    }

    public Set getInstalledComponentNames()
    {
        return mGui4jComponentMap.keySet();
    }

    private void analyzeDocument(Document doc, URL configurationSource)
    {
        LElement e = (LElement)doc.getRootElement();
        if (e.getName().equals(ELEMENT_Gui4jComponents))
        {
            installComponents(e, configurationSource);
        }
    }

    private void analyzeDocument(Properties properties, URL configurationSource)
    {
        installComponents(properties, configurationSource);
    }

    private void installComponents(Properties properties, URL configurationSource)
    {
        for (Iterator it = properties.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            String tag = (String) entry.getKey();
            String className = (String) entry.getValue();
            if (className != null)
            {
                className = className.trim();
            }
            installComponent(tag, className, configurationSource);
        }
    }

    private void installComponents(LElement e, URL configurationSource)
    {
        List children = e.elements();
        for (Iterator it = children.iterator(); it.hasNext();)
        {
            LElement gui4jComponent = (LElement) it.next();
            if (gui4jComponent.getName().equals(ELEMENT_Gui4jComponent))
            {
                installComponent(gui4jComponent, configurationSource);
            }
        }
    }

    private synchronized void installComponent(String tag, String className, URL configurationSource)
    {
        if (isRegistered(tag))
        {
            Object[] args = { tag };
            throw new Gui4jUncheckedException.ResourceError(
                configurationSource.toString(),
                -1,
                RESOURCE_ERROR_gui4jComponent_already_registered,
                args);
        }
        else
        {
            mGui4jComponentMap.put(tag, className);
        }
    }

    private synchronized void installComponent(LElement e, URL configurationSource)
    {
        String className = e.attributeValue(ATTR_FactoryClass);

        try
        {
            Class gui4jComponentClass = Class.forName(className);
            Gui4jComponentFactory gui4jComponentFactory = (Gui4jComponentFactory) gui4jComponentClass.newInstance();
            gui4jComponentFactory.setGui4j(mGui4j);
            gui4jComponentFactory.setGui4jCallFactory(mGui4jCallFactory);
            String name = gui4jComponentFactory.getName();

            if (isRegistered(name))
            {
                Object[] args = { name };
                throw new Gui4jUncheckedException.ResourceError(
                    configurationSource.toString(),
                    Gui4jComponentContainerManager.getLineNumber(e.attribute(ATTR_FactoryClass)),
                    RESOURCE_ERROR_gui4jComponent_already_registered,
                    args);
            }
            else
            {
                mLogger.debug("Installing Gui4jComponent: " + name);
                mGui4jComponentMap.put(name, gui4jComponentFactory);
            }
        }
        catch (Exception exc)
        {
            throw new RuntimeException(exc);
        }
    }

    /**
     * Returns the mGui4jComponentMap.
     * @return Map
     */
    public Map getGui4jComponentMap()
    {
        loadAll();
        return mGui4jComponentMap;
    }

}
