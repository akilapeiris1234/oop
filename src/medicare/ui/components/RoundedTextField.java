package medicare.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;

import medicare.ui.ColorPalette;

/** Rounded text field with padding for cleaner forms. */
public class RoundedTextField extends JTextField {
    public RoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setBackground(ColorPalette.CARD);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g2.setColor(ColorPalette.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }
}

