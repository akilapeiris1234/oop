package medicare.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

import medicare.ui.ColorPalette;

/** Rounded accent button used across the UI. */
public class RoundedButton extends JButton {
    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);
        setBackground(ColorPalette.ACCENT);
        setForeground(Color.WHITE);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isArmed() ? ColorPalette.ACCENT.darker() : ColorPalette.ACCENT);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        super.paintComponent(g);
        g2.dispose();
    }
}

