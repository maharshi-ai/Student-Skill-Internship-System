package com.college.internship.ui.components;

import com.college.internship.ui.Theme;
import com.college.internship.ui.anim.Easing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern Glassmorphic / Layered Surface container with rounded corners,
 * subtle translucent background, multi-layer ambient border, and smooth hover elevation.
 */
public class GlassPanel extends JPanel {

    private int cornerRadius = 16;
    private Color baseBg = Theme.CARD_BG;
    private Color hoverBg = Theme.CARD_BG_HOVER;
    private Color borderColor = Theme.CARD_BORDER;
    private Color borderHighlight = new Color(139, 92, 246, 120); // Purple glow on hover

    private boolean enableHover = false;
    private float hoverProgress = 0f;
    private Timer hoverTimer;

    public GlassPanel() {
        this(16, true);
    }

    public GlassPanel(int cornerRadius) {
        this(cornerRadius, false);
    }

    public GlassPanel(int cornerRadius, boolean enableHover) {
        this.cornerRadius = cornerRadius;
        this.enableHover = enableHover;
        setOpaque(false);

        if (enableHover) {
            setupHoverAnimation();
        }
    }

    private void setupHoverAnimation() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animateHover(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animateHover(false);
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
            float rawT = Math.min(1f, (float) elapsed / 180f); // 180ms hover transition
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

        // Interpolate background color on hover
        Color currentBg;
        if (enableHover && hoverProgress > 0f) {
            int r = (int) (baseBg.getRed() + (hoverBg.getRed() - baseBg.getRed()) * hoverProgress);
            int gr = (int) (baseBg.getGreen() + (hoverBg.getGreen() - baseBg.getGreen()) * hoverProgress);
            int b = (int) (baseBg.getBlue() + (hoverBg.getBlue() - baseBg.getBlue()) * hoverProgress);
            int a = (int) (baseBg.getAlpha() + (hoverBg.getAlpha() - baseBg.getAlpha()) * hoverProgress);
            currentBg = new Color(r, gr, b, a);
        } else {
            currentBg = baseBg;
        }

        RoundRectangle2D shape = new RoundRectangle2D.Float(0.5f, 0.5f, width - 1f, height - 1f, cornerRadius, cornerRadius);

        // Fill background
        g2.setColor(currentBg);
        g2.fill(shape);

        // Subtle top ambient specular gradient
        GradientPaint topSheen = new GradientPaint(
                0, 0, new Color(255, 255, 255, 12),
                0, Math.min(60, height), new Color(255, 255, 255, 0)
        );
        g2.setPaint(topSheen);
        g2.fill(shape);

        // Border: blend from default card border to purple highlight on hover
        Color currentBorder;
        if (enableHover && hoverProgress > 0f) {
            int br = (int) (borderColor.getRed() + (borderHighlight.getRed() - borderColor.getRed()) * hoverProgress);
            int bgCol = (int) (borderColor.getGreen() + (borderHighlight.getGreen() - borderColor.getGreen()) * hoverProgress);
            int bb = (int) (borderColor.getBlue() + (borderHighlight.getBlue() - borderColor.getBlue()) * hoverProgress);
            int ba = (int) (borderColor.getAlpha() + (borderHighlight.getAlpha() - borderColor.getAlpha()) * hoverProgress);
            currentBorder = new Color(br, bgCol, bb, ba);
        } else {
            currentBorder = borderColor;
        }

        g2.setColor(currentBorder);
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(shape);

        g2.dispose();
        super.paintComponent(g);
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        repaint();
    }

    public void setBaseBackground(Color color) {
        this.baseBg = color;
        repaint();
    }
}
