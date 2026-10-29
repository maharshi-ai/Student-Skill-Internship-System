package com.college.internship.ui;

import com.college.internship.exception.AuthenticationException;
import com.college.internship.model.User;
import com.college.internship.service.AuthService;
import com.college.internship.ui.anim.PageTransitionContainer;
import com.college.internship.ui.components.AnimatedButton;
import com.college.internship.ui.components.AnimatedPasswordField;
import com.college.internship.ui.components.AnimatedTextField;
import com.college.internship.ui.components.GlassPanel;
import com.college.internship.ui.components.Icons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Commercial-Grade Authentication Screen.
 * Features a split-view composition with visual branding showcase,
 * animated form controls, eye password visibility toggle,
 * and seamless slide-and-fade transitions between Login and Registration.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private PageTransitionContainer authTransitionContainer;

    // Login Form Components
    private AnimatedTextField emailField;
    private AnimatedPasswordField passwordField;
    private JLabel loginErrorLabel;
    private AnimatedButton signInButton;

    // Registration Form Components
    private AnimatedTextField regNameField;
    private AnimatedTextField regEmailField;
    private AnimatedPasswordField regPasswordField;
    private AnimatedTextField regDeptField;
    private AnimatedTextField regPhoneField;
    private AnimatedTextField regCgpaField;
    private JSpinner regYearSpinner;
    private JTextArea regBioArea;
    private JLabel regErrorLabel;

    public LoginFrame() {
        this.authService = new AuthService();
        initUI();
    }

    private void initUI() {
        setTitle("CampusLink — Student Skill & Internship Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.BG_MAIN);

        // 1. Left Showcase Branding Panel (380px)
        JPanel leftBrandPanel = createLeftShowcasePanel();
        rootPanel.add(leftBrandPanel, BorderLayout.WEST);

        // 2. Right Interactive Authentication Panel (houses PageTransitionContainer)
        authTransitionContainer = new PageTransitionContainer();
        authTransitionContainer.showImmediate(createLoginFormPanel());

        rootPanel.add(authTransitionContainer, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    /**
     * Left visual composition with deep navy/purple gradient, abstract rings,
     * platform highlights, and institutional credentials.
     */
    private JPanel createLeftShowcasePanel() {
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Rich deep navy to midnight violet gradient
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(8, 12, 24),
                        w, h, new Color(34, 16, 68)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                // Subtle ambient glowing concentric circles in background
                g2.setColor(new Color(139, 92, 246, 20));
                g2.draw(new Ellipse2D.Float(-40, -40, 280, 280));
                g2.setColor(new Color(139, 92, 246, 12));
                g2.draw(new Ellipse2D.Float(-80, -80, 360, 360));

                g2.setColor(new Color(79, 70, 229, 15));
                g2.draw(new Ellipse2D.Float(w - 180, h - 180, 260, 260));

                // Right vertical separator border
                g2.setColor(new Color(45, 55, 80, 140));
                g2.drawLine(w - 1, 0, w - 1, h);

                g2.dispose();
            }
        };
        leftPanel.setPreferredSize(new Dimension(380, Integer.MAX_VALUE));
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(38, 36, 38, 36));

        // Top Brand Title
        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);

        // Logo Icon Badge
        JPanel logoBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = getWidth();
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, size, size, 12, 12);
                g2.setPaint(new GradientPaint(0, 0, new Color(139, 92, 246), size, size, new Color(99, 102, 241)));
                g2.fill(rect);
                Icon sp = Icons.get(Icons.Type.SPARKLE, 24, Color.WHITE);
                int ix = (size - sp.getIconWidth()) / 2;
                int iy = (size - sp.getIconHeight()) / 2;
                sp.paintIcon(this, g2, ix, iy);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(46, 46);
            }
        };
        logoBadge.setOpaque(false);
        logoBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandTitle = new JLabel("CampusLink");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandTag = new JLabel("SKILL & INTERNSHIP MANAGEMENT SYSTEM");
        brandTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        brandTag.setForeground(Theme.ACCENT_SUBTLE);
        brandTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        topBox.add(logoBadge);
        topBox.add(Box.createVerticalStrut(14));
        topBox.add(brandTitle);
        topBox.add(Box.createVerticalStrut(2));
        topBox.add(brandTag);

        leftPanel.add(topBox, BorderLayout.NORTH);

        // Center Feature Highlights
        JPanel centerFeatures = new JPanel();
        centerFeatures.setLayout(new BoxLayout(centerFeatures, BoxLayout.Y_AXIS));
        centerFeatures.setOpaque(false);

        JLabel introDesc = new JLabel("<html><body style='width: 250px; color: #CBD5E1; line-height: 1.4;'>" +
                "Empowering students and placement officers with real-time internship tracking, verified skill portfolios, and automated recruitment workflows.</body></html>");
        introDesc.setFont(Theme.FONT_REGULAR);
        introDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerFeatures.add(Box.createVerticalGlue());
        centerFeatures.add(introDesc);
        centerFeatures.add(Box.createVerticalStrut(24));

        centerFeatures.add(createFeatureItem("Direct Corporate Recruitment Matching"));
        centerFeatures.add(Box.createVerticalStrut(12));
        centerFeatures.add(createFeatureItem("Verified Academic & Technical Profiles"));
        centerFeatures.add(Box.createVerticalStrut(12));
        centerFeatures.add(createFeatureItem("Live Application Review Status & Feedback"));
        centerFeatures.add(Box.createVerticalGlue());

        leftPanel.add(centerFeatures, BorderLayout.CENTER);

        // Bottom Institutional Credential
        JLabel footerLbl = new JLabel("Training & Placement Division • Java 17 / MySQL");
        footerLbl.setFont(Theme.FONT_SMALL);
        footerLbl.setForeground(new Color(148, 163, 184, 160));
        leftPanel.add(footerLbl, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createFeatureItem(String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        item.setOpaque(false);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLbl = new JLabel(Icons.get(Icons.Type.CHECK, 16, new Color(52, 211, 153)));
        JLabel textLbl = new JLabel(text);
        textLbl.setFont(Theme.FONT_REGULAR);
        textLbl.setForeground(Theme.TEXT_SECONDARY);

        item.add(iconLbl);
        item.add(textLbl);
        return item;
    }

    /**
     * Right Interactive Login Form Panel.
     */
    private JPanel createLoginFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Theme.BG_MAIN);
        container.setBorder(new EmptyBorder(40, 48, 40, 48));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);

        // Header
        JLabel welcomeLbl = new JLabel("Welcome Back");
        welcomeLbl.setFont(Theme.FONT_TITLE);
        welcomeLbl.setForeground(Theme.TEXT_MAIN);
        welcomeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel("Enter your university credentials to access the portal");
        subLbl.setFont(Theme.FONT_REGULAR);
        subLbl.setForeground(Theme.TEXT_MUTED);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(welcomeLbl);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(subLbl);
        formCard.add(Box.createVerticalStrut(24));

        // Form Fields
        JLabel emailLbl = new JLabel("Email Address");
        emailLbl.setFont(Theme.FONT_BOLD);
        emailLbl.setForeground(Theme.TEXT_SECONDARY);
        emailLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new AnimatedTextField("e.g. student@college.edu");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(Theme.FONT_BOLD);
        passLbl.setForeground(Theme.TEXT_SECONDARY);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new AnimatedPasswordField("••••••••");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.addActionListener(e -> handleLogin());

        // Error message label
        loginErrorLabel = new JLabel(" ");
        loginErrorLabel.setFont(Theme.FONT_SMALL);
        loginErrorLabel.setForeground(Theme.DANGER_TEXT);
        loginErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(emailLbl);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(emailField);
        formCard.add(Box.createVerticalStrut(14));

        formCard.add(passLbl);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(loginErrorLabel);
        formCard.add(Box.createVerticalStrut(12));

        // Sign In Button
        signInButton = new AnimatedButton("Sign In to Portal", AnimatedButton.Style.PRIMARY);
        signInButton.setFont(Theme.FONT_SUBHEADER);
        signInButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        signInButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        signInButton.addActionListener(e -> handleLogin());

        formCard.add(signInButton);
        formCard.add(Box.createVerticalStrut(20));

        // Demo test accounts divider
        JLabel divider = new JLabel("— QUICK DEMO ACCESS —", SwingConstants.CENTER);
        divider.setFont(Theme.FONT_SMALL);
        divider.setForeground(Theme.TEXT_MUTED);
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(divider);
        formCard.add(Box.createVerticalStrut(10));

        // Quick demo buttons
        JPanel demoPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        demoPanel.setOpaque(false);
        demoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        demoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        AnimatedButton demoStudentBtn = new AnimatedButton("Demo Student", AnimatedButton.Style.SECONDARY);
        demoStudentBtn.setFont(Theme.FONT_SMALL);
        demoStudentBtn.addActionListener(e -> {
            emailField.setText("student@college.edu");
            passwordField.setText("student123");
            loginErrorLabel.setText(" ");
        });

        AnimatedButton demoAdminBtn = new AnimatedButton("Demo Admin", AnimatedButton.Style.SECONDARY);
        demoAdminBtn.setFont(Theme.FONT_SMALL);
        demoAdminBtn.addActionListener(e -> {
            emailField.setText("admin@college.edu");
            passwordField.setText("admin123");
            loginErrorLabel.setText(" ");
        });

        demoPanel.add(demoStudentBtn);
        demoPanel.add(demoAdminBtn);
        formCard.add(demoPanel);

        formCard.add(Box.createVerticalGlue());

        // Register prompt footer
        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regRow.setOpaque(false);
        regRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel noAccLbl = new JLabel("Don't have an account?");
        noAccLbl.setFont(Theme.FONT_REGULAR);
        noAccLbl.setForeground(Theme.TEXT_MUTED);

        JButton regLink = new JButton("Register as Student");
        regLink.setFont(Theme.FONT_BOLD);
        regLink.setForeground(Theme.ACCENT_SUBTLE);
        regLink.setBorderPainted(false);
        regLink.setContentAreaFilled(false);
        regLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regLink.addActionListener(e -> {
            // Smoothly slide-transition right panel to Register View
            authTransitionContainer.transitionTo(createRegisterFormPanel(), PageTransitionContainer.TransitionDirection.FORWARD);
        });

        regRow.add(noAccLbl);
        regRow.add(regLink);
        formCard.add(regRow);

        container.add(formCard, BorderLayout.CENTER);
        return container;
    }

    /**
     * Right Interactive Registration Form Panel with smooth transition back to Login.
     */
    private JPanel createRegisterFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Theme.BG_MAIN);
        container.setBorder(new EmptyBorder(26, 44, 26, 44));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);

        // Header
        JLabel titleLbl = new JLabel("Student Registration");
        titleLbl.setFont(Theme.FONT_TITLE);
        titleLbl.setForeground(Theme.TEXT_MAIN);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel("Join the campus placement network and build your profile");
        subLbl.setFont(Theme.FONT_REGULAR);
        subLbl.setForeground(Theme.TEXT_MUTED);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(titleLbl);
        formCard.add(Box.createVerticalStrut(3));
        formCard.add(subLbl);
        formCard.add(Box.createVerticalStrut(14));

        // Form Fields (2 columns)
        JPanel gridFields = new JPanel(new GridLayout(4, 2, 14, 10));
        gridFields.setOpaque(false);
        gridFields.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        gridFields.setAlignmentX(Component.LEFT_ALIGNMENT);

        regNameField = new AnimatedTextField("John Doe");
        regEmailField = new AnimatedTextField("john@college.edu");
        regPasswordField = new AnimatedPasswordField("Choose password");
        regDeptField = new AnimatedTextField("Computer Science");
        regPhoneField = new AnimatedTextField("+1 234 567 890");
        regCgpaField = new AnimatedTextField("e.g. 8.5");

        regYearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2020, 2030, 1));
        regYearSpinner.setFont(Theme.FONT_REGULAR);

        gridFields.add(createLabeledField("Full Name *", regNameField));
        gridFields.add(createLabeledField("Email Address *", regEmailField));
        gridFields.add(createLabeledField("Password *", regPasswordField));
        gridFields.add(createLabeledField("Department *", regDeptField));
        gridFields.add(createLabeledField("Contact Phone", regPhoneField));
        gridFields.add(createLabeledField("CGPA (0.0 - 10.0) *", regCgpaField));
        gridFields.add(createLabeledField("Graduation Year", regYearSpinner));

        formCard.add(gridFields);
        formCard.add(Box.createVerticalStrut(10));

        // Bio Area
        JLabel bioLbl = new JLabel("Short Bio / Overview");
        bioLbl.setFont(Theme.FONT_BOLD);
        bioLbl.setForeground(Theme.TEXT_SECONDARY);
        bioLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        regBioArea = new JTextArea(2, 20);
        Components.styleTextArea(regBioArea);
        regBioArea.setLineWrap(true);
        regBioArea.setWrapStyleWord(true);
        JScrollPane bioScroll = new JScrollPane(regBioArea);
        bioScroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));
        bioScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        bioScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(bioLbl);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(bioScroll);
        formCard.add(Box.createVerticalStrut(6));

        // Error message
        regErrorLabel = new JLabel(" ");
        regErrorLabel.setFont(Theme.FONT_SMALL);
        regErrorLabel.setForeground(Theme.DANGER_TEXT);
        regErrorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(regErrorLabel);
        formCard.add(Box.createVerticalStrut(10));

        // Actions: Register Button + Cancel
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        AnimatedButton backBtn = new AnimatedButton("Back to Sign In", AnimatedButton.Style.SECONDARY);
        backBtn.addActionListener(e -> {
            authTransitionContainer.transitionTo(createLoginFormPanel(), PageTransitionContainer.TransitionDirection.BACKWARD);
        });

        AnimatedButton registerBtn = new AnimatedButton("Complete Registration", AnimatedButton.Style.PRIMARY);
        registerBtn.addActionListener(e -> handleRegister());

        btnRow.add(backBtn);
        btnRow.add(registerBtn);
        formCard.add(btnRow);

        container.add(formCard, BorderLayout.CENTER);
        return container;
    }

    private JPanel createLabeledField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Please enter both email and password.");
            return;
        }

        loginErrorLabel.setText(" ");
        signInButton.setEnabled(false);
        signInButton.setText("Signing in...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.login(email, password);
            }

            @Override
            protected void done() {
                signInButton.setEnabled(true);
                signInButton.setText("Sign In to Portal");

                try {
                    User user = get();
                    if (user.getRole() == User.Role.STUDENT) {
                        new StudentDashboardFrame(user.getId()).setVisible(true);
                    } else {
                        new AdminDashboardFrame(user.getId()).setVisible(true);
                    }
                    dispose();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    loginErrorLabel.setText(cause.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleRegister() {
        try {
            String name = regNameField.getText().trim();
            String email = regEmailField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            String dept = regDeptField.getText().trim();
            String phone = regPhoneField.getText().trim();
            String cgpaStr = regCgpaField.getText().trim();
            double cgpa = Double.parseDouble(cgpaStr.isEmpty() ? "0" : cgpaStr);
            int year = (Integer) regYearSpinner.getValue();
            String bio = regBioArea.getText().trim();

            authService.registerStudent(name, email, password, dept, phone, cgpa, year, bio);

            JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now log in with your credentials.",
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE);

            // Transition back to login with email prefilled
            JPanel loginPanel = createLoginFormPanel();
            emailField.setText(email);
            authTransitionContainer.transitionTo(loginPanel, PageTransitionContainer.TransitionDirection.BACKWARD);

        } catch (NumberFormatException ex) {
            regErrorLabel.setText("Please enter a valid numeric CGPA (e.g. 8.5).");
        } catch (Exception ex) {
            regErrorLabel.setText(ex.getMessage());
        }
    }
}

