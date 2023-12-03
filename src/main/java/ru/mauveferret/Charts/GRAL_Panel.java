package ru.mauveferret.Charts;


import ru.mauveferret.Main;

import java.awt.*;


import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Abstract base class for all visual examples.
 */
public abstract class GRAL_Panel extends JPanel {


    /**
     * Performs basic initialization of an example,
     * like setting a default size.
     */
    public GRAL_Panel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }

    /**
     * Returns a short title for the example.
     * @return A title text.
     */
    public abstract String getTitle();

    /**
     * Returns a more detailed description of the example contents.
     * @return A description of the example.
     */
    public abstract String getDescription();

    /**
     * Opens a frame and shows the example in it.
     * @return the frame instance used for displaying the example.
     */
    public JFrame showInFrame() {
        JFrame frame = new JFrame(getTitle());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(getPreferredSize());

        ImageIcon icon = new ImageIcon(Main.class.getResource("pics/CrocoLogo.png").getPath());
        frame.setIconImage(icon.getImage());
        frame.setVisible(true);
        return frame;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}