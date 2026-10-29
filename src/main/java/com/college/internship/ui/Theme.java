package com.college.internship.ui;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Design system tokens and Theme initialization for Java Swing UI.
 * Premium Dark Navy & Purple Theme.
 */
public class Theme {

    // Dark Navy Backgrounds
    public static final Color BG_MAIN = new Color(11, 15, 25);          // #0B0F19 - Deepest dark navy
    public static final Color BG_SURFACE = new Color(17, 24, 39);       // #111827 - Surface navy
    public static final Color CARD_BG = new Color(22, 30, 49);          // #161E31 - Elevated card background
    public static final Color CARD_BG_HOVER = new Color(28, 38, 62);    // #1C263E
    public static final Color CARD_BORDER = new Color(42, 54, 79);      // #2A364F - Subtle crisp border
    public static final Color HEADER_BG = new Color(13, 17, 30);        // #0D111E - Deep navy banner

    // Purple / Violet Accents
    public static final Color PRIMARY = new Color(139, 92, 246);         // #8B5CF6 - Vibrant Purple
    public static final Color PRIMARY_HOVER = new Color(124, 58, 237);   // #7C3AED - Electric Violet
    public static final Color PRIMARY_DARK = new Color(109, 40, 217);    // #6D28D9 - Deep Violet
    public static final Color ACCENT = new Color(168, 85, 247);          // #A855F7 - Bright Purple
    public static final Color ACCENT_SUBTLE = new Color(192, 132, 252);  // #C084FC - Soft Lavender
    public static final Color PURPLE_BADGE_BG = new Color(59, 7, 100, 190);
    public static final Color PURPLE_BADGE_FG = new Color(216, 180, 254);

    // Secondary / Dark Slate Controls
    public static final Color SECONDARY_BG = new Color(30, 41, 59);      // #1E293B
    public static final Color SECONDARY_HOVER = new Color(51, 65, 85);   // #334155
    public static final Color SECONDARY_BORDER = new Color(71, 85, 105); // #475569
    public static final Color BORDER_COLOR = new Color(42, 54, 79);      // #2A364F

    // Typography
    public static final Color TEXT_MAIN = new Color(248, 250, 252);     // #F8FAFC - Crisp pure light
    public static final Color TEXT_SECONDARY = new Color(203, 213, 225); // #CBD5E1 - Light Slate
    public static final Color TEXT_MUTED = new Color(148, 163, 184);    // #94A3B8 - Muted Gray/Slate
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;

    // Status Colors (Calibrated for dark mode contrast)
    public static final Color SUCCESS_BG = new Color(6, 78, 59, 180);
    public static final Color SUCCESS_BORDER = new Color(5, 150, 105);
    public static final Color SUCCESS_TEXT = new Color(52, 211, 153);
    public static final Color SUCCESS_BUTTON = new Color(16, 185, 129);
    public static final Color SUCCESS_BUTTON_HOVER = new Color(5, 150, 105);

    public static final Color WARNING_BG = new Color(120, 53, 15, 180);
    public static final Color WARNING_BORDER = new Color(217, 119, 6);
    public static final Color WARNING_TEXT = new Color(251, 191, 36);

    public static final Color DANGER_BG = new Color(127, 29, 29, 180);
    public static final Color DANGER_BORDER = new Color(220, 38, 38);
    public static final Color DANGER_TEXT = new Color(248, 113, 113);
    public static final Color DANGER_BUTTON = new Color(220, 38, 38);
    public static final Color DANGER_BUTTON_HOVER = new Color(185, 28, 28);

    public static final Color INFO_BG = new Color(30, 58, 138, 180);
    public static final Color INFO_BORDER = new Color(37, 99, 235);
    public static final Color INFO_TEXT = new Color(147, 197, 253);

    // Inputs & Tables
    public static final Color INPUT_BG = new Color(15, 23, 42);          // #0F172A
    public static final Color INPUT_BORDER = new Color(51, 65, 85);      // #334155
    public static final Color TABLE_BG = new Color(17, 24, 39);          // #111827
    public static final Color TABLE_ALT_BG = new Color(22, 31, 50);      // #161F32
    public static final Color TABLE_SELECTION_BG = new Color(124, 58, 237);// #7C3AED
    public static final Color TABLE_GRID = new Color(34, 45, 68);
    public static final Color TABLE_HEADER_BG = new Color(13, 18, 32);   // #0D1220
    public static final Color TABLE_HEADER_FG = new Color(216, 180, 254); // #D8B4FE

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Setup FlatLaf Dark Look and Feel with custom Navy & Purple styling.
     */
    public static void setupTheme() {
        try {
            FlatDarkLaf.setup();

            // Core UI Arcs and Radius
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 10);

            // TabbedPane Styling
            UIManager.put("TabbedPane.background", BG_MAIN);
            UIManager.put("TabbedPane.foreground", TEXT_MUTED);
            UIManager.put("TabbedPane.selectedBackground", CARD_BG);
            UIManager.put("TabbedPane.selectedForeground", ACCENT_SUBTLE);
            UIManager.put("TabbedPane.underlineColor", PRIMARY);
            UIManager.put("TabbedPane.underlineHeight", 3);
            UIManager.put("TabbedPane.focusColor", new Color(0, 0, 0, 0));
            UIManager.put("TabbedPane.contentBorderColor", CARD_BORDER);
            UIManager.put("TabbedPane.font", FONT_BOLD);

            // Table Styling
            UIManager.put("Table.background", TABLE_BG);
            UIManager.put("Table.foreground", TEXT_MAIN);
            UIManager.put("Table.alternateRowColor", TABLE_ALT_BG);
            UIManager.put("Table.selectionBackground", TABLE_SELECTION_BG);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("Table.gridColor", TABLE_GRID);
            UIManager.put("Table.rowHeight", 38);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);

            UIManager.put("TableHeader.background", TABLE_HEADER_BG);
            UIManager.put("TableHeader.foreground", TABLE_HEADER_FG);
            UIManager.put("TableHeader.font", FONT_BOLD);
            UIManager.put("TableHeader.separatorColor", TABLE_GRID);
            UIManager.put("TableHeader.bottomSeparatorColor", PRIMARY_HOVER);

            // Inputs Styling
            UIManager.put("TextField.background", INPUT_BG);
            UIManager.put("TextField.foreground", TEXT_MAIN);
            UIManager.put("TextField.caretColor", PRIMARY);
            UIManager.put("TextField.borderColor", INPUT_BORDER);
            UIManager.put("TextField.focusedBorderColor", PRIMARY);

            UIManager.put("PasswordField.background", INPUT_BG);
            UIManager.put("PasswordField.foreground", TEXT_MAIN);
            UIManager.put("PasswordField.caretColor", PRIMARY);
            UIManager.put("PasswordField.borderColor", INPUT_BORDER);
            UIManager.put("PasswordField.focusedBorderColor", PRIMARY);

            UIManager.put("TextArea.background", INPUT_BG);
            UIManager.put("TextArea.foreground", TEXT_MAIN);
            UIManager.put("TextArea.caretColor", PRIMARY);

            UIManager.put("ComboBox.background", INPUT_BG);
            UIManager.put("ComboBox.foreground", TEXT_MAIN);
            UIManager.put("ComboBox.selectionBackground", PRIMARY);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);

            UIManager.put("Spinner.background", INPUT_BG);
            UIManager.put("Spinner.foreground", TEXT_MAIN);

            // Popup & Dialog Styling
            UIManager.put("OptionPane.background", CARD_BG);
            UIManager.put("OptionPane.messageForeground", TEXT_MAIN);
            UIManager.put("Panel.background", BG_MAIN);

        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
    }
}
