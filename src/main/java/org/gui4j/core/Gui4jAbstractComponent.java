package org.gui4j.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.util.MethodCall;
import org.gui4j.event.Gui4jEvent;
import org.gui4j.event.Gui4jEventListener;
import org.gui4j.exception.ErrorTags;
import org.gui4j.exception.Gui4jUncheckedException;


/**
 * This class represents a base for all user-defined implementations
 * of Gui4jComponent. It implements feature which are available for
 * java.awt.Component.
 */
abstract public class Gui4jAbstractComponent implements ErrorTags, Gui4jComponent, Serializable
{
    private static final Log mLogger = LogFactory.getLog(Gui4jAbstractComponent.class);

    public static final String STORAGE_POPUP_CONTEXT = "context";
    public static final String STORAGE_POPUP_ORIGIN = "origin";

    private static final String ACTION_SHOW_POPUP = "gui4jShowPopup";

    private final String mId;
    private final Gui4jComponentContainer mGui4jComponentContainer;
    private final List mGui4jComponentPropertyList;
    private final Class mComponentClass;

    private Gui4jCall mOnClick;
    private Gui4jCall mApply;
    private Gui4jCall mOnInit;
    protected Gui4jCall mContext;
    protected Gui4jQualifiedComponent mPopupMenuInPath;

    /**
     * @param gui4jComponentContainer the container where this instance is stored
     * @param componentClass the Swing class which will be generated
     * @param id the identifier of this instance in the specified
     * <code>Gui4jComponentContainer</code> (might be <code>null</code>).
     */
    public Gui4jAbstractComponent(Gui4jComponentContainer gui4jComponentContainer, Class componentClass, String id)
    {
        mId = id;
        mGui4jComponentContainer = gui4jComponentContainer;
        mGui4jComponentPropertyList = new ArrayList();
        mComponentClass = componentClass;
    }

    /**
     * @return the Swing class which will be generated
     */
    public Class getComponentClass()
    {
        return mComponentClass;
    }

    /**
     * Initializes attributes for the generated swing instance
     * @param gui4jComponentInstance
     */
    protected void setProperties(Gui4jComponentInstance gui4jComponentInstance)
    {
        if (getId() != null)
        {
            gui4jComponentInstance.getComponent().setName(getId());
            // mLogger.debug("Setting component name for "+getId());
        }
        Gui4jMouseListener mouseListener = null;
        if (mOnClick != null || mPopupMenuInPath != null)
        {
            mouseListener = initMouseListener(gui4jComponentInstance, mouseListener);
            mouseListener.setOnClick(mOnClick);
            mouseListener.setPopup(mPopupMenuInPath != null);
        }
        if (mApply != null)
        {
            mApply.getValueUseDefaultParam(
                gui4jComponentInstance.getGui4jCallBase(),
                gui4jComponentInstance.getComponent(),
                null);
        }
    }

    private void extendActionMap(final Gui4jComponentInstance gui4jComponentInstance)
    {
        Component component = gui4jComponentInstance.getComponent();
        if (!(component instanceof JComponent))
        {
            return;
        }

        if (mPopupMenuInPath != null)
        {
            JComponent jComponent = (JComponent) component;
            ActionMap actionMap = jComponent.getActionMap();
            actionMap.put(ACTION_SHOW_POPUP, new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    showPopupMenu(gui4jComponentInstance, null);
                }
            });
        }
    }

    /**
     * Method registerEvents
     * @param gui4jSwingContainer
     * @param gui4jCallBase
     * @param eventList
     * @param listener
     */
    public void registerEvents(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jCall[] eventList,
        Gui4jEventListener listener)
    {
        if (eventList != null)
        {
            for (int i = 0; i < eventList.length; i++)
            {
                Gui4jEvent gui4jProperty = (Gui4jEvent) eventList[i].getValue(gui4jCallBase, null, null);
                if (gui4jProperty != null)
                {
                    gui4jProperty.addEventListener(listener);
                    gui4jSwingContainer.registerForDeleteListenerOnGui4jProperty(gui4jProperty, listener);
                }
            }
        }
    }

    /**
     * Method initMouseListener
     * @param gui4jComponentInstance
     * @param listener
     * @return Gui4jMouseListener
     */
    private Gui4jMouseListener initMouseListener(Gui4jComponentInstance gui4jComponentInstance, Gui4jMouseListener listener)
    {
        if (listener == null)
        {
            listener = createMouseListener(gui4jComponentInstance);
            gui4jComponentInstance.getComponent().addMouseListener(listener);
        }
        return listener;
    }

    /**
     * Method createMouseListener
     * @param gui4jComponentInstance
     * @return Gui4jMouseListener
     */
    protected Gui4jMouseListener createMouseListener(Gui4jComponentInstance gui4jComponentInstance)
    {
        return new Gui4jMouseListener(gui4jComponentInstance);
    }

    /**
     * Method addGui4jComponentProperty
     * @param gui4jComponentProperty
     */
    private void addGui4jComponentProperty(Gui4jComponentProperty gui4jComponentProperty)
    {
        mGui4jComponentPropertyList.add(gui4jComponentProperty);
    }

    public Gui4jComponentProperty[] getGui4jComponentProperties()
    {
        if (mGui4jComponentPropertyList.size() > 0)
        {
            Gui4jComponentProperty[] props = new Gui4jComponentProperty[mGui4jComponentPropertyList.size()];
            for (int i = 0; i < mGui4jComponentPropertyList.size(); i++)
            {
                props[i] = (Gui4jComponentProperty) mGui4jComponentPropertyList.get(i);
            }
            return props;
        }
        else
        {
            return null;
        }
    }

    /**
     * @return the <code>Gui4jComponentContainer</code> where this instance is stored
     */
    public Gui4jComponentContainer getGui4jComponentContainer()
    {
        return mGui4jComponentContainer;
    }

    /**
     * @return the <code>Gui4j</code> instance
     */
    public Gui4jInternal getGui4j()
    {
        return mGui4jComponentContainer.getGui4j();
    }

    /**
     * @return the name of the xml file where this instance is defined
     */
    public String getConfigurationName()
    {
        return mGui4jComponentContainer.getConfigurationName();
    }

    /**
     * @see org.gui4j.core.Gui4jComponent#getId()
     */
    public String getId()
    {
        return mId;
    }

    /**
     * Method definePropertySetter
     * @param propertyName
     * @param gui4jAccess
     */
    public void definePropertySetter(String propertyName, Gui4jCall gui4jAccess)
    {
        definePropertySetter(propertyName, gui4jAccess, false);
    }

    /**
     * Method definePropertySetter
     * @param propertyName
     * @param gui4jAccess
     * @param useContext
     */
    public void definePropertySetter(String propertyName, Gui4jCall gui4jAccess, boolean useContext)
    {
        if (gui4jAccess != null)
        {
            Class c = getClass();
            String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            try
            {
                boolean usesSwingComponent = false;
                MethodCall m = null;
                {
                    Class[] args = { Gui4jComponentInstance.class, gui4jAccess.getResultClass()};
                    String context = getGui4jComponentContainer().getConfigurationName();
                    m = getGui4j().getGui4jReflectionManager().getMethod(context, c, methodName, args, false);
                }
                if (m == null)
                {
                    usesSwingComponent = true;
                    Class[] args = { getComponentClass(), gui4jAccess.getResultClass()};
                    String context = getGui4jComponentContainer().getConfigurationName();
                    m = getGui4j().getGui4jReflectionManager().getMethod(context, c, methodName, args, true);
                }
                addGui4jComponentProperty(new Gui4jComponentPropertySetter(gui4jAccess, m, usesSwingComponent, useContext));
            }
            catch (Gui4jUncheckedException e)
            {
                mLogger.error(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Method definePropertyGetter
     * @param propertyName
     * @param gui4jAccess
     */
    public void definePropertyGetter(String propertyName, Gui4jCall gui4jAccess)
    {
        if (gui4jAccess != null)
        {
            Class c = getClass();
            String methodName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            try
            {
                boolean usesSwingComponent = false;
                MethodCall m = null;
                {
                    Class[] args = { Gui4jComponentInstance.class };
                    String context = getGui4jComponentContainer().getConfigurationName();
                    m = getGui4j().getGui4jReflectionManager().getMethod(context, c, methodName, args, false);
                }
                if (m == null)
                {
                    usesSwingComponent = true;
                    Class[] args = { getComponentClass()};
                    String context = getGui4jComponentContainer().getConfigurationName();
                    m = getGui4j().getGui4jReflectionManager().getMethod(context, c, methodName, args, true);
                }
                Class argumentType = gui4jAccess.getValueClass();
                Class resultType = m.getReturnType();
                if (resultType != null && argumentType != null && argumentType.isAssignableFrom(resultType))
                {
                    addGui4jComponentProperty(new Gui4jComponentPropertyGetter(gui4jAccess, m, usesSwingComponent));
                }
                else
                {
                    Object[] eArgs = { propertyName, resultType, argumentType };
                    throw new Gui4jUncheckedException.ResourceError(
                        getConfigurationName(),
                        -1,
                        RESOURCE_ERROR_property_getter_type_incompatible,
                        eArgs);
                }
            }
            catch (Gui4jUncheckedException e)
            {
                mLogger.error(e);
            }
        }
    }

    /**
     * Method applyInitialProperties
     * @param gui4jComponentInstance
     * @param gui4jController
     */
    protected void applyInitialProperties(Gui4jComponentInstance gui4jComponentInstance, Gui4jCallBase gui4jController, boolean handleThreads)
    {
        Component component = gui4jComponentInstance.getComponent();
        if (mOnInit != null)
        {
            Map m = new Gui4jMap1(Gui4jComponentFactory.CONTEXT, gui4jComponentInstance.getContext());
            mOnInit.getValue(gui4jComponentInstance.getGui4jCallBase(), m, null);
        }
        for (Iterator it = mGui4jComponentPropertyList.iterator(); it.hasNext();)
        {
            Gui4jComponentProperty property = (Gui4jComponentProperty) it.next();
            if (property.applyInitially())
            {
                if (property.usesSwingComponent())
                {
                    property.apply(gui4jComponentInstance, component, gui4jController, handleThreads);
                }
                else
                {
                    property.apply(gui4jComponentInstance, gui4jController, handleThreads);
                }
            }
        }
    }

    /**
     * Sets the background.
     * @param component
     * @param background The background to set
     */
    public void setBackground(Component component, Color background)
    {
        component.setBackground(background);
    }

    /**
     * Sets the cursor.
     * @param component
     * @param cursor The cursor to set
     */
    public void setCursor(Component component, Cursor cursor)
    {
        component.setCursor(cursor);
    }

    /**
     * Sets the enabled.
     * @param gui4jComponentInstance
     * @param enabled The enabled to set
     */
    public void setEnabled(Gui4jComponentInstance gui4jComponentInstance, boolean enabled)
    {
        gui4jComponentInstance.getComponent().setEnabled(enabled);
    }

    /**
     * Sets the font.
     * @param component
     * @param font The font to set
     */
    public void setFont(Component component, Font font)
    {
        component.setFont(font);
    }

    /**
     * Sets the foreground.
     * @param component
     * @param foreground The foreground to set
     */
    public void setForeground(Component component, Color foreground)
    {
        component.setForeground(foreground);
    }

    /**
     * Sets the visible attribute.
     * @param component
     * @param visible The visible value to set
     */
    public void setVisible(Component component, boolean visible)
    {
        component.setVisible(visible);
    }

    public void setFocusable(Component component, boolean focusable)
    {
        component.setFocusable(focusable);
    }

    public void setOnClick(Gui4jCall onClick)
    {
        mOnClick = onClick;
    }

    /**
     * Method setPopupMenu
     * @param popupMenuInPath
     */
    public void setPopupMenu(Gui4jQualifiedComponent popupMenuInPath)
    {
        mPopupMenuInPath = popupMenuInPath;
    }

    /**
     * Method setApply
     * @param apply
     */
    public void setApply(Gui4jCall apply)
    {
        mApply = apply;
    }

    /**
     * Method setOnInit
     * @param onInit
     */
    public void setOnInit(Gui4jCall onInit)
    {
        mOnInit = onInit;
    }

    public void setContext(Gui4jCall context)
    {
        mContext = context;
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jComponent#createGui4jComponentInstance(org.gui4j.core.Gui4jSwingContainer, org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    final public Gui4jComponentInstance createGui4jComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        Gui4jComponentInstance gui4jComponentInstance =
            createComponentInstance(gui4jSwingContainer, gui4jCallBase, gui4jComponentInPath);
        applyInitialProperties(gui4jComponentInstance, gui4jComponentInstance.getGui4jCallBase(), false);
        setProperties(gui4jComponentInstance);
        extendActionMap(gui4jComponentInstance);
        return gui4jComponentInstance;
    }

    /**
     * Method createComponentInstance
     * @param gui4jSwingContainer
     * @param gui4jCallBase
     * @param gui4jComponentInPath
     * @return Gui4jComponentInstance
     */
    abstract protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath);

    /*
     * @see org.gui4j.core.Gui4jComponent#refreshComponent(Gui4jComponentInstance)
     */
    public void refreshComponent(Gui4jComponentInstance gui4jComponentInstance)
    {
        applyInitialProperties(gui4jComponentInstance, gui4jComponentInstance.getGui4jCallBase(), true);
    }

    /*
     * @see de.bea.gui4j.Gui4jComponent#dispose(Gui4jComponentInstance)
     */
    public void dispose(Gui4jComponentInstance gui4jComponentInstance)
    {
    }

    /**
     * Gui4jComponents should override this method if they want to supply a context object
     * to its popup menu.
     * @param gui4jComponentInstance
     * @param mouseEvent das mouse event oder <code>null</code> falls PopUp ohne Maus ausgelöst wurde
     * @return Object
     */
    protected Object getPopupContext(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent)
    {
        return null;
    }

    /**
     * Defines location of popup menu. Gui4jComponents should override this method to
     * tune the location behaviour (especially for defining meaningful locations also
     * for non-mouse-triggered popups, i.e. keyboard).
     * @param gui4jComponentInstance
     * @param mouseEvent mouse event for popup request, might be <code>null</code>
     * @param context context for popup menu, may be <code>null</code>
     * @return Point
     */
    protected Point getPopupLocation(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent, Object context)
    {
        if (mouseEvent != null)
        {
            return mouseEvent.getPoint();
        }
        else
        {
            // default implementation to put popup menu in upper left corner
            // of component if not triggered by mouse
            // (components will have to override this method to provide
            // more meaningful behaviour)
            return new Point(0, 0);
        }
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jComponent#handleSuccess(org.gui4j.core.Gui4jComponentInstance)
     */
    public void handleSuccess(Gui4jComponentInstance gui4jComponentInstance)
    {
        gui4jComponentInstance.getGui4j().handleSuccess(
            gui4jComponentInstance.getGui4jCallBase(),
            gui4jComponentInstance.getContext());
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jComponent#handleException(org.gui4j.core.Gui4jComponentInstance, java.lang.Throwable)
     */
    public void handleException(Gui4jComponentInstance gui4jComponentInstance, Throwable t)
    {
        gui4jComponentInstance.getGui4j().handleException(
            gui4jComponentInstance.getGui4jCallBase(),
            t,
            gui4jComponentInstance.getContext());
    }

    public Object evaluateContext(Gui4jCallBase gui4jCallBase)
    {
        if (mContext != null && gui4jCallBase != null)
        {
            return mContext.getValue(gui4jCallBase, null, null);
        }
        return null;
    }

}
