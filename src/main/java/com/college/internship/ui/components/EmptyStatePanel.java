package com.college.internship.ui.components;

import com.college.internship.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Elegant empty state presenter with vector illustration, title, subtitle,
 * and optional call-to-action button.
 */
public class EmptyStatePanel extends JPanel {

    public EmptyStatePanel(String title, String subtitle, Icons.Type iconType, String actionText, Runnable onAction) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(40, 20, 40, 20));

        // Center Icon Circle
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = getWidth();
                g2.setColor(new Color(139, 92, 246, 25));
                g2.fill(new Ellipse2D.Float(0, 0, size, size));

                g2.setColor(new Color(139, 92, 246, 60));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new Ellipse2D.Float(1, 1, size - 2, size - 2));

                Icon icon = Icons.get(iconType, 32, Theme.ACCENT_SUBTLE);
                int ix = (size - icon.getIconWidth()) / 2;
                int iy = (size - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g2, ix, iy);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(68, 68);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_SUBHEADER);
        titleLabel.setForeground(Theme.TEXT_MAIN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("<html><center style='color: #94A3B8;'>" + subtitle + "</center></html>");
        subLabel.setFont(Theme.FONT_REGULAR);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(iconCircle);
        add(Box.createVerticalStrut(14));
        add(titleLabel);
        add(Box.createVerticalStrut(6));
        add(subLabel);

        if (actionText != null && onAction != null) {
            add(Box.createVerticalStrut(18));
            AnimatedButton actionBtn = new AnimatedButton(actionText, AnimatedButton.Style.PRIMARY);
            actionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            actionBtn.addActionListener(e -> onAction.run());
            add(actionBtn);
        }
    }
}
