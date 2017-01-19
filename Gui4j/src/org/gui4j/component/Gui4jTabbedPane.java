package org.gui4j.component;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.Gui4jThreadManager;
import org.gui4j.core.listener.Gui4jTabChangeListener;
import org.gui4j.core.util.EntrySelection;
import org.gui4j.event.Gui4jEventListener;

public final class Gui4jTabbedPane extends Gui4jJComponent
{
    protected static final Log mLogger = LogFactory.getLog(Gui4jTabbedPane.class);
    private final List mEntryList;
    private final int mTabPolicy;
    private final int mTabPlacement;
    private final boolean mEmbedded;
    private Gui4jCall mOnChange;
    private Gui4jCall[] mTabSelection;

    /**
     * Constructor for Gui4jTabbedPane.
     * 
     * @param gui4jComponentContainer
     * @param id
     * @param tabPolicy
     * @param tabPlacement
     */
    public Gui4jTabbedPane(Gui4jComponentContainer gui4jComponentContainer, String id, int tabPolicy, int tabPlacement,
            boolean embedded)
    {
        super(gui4jComponentContainer, JTabbedPane.class, id);
        mEntryList = new ArrayList();
        mTabPolicy = tabPolicy;
        mTabPlacement = tabPlacement;
        mEmbedded = embedded;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer,
     *      org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.setTabLayoutPolicy(mTabPolicy);
        tabbedPane.setTabPlacement(mTabPlacement);

        if (mEmbedded)
        {
            tabbedPane.putClientProperty("jgoodies.embeddedTabs", Boolean.TRUE);
        }

        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, tabbedPane,
                gui4jComponentInPath);
        int numberOfHiddenTabs = 0;
        List visibleEntryList = new ArrayList();
        List entrySelections = new ArrayList();
        boolean firstVisible = true;
        for (Iterator it = mEntryList.iterator(); it.hasNext();)
        {
            Entry entry = (Entry) it.next();

            // Check if tab should be displayed
            if (entry.getVisible() != null)
            {
                Gui4jCall visible = entry.getVisible();
                Boolean displayTab = (Boolean) visible.getValueNoParams(gui4jComponentInstance.getGui4jCallBase(),
                        Boolean.TRUE);

                if (Boolean.FALSE.equals(displayTab))
                {
                    numberOfHiddenTabs++;
                    continue;
                }
            }
            visibleEntryList.add(entry);
            Gui4jComponentInstance subInstance = gui4jSwingContainer.getGui4jComponentInstance(gui4jComponentInPath
                    .getGui4jComponentPath(), entry.getGui4jComponentInPath());

            tabbedPane
                    .addTab((String) entry.getTitle().getValueNoParams(gui4jCallBase, ""), subInstance.getComponent());
            {
                Gui4jCall foreground = entry.getForeground();
                if (foreground != null)
                {
                    tabbedPane.setForegroundAt(entry.mTabNumber - numberOfHiddenTabs, (Color) foreground
                            .getValueNoParams(gui4jCallBase, Color.BLACK));
                }
            }
            {
                Gui4jCall background = entry.getBackground();
                if (background != null)
                {
                    tabbedPane.setBackgroundAt(entry.mTabNumber - numberOfHiddenTabs, (Color) background
                            .getValueNoParams(gui4jCallBase, Color.GRAY));
                }
            }
            {
                Gui4jCall tooltip = entry.getTooltip();
                if (tooltip != null)
                {
                    /*
                     * mLogger.debug( "Setting tooltip at " +
                     * String.valueOf(entry.mTabNumber - numberOfHiddenTabs) + "
                     * to " + (String) tooltip.getValueNoParams(gui4jController,
                     * null));
                     */
                    tabbedPane.setToolTipTextAt(entry.mTabNumber - numberOfHiddenTabs, (String) tooltip
                            .getValueNoParams(gui4jCallBase, null));
                }
            }
            {
                Gui4jCall enabled = entry.getEnabled();
                if (enabled != null)
                {
                    tabbedPane.setEnabledAt(entry.mTabNumber - numberOfHiddenTabs, ((Boolean) enabled.getValueNoParams(
                            gui4jComponentInstance.getGui4jCallBase(), Boolean.TRUE)).booleanValue());
                }
            }
            entry.registerProperties(gui4jComponentInstance, numberOfHiddenTabs);
            {
                EntrySelectionImpl entrySelection = entry.createEntrySelection(gui4jComponentInstance);
                if (entrySelection != null)
                {
                    if (firstVisible)
                    {
                        firstVisible = false;
                        entrySelection.call();
                    }
                    entrySelections.add(entrySelection);
                }
            }
        }
        if (getGui4j().traceMode())
        {
            // mLogger.debug("Setting preferred size of "+getId()+" to "+d);
            mLogger.debug("Preferred size of " + getId() + " is " + tabbedPane.getPreferredSize());
        }
        if (mOnChange != null || !entrySelections.isEmpty())
        {
            Gui4jTabChangeListener changeListener = new Gui4jTabChangeListener(gui4jComponentInstance, entrySelections);
            gui4jComponentInstance.getGui4jSwingContainer().addDispose(changeListener);
            changeListener.setActionPerformed(mOnChange);
            tabbedPane.addChangeListener(changeListener);
        }
        if (mTabSelection != null)
        {
            TabbedPaneListener listener = new TabbedPaneListener(gui4jComponentInstance, visibleEntryList);
            registerEvents(gui4jSwingContainer, gui4jCallBase, mTabSelection, listener);
            listener.eventOccured();
        }
        return gui4jComponentInstance;
    }

    protected void addEntry(Entry entry)
    {
        entry.mTabNumber = mEntryList.size();
        mEntryList.add(entry);
    }

    public void setOnChange(Gui4jCall onChange)
    {
        mOnChange = onChange;
    }

    public void setTabSelection(Gui4jCall[] tabSelection)
    {
        mTabSelection = tabSelection;
    }

    // **********************************************************

    public class Entry implements Serializable
    {
        private final Gui4jCall mTitle;
        private Gui4jCall mForeground;
        private Gui4jCall mBackground;
        private Gui4jCall mTooltip;
        private Gui4jCall mEnabled;
        private Gui4jCall mVisible;
        private Gui4jCall mCondition;
        protected Gui4jCall mOnSelect;
        private final Gui4jQualifiedComponent mGui4jComponentInPath;
        protected int mTabNumber;

        public Entry(Gui4jCall title, Gui4jQualifiedComponent gui4jComponentInPath)
        {
            mTitle = title;
            mGui4jComponentInPath = gui4jComponentInPath;
            addEntry(this);
        }

        EntrySelectionImpl createEntrySelection(Gui4jComponentInstance gui4jComponentInstance)
        {
            if (mOnSelect != null)
            {
                return new EntrySelectionImpl(this, gui4jComponentInstance);
            }
            return null;
        }

        Gui4jCall getTitle()
        {
            return mTitle;
        }

        Gui4jQualifiedComponent getGui4jComponentInPath()
        {
            return mGui4jComponentInPath;
        }

        public void registerProperties(Gui4jComponentInstance gui4jComponentInstance, final int numberOfHiddenTabs)
        {
            final JTabbedPane tabbedPane = (JTabbedPane) gui4jComponentInstance.getSwingComponent();
            Gui4jSwingContainer gui4jSwingContainer = gui4jComponentInstance.getGui4jSwingContainer();
            if (mTitle.getDependantProperties() != null)
            {
                EntryInstance listener = new EntryInstance(this, gui4jComponentInstance, mTitle) {
                    void applyValue(Object value)
                    {
                        tabbedPane.setTitleAt(mTabNumber - numberOfHiddenTabs, (String) value);
                    }
                };
                registerEvents(gui4jSwingContainer, gui4jComponentInstance.getGui4jCallBase(), mTitle
                        .getDependantProperties(), listener);
            }
            if (mForeground != null && mForeground.getDependantProperties() != null)
            {
                EntryInstance listener = new EntryInstance(this, gui4jComponentInstance, mForeground) {
                    void applyValue(Object value)
                    {
                        int idx = mTabNumber - numberOfHiddenTabs;
                        if (idx < tabbedPane.getTabCount())
                        {
                            tabbedPane.setForegroundAt(mTabNumber - numberOfHiddenTabs, (Color) value);
                        }
                    }
                };
                registerEvents(gui4jSwingContainer, gui4jComponentInstance.getGui4jCallBase(), mForeground
                        .getDependantProperties(), listener);
            }
            if (mBackground != null && mBackground.getDependantProperties() != null)
            {
                EntryInstance listener = new EntryInstance(this, gui4jComponentInstance, mBackground) {
                    void applyValue(Object value)
                    {
                        tabbedPane.setBackgroundAt(mTabNumber - numberOfHiddenTabs, (Color) value);
                    }
                };
                registerEvents(gui4jSwingContainer, gui4jComponentInstance.getGui4jCallBase(), mBackground
                        .getDependantProperties(), listener);
            }
            if (mTooltip != null && mTooltip.getDependantProperties() != null)
            {
                EntryInstance listener = new EntryInstance(this, gui4jComponentInstance, mTooltip) {
                    void applyValue(Object value)
                    {
                        /*
                         * mLogger.warn( "Setting tooltip " + value + " of tab " +
                         * String.valueOf(mTabNumber - numberOfHiddenTabs));
                         */
                        tabbedPane.setToolTipTextAt(mTabNumber - numberOfHiddenTabs, (String) value);
                    }
                };
                registerEvents(gui4jSwingContainer, gui4jComponentInstance.getGui4jCallBase(), mTooltip
                        .getDependantProperties(), listener);
            }
            if (mEnabled != null && mEnabled.getDependantProperties() != null)
            {
                EntryInstance listener = new EntryInstance(this, gui4jComponentInstance, mEnabled) {
                    void applyValue(Object value)
                    {
                        if (tabbedPane != null && value != null)
                        {
                            boolean enabled = ((Boolean) value).booleanValue();
                            tabbedPane.setEnabledAt(mTabNumber - numberOfHiddenTabs, enabled);
                        }
                    }
                };
                registerEvents(gui4jSwingContainer, gui4jComponentInstance.getGui4jCallBase(), mEnabled
                        .getDependantProperties(), listener);
            }
        }

        /**
         * Sets the background.
         * 
         * @param background
         *            The background to set
         */
        public void setBackground(Gui4jCall background)
        {
            mBackground = background;
        }

        /**
         * Sets the foreground.
         * 
         * @param foreground
         *            The foreground to set
         */
        public void setForeground(Gui4jCall foreground)
        {
            mForeground = foreground;
        }

        public void setCondition(Gui4jCall condition)
        {
            mCondition = condition;
        }

        public Gui4jCall getCondition()
        {
            return mCondition;
        }

        public void setOnSelect(Gui4jCall onSelect)
        {
            mOnSelect = onSelect;
        }

        /**
         * Returns the background.
         * 
         * @return Gui4jCall
         */
        public Gui4jCall getBackground()
        {
            return mBackground;
        }

        /**
         * Returns the foreground.
         * 
         * @return Gui4jCall
         */
        public Gui4jCall getForeground()
        {
            return mForeground;
        }

        /**
         * Returns the enabled.
         * 
         * @return Gui4jCall
         */
        public Gui4jCall getEnabled()
        {
            return mEnabled;
        }

        /**
         * Returns the visible.
         * 
         * @return Gui4jCall
         */
        public Gui4jCall getVisible()
        {
            return mVisible;
        }

        /**
         * Returns the tooltip.
         * 
         * @return Gui4jCall
         */
        public Gui4jCall getTooltip()
        {
            return mTooltip;
        }

        /**
         * Sets the enabled.
         * 
         * @param enabled
         *            The enabled to set
         */
        public void setEnabled(Gui4jCall enabled)
        {
            mEnabled = enabled;
        }

        /**
         * Sets the visible.
         * 
         * @param visible
         *            The visible to set
         */
        public void setVisible(Gui4jCall visible)
        {
            mVisible = visible;
        }

        /**
         * Sets the tooltip.
         * 
         * @param tooltip
         *            The tooltip to set
         */
        public void setTooltip(Gui4jCall tooltip)
        {
            mTooltip = tooltip;
        }

    }

    abstract class EntryInstance implements Gui4jEventListener, Runnable
    {
        // private final Entry mEntry;
        // private final JTabbedPane mTabbedPane;
        private final Gui4jCallBase mGui4jCallBase;
        private final Gui4jCall mGui4jAccess;
        private Object mValue;

        EntryInstance(Entry e, Gui4jComponentInstance gui4jComponentInstance, Gui4jCall gui4jAccess)
        {
            // mEntry = e;
            // mTabbedPane = (JTabbedPane)
            // gui4jComponentInstance.getSwingComponent();
            mGui4jAccess = gui4jAccess;
            mGui4jCallBase = gui4jComponentInstance.getGui4jCallBase();
        }

        public void eventOccured()
        {
            Map nullMap = null;
            mValue = mGui4jAccess.getValue(mGui4jCallBase, nullMap, null);
            if (true || mValue != null) // execute always
            {
                if (SwingUtilities.isEventDispatchThread())
                {
                    mLogger.warn("Strange, we are in the GUI Event Dispatch Thread");
                    run();
                }
                else
                {
                    Gui4jThreadManager.executeInSwingThreadAndWait(this);
                }
            }
        }

        public void run()
        {
            if (true || mValue != null) // execute always
            {
                applyValue(mValue);
                mValue = null;
            }
        }

        abstract void applyValue(Object value);

    }

    class TabbedPaneListener implements Gui4jEventListener
    {
        private final Gui4jComponentInstance mGui4jComponentInstance;
        private final Gui4jCallBase mGui4jCallBase;
        // private final Gui4jSwingContainer mGui4jSwingContainer;
        private final List mVisibleEntryList;

        public TabbedPaneListener(Gui4jComponentInstance gui4jComponentInstance, List visibleEntryList)
        {
            this.mGui4jComponentInstance = gui4jComponentInstance;
            this.mGui4jCallBase = gui4jComponentInstance.getGui4jCallBase();
            // this.mGui4jSwingContainer =
            // gui4jComponentInstance.getGui4jSwingContainer();
            this.mVisibleEntryList = visibleEntryList;
        }

        public void eventOccured()
        {
            // evaluate conditions in given order and display first
            // element where the condition is valid
            boolean found = false;
            int idx = 0;
            for (Iterator itEntry = mVisibleEntryList.iterator(); !found && itEntry.hasNext(); idx++)
            {
                Entry entry = (Entry) itEntry.next();
                Gui4jCall condition = entry.getCondition();
                if (condition != null)
                {
                    Boolean result = (Boolean) condition.getValueNoParams(mGui4jCallBase, Boolean.FALSE);
                    if (Boolean.TRUE.equals(result))
                    {
                        useGui4jComponent(idx);
                        found = true;
                    }
                }
            }
            if (!found)
            {
                useGui4jComponent(0);
            }
        }

        private void useGui4jComponent(final int idx)
        {
            mLogger.debug("Selecting tab index " + idx + " for tabbedPane with id " + getId());
            final JTabbedPane tabbedPane = (JTabbedPane) mGui4jComponentInstance.getComponent();
            Gui4jThreadManager.executeInSwingThreadAndWait(new Runnable() {
                public void run()
                {
                    tabbedPane.setSelectedIndex(idx);
                }
            });

        }
    }

    /**
     * @see org.gui4j.core.Gui4jAbstractComponent#setBackground(java.awt.Component,
     *      java.awt.Color)
     */
    public void setBackground(Component component, Color background)
    {
        super.setBackground(component, background);
        if (mTabPolicy == JTabbedPane.SCROLL_TAB_LAYOUT)
        {
            Component c = ((JTabbedPane) component).getComponents()[0];
            if (c instanceof JViewport)
            {
                ((JViewport) c).getView().setBackground(background);
            }
        }
    }

    public static class EntrySelectionImpl implements EntrySelection
    {
        private final Entry mEntry;
        private final Gui4jComponentInstance mGui4jComponentInstance;

        EntrySelectionImpl(Entry e, Gui4jComponentInstance gui4jComponentInstance)
        {
            this.mEntry = e;
            this.mGui4jComponentInstance = gui4jComponentInstance;
        }

        public int getTabIndex()
        {
            return mEntry.mTabNumber;
        }

        public Gui4jCall getOnSelect()
        {
            return mEntry.mOnSelect;
        }

        public void call()
        {
            mGui4jComponentInstance.getGui4j().getGui4jThreadManager().performWork(
                    mGui4jComponentInstance.getGui4jCallBase(), mEntry.mOnSelect, null);
        }
    }
}