package com.college.internship.ui.components;

import com.college.internship.ui.Theme;
import com.college.internship.ui.anim.Easing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Premium input field with placeholder text, dark elevated surface,
 * and smooth focus-glow ring animation.
 */
public class AnimatedTextField extends JTextField {

    private String placeholder = "";
    private float focusProgress = 0f;
    private Timer focusTimer;
    private int cornerRadius = 10;

    public AnimatedTextField() {
        this("");
    }

    public AnimatedTextField(String placeholder) {
        this.placeholder = placeholder;
        setBackground(Theme.INPUT_BG);
        setForeground(Theme.TEXT_MAIN);
        setCaretColor(Theme.PRIMARY);
        setFont(Theme.FONT_REGULAR);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 14, 10, 14));

        setupFocusAnimation();
    }

    private void setupFocusAnimation() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                animateFocus(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                animateFocus(false);
            }
        });
    }

    private void animateFocus(boolean gaining) {
        if (focusTimer != null && focusTimer.isRunning()) {
            focusTimer.stop();
        }

        long startTime = System.currentTimeMillis();
        float startVal = focusProgress;
        float targetVal = gaining ? 1f : 0f;

        focusTimer = new Timer(16, ev -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float rawT = Math.min(1f, (float) elapsed / 180f);
            float t = Easing.easeOutCubic(rawT);
            focusProgress = startVal + (targetVal - startVal) * t;

            if (rawT >= 1f) {
                focusTimer.stop();
            }
            repaint();
        });
        focusTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Draw outer glowing focus ring if focused
        if (focusProgress > 0f) {
            int glowAlpha = (int) (60 * focusProgress);
            g2.setColor(new Color(139, 92, 246, glowAlpha));
            g2.setStroke(new BasicStroke(3.0f));
            g2.draw(new RoundRectangle2D.Float(1f, 1f, width - 2f, height - 2f, cornerRadius + 2, cornerRadius + 2));
        }

        // 2. Draw background surface
        RoundRectangle2D shape = new RoundRectangle2D.Float(2f, 2f, width - 4f, height - 4f, cornerRadius, cornerRadius);
        g2.setColor(Theme.INPUT_BG);
        g2.fill(shape);

        // 3. Draw border (interpolated between muted border and vibrant purple)
        Color defaultBorder = Theme.INPUT_BORDER;
        Color activeBorder = Theme.PRIMARY;
        int r = (int) (defaultBorder.getRed() + (activeBorder.getRed() - defaultBorder.getRed()) * focusProgress);
        int gr = (int) (defaultBorder.getGreen() + (activeBorder.getGreen() - defaultBorder.getGreen()) * focusProgress);
        int b = (int) (defaultBorder.getBlue() + (activeBorder.getBlue() - defaultBorder.getBlue()) * focusProgress);

        g2.setColor(new Color(r, gr, b));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(shape);

        g2.dispose();

        super.paintComponent(g);

        // 4. Draw placeholder if field is empty and not focused
        if (getText().isEmpty() && !placeholder.isEmpty() && !hasFocus()) {
            Graphics2D gp = (Graphics2D) g.create();
            gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gp.setColor(Theme.TEXT_MUTED);
            gp.setFont(getFont());
            FontMetrics fm = gp.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            gp.drawString(placeholder, getInsets().left, y);
            gp.dispose();
        }
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
}
