package org.gui4j.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;


public final class Gui4jGridBagLayout extends Gui4jJComponent
{

    private final List mGui4jComponents;

    /**
     * Constructor for Gui4jGridBagLayout.
     * @param gui4jComponentContainer
     * @param id
     */
    public Gui4jGridBagLayout(Gui4jComponentContainer gui4jComponentContainer, String id)
    {
        super(gui4jComponentContainer, JPanel.class, id);
        mGui4jComponents = new Vector();
    }

    public void addPlacement(
        Gui4jQualifiedComponent gui4jComponentInPath,
        int gridX,
        int gridY,
        int gridWidth,
        int gridHeight,
        int fill,
        double weightX,
        double weightY,
        int anchor,
        int ipadx,
        int ipady,
        int top,
        int left,
        int bottom,
        int right)
    {
        Layout l =
            new Layout(
                gui4jComponentInPath,
                gridX,
                gridY,
                gridWidth,
                gridHeight,
                ipadx,
                ipady,
                top,
                left,
                bottom,
                right);
        l.setFill(fill);
        l.setWeightX(weightX);
        l.setWeightY(weightY);
        l.setAnchor(anchor);
        mGui4jComponents.add(l);
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer, org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        // mLogger.debug("Placing component with id "+getId());
        GridBagLayout gridBagLayout = new GridBagLayout();
        JPanel panel = new JPanel(gridBagLayout);
        Gui4jComponentInstance gui4jComponentInstance =
            new Gui4jComponentInstance(gui4jSwingContainer, panel, gui4jComponentInPath);
        for (Iterator it = mGui4jComponents.iterator(); it.hasNext();)
        {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            Layout l = (Layout) it.next();
            Gui4jComponentInstance subInstance =
                gui4jSwingContainer.getGui4jComponentInstance(
                    gui4jComponentInPath.getGui4jComponentPath(),
                    l.getGui4jComponentInPath());
            gridBagConstraints.gridx = l.getGridX();
            gridBagConstraints.gridy = l.getGridY();
            gridBagConstraints.ipadx = l.getIPadX();
            gridBagConstraints.ipady = l.getIPadY();
            gridBagConstraints.gridy = l.getGridY();
            gridBagConstraints.gridwidth = l.getGridWidth();
            gridBagConstraints.gridheight = l.getGridHeight();
            gridBagConstraints.fill = l.getFill();
            gridBagConstraints.weightx = l.getWeightX();
            gridBagConstraints.weighty = l.getWeightY();
            gridBagConstraints.anchor = l.getAnchor();
            gridBagConstraints.insets = l.getInsets();
            // mLogger.debug("Preferred size of placeGbl is "+subInstance.getSwingComponent().getPreferredSize()+", col="+l.getGridX()+", row="+l.getGridY());
            panel.add(subInstance.getComponent(), gridBagConstraints);
        }

        return gui4jComponentInstance;
    }

    private static class Layout implements Serializable
    {
        private final Gui4jQualifiedComponent mGui4jComponentInPath;
        private final int mGridX;
        private final int mGridY;
        private final int mIPadX;
        private final int mIPadY;
        private double mWeightX;
        private double mWeightY;
        private int mAnchor;
        private final int mGridWidth;
        private final int mGridHeight;
        private int mFill;
        private final Insets mInsets;

        Layout(
            Gui4jQualifiedComponent gui4jComponentInPath,
            int gridX,
            int gridY,
            int gridWidth,
            int gridHeight,
            int ipadx,
            int ipady,
            int top,
            int left,
            int bottom,
            int right)
        {
            mGui4jComponentInPath = gui4jComponentInPath;
            mGridX = gridX;
            mGridY = gridY;
            mGridWidth = gridWidth;
            mGridHeight = gridHeight;
            mFill = GridBagConstraints.BOTH;
            mIPadX = ipadx;
            mIPadY = ipady;
            mInsets = new Insets(top, left, bottom, right);
        }
        /**
         * Returns the gridWidth.
         * @return int
         */
        public int getGridWidth()
        {
            return mGridWidth;
        }

        public Insets getInsets()
        {
            return mInsets;
        }

        /**
         * Returns the gridHeight.
         * @return int
         */
        public int getGridHeight()
        {
            return mGridHeight;
        }

        /**
         * Returns the gridX.
         * @return int
         */
        public int getGridX()
        {
            return mGridX;
        }

        /**
         * Returns the gridY.
         * @return int
         */
        public int getGridY()
        {
            return mGridY;
        }

        /**
         * Returns the ipadx.
         * @return int
         */
        public int getIPadX()
        {
            return mIPadX;
        }

        /**
         * Returns the ipady.
         * @return int
         */
        public int getIPadY()
        {
            return mIPadY;
        }

        /**
         * Returns the gui4jComponent.
         * @return Gui4jComponent
         */
        public Gui4jQualifiedComponent getGui4jComponentInPath()
        {
            return mGui4jComponentInPath;
        }

        /**
         * Returns the fill.
         * @return int
         */
        public int getFill()
        {
            return mFill;
        }

        /**
         * Sets the fill.
         * @param fill The fill to set
         */
        public void setFill(int fill)
        {
            mFill = fill;
        }

        /**
         * Returns the weightX.
         * @return int
         */
        public double getWeightX()
        {
            return mWeightX;
        }

        /**
         * Returns the weightY.
         * @return int
         */
        public double getWeightY()
        {
            return mWeightY;
        }

        /**
         * Returns the anchor.
         * @return int
         */
        public int getAnchor()
        {
            return mAnchor;
        }

        /**
         * Sets the weightX.
         * @param weightX The weightX to set
         */
        public void setWeightX(double weightX)
        {
            mWeightX = weightX;
        }

        /**
         * Sets the weightY.
         * @param weightY The weightY to set
         */
        public void setWeightY(double weightY)
        {
            mWeightY = weightY;
        }

        /**
         * Sets the anchor.
         * @param anchor The anchor to set
         */
        public void setAnchor(int anchor)
        {
            mAnchor = anchor;
        }

    }
}
