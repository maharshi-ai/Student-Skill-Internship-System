package com.college.internship.ui;

import com.college.internship.model.*;
import com.college.internship.service.ApplicationService;
import com.college.internship.service.InternshipService;
import com.college.internship.service.StudentService;
import com.college.internship.ui.anim.PageTransitionContainer;
import com.college.internship.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Top-tier Student Dashboard featuring:
 * - Refined SaaS Navigation Sidebar with animated gliding active pill
 * - Smooth 60 FPS directional slide-and-fade Page Transitions
 * - Animated KPI statistic cards (CGPA, Skills, Applications, Opportunities)
 * - Interactive Internship opportunity cards with detail modal
 * - My Applications timeline tracker with live admin feedback
 * - Profile and Technical Skills portfolio management
 */
public class StudentDashboardFrame extends JFrame {

    private final int studentId;
    private final StudentService studentService;
    private final InternshipService internshipService;
    private final ApplicationService applicationService;

    private Student currentStudent;
    private List<Internship> displayedInternships = new ArrayList<>();
    private List<Application> studentApplications = new ArrayList<>();

    // Navigation & Page Transition Architecture
    private NavigationSidebar sidebar;
    private PageTransitionContainer pageContainer;
    private int activePageIndex = 0;

    // View Panels
    private JPanel overviewView;
    private JPanel internshipsView;
    private JPanel applicationsView;
    private JPanel profileView;

    // Browse Internships components
    private AnimatedTextField searchField;
    private JPanel internshipCardsContainer;

    // My Applications components
    private JPanel appCardsContainer;
    private DefaultTableModel appsTableModel;
    private JTable appsTable;
    private JTextArea appDetailArea;

    // Profile & Skills components
    private AnimatedTextField nameField, deptField, phoneField, cgpaField;
    private JSpinner yearSpinner;
    private JTextArea bioArea;
    private JComboBox<Skill> masterSkillCombo;
    private JComboBox<StudentSkill.ProficiencyLevel> levelCombo;
    private DefaultTableModel skillsTableModel;
    private JTable skillsTable;

    public StudentDashboardFrame(int studentId) {
        this.studentId = studentId;
        this.studentService = new StudentService();
        this.internshipService = new InternshipService();
        this.applicationService = new ApplicationService();

        loadStudentData();
        initUI();
    }

    private void loadStudentData() {
        try {
            this.currentStudent = studentService.getStudentProfile(studentId);
            this.displayedInternships = internshipService.getOpenInternships();
            this.studentApplications = applicationService.getStudentApplications(studentId);
        } catch (Exception e) {
            System.err.println("Error loading student data: " + e.getMessage());
        }
    }

    private void initUI() {
        String studentName = currentStudent != null ? currentStudent.getName() : "Student";
        setTitle("CampusLink — " + studentName + " (Student Portal)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.BG_MAIN);

        // 1. Build Navigation Sidebar with Gliding Active Pill
        List<NavigationSidebar.NavItem> navItems = List.of(
                new NavigationSidebar.NavItem("overview", "Dashboard Overview", Icons.Type.DASHBOARD),
                new NavigationSidebar.NavItem("internships", "Browse Internships", Icons.Type.BRIEFCASE),
                new NavigationSidebar.NavItem("applications", "My Applications", Icons.Type.APPLICATIONS),
                new NavigationSidebar.NavItem("profile", "Profile & Skills", Icons.Type.PROFILE)
        );

        sidebar = new NavigationSidebar(
                "CampusLink",
                "STUDENT PORTAL",
                studentName,
                "STUDENT",
                navItems,
                this::handleLogout,
                this::handleNavigationChange
        );
        rootPanel.add(sidebar, BorderLayout.WEST);

        // 2. Build Page Transition Container for Canvas
        pageContainer = new PageTransitionContainer();
        rootPanel.add(pageContainer, BorderLayout.CENTER);

        // Pre-build initial view and show immediately
        overviewView = createOverviewView();
        pageContainer.showImmediate(overviewView);

        setContentPane(rootPanel);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out of the portal?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    private void handleNavigationChange(int newIndex) {
        if (newIndex == activePageIndex) return;

        PageTransitionContainer.TransitionDirection dir = (newIndex > activePageIndex) ?
                PageTransitionContainer.TransitionDirection.FORWARD :
                PageTransitionContainer.TransitionDirection.BACKWARD;

        this.activePageIndex = newIndex;
        JPanel targetPanel;

        switch (newIndex) {
            case 0 -> {
                overviewView = createOverviewView();
                targetPanel = overviewView;
            }
            case 1 -> {
                internshipsView = createInternshipsView();
                targetPanel = internshipsView;
            }
            case 2 -> {
                applicationsView = createApplicationsView();
                targetPanel = applicationsView;
            }
            case 3 -> {
                profileView = createProfileView();
                targetPanel = profileView;
            }
            default -> targetPanel = overviewView;
        }

        pageContainer.transitionTo(targetPanel, dir);
    }

    // =========================================================================
    // PAGE 1: DASHBOARD OVERVIEW
    // =========================================================================
    private JPanel createOverviewView() {
        // Reload fresh counts
        loadStudentData();

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Theme.BG_MAIN);
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));

        // Top Greeting Banner
        JPanel greetingBox = new JPanel();
        greetingBox.setLayout(new BoxLayout(greetingBox, BoxLayout.Y_AXIS));
        greetingBox.setOpaque(false);

        String greeting = getGreetingTime();
        String studentName = currentStudent != null ? currentStudent.getName() : "Student";
        JLabel greetTitle = new JLabel(greeting + ", " + studentName + " 👋");
        greetTitle.setFont(Theme.FONT_TITLE);
        greetTitle.setForeground(Theme.TEXT_MAIN);

        String dept = currentStudent != null ? currentStudent.getDepartment() : "General";
        int year = currentStudent != null ? currentStudent.getGraduationYear() : 2025;
        JLabel greetSubtitle = new JLabel(dept + " Department • Class of " + year + " • Campus Internship & Placement System");
        greetSubtitle.setFont(Theme.FONT_REGULAR);
        greetSubtitle.setForeground(Theme.TEXT_MUTED);

        greetingBox.add(greetTitle);
        greetingBox.add(Box.createVerticalStrut(4));
        greetingBox.add(greetSubtitle);

        panel.add(greetingBox, BorderLayout.NORTH);

        // Center Scrollable Body (Stats + Quick Actions + Recent Activity)
        JPanel centerBody = new JPanel();
        centerBody.setLayout(new BoxLayout(centerBody, BoxLayout.Y_AXIS));
        centerBody.setOpaque(false);

        // 1. KPI Statistic Cards Grid (4 columns)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        double cgpa = currentStudent != null ? currentStudent.getCgpa() : 0.0;
        int skillsCount = (currentStudent != null && currentStudent.getSkills() != null) ? currentStudent.getSkills().size() : 0;
        int appsCount = studentApplications != null ? studentApplications.size() : 0;
        int openCount = displayedInternships != null ? displayedInternships.size() : 0;

        StatCard cgpaCard = new StatCard("ACADEMIC CGPA", cgpa, "%.2f", "Scale: 10.0", Icons.Type.AWARD, new Color(139, 92, 246));
        StatCard skillsCard = new StatCard("VERIFIED SKILLS", skillsCount, "%.0f", "Portfolio Count", Icons.Type.SPARKLE, new Color(16, 185, 129));
        StatCard appsCard = new StatCard("APPLICATIONS", appsCount, "%.0f", "Submitted Pipeline", Icons.Type.APPLICATIONS, new Color(56, 189, 248));
        StatCard openCard = new StatCard("OPEN OPENINGS", openCount, "%.0f", "Corporate Listings", Icons.Type.BRIEFCASE, new Color(245, 158, 11));

        statsGrid.add(cgpaCard);
        statsGrid.add(skillsCard);
        statsGrid.add(appsCard);
        statsGrid.add(openCard);

        centerBody.add(statsGrid);
        centerBody.add(Box.createVerticalStrut(20));

        // 2. Quick Action Shortcut Bar
        GlassPanel actionCard = new GlassPanel(12, false);
        actionCard.setLayout(new BorderLayout(14, 0));
        actionCard.setBorder(new EmptyBorder(12, 18, 12, 18));
        actionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel actLbl = new JLabel("Quick Actions:");
        actLbl.setFont(Theme.FONT_BOLD);
        actLbl.setForeground(Theme.TEXT_SECONDARY);

        JPanel actBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actBtns.setOpaque(false);

        AnimatedButton bBrowse = new AnimatedButton("Explore Internships", AnimatedButton.Style.PRIMARY);
        bBrowse.setFont(Theme.FONT_SMALL);
        bBrowse.addActionListener(e -> sidebar.setSelectedIndex(1));

        AnimatedButton bApps = new AnimatedButton("View Applications", AnimatedButton.Style.SECONDARY);
        bApps.setFont(Theme.FONT_SMALL);
        bApps.addActionListener(e -> sidebar.setSelectedIndex(2));

        AnimatedButton bProf = new AnimatedButton("Update Skills", AnimatedButton.Style.OUTLINE);
        bProf.setFont(Theme.FONT_SMALL);
        bProf.addActionListener(e -> sidebar.setSelectedIndex(3));

        actBtns.add(bBrowse);
        actBtns.add(bApps);
        actBtns.add(bProf);

        actionCard.add(actLbl, BorderLayout.WEST);
        actionCard.add(actBtns, BorderLayout.EAST);

        centerBody.add(actionCard);
        centerBody.add(Box.createVerticalStrut(22));

        // 3. Recent Activity & Submitted Applications
        GlassPanel recentPanel = new GlassPanel(14, false);
        recentPanel.setLayout(new BorderLayout(0, 12));
        recentPanel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        JLabel rhTitle = new JLabel("Recent Internship Applications");
        rhTitle.setFont(Theme.FONT_SUBHEADER);
        rhTitle.setForeground(Theme.TEXT_MAIN);

        JButton seeAllBtn = new JButton("View All Pipeline →");
        seeAllBtn.setFont(Theme.FONT_SMALL);
        seeAllBtn.setForeground(Theme.ACCENT_SUBTLE);
        seeAllBtn.setBorderPainted(false);
        seeAllBtn.setContentAreaFilled(false);
        seeAllBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        seeAllBtn.addActionListener(e -> sidebar.setSelectedIndex(2));

        recentHeader.add(rhTitle, BorderLayout.WEST);
        recentHeader.add(seeAllBtn, BorderLayout.EAST);
        recentPanel.add(recentHeader, BorderLayout.NORTH);

        if (studentApplications == null || studentApplications.isEmpty()) {
            EmptyStatePanel emptyState = new EmptyStatePanel(
                    "No Applications Submitted Yet",
                    "Browse open internships and apply directly to track your progress here.",
                    Icons.Type.BRIEFCASE,
                    "Browse Open Internships",
                    () -> sidebar.setSelectedIndex(1)
            );
            recentPanel.add(emptyState, BorderLayout.CENTER);
        } else {
            // Render up to 3 recent applications
            JPanel recList = new JPanel();
            recList.setLayout(new BoxLayout(recList, BoxLayout.Y_AXIS));
            recList.setOpaque(false);

            int limit = Math.min(3, studentApplications.size());
            for (int i = 0; i < limit; i++) {
                Application app = studentApplications.get(i);
                recList.add(createMiniApplicationRow(app));
                if (i < limit - 1) {
                    recList.add(Box.createVerticalStrut(10));
                }
            }
            recentPanel.add(recList, BorderLayout.CENTER);
        }

        centerBody.add(recentPanel);

        JScrollPane scroll = new JScrollPane(centerBody);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMiniApplicationRow(Application app) {
        GlassPanel row = new GlassPanel(10, true);
        row.setLayout(new BorderLayout(14, 0));
        row.setBorder(new EmptyBorder(12, 16, 12, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        String title = app.getInternship() != null ? app.getInternship().getTitle() : "Internship Position";
        String company = app.getInternship() != null ? app.getInternship().getCompany() : "Company";
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Theme.FONT_BOLD);
        titleLbl.setForeground(Theme.TEXT_MAIN);

        JLabel compLbl = new JLabel(company + " • Applied on: " + app.getAppliedAt().toLocalDate());
        compLbl.setFont(Theme.FONT_SMALL);
        compLbl.setForeground(Theme.TEXT_MUTED);

        left.add(titleLbl);
        left.add(compLbl);

        StatusPill pill = new StatusPill(app.getStatus().name());

        row.add(left, BorderLayout.CENTER);
        row.add(pill, BorderLayout.EAST);
        return row;
    }

    private String getGreetingTime() {
        int hour = LocalTime.now().getHour();
        if (hour >= 5 && hour < 12) return "Good Morning";
        if (hour >= 12 && hour < 17) return "Good Afternoon";
        return "Good Evening";
    }

    // =========================================================================
    // PAGE 2: BROWSE INTERNSHIPS (HIGH-QUALITY CARDS & SEARCH)
    // =========================================================================
    private JPanel createInternshipsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(Theme.BG_MAIN);
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));

        // Top Filter Bar
        GlassPanel searchCard = new GlassPanel(14, false);
        searchCard.setLayout(new BorderLayout(14, 0));
        searchCard.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel leftSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftSearch.setOpaque(false);

        JLabel searchIcon = new JLabel(Icons.get(Icons.Type.SEARCH, 18, Theme.TEXT_MUTED));
        searchField = new AnimatedTextField("Search by role title, company, or tech stack...");
        searchField.setPreferredSize(new Dimension(340, 38));
        searchField.addActionListener(e -> filterInternships());

        AnimatedButton searchBtn = new AnimatedButton("Search", AnimatedButton.Style.PRIMARY);
        searchBtn.setFont(Theme.FONT_SMALL);
        searchBtn.addActionListener(e -> filterInternships());

        AnimatedButton resetBtn = new AnimatedButton("Show All Open", AnimatedButton.Style.SECONDARY);
        resetBtn.setFont(Theme.FONT_SMALL);
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            filterInternships();
        });

        leftSearch.add(searchIcon);
        leftSearch.add(searchField);
        leftSearch.add(searchBtn);
        leftSearch.add(resetBtn);

        searchCard.add(leftSearch, BorderLayout.WEST);
        panel.add(searchCard, BorderLayout.NORTH);

        // Center Cards Container (2 columns grid)
        internshipCardsContainer = new JPanel();
        internshipCardsContainer.setLayout(new GridLayout(0, 2, 16, 16));
        internshipCardsContainer.setOpaque(false);

        populateInternshipCards();

        JScrollPane scroll = new JScrollPane(internshipCardsContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void filterInternships() {
        String keyword = searchField.getText().trim();
        try {
            if (keyword.isEmpty()) {
                displayedInternships = internshipService.getOpenInternships();
            } else {
                displayedInternships = internshipService.searchInternships(keyword);
            }
            populateInternshipCards();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error querying internships: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateInternshipCards() {
        internshipCardsContainer.removeAll();

        if (displayedInternships == null || displayedInternships.isEmpty()) {
            internshipCardsContainer.setLayout(new BorderLayout());
            EmptyStatePanel empty = new EmptyStatePanel(
                    "No Matching Internships Found",
                    "Try adjusting your search keywords or clear filters to view all open opportunities.",
                    Icons.Type.SEARCH,
                    "Reset Search Filters",
                    () -> {
                        searchField.setText("");
                        filterInternships();
                    }
            );
            internshipCardsContainer.add(empty, BorderLayout.CENTER);
        } else {
            internshipCardsContainer.setLayout(new GridLayout(0, 2, 16, 16));
            for (Internship item : displayedInternships) {
                boolean hasApplied = false;
                try {
                    hasApplied = applicationService.hasStudentApplied(studentId, item.getId());
                } catch (Exception ignored) {}

                InternshipCard card = new InternshipCard(item, hasApplied, this::openInternshipDetailModal);
                internshipCardsContainer.add(card);
            }
        }

        internshipCardsContainer.revalidate();
        internshipCardsContainer.repaint();
    }

    private void openInternshipDetailModal(Internship item) {
        InternshipDetailDialog dialog = new InternshipDetailDialog(
                this,
                item,
                studentId,
                getSkillsSummary(),
                applicationService,
                () -> {
                    loadStudentData();
                    populateInternshipCards();
                }
        );
        dialog.setVisible(true);
    }

    // =========================================================================
    // PAGE 3: MY APPLICATIONS TRACKING
    // =========================================================================
    private JPanel createApplicationsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(Theme.BG_MAIN);
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));

        // Top Header
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel hTitle = new JLabel("Application Status & Review Feedback");
        hTitle.setFont(Theme.FONT_TITLE);
        hTitle.setForeground(Theme.TEXT_MAIN);

        AnimatedButton refreshBtn = new AnimatedButton("Refresh Pipeline", AnimatedButton.Style.SECONDARY);
        refreshBtn.setFont(Theme.FONT_SMALL);
        refreshBtn.addActionListener(e -> {
            loadStudentData();
            refreshApplicationsTable();
        });

        topRow.add(hTitle, BorderLayout.WEST);
        topRow.add(refreshBtn, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        // Center Split View: Table + Detail Inspector
        String[] cols = {"App ID", "Role Title", "Company", "Stipend", "Submission Date", "Review Status"};
        appsTableModel = new DefaultTableModel(cols, 0);
        appsTable = Components.createStyledTable(appsTableModel);

        appDetailArea = new JTextArea("Select an application from the table above to view submission details and placement officer feedback.");
        Components.styleTextArea(appDetailArea);
        appDetailArea.setEditable(false);
        appDetailArea.setLineWrap(true);
        appDetailArea.setWrapStyleWord(true);

        JScrollPane detailScroll = new JScrollPane(appDetailArea);
        detailScroll.setPreferredSize(new Dimension(300, 160));
        detailScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER, 1, true),
                "Official Review Notes & Feedback",
                0, 0, Theme.FONT_BOLD, Theme.ACCENT_SUBTLE
        ));
        detailScroll.getViewport().setBackground(Theme.INPUT_BG);

        appsTable.getSelectionModel().addListSelectionListener(e -> updateSelectedAppDetail());

        refreshApplicationsTable();

        JPanel centerBox = new JPanel(new BorderLayout(0, 14));
        centerBox.setOpaque(false);
        centerBox.add(Components.createTableScrollPane(appsTable), BorderLayout.CENTER);
        centerBox.add(detailScroll, BorderLayout.SOUTH);

        panel.add(centerBox, BorderLayout.CENTER);
        return panel;
    }

    private void refreshApplicationsTable() {
        try {
            appsTableModel.setRowCount(0);
            studentApplications = applicationService.getStudentApplications(studentId);

            for (Application app : studentApplications) {
                appsTableModel.addRow(new Object[]{
                        "#" + app.getId(),
                        app.getInternship() != null ? app.getInternship().getTitle() : "N/A",
                        app.getInternship() != null ? app.getInternship().getCompany() : "N/A",
                        app.getInternship() != null ? app.getInternship().getStipend() : "N/A",
                        app.getAppliedAt().toLocalDate(),
                        app.getStatus().name()
                });
            }
        } catch (Exception e) {
            System.err.println("Error fetching applications: " + e.getMessage());
        }
    }

    private void updateSelectedAppDetail() {
        int selectedRow = appsTable.getSelectedRow();
        if (selectedRow != -1 && studentApplications != null && selectedRow < studentApplications.size()) {
            Application app = studentApplications.get(selectedRow);
            String feedback = (app.getAdminFeedback() != null && !app.getAdminFeedback().isEmpty()) ?
                    app.getAdminFeedback() : "No administrator review feedback recorded yet.";

            String detail = "Application Reference: #" + app.getId() + "\n" +
                    "Position: " + (app.getInternship() != null ? app.getInternship().getTitle() : "") + "\n" +
                    "Company: " + (app.getInternship() != null ? app.getInternship().getCompany() : "") + "\n" +
                    "Status: " + app.getStatus().name() + " | Date Submitted: " + app.getAppliedAt() + "\n\n" +
                    "Your Cover Letter / Note:\n" + app.getCoverLetter() + "\n\n" +
                    "Placement Admin Review Feedback:\n" + feedback;

            appDetailArea.setText(detail);
        }
    }

    // =========================================================================
    // PAGE 4: PROFILE & TECHNICAL SKILLS PORTFOLIO
    // =========================================================================
    private JPanel createProfileView() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 22, 0));
        panel.setBackground(Theme.BG_MAIN);
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));

        // 1. Left Card: Academic & Personal Profile
        GlassPanel profileCard = new GlassPanel(16, false);
        profileCard.setLayout(new GridBagLayout());
        profileCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel pHeader = new JLabel("Academic & Personal Profile");
        pHeader.setFont(Theme.FONT_SUBHEADER);
        pHeader.setForeground(Theme.ACCENT_SUBTLE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        profileCard.add(pHeader, gbc);

        gbc.gridwidth = 1;
        nameField = new AnimatedTextField(currentStudent != null ? currentStudent.getName() : "");
        nameField.setText(currentStudent != null ? currentStudent.getName() : "");
        nameField.setPreferredSize(new Dimension(200, 38));
        addProfileField(profileCard, "Full Name *", nameField, 1, gbc);

        deptField = new AnimatedTextField(currentStudent != null ? currentStudent.getDepartment() : "");
        deptField.setText(currentStudent != null ? currentStudent.getDepartment() : "");
        deptField.setPreferredSize(new Dimension(200, 38));
        addProfileField(profileCard, "Department *", deptField, 2, gbc);

        phoneField = new AnimatedTextField(currentStudent != null ? currentStudent.getPhone() : "");
        phoneField.setText(currentStudent != null ? currentStudent.getPhone() : "");
        phoneField.setPreferredSize(new Dimension(200, 38));
        addProfileField(profileCard, "Contact Phone", phoneField, 3, gbc);

        cgpaField = new AnimatedTextField(currentStudent != null ? String.valueOf(currentStudent.getCgpa()) : "0.0");
        cgpaField.setText(currentStudent != null ? String.valueOf(currentStudent.getCgpa()) : "0.0");
        cgpaField.setPreferredSize(new Dimension(200, 38));
        addProfileField(profileCard, "CGPA (0.0 - 10.0) *", cgpaField, 4, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JLabel yLbl = new JLabel("Graduation Year");
        yLbl.setFont(Theme.FONT_BOLD);
        yLbl.setForeground(Theme.TEXT_SECONDARY);
        profileCard.add(yLbl, gbc);

        gbc.gridx = 1;
        int dYear = (currentStudent != null && currentStudent.getGraduationYear() > 0) ? currentStudent.getGraduationYear() : 2025;
        yearSpinner = new JSpinner(new SpinnerNumberModel(dYear, 2020, 2030, 1));
        yearSpinner.setFont(Theme.FONT_REGULAR);
        yearSpinner.setPreferredSize(new Dimension(200, 36));
        profileCard.add(yearSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        JLabel bLbl = new JLabel("Bio / Summary");
        bLbl.setFont(Theme.FONT_BOLD);
        bLbl.setForeground(Theme.TEXT_SECONDARY);
        profileCard.add(bLbl, gbc);

        gbc.gridx = 1;
        bioArea = new JTextArea(currentStudent != null ? currentStudent.getBio() : "", 3, 16);
        Components.styleTextArea(bioArea);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        JScrollPane bioScroll = new JScrollPane(bioArea);
        bioScroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));
        profileCard.add(bioScroll, gbc);

        AnimatedButton saveBtn = new AnimatedButton("Save Profile Changes", AnimatedButton.Style.PRIMARY);
        saveBtn.setFont(Theme.FONT_SUBHEADER);
        saveBtn.addActionListener(e -> handleSaveProfile());
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 8, 4, 8);
        profileCard.add(saveBtn, gbc);

        // 2. Right Card: Technical Skills Portfolio
        GlassPanel skillsCard = new GlassPanel(16, false);
        skillsCard.setLayout(new BorderLayout(0, 12));
        skillsCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel sHeader = new JLabel("Technical Skills Portfolio");
        sHeader.setFont(Theme.FONT_SUBHEADER);
        sHeader.setForeground(Theme.ACCENT_SUBTLE);

        // Add Skill Bar
        JPanel addSkillBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        addSkillBar.setOpaque(false);

        masterSkillCombo = new JComboBox<>();
        masterSkillCombo.setBackground(Theme.INPUT_BG);
        masterSkillCombo.setForeground(Theme.TEXT_MAIN);
        masterSkillCombo.setFont(Theme.FONT_REGULAR);
        masterSkillCombo.setPreferredSize(new Dimension(170, 36));
        loadMasterSkillsCombo();

        levelCombo = new JComboBox<>(StudentSkill.ProficiencyLevel.values());
        levelCombo.setBackground(Theme.INPUT_BG);
        levelCombo.setForeground(Theme.TEXT_MAIN);
        levelCombo.setFont(Theme.FONT_REGULAR);
        levelCombo.setPreferredSize(new Dimension(130, 36));

        AnimatedButton addSkillBtn = new AnimatedButton("Add Skill", AnimatedButton.Style.SUCCESS);
        addSkillBtn.setFont(Theme.FONT_SMALL);
        addSkillBtn.addActionListener(e -> handleAddSkill());

        JLabel skLbl = new JLabel("Skill:");
        skLbl.setFont(Theme.FONT_BOLD);
        skLbl.setForeground(Theme.TEXT_SECONDARY);

        JLabel lvlLbl = new JLabel("Level:");
        lvlLbl.setFont(Theme.FONT_BOLD);
        lvlLbl.setForeground(Theme.TEXT_SECONDARY);

        addSkillBar.add(skLbl);
        addSkillBar.add(masterSkillCombo);
        addSkillBar.add(lvlLbl);
        addSkillBar.add(levelCombo);
        addSkillBar.add(addSkillBtn);

        String[] cols = {"Skill ID", "Competency", "Category", "Proficiency"};
        skillsTableModel = new DefaultTableModel(cols, 0);
        skillsTable = Components.createStyledTable(skillsTableModel);

        AnimatedButton deleteSkillBtn = new AnimatedButton("Remove Selected Skill", AnimatedButton.Style.DANGER);
        deleteSkillBtn.setFont(Theme.FONT_SMALL);
        deleteSkillBtn.addActionListener(e -> handleRemoveSkill());

        JPanel topBox = new JPanel(new BorderLayout(0, 8));
        topBox.setOpaque(false);
        topBox.add(sHeader, BorderLayout.NORTH);
        topBox.add(addSkillBar, BorderLayout.SOUTH);

        skillsCard.add(topBox, BorderLayout.NORTH);
        skillsCard.add(Components.createTableScrollPane(skillsTable), BorderLayout.CENTER);
        skillsCard.add(deleteSkillBtn, BorderLayout.SOUTH);

        refreshSkillsTable();

        panel.add(profileCard);
        panel.add(skillsCard);
        return panel;
    }

    private void addProfileField(JPanel panel, String label, JComponent field, int row, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void loadMasterSkillsCombo() {
        try {
            masterSkillCombo.removeAllItems();
            List<Skill> masterSkills = studentService.getAllMasterSkills();
            for (Skill s : masterSkills) {
                masterSkillCombo.addItem(s);
            }
        } catch (Exception e) {
            System.err.println("Error loading master skills: " + e.getMessage());
        }
    }

    private void refreshSkillsTable() {
        try {
            skillsTableModel.setRowCount(0);
            List<StudentSkill> skills = studentService.getStudentProfile(studentId).getSkills();
            for (StudentSkill ss : skills) {
                skillsTableModel.addRow(new Object[]{
                        ss.getId(),
                        ss.getSkill().getName(),
                        ss.getSkill().getCategory(),
                        ss.getProficiencyLevel().name()
                });
            }
        } catch (Exception e) {
            System.err.println("Error refreshing skills: " + e.getMessage());
        }
    }

    private void handleSaveProfile() {
        try {
            if (currentStudent == null) return;
            currentStudent.setName(nameField.getText().trim());
            currentStudent.setDepartment(deptField.getText().trim());
            currentStudent.setPhone(phoneField.getText().trim());
            currentStudent.setCgpa(Double.parseDouble(cgpaField.getText().trim()));
            currentStudent.setGraduationYear((Integer) yearSpinner.getValue());
            currentStudent.setBio(bioArea.getText().trim());

            studentService.updateProfile(currentStudent);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAddSkill() {
        Skill selectedSkill = (Skill) masterSkillCombo.getSelectedItem();
        StudentSkill.ProficiencyLevel level = (StudentSkill.ProficiencyLevel) levelCombo.getSelectedItem();

        if (selectedSkill == null || level == null) {
            JOptionPane.showMessageDialog(this, "Please select both a skill and proficiency level.", "Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            studentService.addSkillToStudent(studentId, selectedSkill.getId(), level);
            refreshSkillsTable();
            JOptionPane.showMessageDialog(this, "Skill added to your portfolio!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding skill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRemoveSkill() {
        int row = skillsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a skill from the table to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int skillId = (int) skillsTableModel.getValueAt(row, 0);
        try {
            studentService.removeSkillFromStudent(skillId);
            refreshSkillsTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error removing skill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSkillsSummary() {
        if (currentStudent != null && currentStudent.getSkills() != null && !currentStudent.getSkills().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currentStudent.getSkills().size(); i++) {
                sb.append(currentStudent.getSkills().get(i).getSkill().getName());
                if (i < currentStudent.getSkills().size() - 1) sb.append(", ");
            }
            return sb.toString();
        }
        return "software engineering and programming fundamentals";
    }
}
