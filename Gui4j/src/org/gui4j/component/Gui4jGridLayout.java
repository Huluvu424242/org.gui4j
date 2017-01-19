package org.gui4j.component;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gui4j.Gui4jCallBase;
import org.gui4j.core.Gui4jComponentContainer;
import org.gui4j.core.Gui4jComponentInstance;
import org.gui4j.core.Gui4jQualifiedComponent;
import org.gui4j.core.Gui4jSwingContainer;


public final class Gui4jGridLayout extends Gui4jJComponent
{
    private final int mRows;
    private final int mCols;
    private final Gui4jQualifiedComponent[][] mGui4jComponentInPath;

    /**
     * Constructor for Gui4jGridLayout.
     * @param gui4jComponentContainer
     * @param id
     * @param rows
     * @param cols
     */
    public Gui4jGridLayout(Gui4jComponentContainer gui4jComponentContainer, String id, int rows, int cols)
    {
        super(gui4jComponentContainer, JPanel.class, id);
        mRows = rows;
        mCols = cols;
        mGui4jComponentInPath = new Gui4jQualifiedComponent[mRows][mCols];
    }

    public boolean isDefined(int row, int col)
    {
        assert row >= 0 && row < mRows;
        assert col >= 0 && col < mCols;
        return mGui4jComponentInPath[row][col] != null;
    }

    public void addPlacement(int row, int col, Gui4jQualifiedComponent gui4jComponentInPath)
    {
        assert row >= 0 && row < mRows;
        assert col >= 0 && col < mCols;
        assert mGui4jComponentInPath[row][col] == null;
        mGui4jComponentInPath[row][col] = gui4jComponentInPath;
    }

    /* (non-Javadoc)
     * @see org.gui4j.core.Gui4jAbstractComponent#createComponentInstance(org.gui4j.core.Gui4jSwingContainer, org.gui4j.Gui4jCallBase, org.gui4j.core.Gui4jQualifiedComponent)
     */
    protected Gui4jComponentInstance createComponentInstance(
        Gui4jSwingContainer gui4jSwingContainer,
        Gui4jCallBase gui4jCallBase,
        Gui4jQualifiedComponent gui4jComponentInPath)
    {
        GridLayout gridLayout = new GridLayout(mRows, mCols);
        JPanel panel = new JPanel(gridLayout);
        Gui4jComponentInstance gui4jComponentInstance =
            new Gui4jComponentInstance(gui4jSwingContainer, panel, gui4jComponentInPath);
        for (int r = 0; r < mRows; r++)
        {
            for (int c = 0; c < mCols; c++)
            {
                Gui4jQualifiedComponent gui4jComponentInPathSub = mGui4jComponentInPath[r][c];
                if (gui4jComponentInPathSub != null)
                {
                    Gui4jComponentInstance subInstance =
                        gui4jSwingContainer.getGui4jComponentInstance(
                            gui4jComponentInPath.getGui4jComponentPath(),
                            gui4jComponentInPathSub);
                    panel.add(subInstance.getComponent());
                }
                else
                {
                    panel.add(new JLabel());
                }
            }
        }
        return gui4jComponentInstance;
    }

    public void setHSpacing(JComponent jComponent, int hSpacing)
    {
        ((GridLayout) jComponent.getLayout()).setHgap(hSpacing);
    }

    public void setVSpacing(JComponent jComponent, int vSpacing)
    {
        ((GridLayout) jComponent.getLayout()).setVgap(vSpacing);
    }

}
