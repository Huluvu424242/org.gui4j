package org.gui4j.core;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jController;
import org.gui4j.Gui4jDispose;
import org.gui4j.event.Gui4jEvent;
import org.gui4j.event.Gui4jEventListener;

/**
 * Stores the Swing instances and the relation between Swing instance and
 * Gui4jComponent
 */
public abstract class Gui4jSwingContainer
{
    private final Gui4jInternal mGui4j;
    private final String mViewResourceName;
    private final String mViewResourceNameFullyQuantified;
    private Gui4jComponentContainer mGui4jComponentContainer;
    private final List mDispose;
    protected boolean inClosing;

    /**
     * Gui4jQualifiedComponent -> Gui4jComponentInstance
     */
    private final Map mGui4jComponentInstanceMap;

    /**
     * List(Gui4jComponentInstance)
     */
    private final List mGui4jComponentInstances;

    /**
     * List(Gui4jComponentInstanceProperty)
     */
    private final List mGui4jComponentInstancePropertyList;

    private Gui4jController mGui4jController;

    /**
     * Class -> (Object -> Object)
     */
    private final Map mStorageMap;

    private final List mDeleteListeners;

    private final boolean mReadOnlyMode;

    private final Map mGui4jCallBaseMap; // List(Gui4jComponentContainerInclude)
                                            // -> Gui4jCallBase

    /**
     * String(QualifiedPath) -> Gui4jComponentContainerIncludeInstance
     */
    private final Map mGui4jComponentContainerIncludeInstanceMap;

    public Gui4jSwingContainer(Gui4jInternal gui4j, String viewResourceName, Gui4jController gui4jController,
            boolean readOnlyMode)
    {
        mGui4j = gui4j;
        mReadOnlyMode = readOnlyMode;
        mViewResourceName = viewResourceName;
        mDispose = new ArrayList();
        mGui4jController = gui4jController;
        Class c = gui4jController.getClass();
        mViewResourceNameFullyQuantified = Gui4jComponentContainerManager.getResourceNameFullyQuantified(
                Gui4jComponentContainerManager.getBaseName(c), mViewResourceName);
        mGui4jComponentInstanceMap = new HashMap();
        mGui4jComponentInstances = new ArrayList();
        mGui4jComponentInstancePropertyList = new ArrayList();
        mGui4jCallBaseMap = new HashMap();
        mStorageMap = new HashMap();
        mDeleteListeners = new ArrayList();
        mGui4jComponentContainerIncludeInstanceMap = new HashMap();
        try
        {
            mGui4jComponentContainer = gui4j.getGui4jComponentContainerManager().getGui4jComponentContainer(c,
                    mViewResourceNameFullyQuantified);
        }
        catch (Throwable e)
        {
            getGui4j().handleException(gui4jController, e, null);
        }
    }

    public boolean isInClosing()
    {
        return inClosing;
    }

    public abstract void setBusy(boolean busy);

    private Map getClassStorage(Class clazz)
    {
        Map classStorage = (Map) mStorageMap.get(clazz);
        if (classStorage == null)
        {
            classStorage = new HashMap();
            mStorageMap.put(clazz, classStorage);
        }
        return classStorage;
    }

    /**
     * Method getGui4jComponentContainerIncludeInstance.
     * 
     * @param aliasPath
     *            List(Gui4jComponentContainerInclude)
     * @param completePath
     * @param depth
     * @return Gui4jComponentContainerIncludeInstance
     */
    public Gui4jComponentContainerIncludeInstance getGui4jComponentContainerIncludeInstance(String aliasPath,
            Gui4jComponentPath completePath, int depth)
    {
        assert aliasPath != null;
        Gui4jComponentContainerIncludeInstance gui4jComponentContainerInstance = (Gui4jComponentContainerIncludeInstance) mGui4jComponentContainerIncludeInstanceMap
                .get(aliasPath);
        if (gui4jComponentContainerInstance == null)
        {
            List l = new ArrayList();
            completePath.collectInclude(l);
            List lParent = l.subList(0, depth - 1);
            l = l.subList(0, depth);
            assert l.size() == depth;
            Gui4jComponentPath path = Gui4jComponentPath.getInstance(l);
            Gui4jComponentPath pathParent = Gui4jComponentPath.getInstance(lParent);
            gui4jComponentContainerInstance = new Gui4jComponentContainerIncludeInstance(getGui4jCallBase(pathParent),
                    path);
            mGui4jComponentContainerIncludeInstanceMap.put(aliasPath, gui4jComponentContainerInstance);
        }
        return gui4jComponentContainerInstance;
    }

    public boolean inReadOnlyMode()
    {
        return mReadOnlyMode;
    }

    public boolean isDefined(String id)
    {
        return mGui4jComponentContainer.isDefined(id);
    }

    public String getToplevelAttrValue(String field)
    {
        return mGui4jComponentContainer.getToplevelAttrValue(field);
    }

    public Gui4jCall getTitleCall()
    {
        return mGui4jComponentContainer.getTitleCall();
    }

    public Gui4jCall getWindowNameCall()
    {
        return mGui4jComponentContainer.getWindowNameCall();
    }

    public Object getStorage(Class clazz, Object key)
    {
        return getClassStorage(clazz).get(key);
    }

    public void putStorage(Class clazz, Object key, Object value)
    {
        getClassStorage(clazz).put(key, value);
    }

    public Gui4jController getGui4jController()
    {
        return mGui4jController;
    }

    /**
     * Method getGui4jCallBase.
     * 
     * @param path
     *            List(Gui4jComponentContainerInclude)
     * @return Gui4jCallBase
     */
    private Gui4jCallBase getGui4jCallBase(Gui4jComponentPath path)
    {
        Gui4jCallBase base = (Gui4jCallBase) mGui4jCallBaseMap.get(path);
        if (base != null)
        {
            return base;
        }
        base = getGui4jController();
        while (path != null && path.getInclude() != null)
        {
            Gui4jComponentContainerInclude gui4jComponentContainerInclude = path.getInclude();
            Gui4jCall call = gui4jComponentContainerInclude.getGui4jBaseController();
            if (call != null)
            {
                base = (Gui4jCallBase) call.getValueUseDefaultParam(base, base, null);
            }
            path = path.getSon();
        }
        mGui4jCallBaseMap.put(path, base);
        return base;
    }

    public Gui4jCallBase getGui4jCallBase(Gui4jQualifiedComponent gui4jComponentInPath)
    {
        return getGui4jCallBase(gui4jComponentInPath.getGui4jComponentPath());
    }

    public Gui4jComponentInstance getGui4jComponentInstance(Gui4jComponentPath context, Gui4jQualifiedComponent path)
    {
        return getGui4jComponentInstance(new Gui4jQualifiedComponent(context, path));
    }

    public Gui4jComponentInstance getGui4jComponentInstance(Gui4jQualifiedComponent gui4jComponentInPath)
    {
        Gui4jComponentInstance gui4jComponentInstance = (Gui4jComponentInstance) mGui4jComponentInstanceMap
                .get(gui4jComponentInPath);
        if (gui4jComponentInstance != null)
        {
            return gui4jComponentInstance;
        }
        else
        {
            if (gui4jComponentInPath.getGui4jComponent() instanceof Gui4jComponentArg)
            {
                Gui4jQualifiedComponent qualifiedComponent = (Gui4jQualifiedComponent) mGui4jComponentContainer
                        .getParamInstantiation().get(gui4jComponentInPath);
                assert qualifiedComponent != null;
                gui4jComponentInstance = qualifiedComponent.getGui4jComponent().createGui4jComponentInstance(this,
                        getGui4jCallBase(qualifiedComponent), qualifiedComponent);
                addGui4jComponentInstance(gui4jComponentInstance);
                return gui4jComponentInstance;
            }
            else
            {
                gui4jComponentInstance = gui4jComponentInPath.getGui4jComponent().createGui4jComponentInstance(this,
                        getGui4jCallBase(gui4jComponentInPath), gui4jComponentInPath);
                addGui4jComponentInstance(gui4jComponentInstance);
                return gui4jComponentInstance;
            }
        }
    }

    private void addGui4jComponentInstance(Gui4jComponentInstance gui4jComponentInstance)
    {
        Gui4jQualifiedComponent gui4jComponentInPath = gui4jComponentInstance.getGui4jComponentInPath();
        assert gui4jComponentInPath != null;
        List gui4jComponentContainerInstances = new ArrayList();
        Gui4jComponentPath completePath = gui4jComponentInPath.getGui4jComponentPath();
        Gui4jComponentPath path = completePath;
        String aliasPath = "";
        int depth = 0;
        while (path != null && path.getInclude() != null)
        {
            depth++;
            aliasPath = aliasPath + "/" + path.getInclude().getAliasName();
            Gui4jComponentContainerIncludeInstance gui4jComponentContainerInstance = getGui4jComponentContainerIncludeInstance(
                    aliasPath, completePath, depth);
            if (gui4jComponentContainerInstance.mGui4jComponentContainerInclude.getRefresh() != null)
            {
                gui4jComponentContainerInstances.add(gui4jComponentContainerInstance);
                gui4jComponentContainerInstance.add(gui4jComponentInstance);
            }
            path = path.getSon();
        }
        if (gui4jComponentInPath.hasId())
        {
            mGui4jComponentInstanceMap.put(gui4jComponentInPath, gui4jComponentInstance);
        }
        mGui4jComponentInstances.add(gui4jComponentInstance);
        Gui4jComponent gui4jComponent = gui4jComponentInstance.getGui4jComponent();
        Gui4jComponentProperty[] gui4jComponentProperties = gui4jComponent.getGui4jComponentProperties();
        if (gui4jComponentProperties != null)
        {
            for (int i = 0; i < gui4jComponentProperties.length; i++)
            {
                Gui4jComponentProperty gui4jComponentProperty = gui4jComponentProperties[i];
                if (gui4jComponentProperty.getGui4jAccess().hasTriggerEvents())
                {
                    Gui4jComponentInstanceProperty gui4jComponentInstanceProperty = new Gui4jComponentInstanceProperty(
                            gui4jComponentInstance, gui4jComponentProperty);
                    mGui4jComponentInstancePropertyList.add(gui4jComponentInstanceProperty);
                    for (Iterator it = gui4jComponentContainerInstances.iterator(); it.hasNext();)
                    {
                        Gui4jComponentContainerIncludeInstance gui4jComponentContainerInstance = (Gui4jComponentContainerIncludeInstance) it
                                .next();
                        gui4jComponentContainerInstance.add(gui4jComponentInstanceProperty);
                    }
                }
            }
        }
    }

    public Gui4jComponentInstance getGui4jComponentInstance(String id)
    {
        Gui4jQualifiedComponent gui4jComponentInPath = mGui4jComponentContainer.getGui4jQualifiedComponent(id);
        return getGui4jComponentInstance(gui4jComponentInPath);
    }

    /**
     * Returns the JComponent instance for the given id. If there is no
     * component for that id, a new instance is created.
     * 
     * @param id
     * @return Component
     */
    protected Component getComponent(String id)
    {
        Gui4jComponentInstance gui4jComponentInstance = getGui4jComponentInstance(id);
        return gui4jComponentInstance.getComponent();
    }

    public void addDispose(Gui4jDispose clearListener)
    {
        mDispose.add(clearListener);
    }

    public synchronized void dispose()
    {
        for (Iterator it = mDispose.iterator(); it.hasNext();)
        {
            Gui4jDispose disposeElement = (Gui4jDispose) it.next();
            disposeElement.dispose();
        }
        mDispose.clear();
        for (Iterator it = mDeleteListeners.iterator(); it.hasNext();)
        {
            Gui4jPropertyListenerDelete elem = (Gui4jPropertyListenerDelete) it.next();
            elem.removeListener();
        }
        mDeleteListeners.clear();
        mGui4jCallBaseMap.clear();

        mGui4jComponentContainerIncludeInstanceMap.clear();
        for (Iterator it = mGui4jComponentContainerIncludeInstanceMap.values().iterator(); it.hasNext();)
        {
            Gui4jComponentContainerIncludeInstance instance = (Gui4jComponentContainerIncludeInstance) it.next();
            instance.dispose();

        }
        mGui4jComponentInstanceMap.clear();

        for (Iterator it = mGui4jComponentInstances.iterator(); it.hasNext();)
        {
            Gui4jComponentInstance gui4jComponentInstance = (Gui4jComponentInstance) it.next();
            gui4jComponentInstance.dispose();
        }
        mGui4jComponentInstances.clear();
        for (Iterator it = mGui4jComponentInstancePropertyList.iterator(); it.hasNext();)
        {
            Gui4jComponentInstanceProperty gui4jComponentInstanceProperty = (Gui4jComponentInstanceProperty) it.next();
            gui4jComponentInstanceProperty.dispose();
        }
        mGui4jComponentInstancePropertyList.clear();
        mStorageMap.clear();
    }

    public void cleanUp()
    {
        mGui4jController = null;
    }

    public Gui4jInternal getGui4j()
    {
        return mGui4j;
    }

    public void registerForDeleteListenerOnGui4jProperty(Gui4jEvent gui4jProperty,
            Gui4jEventListener gui4jPropertyListener)
    {
        Gui4jPropertyListenerDelete gui4jPropertyListenerDelete = new Gui4jPropertyListenerDelete(gui4jProperty,
                gui4jPropertyListener);
        mDeleteListeners.add(gui4jPropertyListenerDelete);
    }

    /**
     * Erzwing alle Komponenten ihre Attribute neu auszuwerten. Ist gedacht um
     * die GUI zu aktualisieren. Im Moment sehr einfach implementiert. Kann
     * sein, dass diese Implementierung nicht ausreicht und etwa eine
     * refresh-Methode bei den einzelnen Komponenten aufgerufen werden muss.
     */
    public synchronized void refreshAll()
    {
        Gui4jThreadManager.executeInSwingThreadAndContinue(new Runnable() {
            public void run()
            {
                for (Iterator it = mGui4jComponentInstances.iterator(); it.hasNext();)
                {
                    Gui4jComponentInstance gui4jComponentInstance = (Gui4jComponentInstance) it.next();
                    gui4jComponentInstance.getGui4jComponent().refreshComponent(gui4jComponentInstance);
                }
                /*
                 * folgender Code scheint nicht notwendig zu sein for (Iterator
                 * it = mGui4jComponentInstancePropertyList.iterator();
                 * it.hasNext();) { Gui4jComponentInstanceProperty property =
                 * (Gui4jComponentInstanceProperty) it.next();
                 * property.eventOccured(); }
                 */
            }
        });
    }

    static private class Gui4jPropertyListenerDelete
    {
        private final Gui4jEvent mGui4jProperty;
        private final Gui4jEventListener mGui4jPropertyListener;

        Gui4jPropertyListenerDelete(Gui4jEvent gui4jProperty, Gui4jEventListener gui4jPropertyListener)
        {
            mGui4jProperty = gui4jProperty;
            mGui4jPropertyListener = gui4jPropertyListener;
        }

        void removeListener()
        {
            mGui4jProperty.removeEventListener(mGui4jPropertyListener);
        }
    }

    static private class Gui4jComponentInstanceProperty implements Gui4jEventListener
    {
        private final Gui4jComponentInstance mGui4jComponentInstance;
        private final Gui4jComponentProperty mGui4jComponentProperty;
        private final Gui4jCallBase mGui4jController;
        private final List mRegisteredProperties;

        Gui4jComponentInstanceProperty(Gui4jComponentInstance gui4jComponentInstance,
                Gui4jComponentProperty gui4jComponentProperty)
        {
            mGui4jComponentInstance = gui4jComponentInstance;
            mGui4jComponentProperty = gui4jComponentProperty;
            mGui4jController = gui4jComponentInstance.getGui4jCallBase();
            mRegisteredProperties = new ArrayList();
            Gui4jCall gui4jAccess = mGui4jComponentProperty.getGui4jAccess();
            Gui4jCall[] dependantProperties = gui4jAccess.getDependantProperties();
            if (dependantProperties != null)
            {
                for (int i = 0; i < dependantProperties.length; i++)
                {
                    Gui4jEvent gui4jProperty = (Gui4jEvent) dependantProperties[i].getValueNoParams(mGui4jController,
                            null);
                    if (gui4jProperty != null)
                    {
                        gui4jProperty.addEventListener(this);
                        mRegisteredProperties.add(gui4jProperty);
                    }
                }
            }
        }

        public void dispose()
        {
            for (Iterator it = mRegisteredProperties.iterator(); it.hasNext();)
            {
                Gui4jEvent gui4jProperty = (Gui4jEvent) it.next();
                gui4jProperty.removeEventListener(this);
            }
            mRegisteredProperties.clear();
        }

        public void eventOccured()
        {
            mGui4jComponentProperty.apply(mGui4jComponentInstance, mGui4jController, true);
        }

    }

    // *****************************************************************************

    /**
     * Instanzen von dieser Klasse sind einem Include-Pfad (ausgehend von der
     * Wurzel) zugeordnet.
     */
    private static final class Gui4jComponentContainerIncludeInstance implements Gui4jEventListener
    {
        protected final Gui4jComponentContainerInclude mGui4jComponentContainerInclude;
        private final List mPropertyList;
        private final List mGui4jComponentInstanceList;
        private final List mEventList; // List(Event)

        /**
         * Method Gui4jComponentContainerIncludeInstance.
         * 
         * @param gui4jCallBase
         * @param path
         *            List(Gui4jComponentContainerInclude)
         */
        public Gui4jComponentContainerIncludeInstance(Gui4jCallBase gui4jCallBase, Gui4jComponentPath path)
        {
            assert path != null && path.getInclude() != null;
            Gui4jComponentContainerInclude gui4jComponentContainerInclude = null;
            while (path != null && path.getInclude() != null)
            {
                gui4jComponentContainerInclude = path.getInclude();
                path = path.getSon();
            }
            Gui4jCall[] refresh = gui4jComponentContainerInclude.getRefresh();
            mGui4jComponentContainerInclude = gui4jComponentContainerInclude;
            mPropertyList = new ArrayList();
            mGui4jComponentInstanceList = new ArrayList();
            mEventList = new ArrayList();
            // füllen
            if (refresh != null)
            {
                for (int i = 0; i < refresh.length; i++)
                {
                    Gui4jEvent gui4jProperty = (Gui4jEvent) refresh[i].getValueNoParams(gui4jCallBase, null);
                    if (gui4jProperty != null)
                    {
                        mEventList.add(gui4jProperty);
                    }
                }
                for (Iterator it = mEventList.iterator(); it.hasNext();)
                {
                    Gui4jEvent event = (Gui4jEvent) it.next();
                    event.addEventListener(this);
                }
            }
        }

        public void dispose()
        {
            for (Iterator it = mEventList.iterator(); it.hasNext();)
            {
                Gui4jEvent event = (Gui4jEvent) it.next();
                event.removeEventListener(this);
            }
            mEventList.clear();
            mPropertyList.clear();
            mGui4jComponentInstanceList.clear();
        }

        public void add(Gui4jComponentInstance gui4jComponentInstance)
        {
            mGui4jComponentInstanceList.add(gui4jComponentInstance);
        }

        public void add(Gui4jComponentInstanceProperty property)
        {
            // sofern im Refresh die Property nicht gebraucht wird, brauchen
            // wir uns sie auch nicht merken.
        }

        public void refresh()
        {
        }

        public void eventOccured()
        {
            for (Iterator it = mGui4jComponentInstanceList.iterator(); it.hasNext();)
            {
                Gui4jComponentInstance gui4jComponentInstance = (Gui4jComponentInstance) it.next();
                gui4jComponentInstance.getGui4jComponent().refreshComponent(gui4jComponentInstance);
            }

            /*
             * // Kein Refresh der Properties for (Iterator it =
             * mPropertyList.iterator(); it.hasNext();) {
             * Gui4jComponentInstanceProperty property =
             * (Gui4jComponentInstanceProperty) it.next();
             * property.eventOccured(); }
             */
        }

    }

}
