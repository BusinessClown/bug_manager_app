package controller;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import View.BugListView;
import dao.BugDaoImplentation;
import dao.ProjectDao;
import model.*;
import service.BugService;
import util.AppTheme;

public class BugListController {

    private final BugListView bugListView;
    private final User        loggedInUser;

    private final BugDaoImplentation bugDao     = new BugDaoImplentation();
    private final BugService         bugService = new BugService(bugDao);
    private final ProjectDao         projectDao = new ProjectDao();

    private List<Bug>    currentBugs   = new ArrayList<>();
    private boolean      showingMyBugs = true;

    public BugListController(BugListView bugListView, User loggedInUser) {
        this.bugListView  = bugListView;
        this.loggedInUser = loggedInUser;
    }

    // ── Load ──────────────────────────────────────────────────────────────────────
    public void loadMyBugs() {
        showingMyBugs = true;
        List<Bug> all = bugService.getBugsByUser(loggedInUser.getId());
        currentBugs = all.stream()
            .filter(b -> bugListView.isShowingClosedProjects() || !b.isInClosedProject())
            .collect(Collectors.toList());
        bugListView.displayBugs(currentBugs);
        bugListView.showCard(BugListView.CARD_TABLE);
        bugListView.showInfo("Showing your bugs (" + currentBugs.size() + ")");
        refreshProjectFilter();
    }

    public void loadAllBugs() {
        showingMyBugs = false;
        List<Bug> all = bugService.getAllBugs();
        currentBugs = all.stream()
            .filter(b -> bugListView.isShowingClosedProjects() || !b.isInClosedProject())
            .collect(Collectors.toList());
        bugListView.displayBugs(currentBugs);
        bugListView.showCard(BugListView.CARD_TABLE);
        bugListView.showInfo("Showing all bugs (" + currentBugs.size() + ")");
        refreshProjectFilter();
    }

    public void refreshCurrent() {
        if (showingMyBugs) loadMyBugs(); else loadAllBugs();
    }

    public boolean isShowingMyBugs() { return showingMyBugs; }

    /** Reload project names into the filter dropdown. */
    public void refreshProjectFilter() {
        List<String> names = projectDao.findAll().stream()
            .filter(p -> bugListView.isShowingClosedProjects() || !p.isClosed())
            .map(Project::getName)
            .collect(Collectors.toList());
        bugListView.refreshProjectFilter(names);
    }

    // ── Toggle show closed projects ───────────────────────────────────────────────
    public void onToggleClosedProjects() {
        bugListView.setShowingClosedProjects(!bugListView.isShowingClosedProjects());
        refreshCurrent();
    }

    // ── Filter ────────────────────────────────────────────────────────────────────
    public void onApplyFilterClick() {
        String status   = (String) bugListView.getCmbStatus().getSelectedItem();
        String priority = (String) bugListView.getCmbPriority().getSelectedItem();
        String severity = (String) bugListView.getCmbSeverity().getSelectedItem();
        String category = (String) bugListView.getCmbCategory().getSelectedItem();
        String project  = (String) bugListView.getCmbProject().getSelectedItem();
        String keyword  = bugListView.getTxtKeywordSearch().getText();
        String owner    = showingMyBugs ? "My Bugs" : "All Bugs";

        List<Bug> filtered = bugService.getFilteredBugs(status, priority, severity, category,
                project != null ? project : "Any",
                owner, loggedInUser, keyword,
                bugListView.isShowingClosedProjects());
        currentBugs = filtered;
        bugListView.displayBugs(filtered);
        bugListView.showInfo("Filter applied — " + filtered.size() + " result(s)");
    }

    public void onClearFilterClick() {
        bugListView.getCmbStatus().setSelectedItem("Any");
        bugListView.getCmbPriority().setSelectedItem("Any");
        bugListView.getCmbSeverity().setSelectedItem("Any");
        bugListView.getCmbCategory().setSelectedItem("Any");
        bugListView.getCmbProject().setSelectedItem("Any");
        bugListView.getTxtKeywordSearch().setText("");
        refreshCurrent();
    }

    // ── New Project ───────────────────────────────────────────────────────────────
    public void onNewProjectClick() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(AppTheme.BG_PANEL);
        panel.setBorder(new EmptyBorder(20, 24, 12, 24));

        JLabel lbl = new JLabel("New Project Name:");
        lbl.setFont(AppTheme.FONT_LABEL);
        lbl.setForeground(AppTheme.TEXT_MUTED);

        JTextField tf = new JTextField(24);
        tf.setBackground(AppTheme.BG_DARK);
        tf.setForeground(AppTheme.TEXT_PRIMARY);
        tf.setCaretColor(AppTheme.TEXT_PRIMARY);
        tf.setFont(AppTheme.FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
            new EmptyBorder(6,10,6,10)));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(tf,  BorderLayout.CENTER);

        JButton btnCreate = AppTheme.primaryButton("Create");
        JButton btnCancel = AppTheme.secondaryButton("Cancel");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(AppTheme.BG_PANEL);
        btnRow.setBorder(new EmptyBorder(0, 24, 16, 24));
        btnRow.add(btnCancel); btnRow.add(btnCreate);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(AppTheme.BG_PANEL);
        content.add(panel,  BorderLayout.CENTER);
        content.add(btnRow, BorderLayout.SOUTH);

        JDialog dlg = new JDialog(bugListView, "New Project", true);
        dlg.setUndecorated(true);
        dlg.getRootPane().setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
        dlg.setContentPane(content);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(340, dlg.getHeight()));
        dlg.setLocationRelativeTo(bugListView);

        btnCancel.addActionListener(e -> dlg.dispose());
        btnCreate.addActionListener(e -> {
            String name = tf.getText().trim();
            if (name.isEmpty()) { bugListView.showError("Project name cannot be empty."); return; }
            if (projectDao.nameExists(name)) { bugListView.showError("A project with that name already exists."); return; }
            long id = projectDao.insert(name);
            dlg.dispose();
            if (id > 0) {
                bugListView.showSuccess("Project \"" + name + "\" created.");
                refreshProjectFilter();
            } else {
                bugListView.showError("Failed to create project.");
            }
        });

        dlg.setVisible(true);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────────
    public void onAddButtonClick()  { bugListView.showNewBugForm(); bugListView.showInfo("Fill in the form to create a new bug."); }
    public void onReadButtonClick() { Bug b=getSelectedBug(); if(b==null){bugListView.showError("Please select a bug to read.");return;} bugListView.showReadBugPanel(b); }
    public void onEditButtonClick() { Bug b=getSelectedBug(); if(b==null){bugListView.showError("Please select a bug to edit.");return;} bugListView.showEditBugForm(b); }

    public void onEditFromReadPanel() {
        Bug bug = getSelectedBug();
        if (bug == null) { bugListView.showCard(BugListView.CARD_TABLE); return; }
        bugListView.showEditBugForm(bug);
    }

    public void onMarkCompleteClick() {
        Bug bug = getSelectedBug();
        if (bug == null) { bugListView.showError("Please select a bug."); return; }
        bug.setStatus(BugStatus.COMPLETED);
        bugService.updateBug(bug); refreshCurrent();
        bugListView.showSuccess("Bug marked as Completed.");
    }

    public void onMarkInProgressClick() {
        Bug bug = getSelectedBug();
        if (bug == null) { bugListView.showError("Please select a bug."); return; }
        bug.setStatus(BugStatus.IN_PROGRESS);
        bugService.updateBug(bug); refreshCurrent();
        bugListView.showSuccess("Bug marked as In Progress.");
    }

    public void onDeleteClick() {
        Bug bug = getSelectedBug();
        if (bug == null) { bugListView.showError("Please select a bug to delete."); return; }
        if (showThemedConfirm(bugListView, "Delete Bug",
                "Delete \"" + bug.getTitle() + "\"?\nThis cannot be undone.")) {
            bugService.deleteBug(bug.getId());
            refreshCurrent();
            bugListView.showSuccess("Bug deleted.");
        }
    }

    // ── Save form ─────────────────────────────────────────────────────────────────
    public void onSaveFormClick(boolean editMode) {
        View.BugFormPanel form = editMode ? bugListView.getEditBugPanel() : bugListView.getNewBugPanel();

        String title    = form.getTitleText();
        String desc     = form.getDescText();
        String dueDateStr = form.getDueDateText();
        String priority = form.getPriorityText();
        String severity = form.getSeverityText();
        String category = form.getCategoryText();
        String status   = form.getStatusText();
        Project project = form.getSelectedProject();

        if (title.isEmpty())    { bugListView.showError("Title is required."); return; }
        if (dueDateStr.isEmpty()){ bugListView.showError("Due date is required."); return; }

        LocalDate dueDate;
        try { dueDate = LocalDate.parse(dueDateStr); }
        catch (DateTimeParseException e) { bugListView.showError("Due date must be in YYYY-MM-DD format."); return; }
        if (dueDate.isBefore(LocalDate.now())) { bugListView.showError("Due date cannot be in the past."); return; }

        if (editMode) {
            Bug bug = form.getCurrentBug();
            if (bug == null) { bugListView.showError("No bug selected for editing."); return; }
            bug.setTitle(title); bug.setDescription(desc); bug.setDueDate(dueDate);
            bug.setPriority(BugPriority.valueOf(priority));
            bug.setSeverity(BugSeverity.valueOf(severity));
            bug.setCategory(BugCategory.valueOf(category));
            bug.setStatus(BugStatus.valueOf(status));
            bug.setProject(project);
            bugService.updateBug(bug);
            bugListView.showSuccess("Bug \"" + title + "\" updated successfully.");
        } else {
            Bug newBug = new Bug(0L, loggedInUser, title, desc, LocalDate.now(), dueDate,
                    BugStatus.OPEN, BugPriority.valueOf(priority),
                    BugSeverity.valueOf(severity), BugCategory.valueOf(category));
            newBug.setProject(project);
            long newId = bugService.createBug(newBug);
            if (newId == -1) { bugListView.showError("Failed to save bug. Please try again."); return; }
            bugListView.showSuccess("Bug \"" + title + "\" created successfully.");
        }
        refreshCurrent();
        bugListView.showCard(BugListView.CARD_TABLE);
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────────
    public void onSidebarSelect(Bug bug) {
        bugListView.showReadBugPanel(bug);
        syncTableSelection(bug.getId());
    }

    // ── Themed confirm dialog ─────────────────────────────────────────────────────
    public static boolean showThemedConfirm(Component parent, String title, String message) {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(AppTheme.BG_PANEL);
        panel.setBorder(new EmptyBorder(24, 28, 8, 28));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.FONT_HEADING);
        lblTitle.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel lblMsg = new JLabel("<html>" + message.replace("\n","<br>") + "</html>");
        lblMsg.setFont(AppTheme.FONT_BODY);
        lblMsg.setForeground(AppTheme.TEXT_MUTED);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblMsg,   BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(AppTheme.BG_PANEL);
        btnRow.setBorder(new EmptyBorder(0, 28, 20, 28));
        JButton btnNo  = AppTheme.secondaryButton("Cancel");
        JButton btnYes = AppTheme.dangerButton("Delete");

        final boolean[] result = {false};
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title,
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setBackground(AppTheme.BG_PANEL);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1));
        btnNo.addActionListener(e  -> dialog.dispose());
        btnYes.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        btnRow.add(btnNo); btnRow.add(btnYes);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(AppTheme.BG_PANEL);
        content.add(panel,  BorderLayout.CENTER);
        content.add(btnRow, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(360, dialog.getHeight()));
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setVisible(true);
        return result[0];
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────
    private Bug getSelectedBug() {
        int row = bugListView.getTblBugs().getSelectedRow();
        if (row == -1) return null;
        long id = (long) bugListView.getTblBugs().getValueAt(row, 0);
        return bugService.findById(currentBugs, id);
    }

    private void syncTableSelection(long bugId) {
        for (int i = 0; i < bugListView.getTableModel().getRowCount(); i++) {
            if ((long) bugListView.getTableModel().getValueAt(i, 0) == bugId) {
                bugListView.getTblBugs().setRowSelectionInterval(i, i);
                return;
            }
        }
    }
}
