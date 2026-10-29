package com.college.internship.ui.components;

import com.college.internship.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern Status Pill label with a colored status indicator dot and translucent pill badge.
 */
public class StatusPill extends JPanel {

    private String text;
    private Color dotColor;
    private Color bg;
    private Color fg;

    public StatusPill(String status) {
        setStatus(status);
        setOpaque(false);
    }

    public void setStatus(String status) {
        if (status == null) status = "UNKNOWN";
        this.text = status.toUpperCase();

        if (text.contains("ACCEPTED") || text.contains("OPEN") || text.contains("ACTIVE")) {
            dotColor = new Color(52, 211, 153);
            bg = Theme.SUCCESS_BG;
            fg = Theme.SUCCESS_TEXT;
        } else if (text.contains("REJECTED") || text.contains("CLOSED") || text.contains("CANCELLED")) {
            dotColor = new Color(248, 113, 113);
            bg = Theme.DANGER_BG;
            fg = Theme.DANGER_TEXT;
        } else if (text.contains("PENDING") || text.contains("REVIEW")) {
            dotColor = new Color(251, 191, 36);
            bg = Theme.WARNING_BG;
            fg = Theme.WARNING_TEXT;
        } else {
            dotColor = Theme.ACCENT_SUBTLE;
            bg = Theme.PURPLE_BADGE_BG;
            fg = Theme.PURPLE_BADGE_FG;
        }

        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(Theme.FONT_BADGE);
        int textWidth = fm.stringWidth(text);
        // dot (6) + gap (6) + textWidth + horizontal padding (24)
        return new Dimension(textWidth + 36, 26);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Draw rounded pill background
        RoundRectangle2D shape = new RoundRectangle2D.Float(1f, 1f, width - 2f, height - 2f, height, height);
        g2.setColor(bg);
        g2.fill(shape);

        // 1px subtle border
        g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 70));
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(shape);

        // 2. Draw status dot
        int dotSize = 6;
        int dotX = 10;
        int dotY = (height - dotSize) / 2;
        g2.setColor(dotColor);
        g2.fill(new Ellipse2D.Float(dotX, dotY, dotSize, dotSize));

        // 3. Draw text label
        g2.setColor(fg);
        g2.setFont(Theme.FONT_BADGE);
        FontMetrics fm = g2.getFontMetrics();
        int textX = dotX + dotSize + 6;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}
