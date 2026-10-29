package com.college.internship.ui.components;

import com.college.internship.ui.Theme;
import com.college.internship.ui.anim.Easing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Premium password field with focus glow ring and integrated eye visibility toggle.
 */
public class AnimatedPasswordField extends JPasswordField {

    private String placeholder = "";
    private float focusProgress = 0f;
    private Timer focusTimer;
    private int cornerRadius = 10;
    private boolean isPasswordVisible = false;
    private final char defaultEchoChar;

    public AnimatedPasswordField() {
        this("");
    }

    public AnimatedPasswordField(String placeholder) {
        this.placeholder = placeholder;
        this.defaultEchoChar = getEchoChar();

        setBackground(Theme.INPUT_BG);
        setForeground(Theme.TEXT_MAIN);
        setCaretColor(Theme.PRIMARY);
        setFont(Theme.FONT_REGULAR);
        setOpaque(false);
        // Leave room on the right for the eye toggle icon
        setBorder(new EmptyBorder(10, 14, 10, 42));

        setupFocusAnimation();
        setupEyeToggle();
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

    private void setupEyeToggle() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int iconWidth = 32;
                int xStart = getWidth() - iconWidth - 8;
                if (e.getX() >= xStart && e.getX() <= getWidth() - 8) {
                    togglePasswordVisibility();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int iconWidth = 32;
                int xStart = getWidth() - iconWidth - 8;
                if (e.getX() >= xStart && e.getX() <= getWidth() - 8) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.TEXT_CURSOR));
                }
            }
        });
    }

    public void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            setEchoChar((char) 0);
        } else {
            setEchoChar(defaultEchoChar);
        }
        repaint();
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

        // 4. Draw interactive eye visibility icon on right
        Icon eyeIcon = Icons.get(isPasswordVisible ? Icons.Type.EYE_OFF : Icons.Type.EYE, 18,
                isPasswordVisible ? Theme.PRIMARY : Theme.TEXT_MUTED);
        int iconX = width - 30;
        int iconY = (height - 18) / 2;
        eyeIcon.paintIcon(this, g2, iconX, iconY);

        g2.dispose();

        super.paintComponent(g);

        // 5. Draw placeholder if empty
        if (getPassword().length == 0 && !placeholder.isEmpty() && !hasFocus()) {
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
