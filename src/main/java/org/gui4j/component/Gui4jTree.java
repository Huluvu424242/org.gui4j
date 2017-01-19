package org.gui4j.component;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.gui4j.Gui4jCallBase;
import org.gui4j.Gui4jGetValue;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jMap1;
import org.gui4j.core.Gui4jMouseListener;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.Gui4jThreadManager;
import org.gui4j.core.Gui4jTypeManager;
import org.gui4j.core.listener.Gui4jMouseListenerTree;
import org.gui4j.event.Gui4jEventListener;
import org.gui4j.util.Utils;

public final class Gui4jTree extends Gui4jJComponent
{
    public static final String PARAM_ITEM = "item";
    public static final String PARAM_PATH = "path";

    public static final int COMMAND_COLLAPSE = 1;
    public static final int COMMAND_EXPAND = 2;
    public static final int COMMAND_SETROOT = 3;
    public static final int COMMAND_RESETROOT = 4;
    public static final int COMMAND_COLLAPSE_ALL = 5;
    public static final int COMMAND_EXPAND_ALL = 6;

    protected static final Log log = LogFactory.getLog(Gui4jTree.class);

    protected final Gui4jTypeManager nodeManager = new Gui4jTypeManager();
    protected final boolean mLazy;
    protected final boolean mHideRootNode;
    protected final boolean mUseOriginalCollection;
    protected final boolean mInitiallyExpand;

    protected Gui4jCall[] mRefreshEvents;
    protected Gui4jCall onSelectCallTree;
    protected Gui4jCall mSelectedNode;
    protected Gui4jCall mSelectedPath;
    protected Gui4jCall mLazyMessageCall;
    protected final int mNodeIconWidth;

    public Gui4jTree(Gui4jComponentContainer gui4jComponentContainer, String id, boolean lazy, boolean hideRootNode,
            boolean useOriginalCollection, boolean initiallyExpand, int iconNodeWidth)
    {
        super(gui4jComponentContainer, JTree.class, id);

        this.mLazy = lazy;
        this.mHideRootNode = hideRootNode;
        this.mUseOriginalCollection = useOriginalCollection;
        this.mInitiallyExpand = initiallyExpand;
        this.mNodeIconWidth = iconNodeWidth;
    }

    public void setRoot(Gui4jComponentInstance componentInstance, Object root)
    {
        Gui4jJTree tree = (Gui4jJTree) componentInstance.getSwingComponent();

        log.debug("Received request to set new root for tree with id " + getId());

        // determine selection in new tree
        Object node = tree.getLastSelectedPathComponent();
        Object[] path = null;

        if (mSelectedPath != null)
        {
            path = (Object[]) mSelectedPath.getValueNoParams(componentInstance.getGui4jCallBase(), node);
        }

        if (mSelectedNode != null)
        {
            node = mSelectedNode.getValueNoParams(componentInstance.getGui4jCallBase(), node);
        }

        // change root of tree model
        Gui4jTreeRootChanger rootChanger = new Gui4jTreeRootChanger(tree, root, path, node);
        rootChanger.run(mLazy);

    }

    public void setReload(Gui4jComponentInstance componentInstance, Object[] reloadPath)
    {
        Gui4jJTree tree = (Gui4jJTree) componentInstance.getSwingComponent();

        if (reloadPath != null && log.isDebugEnabled())
        {
            log.debug("Received request to reload path: " + Arrays.asList(reloadPath));
        }

        // determine selection in new tree
        Object node = null;
        Object[] path = null;

        if (mSelectedPath != null)
        {
            path = (Object[]) mSelectedPath.getValueNoParams(componentInstance.getGui4jCallBase(), node);
        }

        if (mSelectedNode != null)
        {
            node = mSelectedNode.getValueNoParams(componentInstance.getGui4jCallBase(), node);
        }

        // reload given path
        Gui4jTreeReloader reloader = new Gui4jTreeReloader(tree, reloadPath, path, node);
        reloader.run(mLazy);

    }

    public void setSelectedNodeCall(Gui4jCall selectedNode)
    {
        mSelectedNode = selectedNode;
    }

    public void setSelectedPathCall(Gui4jCall selectedPath)
    {
        mSelectedPath = selectedPath;
    }

    public void setSelectedNode(JTree tree, Object node)
    {
        if (node == null)
        {
            return;
        }

        log.debug("Setting tree node selection to node: " + node);
        Gui4jTreeSelector selector = new Gui4jTreeSelector(tree, node);
        selector.run(mLazy);
    }

    public void setSelectedPath(JTree tree, Object[] path)
    {
        if (path == null)
        {
            return;
        }

        log.debug("Delegating to Gui4jTreeSelector, path: " + Arrays.asList(path));

        Gui4jTreeSelector selector = new Gui4jTreeSelector(tree, path);
        selector.run(mLazy);
    }

    public void addNode(Class clazz, Gui4jCall value, Gui4jCall children, Gui4jCall onSelectCall,
            Gui4jCall onDblClickCall, Gui4jCall iconCall, Gui4jCall isLeafCall, String iconPosition, boolean lazy)
    {
        // convert from icon position to text position
        int textPosition = SwingConstants.TRAILING;
        if (iconPosition.equals("trailing"))
        {
            textPosition = SwingConstants.LEADING;
        }

        Gui4jTreeNode node = new Gui4jTreeNode(value, children, onSelectCall, onDblClickCall, iconCall, isLeafCall,
                textPosition, lazy);
        nodeManager.add(clazz, node);
    }

    public void setRefreshEvents(Gui4jCall[] calls)
    {
        mRefreshEvents = calls;
    }

    public void setOnSelectCallTree(Gui4jCall call)
    {
        onSelectCallTree = call;
    }

    public void setLazyMessageCall(Gui4jCall call)
    {
        mLazyMessageCall = call;
    }

    protected Gui4jComponentInstance createComponentInstance(Gui4jSwingContainer gui4jSwingContainer,
            Gui4jCallBase gui4jCallBase, Gui4jQualifiedComponent gui4jQualifiedComponent)
    {
        Gui4jTreeModel treeModel = new Gui4jTreeModel(gui4jCallBase, gui4jSwingContainer);
        Gui4jJTree tree = new Gui4jJTree(treeModel);
        treeModel.setSelectionModel(tree.getSelectionModel());

        tree.setRootVisible(!mHideRootNode);
        registerEvents(gui4jSwingContainer, gui4jCallBase, mRefreshEvents, treeModel.getRefreshListener());
        Gui4jComponentInstance gui4jComponentInstance = new Gui4jComponentInstance(gui4jSwingContainer, tree,
                gui4jQualifiedComponent);
        return gui4jComponentInstance;
    }

    protected Gui4jMouseListener createMouseListener(Gui4jComponentInstance gui4jComponentInstance)
    {
        return new Gui4jMouseListenerTree(gui4jComponentInstance);
    }

    /*
     * @see de.bea.gui4j.Gui4jAbstractComponent#getPopupContext(de.bea.gui4j.Gui4jComponentInstance,
     *      java.awt.event.MouseEvent)
     */
    protected Object getPopupContext(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent)
    {
        JTree tree = (JTree) gui4jComponentInstance.getComponent();
        Object selectedNode = tree.getLastSelectedPathComponent();
        return selectedNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.bea.gui4j.Gui4jAbstractComponent#getPopupLocation(de.bea.gui4j.Gui4jComponentInstance,
     *      java.awt.event.MouseEvent, java.lang.Object)
     */
    protected Point getPopupLocation(Gui4jComponentInstance gui4jComponentInstance, MouseEvent mouseEvent,
            Object context)
    {
        if (context == null)
        {
            return null;
        }
        if (mouseEvent != null)
        {
            return mouseEvent.getPoint();
        }

        Gui4jJTree tree = (Gui4jJTree) gui4jComponentInstance.getComponent();
        TreePath path = tree.getGui4jTreeModel().getCoreModel().getPath(context, true);
        Rectangle selection = tree.getUI().getPathBounds(tree, path);
        return new Point(selection.x + selection.width, selection.y + selection.height);
    }

    public void handleAction(Gui4jComponentInstance gui4jComponentInstance, Object context, int command)
    {

        Gui4jJTree tree = (Gui4jJTree) gui4jComponentInstance.getSwingComponent();
        Gui4jTreeModel model = (Gui4jTreeModel) tree.getModel();

        switch (command)
        {
        case COMMAND_COLLAPSE:
        case COMMAND_COLLAPSE_ALL:
        case COMMAND_EXPAND:
        case COMMAND_EXPAND_ALL:
        {
            Gui4jTreeExpander expander = new Gui4jTreeExpander(tree, context, command);
            expander.run(mLazy);
        }
            break;

        case COMMAND_SETROOT:
            model.switchDisplayRoot(context);
            break;

        case COMMAND_RESETROOT:
            model.switchDisplayRootToMasterRoot();
            break;
        }
    }

    // **********************************************************************

    /**
     * Encapsulates reflection calls and attributes specified for a certain type
     * (i.e. class) of node.
     */
    private static class Gui4jTreeNode
    {
        private final Gui4jCall valueCall;
        private final Gui4jCall childrenCall;
        private final Gui4jCall onSelectCall;
        private final Gui4jCall onDblClickCall;
        private final Gui4jCall iconCall;
        private final Gui4jCall isLeafCall;
        private final int textPosition;
        protected final boolean lazy;

        public Gui4jTreeNode(Gui4jCall value, Gui4jCall children, Gui4jCall onSelectCall, Gui4jCall onDblClickCall,
                Gui4jCall iconCall, Gui4jCall isLeafCall, int textPosition, boolean lazy)
        {
            this.valueCall = value;
            this.childrenCall = children;
            this.onSelectCall = onSelectCall;
            this.onDblClickCall = onDblClickCall;
            this.iconCall = iconCall;
            this.isLeafCall = isLeafCall;
            this.textPosition = textPosition;
            this.lazy = lazy;
        }

        public Gui4jCall getChildrenCall()
        {
            return childrenCall;
        }

        public Gui4jCall getValueCall()
        {
            return valueCall;
        }

        public Gui4jCall getOnSelectCall()
        {
            return onSelectCall;
        }

        public Gui4jCall getOnDblClickCall()
        {
            return onDblClickCall;
        }

        public Gui4jCall getIconCall()
        {
            return iconCall;
        }

        public int getTextPosition()
        {
            return textPosition;
        }

        public Gui4jCall getIsLeafCall()
        {
            return isLeafCall;
        }

    }

    // **********************************************************************

    /**
     * Subclass of JTree to override cell renderer and selection listener.
     */
    private class Gui4jJTree extends JTree
    {

        private static final int ACTION_DOUBLE_CLICK = 1;
        private static final int ACTION_ON_SELECT = 2;

        protected final Gui4jCallBase gui4jCallBase;

        public Gui4jJTree(Gui4jTreeModel gui4jTreeModel)
        {
            super(gui4jTreeModel);
            gui4jCallBase = gui4jTreeModel.getGui4jCallBase();

            getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            setShowsRootHandles(true);

            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e)
                {
                    if (e.getClickCount() == 2)
                    {
                        performNodeAction(ACTION_DOUBLE_CLICK);
                    }
                }

                public void mousePressed(MouseEvent e)
                {
                }

                public void mouseReleased(MouseEvent e)
                {
                }

                public void mouseEntered(MouseEvent e)
                {
                }

                public void mouseExited(MouseEvent e)
                {
                }
            });

            Gui4jTreeSelectionListener listener = new Gui4jTreeSelectionListener() {
                // react on selection changes
                public void selectionChanged(TreeSelectionEvent e)
                {
                    performNodeAction(ACTION_ON_SELECT);

                    {
                        // call onSelect on Tree level
                        if (onSelectCallTree != null)
                        {
                            TreePath path = getSelectionPath();
                            final Map params = new Gui4jMap1(PARAM_PATH, path == null ? null : path.getPath());
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run()
                                {
                                    getGui4j().getGui4jThreadManager().performWork(gui4jCallBase, onSelectCallTree,
                                            params);
                                }

                            });
                        }
                    }
                }
            };
            addTreeSelectionListener(listener);
            addKeyListener(listener);
            addFocusListener(listener);

            setCellRenderer(new Gui4jTreeCellRenderer(gui4jCallBase));
        }

        public Gui4jTreeModel getGui4jTreeModel()
        {
            return (Gui4jTreeModel) getModel();
        }

        protected void performNodeAction(int action)
        {
            Object node = getLastSelectedPathComponent();
            if (node != null)
            {
                Gui4jTreeNode nodeInfo = (Gui4jTreeNode) nodeManager.get(node.getClass());
                if (nodeInfo != null)
                {
                    Gui4jCall call = null;
                    if (action == ACTION_DOUBLE_CLICK)
                    {
                        call = nodeInfo.getOnDblClickCall();
                    }
                    else if (action == ACTION_ON_SELECT)
                    {
                        call = nodeInfo.getOnSelectCall();
                    }
                    if (call != null)
                    {
                        final Gui4jCall finalCall = call;
                        final Map params = new Gui4jMap1(PARAM_ITEM, node);
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run()
                            {
                                getGui4j().getGui4jThreadManager().performWork(gui4jCallBase, finalCall, params);
                            }

                        });
                    }
                }
            }
        }

        public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus)
        {
            if (value instanceof Gui4jTreeMessage)
            {
                return ((Gui4jTreeMessage) value).getMessage();
            }

            Gui4jTreeNode node = (Gui4jTreeNode) nodeManager.get(value.getClass());
            if (node == null)
            {
                log.warn("Missing node definition for: " + value);
                return "(missing node definition)";
            }
            Gui4jCall valueCall = node.getValueCall();
            assert valueCall != null; // value is a required attribute

            Map params = new Gui4jMap1(PARAM_ITEM, value);
            return (String) valueCall.getValue(gui4jCallBase, params, "undefined");
        }

    }

    // **********************************************************************

    /**
     * Subclass of TreeSelectionListener. Implements a simple strategy to solve
     * the problem that a lot of TreeSelectionEvents are generated and processed
     * when the user keeps pressing the up and down arrow keys. To work
     * correctly, the same instance of this listener must be registered in three
     * ways with a tree, using
     * {@link JTree#addTreeSelectionListener(javax.swing.event.TreeSelectionListener)},
     * {@link Component#addKeyListener(java.awt.event.KeyListener)} and
     * {@link Component#addFocusListener(java.awt.event.FocusListener)}. <br>
     * <br>
     * The problem this class solves is that a TreeSelectionEvent might trigger
     * expensive operations (e.g. loading detailed information for the newly
     * selected tree node from a database). If the user "scrolls down" by
     * pressing the down arrow for a long time, the expensive operation is
     * performed for each visited node. This Listener blocks all
     * TreeSelectionEvents while any key is pressed. Only when a key is released
     * is the most recent event passed to the actual event handler.
     */
    private static abstract class Gui4jTreeSelectionListener implements TreeSelectionListener, KeyListener,
            FocusListener
    {
        private boolean blockNotification = false;
        private TreeSelectionEvent blockedEvent = null;

        private void block()
        {
            blockNotification = true;
        }

        private void unblock()
        {
            blockNotification = false;
            if (blockedEvent != null)
            {
                selectionChanged(blockedEvent);
                blockedEvent = null;
            }
        }

        /**
         * Subclasses must override this method instead of the usual
         * {@link TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)}.
         * 
         * @param e
         */
        public abstract void selectionChanged(TreeSelectionEvent e);

        /**
         * Subclasses should not override this method (as is done with a normal
         * TreeSelectionListener) but
         * {@link #selectionChanged(TreeSelectionEvent)} instead.
         * 
         * @param e
         */
        public final void valueChanged(TreeSelectionEvent e)
        {
            if (blockNotification)
            {
                blockedEvent = e;
            }
            else
            {
                blockedEvent = null;
                selectionChanged(e);
            }
        }

        public void keyPressed(KeyEvent e)
        {
            block();
        }

        public void keyReleased(KeyEvent e)
        {
            unblock();
        }

        public void keyTyped(KeyEvent e)
        {
            // intentionally empty body
        }

        public void focusGained(FocusEvent e)
        {
            unblock();
        }

        public void focusLost(FocusEvent e)
        {
            unblock();
        }
    }

    /**
     * Cell Renderer
     */
    private class Gui4jTreeCellRenderer extends DefaultTreeCellRenderer
    {
        private final Gui4jCallBase gui4jCallBase;

        public Gui4jTreeCellRenderer(Gui4jCallBase gui4jCallBase)
        {
            super();
            setLeafIcon(null);
            setOpenIcon(null);
            setClosedIcon(null);

            this.gui4jCallBase = gui4jCallBase;
        }

        public Dimension getPreferredSize()
        {
            Dimension d = super.getPreferredSize();
            if (mNodeIconWidth == 0) {
                return d;
            } else {
                // Problem: icon and text needs some more text
                return new Dimension((int)(d.getWidth()+mNodeIconWidth),(int)(d.getHeight()));
            }
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean pHasFocus)
        {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, pHasFocus);

            Gui4jTreeNode node = (Gui4jTreeNode) nodeManager.get(value.getClass());
            int textPosition = TRAILING; // default
            if (node != null && node.getIconCall() != null)
            {
                Map params = new Gui4jMap1(PARAM_ITEM, value);
                Icon icon = (Icon) (node.getIconCall().getValue(gui4jCallBase, params, null));
                setIcon(icon);
                textPosition = node.getTextPosition();
            }

            // KKB, 16.6.03
            // Eigentlich sollte hier mit
            // setHorizontalTextPosition(textPosition) die Ausrichtung des
            // icons auf dem JLabel gesetzt werden. Das klappt auch, allerdings
            // geht
            // DefaultTreeCellRenderer.paint() davon aus, dass ein icon immer
            // vorne positioniert
            // ist. Dementsprechend wird der Hintergrund bei einem trailing icon
            // gezeichnet:
            // vorne scheint ein Teil des Knotentexts nicht selektiert zu sein,
            // während hinten
            // das icon optisch teil der selektion ist. Statt paint() komplett
            // selbst zu überschreiben,
            // wird hier ein netter workaround mit Hilfe der
            // ComponentOrientation verwendet.
            setComponentOrientation(textPosition == TRAILING ? ComponentOrientation.LEFT_TO_RIGHT
                    : ComponentOrientation.RIGHT_TO_LEFT);

            return this;
        }

    }

    // **********************************************************************

    /**
     * Underlying model used by the <code>Gui4jJTree</code>. Data is held
     * only for caching reasons. The model directly queries the object tree
     * specified by the root attribute in the xml definition.
     */
    private class Gui4jTreeModel implements TreeModel
    {
        private final Gui4jCallBase gui4jCallBase;
        protected final Gui4jSwingContainer gui4jSwingContainer;

        private final Set listeners; // Set(TreeModelListener)

        protected final List lazyChildrenMessage;

        private TreeSelectionModel selectionModel;
        private Gui4jTreeCoreModel coreModel;

        public Gui4jTreeModel(Gui4jCallBase gui4jCallBase, Gui4jSwingContainer gui4jSwingContainer)
        {
            listeners = new HashSet();
            this.gui4jCallBase = gui4jCallBase;
            this.gui4jSwingContainer = gui4jSwingContainer;

            coreModel = new Gui4jTreeCoreModel(this);

            List list = Collections.EMPTY_LIST;
            if (mLazyMessageCall != null)
            {
                String lazyMessage = (String) mLazyMessageCall.getValueNoParams(gui4jCallBase, null);
                if (lazyMessage != null)
                {
                    list = new ArrayList(1);
                    list.add(new Gui4jTreeMessage(lazyMessage));
                }
            }
            lazyChildrenMessage = list;
        }

        public Gui4jTreeCoreModel getCoreModel()
        {
            return coreModel;
        }

        public void setCoreModel(Gui4jTreeCoreModel newCoreModel)
        {
            if (!SwingUtilities.isEventDispatchThread())
            {
                log.warn("setCoreModel called from thead other than GUI thread, don't do that!");
            }
            coreModel = newCoreModel;
        }

        public void setSelectionModel(TreeSelectionModel selectionModel)
        {
            this.selectionModel = selectionModel;
        }

        public void addTreeModelListener(TreeModelListener l)
        {
            listeners.add(l);
        }

        public Object getChild(Object parent, int index)
        {
            log.debug("getChild called with index " + index + " for parent: " + parent);
            return coreModel.getChildren(parent).get(index);
        }

        public int getChildCount(Object parent)
        {
            // log.debug("getChildCount called for parent: " + parent);
            if (parent instanceof Gui4jTreeMessage)
            {
                return 0;
            }
            return coreModel.getChildren(parent).size();
        }

        public int getIndexOfChild(Object parent, Object child)
        {
            log.debug("getIndexOfChild called (parent: " + parent + ", child: " + child + ")");
            return coreModel.getIndexOfChild(parent, child, false);
        }

        public Object getRoot()
        {
            return coreModel.displayRoot;
        }

        public boolean isLeaf(Object parent)
        {
            return coreModel.isLeaf(parent);
        }

        public void removeTreeModelListener(TreeModelListener l)
        {
            listeners.remove(l);
        }

        public void valueForPathChanged(TreePath path, Object newValue)
        {
        }

        /**
         * Sets the root for the tree display.
         * 
         * @param root
         */
        public void switchDisplayRoot(Object root)
        {
            coreModel.switchDisplayRoot(root);
        }

        /**
         * Sets the root of the object graph as the root of the tree's display.
         */
        public void switchDisplayRootToMasterRoot()
        {
            coreModel.switchDisplayRootToMasterRoot();
        }

        /**
         * Invoke this method after the way the tree nodes are to be represented
         * in the tree has changed.
         */
        public void nodeTitlesChanged()
        {
            coreModel.nodeTitlesChanged();
        }

        public void notifyTreeStructureChanged(final TreePath treePath)
        {
            if (treePath != null)
            {
                if (!SwingUtilities.isEventDispatchThread())
                {
                    // KKB, MA, 23.7.04: Nicht vollständig nachvollziehbares
                    // Problem in FITS (Delete
                    // Measurements)
                    // "behoben" durch Verwendung von "...andWait" statt
                    // "...andContinue". Hm...., wissen
                    // nicht
                    // mehr, warum "...andContinue" ursprünglich verwendet
                    // wurde.
                    // Gui4jThreadManager.executeInSwingThreadAndContinue(new
                    // Runnable()
                    Gui4jThreadManager.executeInSwingThreadAndWait(new Runnable() {
                        public void run()
                        {
                            log.debug("notifying tree after children have been loaded lazily");
                            fireTreeStructureChanged(treePath);
                        }
                    });
                }
            }
        }

        protected void fireTreeStructureChanged(TreePath path)
        {
            log.debug("fireTreeStructureChanged for path: " + path);

            // workaround: if the path contains only the root, then the
            // treeStructureChanged() call will clear the selection, so we
            // remember the selection and restore it later on
            boolean retainRootSelection = path != null && path.getPathCount() == 1
                    && selectionModel.isPathSelected(path);

            TreeModelEvent event = new TreeModelEvent(this, path);
            for (Iterator iter = listeners.iterator(); iter.hasNext();)
            {
                TreeModelListener listener = (TreeModelListener) iter.next();
                listener.treeStructureChanged(event);
            }

            if (retainRootSelection)
            {
                log.debug("retaining root selection after tree structure change");
                selectionModel.setSelectionPath(path);
            }
        }

        public void fireTreeNodesChanged(TreePath path)
        {
            Object parent = path.getLastPathComponent();
            List children = coreModel.getChildren(parent, true);
            TreeModelEvent event;
            if (children != null)
            {
                int[] indices = new int[children.size()];
                for (int index = 0; index < indices.length; index++)
                {
                    indices[index] = index;
                }
                Object[] childrenArray = new Object[children.size()];
                event = new TreeModelEvent(this, path, indices, children.toArray(childrenArray));
            }
            else
            {
                event = new TreeModelEvent(this, path);
            }
            for (Iterator iter = listeners.iterator(); iter.hasNext();)
            {
                TreeModelListener listener = (TreeModelListener) iter.next();
                listener.treeNodesChanged(event);
            }
        }

        public Gui4jCallBase getGui4jCallBase()
        {
            return gui4jCallBase;
        }

        /**
         * Returns a listener that reacts to <code>eventOccurred</code> with a
         * notification of the tree to refresh its display.
         * 
         * @return Gui4jEventListener
         */
        public Gui4jEventListener getRefreshListener()
        {
            return new Gui4jEventListener() {
                public void eventOccured()
                {
                    log.debug("Refresh of tree requested, delegating to GUI Thread.");
                    Gui4jThreadManager.executeInSwingThreadAndWait(new Runnable() {
                        public void run()
                        {
                            nodeTitlesChanged();
                        }
                    });
                }
            };
        }

    }

    // ********************************************************************
    private final class Gui4jTreeCoreModel
    {
        private final Gui4jTreeModel model;

        private final Map childrenMap; // Object -> List
        private final Object childrenMutex;
        private final Set childrenInProgress;

        private final Map leafMap; // Object -> Boolean

        protected Object displayRoot; // root for tree gui to display
        private Object masterRoot; // root for object graph held by model

        public Gui4jTreeCoreModel(Gui4jTreeModel model)
        {
            this(model, false);
        }

        public Gui4jTreeCoreModel(Gui4jTreeModel model, boolean clone)
        {
            this.model = model;

            if (clone)
            {
                displayRoot = model.getCoreModel().displayRoot;
                masterRoot = model.getCoreModel().masterRoot;
                childrenMap = Collections.synchronizedMap(new HashMap(model.getCoreModel().childrenMap));
                leafMap = Collections.synchronizedMap(new HashMap(model.getCoreModel().leafMap));
            }
            else
            {
                displayRoot = null;
                masterRoot = null;
                childrenMap = Collections.synchronizedMap(new HashMap());
                leafMap = Collections.synchronizedMap(new HashMap());
            }

            childrenInProgress = Collections.synchronizedSet(new HashSet());
            childrenMutex = new Object();

        }

        public Gui4jTreeModel getGui4jTreeModel()
        {
            return model;
        }

        /**
         * Sets the root of the object graph held by this model
         * 
         * @param root
         */
        public void setMasterRoot(Object root)
        {
            childrenMap.clear();
            leafMap.clear();
            this.masterRoot = root;
            this.displayRoot = root;
        }

        /**
         * Sets the root for the tree display.
         * 
         * @param root
         */
        public void switchDisplayRoot(Object root)
        {
            this.displayRoot = root;
            TreePath rootPath = (displayRoot == null) ? null : new TreePath(displayRoot);
            model.fireTreeStructureChanged(rootPath);
        }

        /**
         * Sets the root of the object graph as the root of the tree's display.
         */
        public void switchDisplayRootToMasterRoot()
        {
            switchDisplayRoot(masterRoot);
        }

        public boolean isLeaf(Object parent)
        {
            // log.debug("isLeaf called for node: " + parent);
            Gui4jTreeNode nodeDef = (Gui4jTreeNode) nodeManager.get(parent.getClass());
            if (nodeDef == null)
            {
                return true;
            }

            if (mLazy || nodeDef.lazy)
            {
                if (childrenMap.containsKey(parent))
                {
                    return getChildren(parent).isEmpty();
                }
                else
                {
                    if (nodeDef.getIsLeafCall() != null)
                    {
                        if (leafMap.containsKey(parent))
                        {
                            return ((Boolean) leafMap.get(parent)).booleanValue();
                        }
                        else
                        {
                            log.warn("leafMap not filled for: " + parent);
                            return true;
                        }
                    }
                    else if (nodeDef.getChildrenCall() != null)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
            }
            else
            {
                return getChildren(parent).isEmpty();
            }
        }

        public List getChildren(Object parent)
        {
            return getChildren(parent, false);
        }

        protected List getChildren(Object parent, boolean restrictToCache)
        {
            return getChildren(parent, restrictToCache, true);
        }

        protected List getChildren(Object parent, boolean restrictToCache, boolean allowLazy)
        {
            if (parent instanceof Gui4jTreeMessage)
            {
                return Collections.EMPTY_LIST;
            }

            List children = (List) childrenMap.get(parent);
            if (restrictToCache)
            {
                return children;
            }
            if (children == null)
            {
                Gui4jTreeNode nodeDef = (Gui4jTreeNode) nodeManager.get(parent.getClass());
                if (nodeDef == null)
                {
                    log.warn("Missing node definition for: " + parent);
                    children = Collections.EMPTY_LIST;
                }
                else if (allowLazy && (mLazy || nodeDef.lazy))
                {
                    children = model.lazyChildrenMessage;

                    synchronized (childrenInProgress)
                    {
                        if (childrenInProgress.contains(parent))
                        {
                            return children;
                        }
                        else
                        {
                            // double check to avoid multiple retrievals
                            List cache = (List) childrenMap.get(parent);
                            if (cache != null)
                            {
                                return cache;
                            }

                            childrenInProgress.add(parent);
                        }
                    }

                    Gui4jChildrenRetriever retriever = new Gui4jChildrenRetriever(this, nodeDef, parent);
                    retriever.run(true);

                }
                else
                {
                    children = retrieveChildren(nodeDef, parent);
                }
            }
            return children;
        }

        protected int getIndexOfChild(Object parent, Object child, boolean restrictToCache)
        {
            if (parent == null || child == null)
            {
                return -1;
            }

            List children = getChildren(parent, restrictToCache, false);
            if (children != null)
            {
                return children.indexOf(child);
            }
            else
            {
                return -1;
            }
        }

        /**
         * Invoke this method after the way the tree nodes are to be represented
         * in the tree has changed.
         */
        public void nodeTitlesChanged()
        {
            // notify the tree of changed node titles
            if (displayRoot != null)
            {
                nodeTitlesChanged(new TreePath(displayRoot));
            }
        }

        /**
         * Notifies the tree of changes to all nodes from this path's root on.
         * 
         * @param path
         *            the node under which all node representations
         *            (potentially) have changed.
         */
        private void nodeTitlesChanged(TreePath path)
        {
            log.debug("nodeTitlesChanged called for path: " + path);
            model.fireTreeNodesChanged(path);

            // recurse over all children that have been requested
            // before and therefore are in the cache
            Object parent = path.getLastPathComponent();
            List children = getChildren(parent, true);

            if (children != null)
            {
                for (int index = 0; index < children.size(); index++)
                {
                    Object child = children.get(index);
                    if (childrenMap.containsKey(child))
                    {
                        TreePath childPath = path.pathByAddingChild(children.get(index));
                        nodeTitlesChanged(childPath);
                    }
                }
            }
        }

        protected List retrieveChildren(Gui4jTreeNode nodeDef, Object parent)
        {
            log.debug("retrieveChildren called for parent: " + parent);

            List children;
            synchronized (childrenMutex)
            {
                // double check, maybe we waited because another thread
                // retrieved "our" children
                children = (List) childrenMap.get(parent);
                if (children != null)
                {
                    log.debug("children for parent have already been retrieved: " + parent);
                    return children;
                }

                log.debug("retrieveChildren: will retrieve children for: " + parent);
                Gui4jCall childrenCall = nodeDef.getChildrenCall();
                if (childrenCall != null)
                {
                    Map params = new Gui4jMap1(PARAM_ITEM, parent);
                    children = (List) childrenCall.getValue(model.getGui4jCallBase(), params, null);
                    if (!mUseOriginalCollection)
                    {
                        children = children == null ? Collections.EMPTY_LIST : new ArrayList(children);
                    }
                }
                if (children == null)
                {
                    children = Collections.EMPTY_LIST;
                }
                // Important: childrenMap.put() must not be called anywhere else
                childrenMap.put(parent, children);

                for (Iterator iter = children.iterator(); iter.hasNext();)
                {
                    Object child = iter.next();
                    Gui4jTreeNode childDef = (Gui4jTreeNode) nodeManager.get(child.getClass());
                    if (childDef != null && (mLazy || childDef.lazy) && childDef.getIsLeafCall() != null)
                    {
                        retrieveLeaf(childDef, child);
                    }
                }

                childrenInProgress.remove(parent);
            }

            return children;

        }

        protected void retrieveLeaf(Gui4jTreeNode nodeDef, Object node)
        {
            Boolean isLeaf = (Boolean) leafMap.get(node);
            if (isLeaf != null)
            {
                log.warn("leaf info already in map for node: " + node);
                return;
            }

            log.debug("retrieveLeaf: will retrieve isLeaf for: " + node);
            Gui4jCall isLeafCall = nodeDef.getIsLeafCall();
            if (isLeafCall == null)
            {
                log.warn("retrieveLeaf called but isLeafCall not defined.");
                return;
            }

            Map params = new Gui4jMap1(PARAM_ITEM, node);
            isLeaf = (Boolean) isLeafCall.getValue(model.getGui4jCallBase(), params, null);

            if (isLeaf == null)
            {
                log.warn("retrieveLeaf: isLeaf call returns null for: " + node);
                return;
            }
            leafMap.put(node, isLeaf); // Important: leafMap.put() must not be
            // called anywhere else

        }

        /**
         * Return a TreePath representing the path from the root to the given
         * node. The node is found by doing a depth first search from the root
         * of the tree. If you want to avoid this search (e.g. for performance
         * reasons) use {@link #getPath(Object[])}instead.
         * 
         * @param node
         * @return a TreePath or <code>null</code> if the node is not found in
         *         the tree
         */
        public final TreePath getPath(Object node)
        {
            return getPath(node, false);
        }

        public final TreePath getPath(Object node, boolean restrictToCache)
        {
            if (node == null || displayRoot == null)
            {
                return null;
            }

            TreePath rootPath = new TreePath(displayRoot);
            if (node.equals(displayRoot))
            {
                return rootPath;
            }
            return getPath(rootPath, node, restrictToCache);
        }

        public final TreePath getPath(TreePath pathSoFar, Object node, boolean restrictToCache)
        {
            Object potentialParent = pathSoFar.getLastPathComponent();
            if (getIndexOfChild(potentialParent, node, restrictToCache) != -1)
            {
                return pathSoFar.pathByAddingChild(node);
            }
            else
            {
                List children = getChildren(potentialParent, restrictToCache);
                if (children != null)
                {
                    for (int i = 0; i < children.size(); i++)
                    {
                        Object candidate = children.get(i);
                        TreePath attempt = getPath(pathSoFar.pathByAddingChild(candidate), node, restrictToCache);
                        if (attempt != null)
                        {
                            return attempt;
                        }
                    }
                }
                return null;
            }
        }

        public final TreePath getPath(Object[] path)
        {
            Object root = displayRoot;
            if (path == null || path.length == 0 || root == null || !(path[0].equals(root)))
            {
                log.debug("equals Vergleich: " + path[0].equals(root));
                log.debug("getPath: object array is empty or first element is not the tree root. first array element: "
                        + path[0] + ", root: " + root);
                return null;
            }

            TreePath rootPath = new TreePath(root);
            if (path.length == 1)
            {
                return rootPath;
            }
            else
            {
                return getPath(rootPath, path, 1);
            }
        }

        private TreePath getPath(TreePath pathSoFar, Object[] path, int offset)
        {

            Object potentialParent = pathSoFar.getLastPathComponent();
            Object node = path[offset];

            int nextOffset;
            TreePath treePath;

            if (node == null)
            {
                // we interpret a null as a "wildcard" and will try to get the
                // next
                // element using a complete search
                if (path.length <= offset + 1)
                {
                    log.debug("getPath: found null as 'wildcard', but as last path element which doesn't make sense.");
                    return null;
                }
                node = path[offset + 1];
                log.debug("getPath: interpeting null in search path as 'wildcard', using node search to find: " + node);
                treePath = getPath(pathSoFar, node, false);
                nextOffset = offset + 2;
            }
            else
            {
                if (getIndexOfChild(potentialParent, node, false) == -1)
                {
                    log.debug("getPath: child not found for parent: " + potentialParent + ", child: " + node);
                    return null;
                }

                treePath = pathSoFar.pathByAddingChild(node);
                nextOffset = offset + 1;
            }

            if (path.length > nextOffset)
            {
                return getPath(treePath, path, nextOffset);
            }
            else
            {
                return treePath;
            }
        }

        public void removeCacheForSubtree(Object[] path)
        {
            if (path == null || path.length == 0)
            {
                log.warn("removePath called with null or empty path: " + path);
                return;
            }

            Object subtreeRoot = path[path.length - 1];
            removeCacheForSubtree(subtreeRoot);

            Gui4jTreeNode nodeDef = (Gui4jTreeNode) nodeManager.get(subtreeRoot.getClass());
            if ((mLazy || nodeDef.lazy) && nodeDef.getIsLeafCall() != null)
            {
                retrieveLeaf(nodeDef, subtreeRoot);
            }

        }

        protected void removeCacheForSubtree(Object node)
        {
            leafMap.remove(node);
            List children = (List) childrenMap.remove(node);
            if (children != null)
            {
                for (Iterator iter = children.iterator(); iter.hasNext();)
                {
                    Object child = iter.next();
                    removeCacheForSubtree(child);
                }
            }
        }

    }

    // **********************************************************************

    private abstract class Gui4jTreeWorker implements Gui4jGetValue
    {
        protected final Gui4jTreeModel treeModel;

        protected Gui4jTreeWorker(Gui4jTreeModel model)
        {
            treeModel = model;
        }

        public void run(boolean separateThread)
        {
            if (separateThread)
            {
                treeModel.gui4jSwingContainer.setBusy(true);
                getGui4j().getGui4jThreadManager().performWork(treeModel.getGui4jCallBase(), this, null);
            }
            else
            {
                work();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.bea.gui4j.call.Gui4jGetValue#getValue(de.bea.gui4j.Gui4jCallBase,
         *      java.util.Map, java.lang.Object)
         */
        public final Object getValue(Gui4jCallBase gui4jCallBase, Map paramMap, Object defaultValue)
        {
            try
            {
                work();
                return null;
            }
            finally
            {
                treeModel.gui4jSwingContainer.setBusy(false);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see de.bea.gui4j.call.Gui4jGetValue#getValueNoErrorChecking(de.bea.gui4j.Gui4jCallBase,
         *      java.util.Map, java.lang.Object)
         */
        public final Object getValueNoErrorChecking(Gui4jCallBase gui4jCallBase, Map paramMap,
                Gui4jComponentInstance componentInstance)
        {
            return getValue(gui4jCallBase, paramMap, null);
        }

        protected abstract void work();
    }

    // ***************************************************************************************

    private final class Gui4jChildrenRetriever extends Gui4jTreeWorker
    {
        private final Gui4jTreeCoreModel coreModel;
        private final Gui4jTreeNode nodeDef;
        private final Object parent;

        Gui4jChildrenRetriever(Gui4jTreeCoreModel coreModel, Gui4jTreeNode nodeDef, Object parent)
        {
            super(coreModel.getGui4jTreeModel());
            this.coreModel = coreModel;
            this.nodeDef = nodeDef;
            this.parent = parent;
        }

        protected void work()
        {
            log.debug("Gui4jChildrenRetriever starts working...");
            coreModel.retrieveChildren(nodeDef, parent);

            TreePath treePath = coreModel.getPath(parent, true);
            treeModel.notifyTreeStructureChanged(treePath);
        }

        public String toString()
        {
            return "Gui4jTree: retrieving children for: " + parent;
        }

    }

    // *********************************************************************************************

    private final class Gui4jTreeSelector extends Gui4jTreeWorker
    {
        protected final JTree mTree;
        private final Object[] mPath;
        private final Object mNode;

        private Gui4jTreeSelector(JTree tree, Object[] path, Object node)
        {
            super((Gui4jTreeModel) tree.getModel());
            this.mTree = tree;
            this.mPath = path;
            this.mNode = node;
        }

        Gui4jTreeSelector(JTree tree, Object[] path)
        {
            this(tree, path, null);
        }

        Gui4jTreeSelector(JTree tree, Object node)
        {
            this(tree, null, node);
        }

        public void work()
        {
            log.debug("Gui4jTreeSelector starts working...");

            assert mPath != null || mNode != null;

            final TreePath treePath;

            // prefer specified path over specified node
            if (mPath != null)
            {
                treePath = treeModel.getCoreModel().getPath(mPath);
            }
            else
            {
                treePath = treeModel.getCoreModel().getPath(mNode);
            }
            if (treePath != null)
            {
                log.debug("Delegating to GUI thread: tree selection to path: " + treePath);
                Gui4jThreadManager.executeInSwingThreadAndWait(new Runnable() {
                    public void run()
                    {
                        log.debug("TreeSelector: telling tree to select (and scroll to) path: " + treePath);
                        mTree.setSelectionPath(treePath);
                        mTree.scrollPathToVisible(treePath);
                    }
                });
            }
        }

        public String toString()
        {
            return "Gui4jTree: selecting path: " + Utils.arrayToString(mPath) + " or node: " + mNode;
        }

    }

    // *************************************************************************

    private final class Gui4jTreeRootChanger extends Gui4jTreeWorker
    {

        protected final Gui4jJTree tree;
        protected final Object root;
        private final Object[] pathToSelect;
        private final Object nodeToSelect;

        public Gui4jTreeRootChanger(Gui4jJTree tree, Object root, Object[] pathToSelect, Object nodeToSelect)
        {
            super(tree.getGui4jTreeModel());
            this.tree = tree;
            this.root = root;
            this.pathToSelect = pathToSelect;
            this.nodeToSelect = nodeToSelect;
        }

        protected void work()
        {
            log.debug("Gui4jTreeRootChanger starting...");

            final Gui4jTreeCoreModel coreModel = new Gui4jTreeCoreModel(treeModel);

            coreModel.setMasterRoot(root); // this must not and will not notify
            // the tree

            TreePath treePath = null;
            if (pathToSelect != null)
            {
                treePath = coreModel.getPath(pathToSelect);
            }
            else if (nodeToSelect != null)
            {
                treePath = coreModel.getPath(nodeToSelect);
            }

            final TreePath selection = treePath;
            final TreePath rootPath = (root == null) ? null : new TreePath(root);
            Gui4jThreadManager.executeInSwingThreadAndWait(new Runnable() {
                public void run()
                {
                    log.debug("switching core tree model to: " + root);
                    treeModel.setCoreModel(coreModel); // switch over to new
                    // core model

                    // notify tree of root change and desired selection
                    treeModel.fireTreeStructureChanged(rootPath);
                    if (selection != null)
                    {
                        log.debug("RootChanger: telling tree to select (and scroll to) path: " + selection);
                        tree.setSelectionPath(selection);
                        tree.scrollPathToVisible(selection);
                    }
                    if (mInitiallyExpand)
                    {
                        Gui4jTreeExpander treeExpander = new Gui4jTreeExpander(tree, root, COMMAND_EXPAND_ALL);
                        treeExpander.work();
                    }
                }
            });

        }

        public String toString()
        {
            return "Gui4jTree: setting new root: " + root;
        }

    }

    // *************************************************************************

    private final class Gui4jTreeReloader extends Gui4jTreeWorker
    {

        protected final Gui4jJTree tree;
        private final Object[] reloadPath;
        private final Object[] pathToSelect;
        private final Object nodeToSelect;

        public Gui4jTreeReloader(Gui4jJTree tree, Object[] reloadPath, Object[] pathToSelect, Object nodeToSelect)
        {
            super(tree.getGui4jTreeModel());
            this.tree = tree;
            this.reloadPath = reloadPath;
            this.pathToSelect = pathToSelect;
            this.nodeToSelect = nodeToSelect;
        }

        protected void work()
        {
            log.debug("Gui4jTreeReloader starting...");

            if (reloadPath == null || reloadPath.length == 0)
            {
                log.debug("reloadPath null or empty, not reloading anything");
                return;
            }

            // create copy of current core model
            final Gui4jTreeCoreModel coreModel = new Gui4jTreeCoreModel(treeModel, true);

            // remove cache information for whole subtree
            Object subtreeRoot = reloadPath[reloadPath.length - 1];
            if (subtreeRoot == null)
            {
                log.warn("Tried to reload path with null as root of subtree.");
                return;
            }
            coreModel.removeCacheForSubtree(subtreeRoot);

            // refresh isLeaf for root of subtree (if necessary)
            Gui4jTreeNode nodeDef = (Gui4jTreeNode) nodeManager.get(subtreeRoot.getClass());
            if ((mLazy || nodeDef.lazy) && nodeDef.getIsLeafCall() != null)
            {
                coreModel.retrieveLeaf(nodeDef, subtreeRoot);
            }

            // get TreePath for tree structure notification
            final TreePath reloadTreePath = coreModel.getPath(reloadPath);
            if (reloadTreePath == null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Provided path is not part of tree: " + Arrays.asList(reloadPath));
                }
                return;
            }

            // get TreePath for tree selection
            TreePath treePath = null;
            if (pathToSelect != null)
            {
                treePath = coreModel.getPath(pathToSelect);
            }
            else if (nodeToSelect != null)
            {
                // search for node only under the reloaded path, not the whole
                // tree
                if (nodeToSelect.equals(reloadTreePath.getLastPathComponent()))
                {
                    treePath = reloadTreePath;
                }
                else
                {
                    treePath = coreModel.getPath(reloadTreePath, nodeToSelect, false);
                }
            }
            final TreePath selectionPath = treePath;

            // switch core model and notify tree in GUI thread
            Gui4jThreadManager.executeInSwingThreadAndWait(new Runnable() {
                public void run()
                {
                    log.debug("switching core tree model after 'reloading': " + reloadTreePath);
                    treeModel.setCoreModel(coreModel); // switch over to new
                    // core model

                    // notify tree of root change and desired selection
                    treeModel.fireTreeStructureChanged(reloadTreePath);
                    if (selectionPath != null)
                    {
                        tree.setSelectionPath(selectionPath);
                        tree.scrollPathToVisible(selectionPath);
                    }
                }
            });

        }

        public String toString()
        {
            return "Gui4jTree: reloading path: " + Utils.arrayToString(reloadPath);
        }

    }

    // *************************************************************************

    private final class Gui4jTreeExpander extends Gui4jTreeWorker
    {
        protected final Gui4jJTree tree;
        private final Object node;
        private final int command;

        public Gui4jTreeExpander(Gui4jJTree tree, Object node, int command)
        {
            super(tree.getGui4jTreeModel());
            this.tree = tree;
            this.node = node;
            this.command = command;
        }

        protected void work()
        {
            TreePath path = treeModel.getCoreModel().getPath(node, true);
            if (path == null)
            {
                return;
            }

            switch (command)
            {
            case COMMAND_EXPAND_ALL:
            {
                log.debug("expandAll: will expand everything under: " + path);
                expandAll(path);
            }
                break;

            case COMMAND_EXPAND:
            {
                expandPath(path);
            }
                break;

            case COMMAND_COLLAPSE_ALL:
            {
                collapseAll(path);
            }
                break;

            case COMMAND_COLLAPSE:
            {
                collapsePath(path);
            }
                break;

            default:
                log.warn("Unknown Gui4jTreeExpander command: " + command);
            }

        }

        private void expandAll(final TreePath path)
        {
            // recurse over subtree and make all leafs visible
            Object lnode = path.getLastPathComponent();
            List children = treeModel.getCoreModel().getChildren(lnode, false, false);
            if (children.isEmpty())
            {
                makeNodeVisible(path);
            }
            else
            {
                for (Iterator it = children.iterator(); it.hasNext();)
                {
                    Object child = it.next();
                    expandAll(path.pathByAddingChild(child));
                }
            }
        }

        private void collapseAll(final TreePath path)
        {
            if (tree.isExpanded(path))
            {
                // recurse over children
                Object lnode = path.getLastPathComponent();
                List children = treeModel.getCoreModel().getChildren(lnode, true, false);
                if (children != null)
                {
                    for (Iterator it = children.iterator(); it.hasNext();)
                    {
                        Object child = it.next();
                        collapseAll(path.pathByAddingChild(child));
                    }
                }

                // collapse current node
                collapsePath(path);
            }

        }

        private void expandPath(final TreePath path)
        {
            Gui4jThreadManager.executeInSwingThreadAndContinue(new Runnable() {
                public void run()
                {
                    tree.expandPath(path);
                }
            });
        }

        private void makeNodeVisible(final TreePath path)
        {
            Gui4jThreadManager.executeInSwingThreadAndContinue(new Runnable() {
                public void run()
                {
                    tree.makeVisible(path);
                }
            });
        }

        private void collapsePath(final TreePath path)
        {
            Gui4jThreadManager.executeInSwingThreadAndContinue(new Runnable() {
                public void run()
                {
                    tree.collapsePath(path);
                }
            });
        }

        public String toString()
        {
            return "Gui4jTree: expanding or collapsing node: " + node;
        }

    }

    // **********************************************************************

    private static class Gui4jTreeMessage
    {
        private final String message;

        public Gui4jTreeMessage(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }
    }

}