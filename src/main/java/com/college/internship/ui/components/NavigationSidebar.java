package com.college.internship.ui.components;

import com.college.internship.ui.Theme;
import com.college.internship.ui.anim.Easing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Premium SaaS navigation sidebar featuring branding, user profile summary,
 * and a smooth, animated gliding active-tab indicator pill.
 */
public class NavigationSidebar extends JPanel {

    public static class NavItem {
        public final String id;
        public final String label;
        public final Icons.Type icon;

        public NavItem(String id, String label, Icons.Type icon) {
            this.id = id;
            this.label = label;
            this.icon = icon;
        }
    }

    private final List<NavItem> items = new ArrayList<>();
    private final List<Rectangle> itemBounds = new ArrayList<>();
    private int selectedIndex = 0;
    private Consumer<Integer> onSelectListener;

    // Gliding indicator animation state
    private float indicatorY = 0f;
    private float targetIndicatorY = 0f;
    private float startIndicatorY = 0f;
    private Timer glideTimer;

    private static final int ITEM_HEIGHT = 44;
    private static final int ITEM_SPACING = 6;
    private static final int SIDEBAR_WIDTH = 250;

    public NavigationSidebar(String appTitle, String appSubtitle,
                             String userName, String userRole,
                             List<NavItem> navItems,
                             Runnable onLogout,
                             Consumer<Integer> onSelect) {

        this.items.addAll(navItems);
        this.onSelectListener = onSelect;

        setPreferredSize(new Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));
        setBackground(new Color(13, 19, 34)); // Dark surface level 1
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.CARD_BORDER));

        // Top Brand Header
        JPanel brandPanel = createBrandPanel(appTitle, appSubtitle);
        add(brandPanel, BorderLayout.NORTH);

        // Center Navigation Items List
        JPanel navListPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Compute item bounds if needed
                int yOffset = 15;
                itemBounds.clear();
                for (int i = 0; i < items.size(); i++) {
                    Rectangle r = new Rectangle(14, yOffset, getWidth() - 28, ITEM_HEIGHT);
                    itemBounds.add(r);
                    yOffset += ITEM_HEIGHT + ITEM_SPACING;
                }

                // Initialize indicator position on first paint
                if (indicatorY == 0f && !itemBounds.isEmpty()) {
                    indicatorY = itemBounds.get(selectedIndex).y;
                    targetIndicatorY = indicatorY;
                }

                // 1. Draw gliding active indicator pill
                if (!itemBounds.isEmpty()) {
                    int w = getWidth() - 28;
                    RoundRectangle2D pill = new RoundRectangle2D.Float(14, indicatorY, w, ITEM_HEIGHT, 10, 10);

                    // Purple glowing pill gradient
                    GradientPaint gp = new GradientPaint(
                            14, indicatorY, new Color(139, 92, 246, 210),
                            14 + w, indicatorY + ITEM_HEIGHT, new Color(109, 40, 217, 240)
                    );
                    g2.setPaint(gp);
                    g2.fill(pill);

                    // Glowing left accent marker
                    g2.setColor(new Color(216, 180, 254));
                    g2.fill(new RoundRectangle2D.Float(16, indicatorY + 8, 3, ITEM_HEIGHT - 16, 2, 2));
                }

                // 2. Draw Nav Items (Icons & Text)
                for (int i = 0; i < items.size(); i++) {
                    NavItem item = items.get(i);
                    Rectangle bounds = itemBounds.get(i);
                    boolean isSelected = (i == selectedIndex);

                    Color textColor = isSelected ? Color.WHITE : Theme.TEXT_MUTED;
                    Color iconColor = isSelected ? Color.WHITE : Theme.TEXT_MUTED;

                    // Draw Vector Icon
                    Icon icon = Icons.get(item.icon, 18, iconColor);
                    int iconX = bounds.x + 16;
                    int iconY = bounds.y + (bounds.height - icon.getIconHeight()) / 2;
                    icon.paintIcon(this, g2, iconX, iconY);

                    // Draw Label Text
                    g2.setColor(textColor);
                    g2.setFont(isSelected ? Theme.FONT_BOLD : Theme.FONT_REGULAR);
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = iconX + 28;
                    int textY = bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(item.label, textX, textY);
                }

                g2.dispose();
            }
        };
        navListPanel.setOpaque(false);

        // Click & Hover Handling
        navListPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    for (int i = 0; i < itemBounds.size(); i++) {
                        if (itemBounds.get(i).contains(e.getPoint())) {
                            setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        navListPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean hoveringItem = false;
                for (Rectangle r : itemBounds) {
                    if (r.contains(e.getPoint())) {
                        hoveringItem = true;
                        break;
                    }
                }
                navListPanel.setCursor(new Cursor(hoveringItem ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });

        add(navListPanel, BorderLayout.CENTER);

        // Bottom User Profile Summary & Logout
        JPanel bottomPanel = createUserProfilePanel(userName, userRole, onLogout);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createBrandPanel(String title, String subtitle) {
        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(22, 20, 20, 20));

        // Brand Vector Logo Badge
        JPanel logoBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = getWidth();
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, size, size, 10, 10);
                g2.setPaint(new GradientPaint(0, 0, new Color(139, 92, 246), size, size, new Color(79, 70, 229)));
                g2.fill(rect);

                Icon sp = Icons.get(Icons.Type.SPARKLE, 20, Color.WHITE);
                int ix = (size - sp.getIconWidth()) / 2;
                int iy = (size - sp.getIconHeight()) / 2;
                sp.paintIcon(this, g2, ix, iy);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(36, 36);
            }
        };
        logoBadge.setOpaque(false);

        JPanel textBlock = new JPanel();
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Theme.FONT_SUBHEADER);
        titleLbl.setForeground(Theme.TEXT_MAIN);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(Theme.FONT_SMALL);
        subLbl.setForeground(Theme.TEXT_MUTED);

        textBlock.add(titleLbl);
        textBlock.add(Box.createVerticalStrut(1));
        textBlock.add(subLbl);

        brand.add(logoBadge, BorderLayout.WEST);
        brand.add(textBlock, BorderLayout.CENTER);

        return brand;
    }

    private JPanel createUserProfilePanel(String userName, String role, Runnable onLogout) {
        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setBackground(new Color(10, 14, 26)); // Slightly darker footer
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.CARD_BORDER),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Avatar Initials Circle
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = getWidth();
                g2.setColor(new Color(30, 41, 59));
                g2.fill(new Ellipse2D.Float(0, 0, size, size));

                g2.setColor(Theme.ACCENT_SUBTLE);
                g2.setFont(Theme.FONT_BOLD);
                String initial = (userName != null && !userName.trim().isEmpty()) ?
                        userName.trim().substring(0, 1).toUpperCase() : "U";

                FontMetrics fm = g2.getFontMetrics();
                int tx = (size - fm.stringWidth(initial)) / 2;
                int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initial, tx, ty);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(34, 34);
            }
        };
        avatar.setOpaque(false);

        JPanel userBlock = new JPanel();
        userBlock.setLayout(new BoxLayout(userBlock, BoxLayout.Y_AXIS));
        userBlock.setOpaque(false);

        String displayName = userName != null ? userName : "User";
        if (displayName.length() > 14) {
            displayName = displayName.substring(0, 12) + "..";
        }
        JLabel nameLbl = new JLabel(displayName);
        nameLbl.setFont(Theme.FONT_BOLD);
        nameLbl.setForeground(Theme.TEXT_MAIN);

        JLabel roleLbl = new JLabel(role != null ? role.toUpperCase() : "STUDENT");
        roleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        roleLbl.setForeground(Theme.ACCENT_SUBTLE);

        userBlock.add(nameLbl);
        userBlock.add(roleLbl);

        // Sleek Logout Button
        JButton logoutBtn = new JButton(Icons.get(Icons.Type.LOGOUT, 18, Theme.TEXT_MUTED));
        logoutBtn.setToolTipText("Sign Out");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });

        bottom.add(avatar, BorderLayout.WEST);
        bottom.add(userBlock, BorderLayout.CENTER);
        bottom.add(logoutBtn, BorderLayout.EAST);

        return bottom;
    }

    public void setSelectedIndex(int newIndex) {
        if (newIndex < 0 || newIndex >= items.size() || newIndex == selectedIndex) {
            return;
        }

        int previousIndex = selectedIndex;
        this.selectedIndex = newIndex;

        // Smoothly glide active pill indicator
        if (glideTimer != null && glideTimer.isRunning()) {
            glideTimer.stop();
        }

        if (!itemBounds.isEmpty() && newIndex < itemBounds.size()) {
            startIndicatorY = indicatorY;
            targetIndicatorY = itemBounds.get(newIndex).y;
            long startTime = System.currentTimeMillis();

            glideTimer = new Timer(16, e -> {
                long elapsed = System.currentTimeMillis() - startTime;
                float rawT = Math.min(1f, (float) elapsed / 220f); // 220ms fluid glide
                float t = Easing.easeInOutCubic(rawT);
                indicatorY = startIndicatorY + (targetIndicatorY - startIndicatorY) * t;

                if (rawT >= 1f) {
                    indicatorY = targetIndicatorY;
                    glideTimer.stop();
                }
                repaint();
            });
            glideTimer.start();
        } else {
            repaint();
        }

        if (onSelectListener != null) {
            onSelectListener.accept(newIndex);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
