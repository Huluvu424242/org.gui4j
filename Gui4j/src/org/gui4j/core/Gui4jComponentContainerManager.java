package org.gui4j.core;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.LElement;
import org.dom4j.io.LNSAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jResourceProvider;
import org.gui4j.core.util.Extract;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;

/**
 * The Gui4jComponentContainerManager manages different instances of
 * Gui4jComponentContainer. Whenever an instance of Gui4jComponentContainer for
 * a given resource file and given controller class type is requested, the
 * Gui4jComponentManager creates that instance or returns an already created
 * instance for the same arguments.
 */
public final class Gui4jComponentContainerManager implements Serializable, ErrorTags
{
    private final Map mGui4jComponentContainerClassMap;
    private final Map mResourceMap;
    private final Gui4jInternal mGui4j;

    protected static final Log mLogger = LogFactory.getLog(Gui4jComponentContainerManager.class);

    private Gui4jComponentContainerManager(Gui4jInternal gui4j)
    {
        mGui4j = gui4j;
        mGui4jComponentContainerClassMap = new HashMap();
        mResourceMap = new HashMap();
        mLogger.debug("Gui4jComponentContainerClassMap initialisiert");
    }

    public static String getResourceNameFullyQuantified(String baseName, String resourceName)
    {
        {
            int idx = baseName.lastIndexOf('/');
            baseName = baseName.substring(0, idx);
        }
        if (resourceName.startsWith("/"))
        {
            return resourceName;
        }
        while (resourceName.startsWith("../"))
        {
            int idx = baseName.lastIndexOf('/');
            baseName = baseName.substring(0, idx);
            resourceName = resourceName.substring(3);
        }
        return baseName + "/" + resourceName;
    }

    public static String getBaseName(Class c)
    {
        return "/" + c.getName().replace('.', '/');
    }

    public static Gui4jComponentContainerManager getNewInstance(Gui4jInternal gui4j)
    {
        return new Gui4jComponentContainerManager(gui4j);
    }

    synchronized Resource getResource(String resourceNameFullyQuantified)
    {
        assert resourceNameFullyQuantified != null;

        Resource resource = (Resource) mResourceMap.get(resourceNameFullyQuantified);

        if (resource == null)
        {
            mLogger.debug("Resource " + resourceNameFullyQuantified + " not in cache.");
            // no entry for specified resource file
            resource = new Resource(mGui4j, resourceNameFullyQuantified);
            Map classMap = new HashMap();

            mResourceMap.put(resourceNameFullyQuantified, resource);
            if (!mGui4jComponentContainerClassMap.containsKey(resourceNameFullyQuantified))
            {
                mGui4jComponentContainerClassMap.put(resourceNameFullyQuantified, classMap);
            }
        }
        return resource;
    }

    public void clearResources()
    {
        mResourceMap.clear();
    }

    /**
     * If a <code>Gui4jComponentContainer</code> for the specified combination
     * is already loaded, then it is returned. Otherwise, a new instance is
     * created. Note that one instance of <code>Gui4jComponentContainer</code>
     * might be shared by several instances of <code>Gui4jSwingContainer</code>.
     * 
     * @param controllerClass
     * @param resourceNameFullyQuantified
     * @return Gui4jComponentContainer
     */
    public synchronized Gui4jComponentContainer getGui4jComponentContainer(Class controllerClass,
            String resourceNameFullyQuantified)
    {
        mLogger.debug("Resource " + resourceNameFullyQuantified + " for controller " + controllerClass + " requested");

        Gui4jComponentContainer container = null;
        Map classMap = null;

        if (mGui4jComponentContainerClassMap.containsKey(resourceNameFullyQuantified))
        {
            classMap = (Map) mGui4jComponentContainerClassMap.get(resourceNameFullyQuantified);
            container = (Gui4jComponentContainer) classMap.get(controllerClass);
        }

        if (container == null)
        {
            Resource resource = getResource(resourceNameFullyQuantified);
            classMap = (Map) mGui4jComponentContainerClassMap.get(resourceNameFullyQuantified);
            // no resource file for specified class
            container = new Gui4jComponentContainerImpl(mGui4j, controllerClass, resourceNameFullyQuantified, resource);
            classMap.put(controllerClass, container);
        }
        return container;
    }

    static class ImportInfo
    {
        String alias;
        String relName;
        String controller;
        String refresh;
        int controllerLineNo;
        int refreshLineNo;
        int urlLineNo;
        Map paramMap; // ParamId -> Id
    }

    static class Resource implements Serializable, ErrorTags
    {
        private static final Log mResourceLogger = LogFactory.getLog(Resource.class);

        private final Gui4jInternal mGui4j;
        private final String mResourceNameFullyQualified;
        private boolean mAlreadyLoaded;
        private List mChildren;
        private Map mToplevelAttributes;
        private final Gui4jStyleContainer mStyleContainer;
        private final List mGui4jImportList; // List(ImportInfo)
        private final Map mClassAliasMap; // String -> Class
        private final Map mClassAliasMapResourceName; // String -> String
        private final Map mDefMap; // String -> String
        private final Map mDefMapResourceName; // String -> String
        private final Set mParamIds; // Set(String)
        private final List mErrorList;
        private int mLineNoOfTopLevelAttrs;

        public Resource(Gui4jInternal gui4j, String resourceNameFullyQuantified)
        {
            mResourceLogger.debug("Reading resource " + resourceNameFullyQuantified);
            mGui4j = gui4j;
            mResourceNameFullyQualified = resourceNameFullyQuantified;
            mStyleContainer = new Gui4jStyleContainer(mResourceNameFullyQualified);
            mGui4jImportList = new ArrayList();
            mClassAliasMap = new HashMap();
            mClassAliasMapResourceName = new HashMap();
            mDefMap = new HashMap();
            mDefMapResourceName = new HashMap();
            mParamIds = new HashSet();
            mToplevelAttributes = new HashMap();
            mErrorList = new ArrayList();
            load();
        }
        
        public int getLineNoOfTopLevelAttrs()
        {
            return mLineNoOfTopLevelAttrs;
        }

        public List getToplevelDefinitions()
        {
            assert mChildren != null;
            return mChildren;
        }

        public Map getToplevelAttributes()
        {
            return mToplevelAttributes;
        }

        public List getErrorList()
        {
            return mErrorList;
        }

        private void load()
        {
            if (mAlreadyLoaded)
            {
                return;
            }

            try
            {
                LNSAXReader builder = new LNSAXReader(mGui4j.validateXML());
                builder.setValidation(mGui4j.validateXML());
                final Gui4jResourceProvider resourceProvider = mGui4j.getResourceProvider();
                assert resourceProvider != null;
                InputStream in = resourceProvider.getResource(mResourceNameFullyQualified);
                if (in == null)
                {
                    throw new NullPointerException(
                            "Resource provider did not return InputStream for requested resource: "
                                    + mResourceNameFullyQualified);
                }
                final EntityResolver entityResolver = builder.getEntityResolver();
                builder.setEntityResolver(new EntityResolver() {

                    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
                    {
                        mLogger.debug("Resolving entity for: PublicId: " + publicId + ", SystemId: " + systemId);
                        if (systemId != null)
                        {
                            // In Java 1.5 the system Id starts with file (very
                            // strange)
                            systemId = systemId.replaceFirst("file://", "");
                            while (systemId.indexOf("../") != -1)
                            {
                                systemId = systemId.replaceFirst("/[^/]+/\\.\\./", "/");
                            }
                            InputStream dtdResource = resourceProvider.getResource(systemId);
                            if (dtdResource == null)
                            {
                                throw new NullPointerException(
                                        "Resource Provider did not return Input Stream for DTD. Requested resource: "
                                                + systemId);
                            }
                            return new InputSource(dtdResource);
                        }
                        else
                        {
                            return entityResolver.resolveEntity(publicId, systemId);
                        }
                    }

                });
                Document doc = builder.read(in, mResourceNameFullyQualified);
                analyzeDocument(doc);
            }
            catch (DocumentException e)
            {
                String[] args = new String[] { e.getMessage() };
                throw new Gui4jUncheckedException.ResourceError(mResourceNameFullyQualified, -1,
                        RESOURCE_ERROR_jdom_exception, args, e);
            }

            mAlreadyLoaded = true;
        }

        private void analyzeDocument(Document doc)
        {
            LElement root = (LElement) doc.getRootElement();
            for (Iterator iter = root.attributeIterator(); iter.hasNext();)
            {
                Attribute attribute = (Attribute) iter.next();
                mToplevelAttributes.put(attribute.getName(), attribute.getValue());
            }
            mLineNoOfTopLevelAttrs = root.getLineNumber();
            mChildren = root.elements();
            Stack gui4jStyles = new Stack();
            for (Iterator it = mChildren.iterator(); it.hasNext();)
            {
                try
                {
                    LElement e = (LElement) it.next();
                    String name = e.getName();
                    if (gui4jStyles.size() > 0
                            && mGui4j.getGui4jComponentManager().getInstalledComponentNames().contains(name))
                    {
                        insertGui4jStyle((String) gui4jStyles.peek(), e);
                    }
                    if (name.equals(Gui4jComponentManager.ELEMENT_Gui4jStyleBegin))
                    {
                        String styleName = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jStyleName);
                        gui4jStyles.push(styleName);
                    }
                    if (name.equals(Gui4jComponentManager.ELEMENT_Gui4jStyleEnd))
                    {
                        if (gui4jStyles.size() > 0)
                        {
                            gui4jStyles.pop();
                        }
                        else
                        {
                            throw new Gui4jUncheckedException.ResourceError(mResourceNameFullyQualified, e
                                    .getLineNumber(), RESOURCE_ERROR_unexpected_gui4jStyle_end);
                        }
                    }
                    if (name.equals(Gui4jComponentManager.ELEMENT_Gui4jParam))
                    {
                        String id = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jParamId);
                        mParamIds.add(id);
                    }
                    if (name.equals(Gui4jComponentManager.ELEMENT_Gui4jStyle))
                    {
                        String styleName = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jStyleName);
                        String styleExtends = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jStyleExtends);
                        mStyleContainer.createStyle(mResourceNameFullyQualified, styleName, styleExtends);
                        int startIdx = Gui4jComponentManager.ELEMENT_Gui4jStyle.length() + 1;
                        for (Iterator it2 = e.elementIterator(); it2.hasNext();)
                        {
                            LElement child = (LElement) it2.next();
                            mStyleContainer.addAttributes(child.getName().substring(startIdx), styleName, child
                                    .attributes());
                        }
                        it.remove();
                    }
                    if (name.equals(Gui4jComponentManager.ELEMENT_Gui4jInclude))
                    {
                        Map paramMap = new HashMap();
                        for (Iterator itInclude = e.elementIterator(); itInclude.hasNext();)
                        {
                            LElement child = (LElement) itInclude.next();
                            assert child.getName().equals(Gui4jComponentManager.ELEMENT_Gui4jArg);
                            String paramId = child.attributeValue(Gui4jComponentManager.FIELD_Gui4jArgParam);
                            String id = child.attributeValue(Gui4jComponentManager.FIELD_Gui4jArgId);
                            assert paramId != null;
                            assert id != null;
                            paramMap.put(paramId, id);

                        }
                        String alias = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jIncludeAlias);
                        String includeNameRel = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jIncludeURL);
                        String baseController = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jIncludeController);
                        String refreshCall = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jIncludeRefresh);
                        // try
                        {
                            String resourceIncludeName = getResourceNameFullyQuantified(mResourceNameFullyQualified,
                                    includeNameRel);
                            ImportInfo importInfo = new ImportInfo();
                            importInfo.paramMap = paramMap;
                            importInfo.alias = alias;
                            importInfo.controller = baseController;
                            importInfo.refresh = refreshCall;
                            importInfo.relName = includeNameRel;
                            importInfo.urlLineNo = getLineNumber(e
                                    .attribute(Gui4jComponentManager.FIELD_Gui4jIncludeURL));
                            importInfo.controllerLineNo = getLineNumber(e
                                    .attribute(Gui4jComponentManager.FIELD_Gui4jIncludeController));
                            importInfo.refreshLineNo = getLineNumber(e
                                    .attribute(Gui4jComponentManager.FIELD_Gui4jIncludeRefresh));
                            mGui4jImportList.add(importInfo);
                            Resource importResource = mGui4j.getGui4jComponentContainerManager().getResource(
                                    resourceIncludeName);
                            mStyleContainer.extendBy(importResource.getGui4jStyleContainer());
                            Map importClassAliasMap = importResource.getClassAliasMap();
                            mErrorList.addAll(importResource.getErrorList());
                            Map importClassAliasMapResourceName = importResource.getClassAliasMapResourceName();
                            for (Iterator it2 = importClassAliasMap.entrySet().iterator(); it2.hasNext();)
                            {
                                Map.Entry entry = (Map.Entry) it2.next();
                                String aliasName = (String) entry.getKey();
                                insertAlias((String) importClassAliasMapResourceName.get(aliasName), aliasName,
                                        (Class) entry.getValue());
                            }

                            Map importDefMap = importResource.getDefMap();
                            Map importDefMapResourceName = importResource.getDefMapResourceName();
                            for (Iterator it2 = importDefMap.entrySet().iterator(); it2.hasNext();)
                            {
                                Map.Entry entry = (Map.Entry) it2.next();
                                String defName = (String) entry.getKey();
                                insertDef((String) importDefMapResourceName.get(defName), defName, (String) entry
                                        .getValue());
                            }
                        }
                        /*
                         * // XXX catch (MalformedURLException me) { Object[]
                         * args = { urlRel }; throw new
                         * Gui4jUncheckedException.ResourceError(
                         * mConfigurationSource, getLineLineNumber(e),
                         * RESOURCE_ERROR_invalid_url, args, me); }
                         */

                        it.remove();
                    }
                    if (name.equals(Gui4jComponentManager.ELEMENT_Gui4jClassAlias))
                    {
                        String aliasName = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jClassAliasName);
                        String className = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jClassAliasClass);
                        try
                        {
                            Class c = Class.forName(className);
                            insertAlias(mResourceNameFullyQualified, aliasName, c);
                        }
                        catch (ClassNotFoundException exc)
                        {
                            Object[] args = { aliasName, className };
                            throw new Gui4jUncheckedException.ResourceError(mResourceNameFullyQualified, e
                                    .getLineNumber(), RESOURCE_ERROR_alias_class_not_found, args, exc);
                        }
                        it.remove();
                    }
                    if (name.equals(Gui4jComponentManager.ELEMENT_Gui4jDef))
                    {
                        String defName = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jDefName);
                        String defValue = e.attributeValue(Gui4jComponentManager.FIELD_Gui4jDefValue);
                        insertDef(mResourceNameFullyQualified, defName, defValue);
                        it.remove();
                    }
                }
                catch (Throwable t)
                {
                    mErrorList.add(t);
                }
            }
        }

        private void insertGui4jStyle(String styleName, LElement e)
        {
            if (e.attributeValue(Gui4jComponentFactory.FIELD_Style) == null)
            {
                e.addAttribute(Gui4jComponentFactory.FIELD_Style, styleName);
            }
            for (Iterator it = e.elementIterator(); it.hasNext();)
            {
                LElement child = (LElement) it.next();
                insertGui4jStyle(styleName, child);
            }
        }

        private void insertDef(String resourceName, String name, String value)
        {
            if (mDefMap.containsKey(name) && !resourceName.equals(mDefMapResourceName.get(name)))
            {
                Gui4jComponentContainerManager.mLogger.warn("macro '" + name + "' already defined with value '"
                        + mDefMap.get(name) + "'");
            }
            else
            {
                mDefMap.put(name, value);
                mDefMapResourceName.put(name, resourceName);
            }
        }

        private void insertAlias(String resourceName, String aliasName, Class c)
        {
            if (mClassAliasMap.containsKey(aliasName)
                    && !resourceName.equals(mClassAliasMapResourceName.get(aliasName)))
            {
                Object[] args = { aliasName };
                throw new Gui4jUncheckedException.ResourceError(mResourceNameFullyQualified, -1,
                        RESOURCE_ERROR_alias_already_defined, args);
            }
            mClassAliasMap.put(aliasName, c);
            mClassAliasMapResourceName.put(aliasName, resourceName);
        }

        public Gui4jStyleContainer getGui4jStyleContainer()
        {
            return mStyleContainer;
        }

        public String toString()
        {
            return mResourceNameFullyQualified;
        }

        public ImportInfo[] getImportList()
        {
            ImportInfo[] info = new ImportInfo[mGui4jImportList.size()];
            int i = 0;
            for (Iterator it = mGui4jImportList.iterator(); it.hasNext();)
            {
                ImportInfo importInfo = (ImportInfo) it.next();
                info[i] = importInfo;
                i++;
            }
            return info;
        }

        public Set getParamIds()
        {
            return mParamIds;
        }

        Map getClassAliasMap()
        {
            return mClassAliasMap;
        }

        Map getClassAliasMapResourceName()
        {
            return mClassAliasMapResourceName;
        }

        Map getDefMap()
        {
            return mDefMap;
        }

        Map getDefMapResourceName()
        {
            return mDefMapResourceName;
        }

    }

    static class Gui4jComponentContainerImpl implements Serializable, Gui4jComponentContainer
    {
        private final Class mGui4jControllerClass;
        private final Map mElements; // String -> Element
        private final Map mGui4jComponentsInPath; // Id ->
        // Gui4jComponentInPath
        private final Gui4jInternal mGui4j;
        private final Gui4jStyleContainer mGui4jStyleContainer;
        private final Set mParamIds;
        private final Map mParamInstantiation;
        private final Map mToplevelAttributes;
        private final Gui4jCall mTitleCall;
        private final Gui4jCall mWindowNameCall;

        /**
         * // List(Gui4jComponentContainerInclude)
         */
        private final List mGui4jComponentContainerImports;

        // private final Map mGui4jComponentContainerMap;
        private final Map mClassAliasMap;
        private final Map mDefMap;
        private final String mRessourceNameFullyQualified;
        private final List mErrorList;

        private final static Log mLoggerComponentContainer = LogFactory.getLog(Gui4jComponentContainerImpl.class);

        protected Gui4jComponentContainerImpl(Gui4jInternal gui4j, Class gui4jControllerClass,
                String resourceNameFullyQuantified, Resource resource)
        {
            mGui4j = gui4j;
            mGui4jControllerClass = gui4jControllerClass;
            mElements = new HashMap();
            mGui4jComponentsInPath = new HashMap();
            mGui4jStyleContainer = resource.getGui4jStyleContainer();
            mGui4jComponentContainerImports = new ArrayList();
            // mGui4jComponentContainerMap = new HashMap();
            mClassAliasMap = resource.getClassAliasMap();
            mDefMap = resource.getDefMap();
            mRessourceNameFullyQualified = resourceNameFullyQuantified;
            mParamIds = resource.getParamIds();
            mToplevelAttributes = resource.getToplevelAttributes();
            mErrorList = new ArrayList(resource.getErrorList());
            String titleCallStr = getToplevelAttrValue(Gui4jComponentManager.FIELD_Gui4jViewTitle);
            String windowNameCallStr = getToplevelAttrValue(Gui4jComponentManager.FIELD_Gui4jViewName);

            Gui4jCallFactory parser = gui4j.createCallFactory();
            Gui4jAbstractComponent dummyComponent = new Gui4jAbstractComponent(this, Object.class, "") {
                protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
                        Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
                {
                    assert false;
                    return null;
                }

                public final void showPopupMenu(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent)
                {

                }

            };

            if (titleCallStr != null)
            {
                mTitleCall = parser.getInstance(dummyComponent, resource.getLineNoOfTopLevelAttrs(), titleCallStr);
                Gui4jTypeCheck.ensureType(String.class, mTitleCall, titleCallStr);
            }
            else
            {
                mTitleCall = null;
            }
            if (windowNameCallStr != null)
            {
                mWindowNameCall = parser.getInstance(dummyComponent, resource.getLineNoOfTopLevelAttrs(), windowNameCallStr);
                Gui4jTypeCheck.ensureType(String.class, mWindowNameCall, windowNameCallStr);
            }
            else
            {
                mWindowNameCall = null;
            }

            for (Iterator it = mParamIds.iterator(); it.hasNext();)
            {
                String id = (String) it.next();
                Gui4jComponentPath gui4jComponentPath = new Gui4jComponentPath();
                Gui4jComponent gui4jComponent = new Gui4jComponentArg(getGui4j(), id);
                mGui4jComponentsInPath.put(id, new Gui4jQualifiedComponent(gui4jComponentPath, gui4jComponent));
            }

            // add imported components
            ImportInfo[] importInfo = resource.getImportList();
            for (int i = 0; i < importInfo.length; i++)
            {
                try
                {
                    Class baseController = mGui4jControllerClass;
                    Gui4jCall call = null;
                    Gui4jCall[] refresh = null;
                    if (importInfo[i].controller != null)
                    {
                        // Anderer Base-Controller definiert
                        Gui4jCall refreshCall = parser.getInstance(dummyComponent, importInfo[i].refreshLineNo,
                                importInfo[i].refresh);
                        if (refreshCall != null)
                        {
                            refresh = refreshCall.getDependantProperties();
                            if (refresh == null || refresh.length == 0)
                            {
                                mLoggerComponentContainer.warn("Set of dependant events is empty");
                            }
                        }
                        call = parser.getInstance(dummyComponent, importInfo[i].controllerLineNo, baseController,
                                importInfo[i].controller);
                        if (call != null)
                        {
                            baseController = call.getResultClass();
                        }
                    }
                    Gui4jComponentContainer gui4jComponentContainer = mGui4j.getGui4jComponentContainerManager()
                            .getGui4jComponentContainer(baseController,
                                    getResourceNameFullyQuantified(resourceNameFullyQuantified, importInfo[i].relName));
                    for (Iterator it = gui4jComponentContainer.getParamIds().iterator(); it.hasNext();)
                    {
                        String paramId = (String) it.next();
                        if (!importInfo[i].paramMap.containsKey(paramId))
                        {
                            Object[] args = { paramId, gui4jComponentContainer.getConfigurationName() };
                            throw new Gui4jUncheckedException.ResourceError(mRessourceNameFullyQualified, -1,
                                    RESOURCE_ERROR_unknown_param, args);
                        }
                    }
                    for (Iterator it = importInfo[i].paramMap.keySet().iterator(); it.hasNext();)
                    {
                        String paramId = (String) it.next();
                        if (!gui4jComponentContainer.getParamIds().contains(paramId))
                        {
                            Object[] args = { paramId, gui4jComponentContainer.getConfigurationName() };
                            throw new Gui4jUncheckedException.ResourceError(mRessourceNameFullyQualified, -1,
                                    RESOURCE_ERROR_unknown_param_in_include, args);
                        }
                    }
                    Gui4jComponentContainerInclude gui4jComponentContainerInclude = new Gui4jComponentContainerInclude(
                            this, gui4jComponentContainer, importInfo[i].paramMap, importInfo[i].alias);
                    gui4jComponentContainerInclude.setGui4jBaseController(call);
                    gui4jComponentContainerInclude.setRefresh(refresh);
                    mGui4jComponentContainerImports.add(gui4jComponentContainerInclude);
                }
                catch (Throwable t)
                {
                    mErrorList.add(t);
                }

            }

            // add local components
            mLoggerComponentContainer.debug("Looping through top-level definition of resource "
                    + resourceNameFullyQuantified);
            for (Iterator it = resource.getToplevelDefinitions().iterator(); it.hasNext();)
            {
                try
                {
                    LElement e = (LElement) it.next();
                    String id = e.attributeValue(Gui4jComponentFactory.FIELD_Id);
                    if (id != null)
                    {
                        if (isDefined(id))
                        {
                            Object[] args = { id };
                            throw new Gui4jUncheckedException.ResourceError(mRessourceNameFullyQualified, e
                                    .getLineNumber(), RESOURCE_ERROR_gui4jComponent_already_defined, args);
                        }
                        else
                        {
                            mElements.put(id, e);
                        }
                    }
                }
                catch (Throwable t)
                {
                    mErrorList.add(t);
                }
            }

            mParamInstantiation = computeParamInstantiation();

            if (mErrorList.size() > 0)
            {
                throw new Gui4jUncheckedException.ErrorList(mErrorList);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.gui4j.core.Gui4jComponentContainer#getTitleCall()
         */
        public Gui4jCall getTitleCall()
        {
            return mTitleCall;
        }

        
        /* (non-Javadoc)
         * @see org.gui4j.core.Gui4jComponentContainer#getWindowNameCall()
         */
        public Gui4jCall getWindowNameCall()
        {
            return mWindowNameCall;
        }

        public Gui4jInternal getGui4j()
        {
            return mGui4j;
        }

        public Set getParamIds()
        {
            return mParamIds;
        }

        public Set getMappedIds()
        {
            Set ids = new HashSet();
            for (Iterator it = mGui4jComponentContainerImports.iterator(); it.hasNext();)
            {
                Gui4jComponentContainerInclude include = (Gui4jComponentContainerInclude) it.next();
                ids.addAll(include.getParamMap().values());
            }
            return ids;
        }

        public Map getParamInstantiation()
        {
            return mParamInstantiation;
        }

        private Map computeParamInstantiation()
        {
            Map m = new HashMap();
            for (Iterator it = mGui4jComponentContainerImports.iterator(); it.hasNext();)
            {
                Gui4jComponentContainerInclude include = (Gui4jComponentContainerInclude) it.next();
                Gui4jComponentPath path = new Gui4jComponentPath(include);
                // modifiziere include Instanziierung
                for (Iterator itInclude = include.getGui4jComponentContainer().getParamInstantiation().entrySet()
                        .iterator(); itInclude.hasNext();)
                {
                    Map.Entry entry = (Map.Entry) itInclude.next();
                    Gui4jQualifiedComponent param = (Gui4jQualifiedComponent) entry.getKey();
                    Gui4jQualifiedComponent id = (Gui4jQualifiedComponent) entry.getValue();
                    m.put(new Gui4jQualifiedComponent(path, param), new Gui4jQualifiedComponent(path, id));
                }
                for (Iterator itInclude = include.getParamMap().entrySet().iterator(); itInclude.hasNext();)
                {
                    Map.Entry entry = (Map.Entry) itInclude.next();
                    String param = (String) entry.getKey();
                    String id = (String) entry.getValue();
                    m.put(new Gui4jQualifiedComponent(path, include.getGui4jComponentContainer()
                            .getGui4jQualifiedComponent(param)), getGui4jQualifiedComponent(id));
                }
            }
            return m;
        }

        public Class getClassForAliasName(String aliasName)
        {
            return (Class) mClassAliasMap.get(aliasName);
        }

        public synchronized Gui4jQualifiedComponent extractGui4jComponent(LElement e)
        {
            String name = e.getName();
            if (name.equals(Gui4jComponentManager.ELEMENT_Gui4jRef))
            {
                return getGui4jQualifiedComponent(e.attributeValue(Gui4jComponentFactory.FIELDGui4jRef_Id));
            }
            else
            {
                autoExtend(e);
                String id = e.attributeValue(Gui4jComponentFactory.FIELD_Id);
                Gui4jComponentFactory factory = mGui4j.getGui4jComponentManager().getGui4jComponentFactory(name);
                Gui4jComponent gui4jComponent = factory.defineBy(this, id, e);
                Gui4jQualifiedComponent gui4jComponentInPath = new Gui4jQualifiedComponent(new Gui4jComponentPath(),
                        gui4jComponent);
                if (id != null)
                {
                    if (isDefined(id))
                    {
                        Object[] args = { id };
                        throw new Gui4jUncheckedException.ResourceError(mRessourceNameFullyQualified,
                                e.getLineNumber(), RESOURCE_ERROR_gui4jComponent_already_defined, args);
                    }
                    else
                    {
                        mGui4jComponentsInPath.put(id, gui4jComponentInPath);
                    }
                }
                return gui4jComponentInPath;
            }
        }

        public void autoExtend(LElement e)
        {
            mGui4jStyleContainer.extend(e);
        }

        public String getAttrValueReplaceAll(LElement e, String field)
        {
            String str = e.attributeValue(field);
            if (str != null && str.length() > 0 && str.indexOf('$') != -1)
            {
                for (Iterator it = mDefMap.entrySet().iterator(); it.hasNext();)
                {
                    Map.Entry entry = (Map.Entry) it.next();
                    String macroName = (String) entry.getKey();
                    String macroValue = (String) entry.getValue();
                    str = str.replaceAll("\\$" + macroName, macroValue);
                }
            }
            return str;
        }

        public String getAttrValue(LElement e, String field)
        {
            String str = e.attributeValue(field);
            if (str != null && str.length() > 0 && str.charAt(0) == '$')
            {
                String value = (String) mDefMap.get(str.substring(1));
                if (value != null)
                {
                    return value;
                }
                else
                {
                    mLoggerComponentContainer.warn("Cannot find definition for '" + str.substring(1) + "'");
                }
            }
            return str;
        }

        public boolean getBooleanAttrValue(LElement e, String field, boolean dflt)
        {
            String value = getAttrValue(e, field);
            if (value == null)
            {
                return dflt;
            }
            return "true".equalsIgnoreCase(value);
        }

        public synchronized Gui4jQualifiedComponent getGui4jQualifiedComponent(String id)
        {
            Gui4jQualifiedComponent gui4jComponentInPath = (Gui4jQualifiedComponent) mGui4jComponentsInPath.get(id);
            if (gui4jComponentInPath == null)
            {
                LElement e = (LElement) mElements.get(id);
                if (e == null)
                {
                    int idxSep = id.indexOf('/');
                    if (idxSep != -1)
                    {
                        String prefix = id.substring(0, idxSep);
                        String suffix = id.substring(idxSep + 1);
                        // look in imported resources
                        for (Iterator it = mGui4jComponentContainerImports.iterator(); it.hasNext();)
                        {
                            Gui4jComponentContainerInclude gui4jComponentContainerInclude = (Gui4jComponentContainerInclude) it
                                    .next();
                            if (gui4jComponentContainerInclude.getAliasName().equals(prefix))
                            {
                                Gui4jComponentContainer gui4jComponentContainer = gui4jComponentContainerInclude
                                        .getGui4jComponentContainer();
                                if (gui4jComponentContainer.isDefined(suffix))
                                {
                                    Gui4jQualifiedComponent path = gui4jComponentContainer
                                            .getGui4jQualifiedComponent(suffix);
                                    return new Gui4jQualifiedComponent(gui4jComponentContainerInclude, path);
                                }
                            }
                        }
                    }

                    Object[] args = { id };
                    throw new Gui4jUncheckedException.ResourceError(mRessourceNameFullyQualified, -1,
                            RESOURCE_ERROR_gui4jComponent_not_defined, args);
                }

                String name = e.getName();
                Gui4jComponentFactory factory = mGui4j.getGui4jComponentManager().getGui4jComponentFactory(name);
                mGui4jStyleContainer.extend(e);
                Gui4jComponent gui4jComponent = factory.defineBy(this, id, e);
                if (gui4jComponent == null)
                {
                    throw new Gui4jUncheckedException.ProgrammingError(PROGRAMMING_ERROR_parameter_null);
                }
                else
                {
                    Gui4jQualifiedComponent path = new Gui4jQualifiedComponent(new Gui4jComponentPath(), gui4jComponent);
                    mGui4jComponentsInPath.put(id, path);

                    // Remove JDOM tree entry for 'id', because it is no longer
                    // needed for this swingContainerClass type
                    mElements.remove(id);
                    return path;
                }
            }
            else
            {
                return gui4jComponentInPath;
            }
        }

        public Class getGui4jControllerClass()
        {
            return mGui4jControllerClass;
        }

        public boolean isDefined(String id)
        {
            assert id != null;
            Gui4jQualifiedComponent gui4jComponentInPath = (Gui4jQualifiedComponent) mGui4jComponentsInPath.get(id);
            boolean found = gui4jComponentInPath != null;
            if (!found)
            {
                LElement e = (LElement) mElements.get(id);
                found = e != null;
            }

            int idxSep = id.indexOf('/');
            if (idxSep != -1)
            {
                String prefix = id.substring(0, idxSep);
                String suffix = id.substring(idxSep + 1);
                // look in imported containers
                for (Iterator it = mGui4jComponentContainerImports.iterator(); !found && it.hasNext();)
                {
                    Gui4jComponentContainerInclude gui4jComponentContainerInclude = (Gui4jComponentContainerInclude) it
                            .next();
                    if (gui4jComponentContainerInclude.getAliasName().equals(prefix))
                    {
                        found = gui4jComponentContainerInclude.getGui4jComponentContainer().isDefined(suffix);
                    }
                }
            }
            return found;
        }

        public String getConfigurationName()
        {
            return mRessourceNameFullyQualified;
        }

        /*
         * public List
         * getGui4jComponentContainerIncludePath(Gui4jComponentContainer
         * gui4jComponentContainer) { if
         * (mGui4jComponentContainerMap.containsKey(gui4jComponentContainer)) {
         * return (List)
         * mGui4jComponentContainerMap.get(gui4jComponentContainer); }
         * 
         * if (this == gui4jComponentContainer) { List path = new ArrayList();
         * mGui4jComponentContainerMap.put(gui4jComponentContainer, path);
         * return path; } else { // look in imported containers for (Iterator it =
         * mGui4jComponentContainerImports.iterator(); it.hasNext();) {
         * Gui4jComponentContainerInclude gui4jComponentContainerInclude =
         * (Gui4jComponentContainerInclude) it.next(); List path =
         * gui4jComponentContainerInclude .getGui4jComponentContainer()
         * .getGui4jComponentContainerIncludePath( gui4jComponentContainer); if
         * (path != null) { path = new ArrayList(path); path.add(0,
         * gui4jComponentContainerInclude);
         * mGui4jComponentContainerMap.put(gui4jComponentContainer, path);
         * return path; } }
         * mGui4jComponentContainerMap.put(gui4jComponentContainer, null);
         * return null; } }
         */

        /**
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return Extract.getClassname(getClass()) + "[" + getConfigurationName() + "]";
        }

        public String getToplevelAttrValue(String field)
        {
            return (String) mToplevelAttributes.get(field);
        }

    }

    public static int getLineNumber(Attribute attribute)
    {
        if (attribute == null)
        {
            return -1;
        }
        return getLineNumber(attribute.getParent());
    }

    public static int getLineNumber(Element element)
    {
        if (element == null)
        {
            return -1;
        }
        return ((LElement) element).getLineNumber();
    }

}
