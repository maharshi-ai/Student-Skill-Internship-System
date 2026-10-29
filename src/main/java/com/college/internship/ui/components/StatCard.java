package com.college.internship.ui.components;

import com.college.internship.ui.Theme;
import com.college.internship.ui.anim.AnimatedCounter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Commercial KPI statistic card with vector icon badge, animated number counter,
 * and ambient glass styling.
 */
public class StatCard extends GlassPanel {

    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final Icons.Type iconType;
    private final Color accentColor;

    public StatCard(String title, double targetValue, String format, String subtitle, Icons.Type iconType, Color accentColor) {
        super(16, true);
        this.iconType = iconType;
        this.accentColor = accentColor;

        setLayout(new BorderLayout(14, 0));
        setBorder(new EmptyBorder(18, 20, 18, 20));

        // Left Icon Badge
        JPanel iconBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = getWidth();
                RoundRectangle2D box = new RoundRectangle2D.Float(0, 0, size, size, 12, 12);

                // Semi-translucent accent tint
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35));
                g2.fill(box);

                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 90));
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(box);

                // Draw vector icon in center
                Icon icon = Icons.get(iconType, 22, accentColor);
                int ix = (size - icon.getIconWidth()) / 2;
                int iy = (size - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g2, ix, iy);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(46, 46);
            }
        };
        iconBadge.setOpaque(false);

        // Center Text Block
        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setOpaque(false);

        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(Theme.FONT_SMALL);
        titleLabel.setForeground(Theme.TEXT_MUTED);

        valueLabel = new JLabel("0");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(Theme.TEXT_MAIN);

        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(Theme.FONT_SMALL);
        subtitleLabel.setForeground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 210));

        textBlock.add(titleLabel);
        textBlock.add(Box.createVerticalStrut(2));
        textBlock.add(valueLabel);
        textBlock.add(Box.createVerticalStrut(3));
        textBlock.add(subtitleLabel);

        add(iconBadge, BorderLayout.WEST);
        add(textBlock, BorderLayout.CENTER);

        // Run smooth entrance counter animation
        AnimatedCounter.animateTo(valueLabel, targetValue, format);
    }

    public void updateValue(double newValue, String format) {
        AnimatedCounter.animateTo(valueLabel, newValue, format);
    }
}
