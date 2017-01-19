package org.gui4j.component;

import java.awt.Component;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jCall;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;
import org.gui4j.core.Gui4jThreadManager;


public class Gui4jKeyMap extends Gui4jAbstractPopupComponent
{
    private static final Component dummyInstance = new Label();
    private static final Log log = LogFactory.getLog(Gui4jKeyMap.class);

    private List mList = new ArrayList();

    /**
     * @param gui4jComponentContainer
     * @param componentClass
     * @param id
     */
    public Gui4jKeyMap(Gui4jComponentContainer gui4jComponentContainer, Class componentClass, String id)
    {
        super(gui4jComponentContainer, componentClass, id);
    }

    public void addInput(int focusCondition, Gui4jCall stroke, Gui4jCall action, Gui4jCall actionCommand)
    {
        mList.add(new Gui4jInput(focusCondition, stroke, action, actionCommand));
    }

    public void addAction(Gui4jCall action, Gui4jCall actionCommand)
    {
        mList.add(new Gui4jAction(action, actionCommand));
    }

    public void addKeyMapWithId(String id)
    {
        mList.add(id);
    }

    public static void applyDefinitions(final Gui4jComponentInstance componentInstance, JComponent component)
    {
        assert componentInstance.getGui4jComponent() instanceof Gui4jKeyMap;
        assert component != null;
        Gui4jKeyMap keyMap = (Gui4jKeyMap) componentInstance.getGui4jComponent();
        final Gui4jThreadManager threadManager = keyMap.getGui4j().getGui4jThreadManager();
        for (Iterator it = keyMap.mList.iterator(); it.hasNext();)
        {
            Object object = it.next();
            if (object instanceof Gui4jInput)
            {
                Gui4jInput input = (Gui4jInput) object;
                String stroke =
                    (String) input.mStroke.getValueNoParams(componentInstance.getGui4jCallBase(), null);
                Object actionObj = null;
                if (input.mAction != null)
                {
                    actionObj = input.mAction.getValueNoParams(componentInstance.getGui4jCallBase(), null);
                }
                KeyStroke keyStroke = KeyStroke.getKeyStroke(stroke);
                if (keyStroke == null)
                {
                    log.warn("Invalid keystroke " + stroke + " in keyMap with id " + keyMap.getId());
                }
                if (actionObj == null)
                {
                    actionObj = new Object();
                }
                component.getInputMap(input.focusCondition).put(keyStroke, actionObj);
                if (input.mActionCommand != null)
                {
                    addActionCommand(
                        componentInstance.getGui4jCallBase(),
                        threadManager,
                        component,
                        actionObj,
                        input.mActionCommand);
                }
            }
            if (object instanceof Gui4jAction)
            {
                final Gui4jAction action = (Gui4jAction) object;
                Object actionObj = action.mAction.getValueNoParams(componentInstance.getGui4jCallBase(), null);
                addActionCommand(
                    componentInstance.getGui4jCallBase(),
                    threadManager,
                    component,
                    actionObj,
                    action.mActionCommand);
            }
            if (object instanceof String)
            {
                String addKeyMapId = (String) object;
                Gui4jQualifiedComponent gui4jComponentInPath =
                    componentInstance.getGui4jComponent().getGui4jComponentContainer().getGui4jQualifiedComponent(
                        addKeyMapId);
                Gui4jComponentInstance keyMapInstance =
                    componentInstance.getGui4jComponentInstance(gui4jComponentInPath);
                Gui4jKeyMap.applyDefinitions(keyMapInstance, component);
            }
        }
    }

    private static void addActionCommand(
        final Gui4jCallBase gui4jCallBase,
        final Gui4jThreadManager threadManager,
        final JComponent component,
        final Object action,
        final Gui4jCall actionCommand)
    {
        Action swingAction = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                threadManager.performWork(gui4jCallBase, actionCommand, null);
            }
        };
        component.getActionMap().put(action, swingAction);
    }

    /* (non-Javadoc)
     * @see de.bea.gui4j.Gui4jAbstractComponent#createComponentInstance(de.bea.gui4j.Gui4jSwingContainer, de.bea.gui4j.Gui4jCallBase, de.bea.gui4j.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        return new Gui4jComponentInstance(gui4jSwingContainer, dummyInstance, gui4jComponentInPath);
    }

    private static final class Gui4jInput
    {
        protected final int focusCondition;
        protected final Gui4jCall mStroke;
        protected final Gui4jCall mAction;
        protected final Gui4jCall mActionCommand;

        private Gui4jInput(int focusCondition, Gui4jCall stroke, Gui4jCall action, Gui4jCall actionCommand)
        {
            this.focusCondition = focusCondition;
            this.mStroke = stroke;
            this.mAction = action;
            this.mActionCommand = actionCommand;
        }
    }

    private static final class Gui4jAction
    {
        protected final Gui4jCall mAction;
        protected final Gui4jCall mActionCommand;

        private Gui4jAction(Gui4jCall action, Gui4jCall actionCommand)
        {
            assert action != null && actionCommand != null;
            this.mAction = action;
            this.mActionCommand = actionCommand;
        }
    }

}
