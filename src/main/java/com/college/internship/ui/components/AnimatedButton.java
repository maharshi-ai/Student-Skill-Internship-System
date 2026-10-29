package com.college.internship.ui.components;

import com.college.internship.ui.Theme;
import com.college.internship.ui.anim.Easing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Commercial-grade animated button featuring rich gradients, interactive hover
 * brightness transitions, and tactile press feedback.
 */
public class AnimatedButton extends JButton {

    public enum Style {
        PRIMARY, SECONDARY, SUCCESS, DANGER, OUTLINE
    }

    private final Style style;
    private int cornerRadius = 10;
    private float hoverProgress = 0f;
    private boolean isPressed = false;
    private Timer hoverTimer;

    public AnimatedButton(String text) {
        this(text, Style.PRIMARY);
    }

    public AnimatedButton(String text, Style style) {
        this(text, null, style);
    }

    public AnimatedButton(String text, Icon icon, Style style) {
        super(text, icon);
        this.style = style;
        setFont(Theme.FONT_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 20, 10, 20));

        setupInteractions();
    }

    private void setupInteractions() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    animateHover(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = false;
                    animateHover(false);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = false;
                    repaint();
                }
            }
        });
    }

    private void animateHover(boolean entering) {
        if (hoverTimer != null && hoverTimer.isRunning()) {
            hoverTimer.stop();
        }

        long startTime = System.currentTimeMillis();
        float startVal = hoverProgress;
        float targetVal = entering ? 1f : 0f;

        hoverTimer = new Timer(16, ev -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float rawT = Math.min(1f, (float) elapsed / 150f); // 150ms hover response
            float t = Easing.easeOutCubic(rawT);
            hoverProgress = startVal + (targetVal - startVal) * t;

            if (rawT >= 1f) {
                hoverTimer.stop();
            }
            repaint();
        });
        hoverTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Subtle press scale transformation
        if (isPressed) {
            g2.translate(1, 1);
        }

        RoundRectangle2D shape = new RoundRectangle2D.Float(1f, 1f, width - 2f, height - 2f, cornerRadius, cornerRadius);

        Color startColor;
        Color endColor;
        Color textColor;
        Color borderColor = null;

        switch (style) {
            case PRIMARY -> {
                // Purple gradient
                startColor = new Color(139, 92, 246);
                endColor = new Color(109, 40, 217);
                textColor = Color.WHITE;
            }
            case SUCCESS -> {
                // Emerald gradient
                startColor = new Color(16, 185, 129);
                endColor = new Color(5, 150, 105);
                textColor = Color.WHITE;
            }
            case DANGER -> {
                // Crimson gradient
                startColor = new Color(239, 68, 68);
                endColor = new Color(185, 28, 28);
                textColor = Color.WHITE;
            }
            case OUTLINE -> {
                startColor = new Color(30, 41, 59, 100);
                endColor = new Color(20, 27, 45, 120);
                textColor = Theme.ACCENT_SUBTLE;
                borderColor = Theme.PRIMARY;
            }
            case SECONDARY -> {
                startColor = new Color(30, 41, 59);
                endColor = new Color(22, 30, 49);
                textColor = Theme.TEXT_SECONDARY;
                borderColor = Theme.SECONDARY_BORDER;
            }
            default -> {
                startColor = new Color(30, 41, 59);
                endColor = new Color(22, 30, 49);
                textColor = Theme.TEXT_SECONDARY;
                borderColor = Theme.SECONDARY_BORDER;
            }
        }

        // Apply hover brightness boost
        if (hoverProgress > 0f) {
            int boost = (int) (24 * hoverProgress);
            startColor = brighten(startColor, boost);
            endColor = brighten(endColor, boost);
            if (borderColor != null) {
                borderColor = brighten(borderColor, boost);
            }
        }

        // Paint background gradient
        GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
        g2.setPaint(gp);
        g2.fill(shape);

        // Ambient top highlight line
        g2.setColor(new Color(255, 255, 255, 30));
        g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, width - 3f, height - 3f, cornerRadius - 1, cornerRadius - 1));

        // Draw border if defined
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(shape);
        }

        g2.dispose();

        // Draw label text and icon
        setForeground(textColor);
        super.paintComponent(g);
    }

    private Color brighten(Color c, int amount) {
        int r = Math.min(255, c.getRed() + amount);
        int g = Math.min(255, c.getGreen() + amount);
        int b = Math.min(255, c.getBlue() + amount);
        return new Color(r, g, b, c.getAlpha());
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
