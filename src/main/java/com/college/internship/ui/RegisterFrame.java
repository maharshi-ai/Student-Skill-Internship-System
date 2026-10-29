package com.college.internship.ui;

import com.college.internship.service.AuthService;
import com.college.internship.ui.components.AnimatedButton;
import com.college.internship.ui.components.AnimatedPasswordField;
import com.college.internship.ui.components.AnimatedTextField;
import com.college.internship.ui.components.GlassPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Student Registration Screen.
 * Styled with a modern Dark Navy and Purple aesthetic, animated inputs,
 * and eye password toggle.
 */
public class RegisterFrame extends JFrame {

    private final AuthService authService;

    private AnimatedTextField nameField;
    private AnimatedTextField emailField;
    private AnimatedPasswordField passwordField;
    private AnimatedTextField deptField;
    private AnimatedTextField phoneField;
    private AnimatedTextField cgpaField;
    private JSpinner yearSpinner;
    private JTextArea bioArea;
    private JLabel errorLabel;

    public RegisterFrame() {
        this.authService = new AuthService();
        initUI();
    }

    private void initUI() {
        setTitle("Student Registration - Skill & Internship System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 740);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        GlassPanel cardPanel = new GlassPanel(16, false);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(new EmptyBorder(22, 26, 22, 26));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header Section
        JPanel headerBox = new JPanel();
        headerBox.setLayout(new BoxLayout(headerBox, BoxLayout.Y_AXIS));
        headerBox.setOpaque(false);

        JLabel badge = Components.createBadge("STUDENT ONBOARDING", Theme.PURPLE_BADGE_BG, Theme.PURPLE_BADGE_FG);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Create Student Account");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_MAIN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Join the campus internship & skill tracking portal");
        subLabel.setFont(Theme.FONT_REGULAR);
        subLabel.setForeground(Theme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerBox.add(badge);
        headerBox.add(Box.createVerticalStrut(8));
        headerBox.add(titleLabel);
        headerBox.add(Box.createVerticalStrut(4));
        headerBox.add(subLabel);
        headerBox.add(Box.createVerticalStrut(14));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(headerBox, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields
        nameField = new AnimatedTextField("e.g. John Doe");
        nameField.setPreferredSize(new Dimension(260, 38));
        addFormField(cardPanel, "Full Name *", nameField, 1, gbc);

        emailField = new AnimatedTextField("e.g. john@college.edu");
        emailField.setPreferredSize(new Dimension(260, 38));
        addFormField(cardPanel, "Email Address *", emailField, 2, gbc);

        // Password with eye visibility toggle
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel passLbl = new JLabel("Password *");
        passLbl.setFont(Theme.FONT_BOLD);
        passLbl.setForeground(Theme.TEXT_SECONDARY);
        cardPanel.add(passLbl, gbc);

        gbc.gridx = 1;
        passwordField = new AnimatedPasswordField("Choose a password");
        passwordField.setPreferredSize(new Dimension(260, 38));
        cardPanel.add(passwordField, gbc);

        deptField = new AnimatedTextField("e.g. Computer Science");
        deptField.setPreferredSize(new Dimension(260, 38));
        addFormField(cardPanel, "Department *", deptField, 4, gbc);

        phoneField = new AnimatedTextField("e.g. +1 234 567 890");
        phoneField.setPreferredSize(new Dimension(260, 38));
        addFormField(cardPanel, "Contact Phone", phoneField, 5, gbc);

        cgpaField = new AnimatedTextField("e.g. 8.5");
        cgpaField.setPreferredSize(new Dimension(260, 38));
        addFormField(cardPanel, "CGPA (0.0 - 10.0) *", cgpaField, 6, gbc);

        // Graduation Year Spinner
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel yearLbl = new JLabel("Graduation Year");
        yearLbl.setFont(Theme.FONT_BOLD);
        yearLbl.setForeground(Theme.TEXT_SECONDARY);
        cardPanel.add(yearLbl, gbc);

        gbc.gridx = 1;
        yearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2020, 2030, 1));
        yearSpinner.setFont(Theme.FONT_REGULAR);
        yearSpinner.setPreferredSize(new Dimension(260, 36));
        cardPanel.add(yearSpinner, gbc);

        // Bio
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel bioLbl = new JLabel("Short Bio / Overview");
        bioLbl.setFont(Theme.FONT_BOLD);
        bioLbl.setForeground(Theme.TEXT_SECONDARY);
        cardPanel.add(bioLbl, gbc);

        gbc.gridx = 1;
        bioArea = new JTextArea(3, 20);
        Components.styleTextArea(bioArea);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        JScrollPane bioScroll = new JScrollPane(bioArea);
        bioScroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));
        cardPanel.add(bioScroll, gbc);

        // Error message
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.DANGER_TEXT);
        cardPanel.add(errorLabel, gbc);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setOpaque(false);

        AnimatedButton cancelBtn = new AnimatedButton("Cancel", AnimatedButton.Style.SECONDARY);
        cancelBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        AnimatedButton submitBtn = new AnimatedButton("Register Account", AnimatedButton.Style.PRIMARY);
        submitBtn.addActionListener(e -> handleRegister());

        btnPanel.add(cancelBtn);
        btnPanel.add(submitBtn);

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 10, 5, 10);
        cardPanel.add(btnPanel, gbc);

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, int row, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.TEXT_SECONDARY);
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void handleRegister() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String dept = deptField.getText().trim();
            String phone = phoneField.getText().trim();
            double cgpa = Double.parseDouble(cgpaField.getText().trim().isEmpty() ? "0" : cgpaField.getText().trim());
            int year = (Integer) yearSpinner.getValue();
            String bio = bioArea.getText().trim();

            authService.registerStudent(name, email, password, dept, phone, cgpa, year, bio);

            JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now log in.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            new LoginFrame().setVisible(true);
            dispose();

        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter a valid numeric CGPA (e.g. 8.5).");
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
        }
    }
}

