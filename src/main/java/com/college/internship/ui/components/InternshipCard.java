package com.college.internship.ui.components;

import com.college.internship.model.Internship;
import com.college.internship.ui.Components;
import com.college.internship.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * Premium opportunity card showcasing internship positions with company badge,
 * stipend pill, deadline tag, requirements preview, and one-click interactive actions.
 */
public class InternshipCard extends GlassPanel {

    private final Internship internship;
    private final boolean alreadyApplied;
    private final Consumer<Internship> onSelect;

    public InternshipCard(Internship internship, boolean alreadyApplied, Consumer<Internship> onSelect) {
        super(14, true);
        this.internship = internship;
        this.alreadyApplied = alreadyApplied;
        this.onSelect = onSelect;

        setLayout(new BorderLayout(16, 12));
        setBorder(new EmptyBorder(16, 18, 16, 18));

        initUI();
    }

    private void initUI() {
        // Top Header: Company initial circle + Role title + Company name + Status Pill
        JPanel topRow = new JPanel(new BorderLayout(12, 0));
        topRow.setOpaque(false);

        // Company Logo / Initial Circle
        JPanel logoCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = getWidth();
                String comp = internship.getCompany() != null ? internship.getCompany().trim() : "C";
                String initial = comp.isEmpty() ? "C" : comp.substring(0, 1).toUpperCase();

                // Distinct gradient circle based on initial hash
                int hash = Math.abs(comp.hashCode());
                Color c1 = new Color(80 + (hash % 100), 70 + ((hash / 2) % 100), 220);
                Color c2 = new Color(139, 92, 246);

                g2.setPaint(new GradientPaint(0, 0, c1, size, size, c2));
                g2.fill(new Ellipse2D.Float(0, 0, size, size));

                g2.setColor(Color.WHITE);
                g2.setFont(Theme.FONT_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (size - fm.stringWidth(initial)) / 2;
                int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initial, tx, ty);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(42, 42);
            }
        };
        logoCircle.setOpaque(false);

        // Title and Company block
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel titleLbl = new JLabel(internship.getTitle());
        titleLbl.setFont(Theme.FONT_SUBHEADER);
        titleLbl.setForeground(Theme.TEXT_MAIN);

        JLabel compLocLbl = new JLabel(internship.getCompany() + " • " + internship.getLocation());
        compLocLbl.setFont(Theme.FONT_SMALL);
        compLocLbl.setForeground(Theme.TEXT_MUTED);

        titleBlock.add(titleLbl);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(compLocLbl);

        topRow.add(logoCircle, BorderLayout.WEST);
        topRow.add(titleBlock, BorderLayout.CENTER);

        // Status or Applied Pill
        if (alreadyApplied) {
            JLabel appliedBadge = Components.createBadge("ALREADY APPLIED", new Color(6, 78, 59, 180), new Color(52, 211, 153));
            topRow.add(appliedBadge, BorderLayout.EAST);
        } else {
            StatusPill statusPill = new StatusPill(internship.getStatus().name());
            topRow.add(statusPill, BorderLayout.EAST);
        }

        add(topRow, BorderLayout.NORTH);

        // Center: Description snippet preview
        String desc = internship.getDescription();
        if (desc != null && desc.length() > 140) {
            desc = desc.substring(0, 137) + "...";
        }
        JLabel descLabel = new JLabel("<html><body style='width: 320px; color: #94A3B8;'>" + (desc != null ? desc : "No description provided.") + "</body></html>");
        descLabel.setFont(Theme.FONT_REGULAR);
        add(descLabel, BorderLayout.CENTER);

        // Bottom Row: Stipend, Deadline & Action button
        JPanel bottomRow = new JPanel(new BorderLayout(10, 0));
        bottomRow.setOpaque(false);

        JPanel metaTags = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        metaTags.setOpaque(false);

        JLabel stipendTag = Components.createBadge(internship.getStipend(), new Color(16, 185, 129, 30), new Color(52, 211, 153));
        JLabel deadlineTag = Components.createBadge("Deadline: " + internship.getDeadline(), new Color(30, 41, 59, 160), Theme.TEXT_MUTED);

        metaTags.add(stipendTag);
        metaTags.add(deadlineTag);

        AnimatedButton viewBtn = new AnimatedButton(alreadyApplied ? "View Position" : "View & Apply",
                alreadyApplied ? AnimatedButton.Style.SECONDARY : AnimatedButton.Style.PRIMARY);
        viewBtn.setFont(Theme.FONT_SMALL);
        viewBtn.setMargin(new Insets(6, 14, 6, 14));
        viewBtn.addActionListener(e -> onSelect.accept(internship));

        bottomRow.add(metaTags, BorderLayout.WEST);
        bottomRow.add(viewBtn, BorderLayout.EAST);

        add(bottomRow, BorderLayout.SOUTH);
    }
}
