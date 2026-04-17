package View;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.ProjectDao;
import model.*;
import model.Project.ProjectStatus;
import util.AppTheme;

/**
 * Inline New / Edit bug form.
 * Row order: Title | Description | Due Date | Category  Severity  Priority | Project | [Status edit-only] | Buttons
 */
public class BugFormPanel extends JPanel {

    private final BugListView parent;
    private final boolean     editMode;

    private JTextField    txtTitle   = new JTextField();
    private JTextArea     txtDesc    = new JTextArea(6, 40);
    private JTextField    txtDueDate = new JTextField();

    private JComboBox<String> cmbCategory = new JComboBox<>(new String[]{
        "FUNCTIONAL","GRAPHICAL","PERFORMANCE","TECHNICAL","SECURITY","COMPATIBILITY"});
    private JComboBox<String> cmbSeverity = new JComboBox<>(new String[]{
        "BLOCKER","SEVERE","MAJOR","MINOR","COSMETIC"});
    private JComboBox<String> cmbPriority = new JComboBox<>(new String[]{
        "LOW","MEDIUM","HIGH"});
    private JComboBox<String> cmbStatus   = new JComboBox<>(new String[]{
        "OPEN","IN_PROGRESS","COMPLETED"});

    // Project dropdown — populated dynamically
    private JComboBox<String> cmbProject  = new JComboBox<>();

    private JLabel  lblFormTitle = new JLabel();
    private JButton btnSave, btnCancel;
    private Bug     currentBug;

    // Holds the project list loaded when form is built / refreshed
    private List<Project> projectList;

    public BugFormPanel(BugListView parent, boolean editMode) {
        this.parent   = parent;
        this.editMode = editMode;
        buildUI();
    }

    private void buildUI() {
        setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(AppTheme.BG_HEADER);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR),
            new EmptyBorder(14,24,14,24)));
        lblFormTitle.setFont(AppTheme.FONT_HEADING);
        lblFormTitle.setForeground(AppTheme.TEXT_PRIMARY);
        lblFormTitle.setText(editMode ? "Edit Bug" : "New Bug");
        hdr.add(lblFormTitle, BorderLayout.WEST);
        add(hdr, BorderLayout.NORTH);

        // Form card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(AppTheme.BORDER_PANEL, new EmptyBorder(24,36,24,36)));

        int row = 0;

        // Title
        card.add(lbl("Title *"), lgbc(0, row, 3)); row++;
        styleField(txtTitle);
        card.add(txtTitle, fgbc(0, row, 3, 1.0)); row++;

        // Description
        card.add(lbl("Description"), lgbc(0, row, 3)); row++;
        txtDesc.setFont(AppTheme.FONT_BODY);
        txtDesc.setBackground(AppTheme.BG_DARK);
        txtDesc.setForeground(AppTheme.TEXT_PRIMARY);
        txtDesc.setCaretColor(AppTheme.TEXT_PRIMARY);
        txtDesc.setLineWrap(true); txtDesc.setWrapStyleWord(true);
        txtDesc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR), new EmptyBorder(6,10,6,10)));
        JScrollPane ds = new JScrollPane(txtDesc);
        ds.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
        GridBagConstraints dsc = fgbc(0, row, 3, 1.0); dsc.ipady = 80;
        card.add(ds, dsc); row++;

        // Due Date
        card.add(lbl("Due Date (YYYY-MM-DD) *"), lgbc(0, row, 3)); row++;
        styleField(txtDueDate);
        card.add(txtDueDate, fgbc(0, row, 3, 1.0)); row++;

        // Category | Severity | Priority — three columns same row
        card.add(lbl("Category"), lgbc(0, row, 1));
        card.add(lbl("Severity"), lgbc(1, row, 1));
        card.add(lbl("Priority"), lgbc(2, row, 1));
        row++;
        styleCombo(cmbCategory); styleCombo(cmbSeverity); styleCombo(cmbPriority);
        GridBagConstraints cg = fgbc(0, row, 1, 0.34); cg.insets = new Insets(0,0,4,10);
        card.add(cmbCategory, cg);
        GridBagConstraints sg = fgbc(1, row, 1, 0.33); sg.insets = new Insets(0,0,4,10);
        card.add(cmbSeverity, sg);
        GridBagConstraints pg = fgbc(2, row, 1, 0.33); pg.insets = new Insets(0,0,4,0);
        card.add(cmbPriority, pg);
        row++;

        // Project
        card.add(lbl("Project"), lgbc(0, row, 3)); row++;
        styleCombo(cmbProject);
        card.add(cmbProject, fgbc(0, row, 3, 1.0)); row++;

        // Status (edit mode only)
        if (editMode) {
            card.add(lbl("Status"), lgbc(0, row, 3)); row++;
            styleCombo(cmbStatus);
            card.add(cmbStatus, fgbc(0, row, 3, 1.0)); row++;
        }

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnSave   = AppTheme.primaryButton(editMode ? "Save Changes" : "Create Bug");
        btnCancel = AppTheme.secondaryButton("Cancel");
        btnRow.add(btnCancel); btnRow.add(btnSave);
        GridBagConstraints bc = fgbc(0, row, 3, 1.0); bc.insets = new Insets(16,0,0,0);
        card.add(btnRow, bc);

        btnSave.addActionListener(e   -> parent.getController().onSaveFormClick(editMode));
        btnCancel.addActionListener(e -> { parent.restoreSidebar(); parent.showCard(BugListView.CARD_TABLE); parent.showInfo("Cancelled."); });

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(AppTheme.BG_DARK);
        wrapper.setBorder(new EmptyBorder(24,40,24,40));
        wrapper.add(card, new GridBagConstraints(){{fill=BOTH;weightx=1;weighty=1;}});
        add(wrapper, BorderLayout.CENTER);
    }

    // ── Public API ────────────────────────────────────────────────────────────────

    /** Reload open projects into dropdown. Call before showing this panel. */
    public void refreshProjects() {
        ProjectDao dao = new ProjectDao();
        // Open projects always shown; if editing a bug with a closed project, that project is also shown
        projectList = dao.findOpen();
        cmbProject.removeAllItems();
        for (Project p : projectList) cmbProject.addItem(p.getName());
    }

    /** Reload projects list but also ensure the bug's current (possibly closed) project is included. */
    public void refreshProjectsForEdit(Project currentProject) {
        ProjectDao dao = new ProjectDao();
        projectList = dao.findOpen();
        // Ensure current project is in the list even if closed
        if (currentProject != null && currentProject.isClosed()) {
            boolean found = projectList.stream().anyMatch(p -> p.getId() == currentProject.getId());
            if (!found) projectList.add(0, currentProject);
        }
        cmbProject.removeAllItems();
        for (Project p : projectList) cmbProject.addItem(p.getName());
    }

    public void clearForm() {
        currentBug = null;
        txtTitle.setText(""); txtDesc.setText(""); txtDueDate.setText("");
        cmbCategory.setSelectedItem("FUNCTIONAL");
        cmbSeverity.setSelectedItem("MAJOR");
        cmbPriority.setSelectedItem("MEDIUM");
        cmbStatus.setSelectedItem("OPEN");
        refreshProjects();
        if (cmbProject.getItemCount() > 0) cmbProject.setSelectedIndex(0);
    }

    public void populateForm(Bug bug) {
        currentBug = bug;
        txtTitle.setText(bug.getTitle());
        txtDesc.setText(bug.getDescription() != null ? bug.getDescription() : "");
        txtDueDate.setText(bug.getDueDate() != null ? bug.getDueDate().toString() : "");
        cmbCategory.setSelectedItem(bug.getCategory() != null ? bug.getCategory().name() : "FUNCTIONAL");
        cmbSeverity.setSelectedItem(bug.getSeverity() != null ? bug.getSeverity().name() : "MAJOR");
        cmbPriority.setSelectedItem(bug.getPriority().name());
        cmbStatus.setSelectedItem(bug.getStatus().name());
        refreshProjectsForEdit(bug.getProject());
        if (bug.getProject() != null) cmbProject.setSelectedItem(bug.getProject().getName());
    }

    // ── Getters ───────────────────────────────────────────────────────────────────
    public String getTitleText()    { return txtTitle.getText().trim(); }
    public String getDescText()     { return txtDesc.getText().trim(); }
    public String getDueDateText()  { return txtDueDate.getText().trim(); }
    public String getCategoryText() { return (String) cmbCategory.getSelectedItem(); }
    public String getSeverityText() { return (String) cmbSeverity.getSelectedItem(); }
    public String getPriorityText() { return (String) cmbPriority.getSelectedItem(); }
    public String getStatusText()   { return (String) cmbStatus.getSelectedItem(); }
    public Bug    getCurrentBug()   { return currentBug; }

    /** Returns the Project object selected, or null. */
    public Project getSelectedProject() {
        String name = (String) cmbProject.getSelectedItem();
        if (name == null || projectList == null) return null;
        return projectList.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    // ── Layout helpers ────────────────────────────────────────────────────────────
    private GridBagConstraints lgbc(int x, int y, int w) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x; c.gridy=y; c.gridwidth=w;
        c.anchor=GridBagConstraints.WEST; c.insets=new Insets(10,0,2,0);
        return c;
    }
    private GridBagConstraints fgbc(int x, int y, int w, double wx) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=x; c.gridy=y; c.gridwidth=w;
        c.fill=GridBagConstraints.HORIZONTAL; c.weightx=wx; c.insets=new Insets(0,0,4,0);
        return c;
    }
    private JLabel lbl(String t) {
        JLabel l = new JLabel(t); l.setFont(AppTheme.FONT_LABEL); l.setForeground(AppTheme.TEXT_MUTED); return l;
    }
    private void styleField(JTextField f) {
        f.setFont(AppTheme.FONT_BODY); f.setBackground(AppTheme.BG_DARK);
        f.setForeground(AppTheme.TEXT_PRIMARY); f.setCaretColor(AppTheme.TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR), new EmptyBorder(6,10,6,10)));
    }
    private void styleCombo(JComboBox<?> c) {
        c.setBackground(AppTheme.BG_PANEL); c.setForeground(AppTheme.TEXT_PRIMARY); c.setFont(AppTheme.FONT_BODY);
    }
}
