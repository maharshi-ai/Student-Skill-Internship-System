package com.college.internship.ui;

import com.college.internship.dao.UserDao;
import com.college.internship.dao.UserDaoImpl;
import com.college.internship.model.Admin;
import com.college.internship.model.Application;
import com.college.internship.model.Internship;
import com.college.internship.model.Student;
import com.college.internship.model.User;
import com.college.internship.service.ApplicationService;
import com.college.internship.service.InternshipService;
import com.college.internship.service.StudentService;
import com.college.internship.ui.anim.PageTransitionContainer;
import com.college.internship.ui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Top-tier Placement Administrator Dashboard featuring:
 * - Premium SaaS Sidebar with gliding active-pill indicator
 * - 60 FPS directional slide-and-fade page transitions
 * - Real-time KPI stat cards with animated counters (Students, Internships, Applications, Reviews)
 * - Complete Internship listing lifecycle (Post, Live Table Search, Update, Delete)
 * - Searchable Student Talent Roster with detailed skill portfolio inspector
 * - Interactive Candidate Application Pipeline with acceptance/rejection feedback notes
 */
public class AdminDashboardFrame extends JFrame {

    private final int adminId;
    private Admin currentAdmin;

    private final InternshipService internshipService;
    private final StudentService studentService;
    private final ApplicationService applicationService;
    private final UserDao userDao;

    // Navigation & Page Transitions
    private NavigationSidebar sidebar;
    private PageTransitionContainer pageContainer;
    private int activePageIndex = 0;

    // Cached Data
    private List<Internship> allInternships = new ArrayList<>();
    private List<Student> allStudents = new ArrayList<>();
    private List<Application> allApplications = new ArrayList<>();

    // Views
    private JPanel overviewView;
    private JPanel manageInternshipsView;
    private JPanel registeredStudentsView;
    private JPanel reviewApplicationsView;

    // Manage Internships UI Components
    private AnimatedTextField titleField, companyField, locationField, stipendField, deadlineField;
    private JComboBox<Internship.Status> statusCombo;
    private JTextArea descArea, reqArea;
    private DefaultTableModel internshipTableModel;
    private JTable internshipTable;
    private TableRowSorter<DefaultTableModel> internshipSorter;
    private int selectedInternshipId = -1;

    // Registered Students UI Components
    private DefaultTableModel studentTableModel;
    private JTable studentTable;
    private TableRowSorter<DefaultTableModel> studentSorter;
    private JLabel studentNameLabel, studentMetaLabel, studentEmailPhoneLabel;
    private JTextArea studentBioArea;
    private JPanel studentSkillsContainer;

    // Applications UI Components
    private DefaultTableModel appTableModel;
    private JTable appTable;
    private TableRowSorter<DefaultTableModel> appSorter;
    private JLabel appStudentNameLabel, appInternshipRoleLabel, appDateStatusLabel;
    private JTextArea appCoverArea, appFeedbackArea;
    private AnimatedButton acceptBtn, rejectBtn;

    public AdminDashboardFrame(int adminId) {
        this.adminId = adminId;
        this.internshipService = new InternshipService();
        this.studentService = new StudentService();
        this.applicationService = new ApplicationService();
        this.userDao = new UserDaoImpl();

        loadAdminProfile();
        loadAllData();
        initUI();
    }

    private void loadAdminProfile() {
        try {
            Optional<User> uOpt = userDao.findById(adminId);
            if (uOpt.isPresent()) {
                if (uOpt.get() instanceof Admin a) {
                    this.currentAdmin = a;
                } else {
                    User u = uOpt.get();
                    this.currentAdmin = new Admin(u.getId(), u.getName(), u.getEmail(), "", "", u.getCreatedAt(), "Training & Placement Cell");
                }
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load admin profile details: " + e.getMessage());
        }
    }

    private void loadAllData() {
        try {
            this.allInternships = internshipService.getAllInternships();
            this.allStudents = studentService.getAllStudents();
            this.allApplications = applicationService.getAllApplications();
        } catch (Exception e) {
            System.err.println("Error loading admin system data: " + e.getMessage());
        }
    }

    private void initUI() {
        String adminName = currentAdmin != null ? currentAdmin.getName() : "Placement Officer";
        setTitle("CampusLink — Administrator Command Center (" + adminName + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 820);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.BG_MAIN);

        // 1. Modern SaaS Sidebar
        List<NavigationSidebar.NavItem> navItems = List.of(
                new NavigationSidebar.NavItem("overview", "Overview & Analytics", Icons.Type.DASHBOARD),
                new NavigationSidebar.NavItem("internships", "Manage Internships", Icons.Type.BRIEFCASE),
                new NavigationSidebar.NavItem("students", "Registered Students", Icons.Type.PROFILE),
                new NavigationSidebar.NavItem("applications", "Review Applications", Icons.Type.APPLICATIONS)
        );

        sidebar = new NavigationSidebar(
                "CampusLink",
                "ADMINISTRATOR",
                adminName,
                "PLACEMENT CELL",
                navItems,
                this::handleLogout,
                this::handleNavigationChange
        );
        rootPanel.add(sidebar, BorderLayout.WEST);

        // 2. Smooth Page Transition Container
        pageContainer = new PageTransitionContainer();
        rootPanel.add(pageContainer, BorderLayout.CENTER);

        // Build and display initial overview
        overviewView = createOverviewView();
        pageContainer.showImmediate(overviewView);

        setContentPane(rootPanel);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of the Administrator Command Center?",
                "Confirm Logout",
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
                manageInternshipsView = createManageInternshipsView();
                targetPanel = manageInternshipsView;
            }
            case 2 -> {
                registeredStudentsView = createRegisteredStudentsView();
                targetPanel = registeredStudentsView;
            }
            case 3 -> {
                reviewApplicationsView = createReviewApplicationsView();
                targetPanel = reviewApplicationsView;
            }
            default -> targetPanel = overviewView;
        }

        pageContainer.transitionTo(targetPanel, dir);
    }

    private String getGreetingTime() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }

    // =========================================================================
    // PAGE 0: OVERVIEW & ANALYTICS
    // =========================================================================
    private JPanel createOverviewView() {
        loadAllData();

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Theme.BG_MAIN);
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));

        // Top Greeting Banner
        JPanel greetingBox = new JPanel(new BorderLayout());
        greetingBox.setOpaque(false);

        JPanel titlesBox = new JPanel();
        titlesBox.setLayout(new BoxLayout(titlesBox, BoxLayout.Y_AXIS));
        titlesBox.setOpaque(false);

        String adminName = currentAdmin != null ? currentAdmin.getName() : "Placement Officer";
        JLabel greetTitle = new JLabel(getGreetingTime() + ", " + adminName + " 🛡️");
        greetTitle.setFont(Theme.FONT_TITLE);
        greetTitle.setForeground(Theme.TEXT_MAIN);

        JLabel greetSubtitle = new JLabel("Training & Placement Cell • Operations Hub • Recruitment Analytics & Review");
        greetSubtitle.setFont(Theme.FONT_REGULAR);
        greetSubtitle.setForeground(Theme.TEXT_MUTED);

        titlesBox.add(greetTitle);
        titlesBox.add(Box.createVerticalStrut(4));
        titlesBox.add(greetSubtitle);

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        quickActions.setOpaque(false);

        AnimatedButton postActionBtn = new AnimatedButton("Post Opportunity", Icons.get(Icons.Type.PLUS, 16, Color.WHITE), AnimatedButton.Style.PRIMARY);
        postActionBtn.addActionListener(e -> sidebar.setSelectedIndex(1));

        AnimatedButton reviewActionBtn = new AnimatedButton("Review Pipeline", Icons.get(Icons.Type.APPLICATIONS, 16, Color.WHITE), AnimatedButton.Style.SECONDARY);
        reviewActionBtn.addActionListener(e -> sidebar.setSelectedIndex(3));

        quickActions.add(postActionBtn);
        quickActions.add(reviewActionBtn);

        greetingBox.add(titlesBox, BorderLayout.WEST);
        greetingBox.add(quickActions, BorderLayout.EAST);

        panel.add(greetingBox, BorderLayout.NORTH);

        // Center Content: Stats + Split Layout
        JPanel centerBody = new JPanel();
        centerBody.setLayout(new BoxLayout(centerBody, BoxLayout.Y_AXIS));
        centerBody.setOpaque(false);

        // 1. KPI Statistic Cards Grid (4 columns)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        long openInternshipsCount = allInternships.stream()
                .filter(i -> i.getStatus() == Internship.Status.OPEN)
                .count();

        long pendingReviewsCount = allApplications.stream()
                .filter(a -> a.getStatus() == Application.Status.PENDING)
                .count();

        long acceptedCount = allApplications.stream()
                .filter(a -> a.getStatus() == Application.Status.ACCEPTED)
                .count();

        StatCard cardStudents = new StatCard(
                "Total Students",
                allStudents.size(),
                "%.0f",
                "Registered candidate pool",
                Icons.Type.PROFILE,
                Theme.PRIMARY
        );

        StatCard cardInternships = new StatCard(
                "Active Internships",
                openInternshipsCount,
                "%.0f",
                "Open for student applications",
                Icons.Type.BRIEFCASE,
                new Color(59, 130, 246)
        );

        StatCard cardApplications = new StatCard(
                "Total Applications",
                allApplications.size(),
                "%.0f",
                "Submissions across portal",
                Icons.Type.APPLICATIONS,
                new Color(168, 85, 247)
        );

        StatCard cardPending = new StatCard(
                "Pending Reviews",
                pendingReviewsCount,
                "%.0f",
                "Awaiting placement decision",
                Icons.Type.SPARKLE,
                Theme.WARNING_TEXT
        );

        statsGrid.add(cardStudents);
        statsGrid.add(cardInternships);
        statsGrid.add(cardApplications);
        statsGrid.add(cardPending);

        centerBody.add(statsGrid);
        centerBody.add(Box.createVerticalStrut(22));

        // 2. Split Lower Section: Recent Submissions Table & Analytics Summary
        JPanel lowerSplit = new JPanel(new BorderLayout(18, 0));
        lowerSplit.setOpaque(false);

        // Recent Applications Card (Left, 65%)
        GlassPanel recentAppsPanel = new GlassPanel(16);
        recentAppsPanel.setLayout(new BorderLayout(0, 14));
        recentAppsPanel.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);

        JLabel recentTitle = new JLabel("Recent Candidate Submissions");
        recentTitle.setFont(Theme.FONT_SUBHEADER);
        recentTitle.setForeground(Theme.TEXT_MAIN);

        JLabel recentSub = new JLabel("Latest applications awaiting verification or feedback");
        recentSub.setFont(Theme.FONT_SMALL);
        recentSub.setForeground(Theme.TEXT_MUTED);

        JPanel titleGrp = new JPanel();
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));
        titleGrp.setOpaque(false);
        titleGrp.add(recentTitle);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(recentSub);

        AnimatedButton viewAllBtn = new AnimatedButton("Manage All", AnimatedButton.Style.OUTLINE);
        viewAllBtn.addActionListener(e -> sidebar.setSelectedIndex(3));

        recentHeader.add(titleGrp, BorderLayout.WEST);
        recentHeader.add(viewAllBtn, BorderLayout.EAST);
        recentAppsPanel.add(recentHeader, BorderLayout.NORTH);

        if (allApplications.isEmpty()) {
            EmptyStatePanel empty = new EmptyStatePanel(
                    "No Applications Received Yet",
                    "Once students browse opportunities and submit their profiles, applications will appear here.",
                    Icons.Type.APPLICATIONS,
                    null,
                    null
            );
            recentAppsPanel.add(empty, BorderLayout.CENTER);
        } else {
            String[] cols = {"App ID", "Student Name", "Position Title", "Company", "Date", "Status"};
            DefaultTableModel recentModel = new DefaultTableModel(cols, 0);
            JTable recentTable = Components.createStyledTable(recentModel);

            int maxRows = Math.min(8, allApplications.size());
            for (int i = 0; i < maxRows; i++) {
                Application a = allApplications.get(i);
                recentModel.addRow(new Object[]{
                        "#" + a.getId(),
                        a.getStudent() != null ? a.getStudent().getName() : "N/A",
                        a.getInternship() != null ? a.getInternship().getTitle() : "N/A",
                        a.getInternship() != null ? a.getInternship().getCompany() : "N/A",
                        a.getAppliedAt().toLocalDate(),
                        a.getStatus().name()
                });
            }

            recentAppsPanel.add(Components.createTableScrollPane(recentTable), BorderLayout.CENTER);
        }

        // Quick Operations Summary (Right, 35%)
        GlassPanel opsPanel = new GlassPanel(16);
        opsPanel.setLayout(new BoxLayout(opsPanel, BoxLayout.Y_AXIS));
        opsPanel.setBorder(new EmptyBorder(18, 20, 18, 20));
        opsPanel.setPreferredSize(new Dimension(340, 300));

        JLabel opsTitle = new JLabel("Placement Pulse & Stats");
        opsTitle.setFont(Theme.FONT_SUBHEADER);
        opsTitle.setForeground(Theme.TEXT_MAIN);

        opsPanel.add(opsTitle);
        opsPanel.add(Box.createVerticalStrut(16));

        // Metrics breakdown
        opsPanel.add(createPulseRow("Accepted Placements", String.valueOf(acceptedCount), Theme.SUCCESS_TEXT));
        opsPanel.add(Box.createVerticalStrut(10));
        opsPanel.add(createPulseRow("Pending In Review", String.valueOf(pendingReviewsCount), Theme.WARNING_TEXT));
        opsPanel.add(Box.createVerticalStrut(10));
        opsPanel.add(createPulseRow("Active Internship Posts", String.valueOf(openInternshipsCount), Theme.ACCENT_SUBTLE));
        opsPanel.add(Box.createVerticalStrut(10));
        opsPanel.add(createPulseRow("Total Talent Enrolled", String.valueOf(allStudents.size()), Theme.PRIMARY));

        opsPanel.add(Box.createVerticalStrut(20));

        JLabel shortcutsLbl = new JLabel("QUICK NAVIGATION");
        shortcutsLbl.setFont(Theme.FONT_BADGE);
        shortcutsLbl.setForeground(Theme.TEXT_MUTED);
        opsPanel.add(shortcutsLbl);
        opsPanel.add(Box.createVerticalStrut(8));

        AnimatedButton navInternBtn = new AnimatedButton("Manage Internship Listings", Icons.get(Icons.Type.BRIEFCASE, 16, Theme.TEXT_MAIN), AnimatedButton.Style.SECONDARY);
        navInternBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        navInternBtn.addActionListener(e -> sidebar.setSelectedIndex(1));

        AnimatedButton navStudBtn = new AnimatedButton("Inspect Student Roster", Icons.get(Icons.Type.PROFILE, 16, Theme.TEXT_MAIN), AnimatedButton.Style.SECONDARY);
        navStudBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        navStudBtn.addActionListener(e -> sidebar.setSelectedIndex(2));

        opsPanel.add(navInternBtn);
        opsPanel.add(Box.createVerticalStrut(8));
        opsPanel.add(navStudBtn);

        lowerSplit.add(recentAppsPanel, BorderLayout.CENTER);
        lowerSplit.add(opsPanel, BorderLayout.EAST);

        centerBody.add(lowerSplit);

        JScrollPane scroll = new JScrollPane(centerBody);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPulseRow(String label, String value, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_REGULAR);
        lbl.setForeground(Theme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(Theme.FONT_BOLD);
        val.setForeground(color);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    // =========================================================================
    // PAGE 1: MANAGE INTERNSHIPS (POST, EDIT, DELETE, LIVE TABLE)
    // =========================================================================
    private JPanel createManageInternshipsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(Theme.BG_MAIN);
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Top Section: Title & Actions
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Internship Management & Listings");
        pageTitle.setFont(Theme.FONT_TITLE);
        pageTitle.setForeground(Theme.TEXT_MAIN);

        JLabel pageSub = new JLabel("Publish new career opportunities, update active specifications, and maintain live positions.");
        pageSub.setFont(Theme.FONT_REGULAR);
        pageSub.setForeground(Theme.TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        AnimatedButton refreshBtn = new AnimatedButton("Refresh Listings", Icons.get(Icons.Type.REFRESH, 16, Theme.TEXT_MAIN), AnimatedButton.Style.SECONDARY);
        refreshBtn.addActionListener(e -> refreshInternshipsTable());

        topBar.add(titleBox, BorderLayout.WEST);
        topBar.add(refreshBtn, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Split Layout: Form Card (Left ~460px) and Live Table Card (Right)
        JPanel splitContainer = new JPanel(new BorderLayout(20, 0));
        splitContainer.setOpaque(false);

        // LEFT: Form Card
        GlassPanel formCard = new GlassPanel(16);
        formCard.setLayout(new BorderLayout(0, 14));
        formCard.setBorder(new EmptyBorder(18, 20, 18, 20));
        formCard.setPreferredSize(new Dimension(460, 600));

        JLabel formHeader = new JLabel("Create / Edit Opportunity");
        formHeader.setFont(Theme.FONT_SUBHEADER);
        formHeader.setForeground(Theme.ACCENT_SUBTLE);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setOpaque(false);

        titleField = new AnimatedTextField("e.g. Software Engineer Intern");
        companyField = new AnimatedTextField("e.g. Microsoft");
        locationField = new AnimatedTextField("e.g. Remote / Seattle, WA");
        stipendField = new AnimatedTextField("e.g. $1,200/mo");
        deadlineField = new AnimatedTextField("YYYY-MM-DD");
        deadlineField.setText(LocalDate.now().plusMonths(1).toString());

        formFields.add(createFormField("Job Title *", titleField));
        formFields.add(Box.createVerticalStrut(10));
        formFields.add(createFormField("Company Name *", companyField));
        formFields.add(Box.createVerticalStrut(10));
        formFields.add(createFormField("Work Location *", locationField));
        formFields.add(Box.createVerticalStrut(10));
        formFields.add(createFormField("Stipend / Compensation", stipendField));
        formFields.add(Box.createVerticalStrut(10));
        formFields.add(createFormField("Application Deadline (YYYY-MM-DD)", deadlineField));
        formFields.add(Box.createVerticalStrut(10));

        // Status Combo
        JPanel statusBox = new JPanel(new BorderLayout(0, 4));
        statusBox.setOpaque(false);
        JLabel statusLbl = new JLabel("Listing Status");
        statusLbl.setFont(Theme.FONT_BOLD);
        statusLbl.setForeground(Theme.TEXT_SECONDARY);

        statusCombo = new JComboBox<>(Internship.Status.values());
        statusCombo.setBackground(Theme.INPUT_BG);
        statusCombo.setForeground(Theme.TEXT_MAIN);
        statusCombo.setFont(Theme.FONT_REGULAR);
        statusCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));

        statusBox.add(statusLbl, BorderLayout.NORTH);
        statusBox.add(statusCombo, BorderLayout.CENTER);
        formFields.add(statusBox);
        formFields.add(Box.createVerticalStrut(10));

        // Description & Requirements
        descArea = new JTextArea(3, 20);
        Components.styleTextArea(descArea);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));

        reqArea = new JTextArea(3, 20);
        Components.styleTextArea(reqArea);
        reqArea.setLineWrap(true);
        reqArea.setWrapStyleWord(true);
        JScrollPane reqScroll = new JScrollPane(reqArea);
        reqScroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));

        formFields.add(createTextAreaField("Role Overview & Description", descScroll));
        formFields.add(Box.createVerticalStrut(10));
        formFields.add(createTextAreaField("Candidate Requirements & Qualifications", reqScroll));

        JScrollPane formScroll = new JScrollPane(formFields);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.getVerticalScrollBar().setUnitIncrement(14);
        formCard.add(formScroll, BorderLayout.CENTER);

        // Buttons Action Box
        JPanel btnBox = new JPanel(new GridLayout(2, 2, 8, 8));
        btnBox.setOpaque(false);
        btnBox.setBorder(new EmptyBorder(12, 0, 0, 0));

        AnimatedButton saveBtn = new AnimatedButton("Post Internship", Icons.get(Icons.Type.PLUS, 16, Color.WHITE), AnimatedButton.Style.SUCCESS);
        saveBtn.addActionListener(e -> handleCreateInternship());

        AnimatedButton updateBtn = new AnimatedButton("Update Selected", Icons.get(Icons.Type.CHECK, 16, Color.WHITE), AnimatedButton.Style.PRIMARY);
        updateBtn.addActionListener(e -> handleUpdateInternship());

        AnimatedButton deleteBtn = new AnimatedButton("Delete Selected", Icons.get(Icons.Type.TRASH, 16, Color.WHITE), AnimatedButton.Style.DANGER);
        deleteBtn.addActionListener(e -> handleDeleteInternship());

        AnimatedButton clearBtn = new AnimatedButton("Clear Form", Icons.get(Icons.Type.REFRESH, 16, Theme.TEXT_MAIN), AnimatedButton.Style.SECONDARY);
        clearBtn.addActionListener(e -> clearFormFields());

        btnBox.add(saveBtn);
        btnBox.add(updateBtn);
        btnBox.add(deleteBtn);
        btnBox.add(clearBtn);
        formCard.add(btnBox, BorderLayout.SOUTH);

        // RIGHT: Live Table Card
        GlassPanel tableCard = new GlassPanel(16);
        tableCard.setLayout(new BorderLayout(0, 14));
        tableCard.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel tableHeaderBox = new JPanel(new BorderLayout(14, 0));
        tableHeaderBox.setOpaque(false);

        JLabel tableTitle = new JLabel("Posted Listings Directory");
        tableTitle.setFont(Theme.FONT_SUBHEADER);
        tableTitle.setForeground(Theme.TEXT_MAIN);

        AnimatedTextField searchListingsField = new AnimatedTextField("Search by role or company...");
        searchListingsField.setPreferredSize(new Dimension(240, 36));

        tableHeaderBox.add(tableTitle, BorderLayout.WEST);
        tableHeaderBox.add(searchListingsField, BorderLayout.EAST);
        tableCard.add(tableHeaderBox, BorderLayout.NORTH);

        String[] cols = {"ID", "Role Title", "Company", "Stipend", "Deadline", "Status"};
        internshipTableModel = new DefaultTableModel(cols, 0);
        internshipTable = Components.createStyledTable(internshipTableModel);
        internshipSorter = new TableRowSorter<>(internshipTableModel);
        internshipTable.setRowSorter(internshipSorter);

        internshipTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelectedInternship();
            }
        });

        searchListingsField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterListings(); }
            public void removeUpdate(DocumentEvent e) { filterListings(); }
            public void changedUpdate(DocumentEvent e) { filterListings(); }

            private void filterListings() {
                String text = searchListingsField.getText().trim();
                if (text.isEmpty()) {
                    internshipSorter.setRowFilter(null);
                } else {
                    internshipSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
                }
            }
        });

        tableCard.add(Components.createTableScrollPane(internshipTable), BorderLayout.CENTER);

        splitContainer.add(formCard, BorderLayout.WEST);
        splitContainer.add(tableCard, BorderLayout.CENTER);

        panel.add(splitContainer, BorderLayout.CENTER);

        // Populate table
        refreshInternshipsTable();

        return panel;
    }

    private JPanel createFormField(String label, JTextField field) {
        JPanel box = new JPanel(new BorderLayout(0, 4));
        box.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        box.add(lbl, BorderLayout.NORTH);
        box.add(field, BorderLayout.CENTER);
        return box;
    }

    private JPanel createTextAreaField(String label, JScrollPane scroll) {
        JPanel box = new JPanel(new BorderLayout(0, 4));
        box.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        box.add(lbl, BorderLayout.NORTH);
        box.add(scroll, BorderLayout.CENTER);
        return box;
    }

    private void refreshInternshipsTable() {
        try {
            internshipTableModel.setRowCount(0);
            allInternships = internshipService.getAllInternships();

            for (Internship item : allInternships) {
                internshipTableModel.addRow(new Object[]{
                        "#" + item.getId(),
                        item.getTitle(),
                        item.getCompany(),
                        item.getStipend(),
                        item.getDeadline(),
                        item.getStatus().name()
                });
            }
        } catch (Exception e) {
            System.err.println("Error loading internships table: " + e.getMessage());
        }
    }

    private void populateFormFromSelectedInternship() {
        int viewRow = internshipTable.getSelectedRow();
        if (viewRow != -1 && allInternships != null) {
            int modelRow = internshipTable.convertRowIndexToModel(viewRow);
            if (modelRow >= 0 && modelRow < allInternships.size()) {
                Internship item = allInternships.get(modelRow);
                selectedInternshipId = item.getId();
                titleField.setText(item.getTitle());
                companyField.setText(item.getCompany());
                locationField.setText(item.getLocation());
                stipendField.setText(item.getStipend());
                deadlineField.setText(item.getDeadline().toString());
                statusCombo.setSelectedItem(item.getStatus());
                descArea.setText(item.getDescription());
                reqArea.setText(item.getRequirements());
            }
        }
    }

    private void clearFormFields() {
        selectedInternshipId = -1;
        titleField.setText("");
        companyField.setText("");
        locationField.setText("");
        stipendField.setText("");
        deadlineField.setText(LocalDate.now().plusMonths(1).toString());
        descArea.setText("");
        reqArea.setText("");
        statusCombo.setSelectedIndex(0);
        internshipTable.clearSelection();
    }

    private void handleCreateInternship() {
        try {
            String title = titleField.getText().trim();
            String company = companyField.getText().trim();
            String location = locationField.getText().trim();
            String stipend = stipendField.getText().trim();
            LocalDate deadline = LocalDate.parse(deadlineField.getText().trim());
            String desc = descArea.getText().trim();
            String req = reqArea.getText().trim();

            if (title.isEmpty() || company.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields (Title, Company, Location).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            internshipService.createInternship(title, company, location, desc, req, stipend, deadline);
            JOptionPane.showMessageDialog(this, "Internship opportunity published successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFormFields();
            refreshInternshipsTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating internship: " + e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateInternship() {
        if (selectedInternshipId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an internship from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Internship item = new Internship();
            item.setId(selectedInternshipId);
            item.setTitle(titleField.getText().trim());
            item.setCompany(companyField.getText().trim());
            item.setLocation(locationField.getText().trim());
            item.setStipend(stipendField.getText().trim());
            item.setDeadline(LocalDate.parse(deadlineField.getText().trim()));
            item.setStatus((Internship.Status) statusCombo.getSelectedItem());
            item.setDescription(descArea.getText().trim());
            item.setRequirements(reqArea.getText().trim());

            internshipService.updateInternship(item);
            JOptionPane.showMessageDialog(this, "Internship updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFormFields();
            refreshInternshipsTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating internship: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteInternship() {
        if (selectedInternshipId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an internship from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this internship listing? Active applications may be impacted.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                internshipService.deleteInternship(selectedInternshipId);
                clearFormFields();
                refreshInternshipsTable();
                JOptionPane.showMessageDialog(this, "Internship listing deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting listing: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // PAGE 2: REGISTERED STUDENTS TALENT ROSTER
    // =========================================================================
    private JPanel createRegisteredStudentsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(Theme.BG_MAIN);
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout(14, 0));
        topBar.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Enrolled Student Talent Roster");
        pageTitle.setFont(Theme.FONT_TITLE);
        pageTitle.setForeground(Theme.TEXT_MAIN);

        JLabel pageSub = new JLabel("Search candidates, evaluate academic performance, and inspect verified skill portfolios.");
        pageSub.setFont(Theme.FONT_REGULAR);
        pageSub.setForeground(Theme.TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBox.setOpaque(false);

        AnimatedTextField searchStudentField = new AnimatedTextField("Search by name, email, department...");
        searchStudentField.setPreferredSize(new Dimension(280, 38));

        AnimatedButton refreshBtn = new AnimatedButton("Refresh Roster", Icons.get(Icons.Type.REFRESH, 16, Theme.TEXT_MAIN), AnimatedButton.Style.SECONDARY);
        refreshBtn.addActionListener(e -> refreshStudentsTable());

        actionBox.add(searchStudentField);
        actionBox.add(refreshBtn);

        topBar.add(titleBox, BorderLayout.WEST);
        topBar.add(actionBox, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Center: Students Table
        String[] cols = {"Student ID", "Full Name", "Institutional Email", "Department", "CGPA", "Grad Year"};
        studentTableModel = new DefaultTableModel(cols, 0);
        studentTable = Components.createStyledTable(studentTableModel);
        studentSorter = new TableRowSorter<>(studentTableModel);
        studentTable.setRowSorter(studentSorter);

        searchStudentField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterStudents(); }
            public void removeUpdate(DocumentEvent e) { filterStudents(); }
            public void changedUpdate(DocumentEvent e) { filterStudents(); }

            private void filterStudents() {
                String text = searchStudentField.getText().trim();
                if (text.isEmpty()) {
                    studentSorter.setRowFilter(null);
                } else {
                    studentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
                }
            }
        });

        // Bottom: Detailed Student Dossier
        GlassPanel dossierCard = new GlassPanel(16);
        dossierCard.setLayout(new BorderLayout(18, 0));
        dossierCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        dossierCard.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));

        // Left info column
        JPanel leftInfo = new JPanel();
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        leftInfo.setOpaque(false);
        leftInfo.setPreferredSize(new Dimension(320, 170));

        studentNameLabel = new JLabel("Select a student to view dossier");
        studentNameLabel.setFont(Theme.FONT_SUBHEADER);
        studentNameLabel.setForeground(Theme.TEXT_MAIN);

        studentMetaLabel = new JLabel("Department • CGPA • Class Year");
        studentMetaLabel.setFont(Theme.FONT_REGULAR);
        studentMetaLabel.setForeground(Theme.ACCENT_SUBTLE);

        studentEmailPhoneLabel = new JLabel("Contact: —");
        studentEmailPhoneLabel.setFont(Theme.FONT_SMALL);
        studentEmailPhoneLabel.setForeground(Theme.TEXT_MUTED);

        leftInfo.add(studentNameLabel);
        leftInfo.add(Box.createVerticalStrut(4));
        leftInfo.add(studentMetaLabel);
        leftInfo.add(Box.createVerticalStrut(6));
        leftInfo.add(studentEmailPhoneLabel);

        // Center bio column
        JPanel centerBio = new JPanel(new BorderLayout(0, 4));
        centerBio.setOpaque(false);
        JLabel bioTitle = new JLabel("Professional Statement / Bio");
        bioTitle.setFont(Theme.FONT_BOLD);
        bioTitle.setForeground(Theme.TEXT_SECONDARY);

        studentBioArea = new JTextArea("Candidate biography and statement of purpose will appear here.");
        Components.styleTextArea(studentBioArea);
        studentBioArea.setEditable(false);
        studentBioArea.setLineWrap(true);
        studentBioArea.setWrapStyleWord(true);
        JScrollPane bioScroll = new JScrollPane(studentBioArea);
        bioScroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));

        centerBio.add(bioTitle, BorderLayout.NORTH);
        centerBio.add(bioScroll, BorderLayout.CENTER);

        // Right skills column
        JPanel rightSkills = new JPanel(new BorderLayout(0, 4));
        rightSkills.setOpaque(false);
        rightSkills.setPreferredSize(new Dimension(340, 170));

        JLabel skillsTitle = new JLabel("Technical Skills Portfolio");
        skillsTitle.setFont(Theme.FONT_BOLD);
        skillsTitle.setForeground(Theme.TEXT_SECONDARY);

        studentSkillsContainer = new JPanel();
        studentSkillsContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        studentSkillsContainer.setOpaque(false);

        JScrollPane skillsScroll = new JScrollPane(studentSkillsContainer);
        skillsScroll.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER, 1, true));
        skillsScroll.getViewport().setBackground(Theme.INPUT_BG);

        rightSkills.add(skillsTitle, BorderLayout.NORTH);
        rightSkills.add(skillsScroll, BorderLayout.CENTER);

        dossierCard.add(leftInfo, BorderLayout.WEST);
        dossierCard.add(centerBio, BorderLayout.CENTER);
        dossierCard.add(rightSkills, BorderLayout.EAST);

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedStudentDetail();
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                Components.createTableScrollPane(studentTable), dossierCard);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        panel.add(splitPane, BorderLayout.CENTER);

        refreshStudentsTable();
        return panel;
    }

    private void refreshStudentsTable() {
        try {
            studentTableModel.setRowCount(0);
            allStudents = studentService.getAllStudents();

            for (Student s : allStudents) {
                studentTableModel.addRow(new Object[]{
                        "#" + s.getId(),
                        s.getName(),
                        s.getEmail(),
                        s.getDepartment(),
                        String.format("%.2f", s.getCgpa()),
                        s.getGraduationYear()
                });
            }
        } catch (Exception e) {
            System.err.println("Error refreshing students table: " + e.getMessage());
        }
    }

    private void updateSelectedStudentDetail() {
        int viewRow = studentTable.getSelectedRow();
        if (viewRow != -1 && allStudents != null) {
            int modelRow = studentTable.convertRowIndexToModel(viewRow);
            if (modelRow >= 0 && modelRow < allStudents.size()) {
                Student s = allStudents.get(modelRow);

                studentNameLabel.setText(s.getName() + " (#" + s.getId() + ")");
                studentMetaLabel.setText(s.getDepartment() + " • CGPA: " + String.format("%.2f", s.getCgpa()) + " • Class of " + s.getGraduationYear());
                studentEmailPhoneLabel.setText("Email: " + s.getEmail() + " | Phone: " + (s.getPhone() != null && !s.getPhone().isEmpty() ? s.getPhone() : "N/A"));
                studentBioArea.setText(s.getBio() != null && !s.getBio().isEmpty() ? s.getBio() : "No bio entered yet.");

                studentSkillsContainer.removeAll();
                if (s.getSkills() != null && !s.getSkills().isEmpty()) {
                    for (var sk : s.getSkills()) {
                        String skillName = sk.getSkill() != null ? sk.getSkill().getName() : "Skill";
                        String prof = sk.getProficiencyLevel() != null ? sk.getProficiencyLevel().name() : "";
                        studentSkillsContainer.add(Components.createBadge(skillName + " (" + prof + ")", Theme.PURPLE_BADGE_BG, Theme.PURPLE_BADGE_FG));
                    }
                } else {
                    JLabel noSkills = new JLabel("No skills added yet.");
                    noSkills.setFont(Theme.FONT_SMALL);
                    noSkills.setForeground(Theme.TEXT_MUTED);
                    studentSkillsContainer.add(noSkills);
                }
                studentSkillsContainer.revalidate();
                studentSkillsContainer.repaint();
            }
        }
    }

    // =========================================================================
    // PAGE 3: REVIEW APPLICATIONS (PIPELINE, COVER LETTER, DECISION NOTES)
    // =========================================================================
    private JPanel createReviewApplicationsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(Theme.BG_MAIN);
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Top Header Bar
        JPanel topBar = new JPanel(new BorderLayout(14, 0));
        topBar.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel pageTitle = new JLabel("Candidate Application Pipeline");
        pageTitle.setFont(Theme.FONT_TITLE);
        pageTitle.setForeground(Theme.TEXT_MAIN);

        JLabel pageSub = new JLabel("Inspect applicant credentials, read cover letters, and issue official acceptances or rejection feedback.");
        pageSub.setFont(Theme.FONT_REGULAR);
        pageSub.setForeground(Theme.TEXT_MUTED);

        titleBox.add(pageTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(pageSub);

        JPanel filterBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterBox.setOpaque(false);

        AnimatedTextField searchAppField = new AnimatedTextField("Search applicant or role...");
        searchAppField.setPreferredSize(new Dimension(240, 38));

        JComboBox<String> statusFilterCombo = new JComboBox<>(new String[]{"All Statuses", "PENDING", "ACCEPTED", "REJECTED"});
        statusFilterCombo.setBackground(Theme.INPUT_BG);
        statusFilterCombo.setForeground(Theme.TEXT_MAIN);
        statusFilterCombo.setFont(Theme.FONT_REGULAR);
        statusFilterCombo.setPreferredSize(new Dimension(140, 38));

        AnimatedButton refreshBtn = new AnimatedButton("Refresh", Icons.get(Icons.Type.REFRESH, 16, Theme.TEXT_MAIN), AnimatedButton.Style.SECONDARY);
        refreshBtn.addActionListener(e -> refreshApplicationsTable());

        filterBox.add(searchAppField);
        filterBox.add(statusFilterCombo);
        filterBox.add(refreshBtn);

        topBar.add(titleBox, BorderLayout.WEST);
        topBar.add(filterBox, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Center Applications Table
        String[] cols = {"App ID", "Student Name", "Position Applied", "Company", "Submission Date", "Status"};
        appTableModel = new DefaultTableModel(cols, 0);
        appTable = Components.createStyledTable(appTableModel);
        appSorter = new TableRowSorter<>(appTableModel);
        appTable.setRowSorter(appSorter);

        Runnable applyFilters = () -> {
            String text = searchAppField.getText().trim();
            String status = (String) statusFilterCombo.getSelectedItem();

            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            if (!text.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
            }
            if (status != null && !status.equals("All Statuses")) {
                filters.add(RowFilter.regexFilter(status, 5));
            }

            if (filters.isEmpty()) {
                appSorter.setRowFilter(null);
            } else {
                appSorter.setRowFilter(RowFilter.andFilter(filters));
            }
        };

        searchAppField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters.run(); }
            public void removeUpdate(DocumentEvent e) { applyFilters.run(); }
            public void changedUpdate(DocumentEvent e) { applyFilters.run(); }
        });

        statusFilterCombo.addActionListener(e -> applyFilters.run());

        // Bottom Review Dossier Card
        GlassPanel reviewCard = new GlassPanel(16);
        reviewCard.setLayout(new BorderLayout(16, 12));
        reviewCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        reviewCard.setPreferredSize(new Dimension(Integer.MAX_VALUE, 240));

        // Top info header
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setOpaque(false);

        appStudentNameLabel = new JLabel("Select an application from the table to begin review");
        appStudentNameLabel.setFont(Theme.FONT_SUBHEADER);
        appStudentNameLabel.setForeground(Theme.TEXT_MAIN);

        appInternshipRoleLabel = new JLabel("Position: —");
        appInternshipRoleLabel.setFont(Theme.FONT_REGULAR);
        appInternshipRoleLabel.setForeground(Theme.ACCENT_SUBTLE);

        appDateStatusLabel = new JLabel("Submitted: — • Status: —");
        appDateStatusLabel.setFont(Theme.FONT_SMALL);
        appDateStatusLabel.setForeground(Theme.TEXT_MUTED);

        headerLeft.add(appStudentNameLabel);
        headerLeft.add(Box.createVerticalStrut(2));
        headerLeft.add(appInternshipRoleLabel);
        headerLeft.add(Box.createVerticalStrut(4));
        headerLeft.add(appDateStatusLabel);

        cardHeader.add(headerLeft, BorderLayout.WEST);
        reviewCard.add(cardHeader, BorderLayout.NORTH);

        // Center 2 columns: Student Cover Letter and Placement Officer Notes
        JPanel textColumns = new JPanel(new GridLayout(1, 2, 16, 0));
        textColumns.setOpaque(false);

        // Left: Cover Letter
        JPanel coverBox = new JPanel(new BorderLayout(0, 4));
        coverBox.setOpaque(false);
        JLabel coverLbl = new JLabel("Student Cover Letter & Pitch");
        coverLbl.setFont(Theme.FONT_BOLD);
        coverLbl.setForeground(Theme.TEXT_SECONDARY);

        appCoverArea = new JTextArea("Cover letter details will appear here.");
        Components.styleTextArea(appCoverArea);
        appCoverArea.setEditable(false);
        appCoverArea.setLineWrap(true);
        appCoverArea.setWrapStyleWord(true);
        JScrollPane coverScroll = new JScrollPane(appCoverArea);
        coverScroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));

        coverBox.add(coverLbl, BorderLayout.NORTH);
        coverBox.add(coverScroll, BorderLayout.CENTER);

        // Right: Feedback Area
        JPanel feedbackBox = new JPanel(new BorderLayout(0, 4));
        feedbackBox.setOpaque(false);
        JLabel feedbackLbl = new JLabel("Placement Officer Feedback & History");
        feedbackLbl.setFont(Theme.FONT_BOLD);
        feedbackLbl.setForeground(Theme.TEXT_SECONDARY);

        appFeedbackArea = new JTextArea("Recorded evaluation notes will appear here.");
        Components.styleTextArea(appFeedbackArea);
        appFeedbackArea.setEditable(false);
        appFeedbackArea.setLineWrap(true);
        appFeedbackArea.setWrapStyleWord(true);
        JScrollPane feedbackScroll = new JScrollPane(appFeedbackArea);
        feedbackScroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));

        feedbackBox.add(feedbackLbl, BorderLayout.NORTH);
        feedbackBox.add(feedbackScroll, BorderLayout.CENTER);

        textColumns.add(coverBox);
        textColumns.add(feedbackBox);
        reviewCard.add(textColumns, BorderLayout.CENTER);

        // Bottom Decision Actions
        JPanel decisionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        decisionBox.setOpaque(false);

        acceptBtn = new AnimatedButton("Accept Application", Icons.get(Icons.Type.CHECK, 16, Color.WHITE), AnimatedButton.Style.SUCCESS);
        acceptBtn.setEnabled(false);
        acceptBtn.addActionListener(e -> handleUpdateApplicationStatus(Application.Status.ACCEPTED));

        rejectBtn = new AnimatedButton("Reject Application", Icons.get(Icons.Type.CLOSE, 16, Color.WHITE), AnimatedButton.Style.DANGER);
        rejectBtn.setEnabled(false);
        rejectBtn.addActionListener(e -> handleUpdateApplicationStatus(Application.Status.REJECTED));

        decisionBox.add(rejectBtn);
        decisionBox.add(acceptBtn);
        reviewCard.add(decisionBox, BorderLayout.SOUTH);

        appTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedApplicationDetail();
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                Components.createTableScrollPane(appTable), reviewCard);
        splitPane.setResizeWeight(0.60);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        panel.add(splitPane, BorderLayout.CENTER);

        refreshApplicationsTable();
        return panel;
    }

    private void refreshApplicationsTable() {
        try {
            appTableModel.setRowCount(0);
            allApplications = applicationService.getAllApplications();

            for (Application app : allApplications) {
                appTableModel.addRow(new Object[]{
                        "#" + app.getId(),
                        app.getStudent() != null ? app.getStudent().getName() : "N/A",
                        app.getInternship() != null ? app.getInternship().getTitle() : "N/A",
                        app.getInternship() != null ? app.getInternship().getCompany() : "N/A",
                        app.getAppliedAt().toLocalDate(),
                        app.getStatus().name()
                });
            }
        } catch (Exception e) {
            System.err.println("Error refreshing applications table: " + e.getMessage());
        }
    }

    private void updateSelectedApplicationDetail() {
        int viewRow = appTable.getSelectedRow();
        if (viewRow != -1 && allApplications != null) {
            int modelRow = appTable.convertRowIndexToModel(viewRow);
            if (modelRow >= 0 && modelRow < allApplications.size()) {
                Application app = allApplications.get(modelRow);
                Student s = app.getStudent();
                Internship i = app.getInternship();

                String studentName = s != null ? s.getName() : "Unknown Student";
                String deptCgpa = s != null ? (s.getDepartment() + " | CGPA: " + String.format("%.2f", s.getCgpa())) : "N/A";
                appStudentNameLabel.setText(studentName + " (" + deptCgpa + ")");

                String role = i != null ? i.getTitle() : "Unknown Position";
                String company = i != null ? i.getCompany() : "Unknown Company";
                appInternshipRoleLabel.setText(role + " at " + company);

                appDateStatusLabel.setText("Submitted: " + app.getAppliedAt() + " • Status: " + app.getStatus().name());

                appCoverArea.setText(app.getCoverLetter() != null && !app.getCoverLetter().isEmpty() ?
                        app.getCoverLetter() : "No cover letter provided.");

                appFeedbackArea.setText(app.getAdminFeedback() != null && !app.getAdminFeedback().isEmpty() ?
                        app.getAdminFeedback() : "No officer feedback recorded yet.");

                acceptBtn.setEnabled(true);
                rejectBtn.setEnabled(true);
                return;
            }
        }

        acceptBtn.setEnabled(false);
        rejectBtn.setEnabled(false);
    }

    private void handleUpdateApplicationStatus(Application.Status status) {
        int viewRow = appTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an application from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = appTable.convertRowIndexToModel(viewRow);
        Application app = allApplications.get(modelRow);

        JTextArea feedbackArea = new JTextArea(4, 30);
        Components.styleTextArea(feedbackArea);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setText(status == Application.Status.ACCEPTED ?
                "Congratulations! Your profile has been accepted for the interview round." :
                "Thank you for applying. Unfortunately, your profile was not selected at this time.");

        JScrollPane scroll = new JScrollPane(feedbackArea);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                new Object[]{
                        "Review Decision: " + status.name(),
                        "Please provide feedback or an explanatory note for the student:",
                        scroll
                },
                "Confirm " + status.name() + " Decision",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (confirm == JOptionPane.OK_OPTION) {
            try {
                applicationService.updateStatus(app.getId(), status, feedbackArea.getText().trim());
                refreshApplicationsTable();
                updateSelectedApplicationDetail();
                JOptionPane.showMessageDialog(this, "Application status successfully updated to " + status.name() + "!", "Decision Recorded", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating application status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
