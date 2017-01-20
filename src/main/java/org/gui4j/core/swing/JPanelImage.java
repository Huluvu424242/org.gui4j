package org.gui4j.core.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 * A normal JPanel with the added possibility to 
 * optionally set a background image.
 * The image will be scaled to match the current size
 * of the panel.
 */
public class JPanelImage extends JPanel
{
    public static final String SCALE = "scale";
    public static final String TILE = "tile";
    public static final String SINGLE = "single";

    private Image img;
    private String mode;

    public JPanelImage()
    {
        this(null);
    }

    /**
     * Constructor.
     * @param img The background image. If it is <code>null</code>,
     * this JPanelImage will behave like a normal JPanel.
     */
    public JPanelImage(Image img)
    {
        super(new BorderLayout());
        this.img = img;
    }

    protected void paintComponent(Graphics g)
    {
        // We implement the background image as custom painting.
        // The background of the normal JPanel will be painted by its
        // UI delegate in super.paintComponent() and then we
        // "overpaint" it by the image. That way, we don't have to call
        // setOpaque(false) and the handling of null image is straight forward.

        super.paintComponent(g);
        if (img != null && isOpaque())
        {            
            Insets insets = getInsets();
            int top = insets.top;
            int left = insets.left;
            Dimension size = getSize();
            size = new Dimension(size.width - (insets.left + insets.right), size.height - (insets.top + insets.bottom));
            
            if (mode == SCALE)
            {
                g.drawImage(img, left, top, size.width, size.height, this);
            }
            else if (mode == TILE)
            {
                int width = img.getWidth(this);
                int height = img.getHeight(this);
                if (width > 0 && height > 0)
                {
                    for (int row = top; row < size.height; row += height)
                    {
                        for (int col = left; col < size.width; col += width)
                        {
                            g.drawImage(img, col, row, this);
                        }
                    }
                }
            }
            else  // SINGLE or no mode specified
            {
                g.drawImage(img, left, top, this);
            }
        }
    }

    public void setImg(Image image)
    {
        img = image;
        //repaint();
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

}
