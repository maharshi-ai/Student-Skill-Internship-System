package com.college.internship.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Reusable UI Components for Java Swing interface.
 * Implements a modern dark navy & purple design system.
 */
public class Components {

    public enum ButtonStyle {
        PRIMARY, SECONDARY, DANGER, SUCCESS
    }

    /**
     * Custom styled button with hover animation and rounded borders.
     */
    public static JButton createButton(String text, ButtonStyle style) {
        JButton button = new JButton(text);
        button.setFont(Theme.FONT_BOLD);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));

        final Color bg;
        final Color hoverBg;
        final Color fg;
        final Color borderColor;

        switch (style) {
            case PRIMARY -> {
                bg = Theme.PRIMARY;
                hoverBg = Theme.PRIMARY_HOVER;
                fg = Theme.TEXT_ON_PRIMARY;
                borderColor = Theme.PRIMARY_HOVER;
            }
            case SECONDARY -> {
                bg = Theme.SECONDARY_BG;
                hoverBg = Theme.SECONDARY_HOVER;
                fg = Theme.TEXT_SECONDARY;
                borderColor = Theme.SECONDARY_BORDER;
            }
            case DANGER -> {
                bg = Theme.DANGER_BUTTON;
                hoverBg = Theme.DANGER_BUTTON_HOVER;
                fg = Color.WHITE;
                borderColor = Theme.DANGER_BORDER;
            }
            case SUCCESS -> {
                bg = Theme.SUCCESS_BUTTON;
                hoverBg = Theme.SUCCESS_BUTTON_HOVER;
                fg = Color.WHITE;
                borderColor = Theme.SUCCESS_BORDER;
            }
            default -> {
                bg = Theme.PRIMARY;
                hoverBg = Theme.PRIMARY_HOVER;
                fg = Theme.TEXT_ON_PRIMARY;
                borderColor = Theme.PRIMARY_HOVER;
            }
        }

        button.setBackground(bg);
        button.setForeground(fg);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                new EmptyBorder(8, 18, 8, 18)
        ));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bg);
                }
            }
        });

        return button;
    }

    /**
     * Styled Card container panel with elevated dark navy background and subtle border.
     */
    public static JPanel createCardPanel() {
        return createCardPanel(20);
    }

    /**
     * Styled Card container panel with custom inner padding.
     */
    public static JPanel createCardPanel(int padding) {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER, 1, true),
                new EmptyBorder(padding, padding, padding, padding)
        ));
        return panel;
    }

    /**
     * Status Badge Pill label generator with rounded appearance.
     */
    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setForeground(fg);
        label.setFont(Theme.FONT_BADGE);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 80), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        return label;
    }

    /**
     * Helper to create contextual status badge label.
     */
    public static JLabel createStatusBadge(String status) {
        if (status == null) status = "UNKNOWN";
        String upper = status.toUpperCase();

        Color bg;
        Color fg;

        if (upper.contains("ACCEPTED") || upper.contains("OPEN") || upper.contains("ACTIVE") || upper.contains("EXPERT")) {
            bg = Theme.SUCCESS_BG;
            fg = Theme.SUCCESS_TEXT;
        } else if (upper.contains("REJECTED") || upper.contains("CLOSED") || upper.contains("CANCELLED")) {
            bg = Theme.DANGER_BG;
            fg = Theme.DANGER_TEXT;
        } else if (upper.contains("PENDING") || upper.contains("REVIEW") || upper.contains("ADVANCED")) {
            bg = Theme.WARNING_BG;
            fg = Theme.WARNING_TEXT;
        } else {
            bg = Theme.PURPLE_BADGE_BG;
            fg = Theme.PURPLE_BADGE_FG;
        }

        return createBadge(upper, bg, fg);
    }

    /**
     * Creates styled JTable with dark alternating rows, status badges, and electric purple selection.
     */
    public static JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFont(Theme.FONT_REGULAR);
        table.setRowHeight(38);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Theme.TABLE_BG);
        table.setForeground(Theme.TEXT_MAIN);
        table.setSelectionBackground(Theme.TABLE_SELECTION_BG);
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.TABLE_GRID);

        // Custom Cell Renderer with padding, alternating row colors, and status styling
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Theme.TABLE_BG : Theme.TABLE_ALT_BG);
                    c.setForeground(Theme.TEXT_MAIN);
                } else {
                    c.setBackground(Theme.TABLE_SELECTION_BG);
                    c.setForeground(Color.WHITE);
                }

                // If column header is ID or Date or Grad Year, center it
                String colName = t.getColumnName(column).toLowerCase();
                if (colName.contains("id") || colName.contains("date") || colName.contains("year") || colName.contains("cgpa")) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }

                // If column is status or proficiency, format text color
                if (colName.contains("status") || colName.contains("level") || colName.contains("proficiency")) {
                    String strVal = String.valueOf(value).toUpperCase();
                    if (!isSelected) {
                        if (strVal.contains("ACCEPTED") || strVal.contains("OPEN") || strVal.contains("ACTIVE") || strVal.contains("EXPERT")) {
                            setForeground(Theme.SUCCESS_TEXT);
                        } else if (strVal.contains("REJECTED") || strVal.contains("CLOSED")) {
                            setForeground(Theme.DANGER_TEXT);
                        } else if (strVal.contains("PENDING")) {
                            setForeground(Theme.WARNING_TEXT);
                        } else {
                            setForeground(Theme.ACCENT_SUBTLE);
                        }
                    }
                    setFont(Theme.FONT_BOLD);
                } else {
                    setFont(Theme.FONT_REGULAR);
                }

                return c;
            }
        };

        table.setDefaultRenderer(Object.class, defaultRenderer);
        return table;
    }

    /**
     * Helper to wrap a JTable into a styled JScrollPane.
     */
    public static JScrollPane createTableScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER, 1, true));
        scrollPane.getViewport().setBackground(Theme.TABLE_BG);
        return scrollPane;
    }

    /**
     * Styles standard text input component with dark theme tokens.
     */
    public static void styleInputField(JTextField field) {
        field.setBackground(Theme.INPUT_BG);
        field.setForeground(Theme.TEXT_MAIN);
        field.setCaretColor(Theme.PRIMARY);
        field.setFont(Theme.FONT_REGULAR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    /**
     * Styles standard text area component with dark theme tokens.
     */
    public static void styleTextArea(JTextArea area) {
        area.setBackground(Theme.INPUT_BG);
        area.setForeground(Theme.TEXT_MAIN);
        area.setCaretColor(Theme.PRIMARY);
        area.setFont(Theme.FONT_REGULAR);
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
    }

    /**
     * Creates a header banner panel with deep navy and purple glow.
     */
    public static JPanel createHeaderBanner(String title, String subtitle, String badgeText, JComponent rightComponent) {
        JPanel header = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle gradient from deep dark navy to midnight violet
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(13, 17, 30),
                        getWidth(), 0, new Color(34, 21, 60)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Bottom glowing accent line
                g2.setColor(new Color(139, 92, 246, 160));
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setBorder(new EmptyBorder(18, 28, 18, 28));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        if (badgeText != null && !badgeText.isEmpty()) {
            JLabel badge = createBadge(badgeText, Theme.PURPLE_BADGE_BG, Theme.PURPLE_BADGE_FG);
            badge.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftPanel.add(badge);
            leftPanel.add(Box.createVerticalStrut(6));
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_MAIN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(titleLabel);

        if (subtitle != null && !subtitle.isEmpty()) {
            leftPanel.add(Box.createVerticalStrut(4));
            JLabel subLabel = new JLabel(subtitle);
            subLabel.setFont(Theme.FONT_REGULAR);
            subLabel.setForeground(Theme.TEXT_MUTED);
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftPanel.add(subLabel);
        }

        header.add(leftPanel, BorderLayout.WEST);

        if (rightComponent != null) {
            JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightWrapper.setOpaque(false);
            rightWrapper.add(rightComponent);
            header.add(rightWrapper, BorderLayout.EAST);
        }

        return header;
    }
}
