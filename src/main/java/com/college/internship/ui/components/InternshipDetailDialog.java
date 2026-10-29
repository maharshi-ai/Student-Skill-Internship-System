package com.college.internship.ui.components;

import com.college.internship.model.Internship;
import com.college.internship.service.ApplicationService;
import com.college.internship.ui.Components;
import com.college.internship.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Premium modal dialog for inspecting complete internship specifications,
 * eligibility criteria, and submitting cover letters.
 */
public class InternshipDetailDialog extends JDialog {

    private final Internship internship;
    private final int studentId;
    private final ApplicationService applicationService;
    private final Runnable onApplicationSubmitted;

    public InternshipDetailDialog(Window parent, Internship internship, int studentId,
                                  String studentSkillsSummary,
                                  ApplicationService applicationService,
                                  Runnable onApplicationSubmitted) {
        super(parent, "Internship Details — " + internship.getTitle(), ModalityType.APPLICATION_MODAL);
        this.internship = internship;
        this.studentId = studentId;
        this.applicationService = applicationService;
        this.onApplicationSubmitted = onApplicationSubmitted;

        setSize(680, 640);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_MAIN);

        initUI(studentSkillsSummary);
    }

    private void initUI(String studentSkillsSummary) {
        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(Theme.BG_MAIN);
        main.setBorder(new EmptyBorder(24, 28, 24, 28));

        // 1. Header Card
        GlassPanel headerCard = new GlassPanel(12, false);
        headerCard.setLayout(new BorderLayout(12, 6));
        headerCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel titleLbl = new JLabel(internship.getTitle());
        titleLbl.setFont(Theme.FONT_TITLE);
        titleLbl.setForeground(Theme.TEXT_MAIN);

        JLabel compLbl = new JLabel(internship.getCompany() + " • " + internship.getLocation());
        compLbl.setFont(Theme.FONT_SUBHEADER);
        compLbl.setForeground(Theme.ACCENT_SUBTLE);

        JPanel metaPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        metaPills.setOpaque(false);
        metaPills.add(Components.createBadge(internship.getStipend(), new Color(16, 185, 129, 30), new Color(52, 211, 153)));
        metaPills.add(Components.createBadge("Deadline: " + internship.getDeadline(), new Color(30, 41, 59, 180), Theme.TEXT_MUTED));
        metaPills.add(new StatusPill(internship.getStatus().name()));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(titleLbl);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(compLbl);
        titleBlock.add(Box.createVerticalStrut(8));
        titleBlock.add(metaPills);

        headerCard.add(titleBlock, BorderLayout.CENTER);
        main.add(headerCard, BorderLayout.NORTH);

        // 2. Center Scrollable Content (Description + Requirements + Application Note)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Description Card
        GlassPanel descCard = createSectionCard("ROLE DESCRIPTION", internship.getDescription());
        contentPanel.add(descCard);
        contentPanel.add(Box.createVerticalStrut(14));

        // Requirements Card
        GlassPanel reqCard = createSectionCard("SKILLS & ELIGIBILITY CRITERIA", internship.getRequirements());
        contentPanel.add(reqCard);
        contentPanel.add(Box.createVerticalStrut(14));

        // Cover Letter Area (Only if student is logged in and role is OPEN)
        boolean hasApplied = false;
        if (studentId > 0) {
            try {
                hasApplied = applicationService.hasStudentApplied(studentId, internship.getId());
            } catch (Exception ignored) {}
        }

        JTextArea coverArea = null;
        if (studentId > 0 && !hasApplied && internship.getStatus() == Internship.Status.OPEN) {
            GlassPanel applyCard = new GlassPanel(12, false);
            applyCard.setLayout(new BorderLayout(0, 8));
            applyCard.setBorder(new EmptyBorder(14, 16, 14, 16));

            JLabel applyHeader = new JLabel("SUBMIT COVER LETTER / PERSONAL NOTE");
            applyHeader.setFont(Theme.FONT_SMALL);
            applyHeader.setForeground(Theme.ACCENT_SUBTLE);

            coverArea = new JTextArea(4, 30);
            Components.styleTextArea(coverArea);
            coverArea.setLineWrap(true);
            coverArea.setWrapStyleWord(true);
            coverArea.setText("I am enthusiastic about applying for the " + internship.getTitle() + " position at " +
                    internship.getCompany() + ". My skill set includes " + studentSkillsSummary + ".");

            JScrollPane coverScroll = new JScrollPane(coverArea);
            coverScroll.setBorder(BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1, true));

            applyCard.add(applyHeader, BorderLayout.NORTH);
            applyCard.add(coverScroll, BorderLayout.CENTER);

            contentPanel.add(applyCard);
        } else if (hasApplied) {
            JLabel appliedBanner = Components.createBadge("YOU HAVE ALREADY SUBMITTED AN APPLICATION FOR THIS POSITION",
                    Theme.SUCCESS_BG, Theme.SUCCESS_TEXT);
            appliedBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(appliedBanner);
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        main.add(scrollPane, BorderLayout.CENTER);

        // 3. Footer Action Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);

        AnimatedButton closeBtn = new AnimatedButton("Close", AnimatedButton.Style.SECONDARY);
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);

        if (studentId > 0 && !hasApplied && internship.getStatus() == Internship.Status.OPEN) {
            final JTextArea finalCoverArea = coverArea;
            AnimatedButton submitBtn = new AnimatedButton("Submit Application", AnimatedButton.Style.PRIMARY);
            submitBtn.addActionListener(e -> {
                String note = finalCoverArea != null ? finalCoverArea.getText().trim() : "";
                try {
                    applicationService.applyForInternship(studentId, internship.getId(), note);
                    JOptionPane.showMessageDialog(this,
                            "Application submitted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    if (onApplicationSubmitted != null) {
                        onApplicationSubmitted.run();
                    }
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Application failed: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            footer.add(submitBtn);
        }

        main.add(footer, BorderLayout.SOUTH);
        setContentPane(main);
    }

    private GlassPanel createSectionCard(String heading, String content) {
        GlassPanel card = new GlassPanel(12, false);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel hLbl = new JLabel(heading);
        hLbl.setFont(Theme.FONT_SMALL);
        hLbl.setForeground(Theme.TEXT_MUTED);

        JTextArea area = new JTextArea(content != null ? content : "None specified.");
        Components.styleTextArea(area);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(0, 0, 0, 0));

        card.add(hLbl, BorderLayout.NORTH);
        card.add(area, BorderLayout.CENTER);
        return card;
    }
}
