package View;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import controller.BugListController;
import model.Bug;
import model.BugStatus;
import model.User;
import util.AppTheme;

public class BugListView extends JFrame {

    private final User loggedInUser;
    private BugListController controller;

    // ── Header ───────────────────────────────────────────────────────────────────
    private JLabel  lblWelcome;
    private JButton btnMyBugs, btnAllBugs, btnAdminUsers, btnNewProject, btnProjectMgmt, btnLogout;

    // ── Sidebar ──────────────────────────────────────────────────────────────────
    private DefaultListModel<String> sidebarModel = new DefaultListModel<>();
    private JList<String>            sidebarList  = new JList<>(sidebarModel);
    private List<Bug>                sidebarBugs;
    private JLabel                   sidebarLabel;

    // ── Filter bar ───────────────────────────────────────────────────────────────
    private JComboBox<String> cmbStatus   = new JComboBox<>(new String[]{"Any","OPEN","IN_PROGRESS","COMPLETED"});
    private JComboBox<String> cmbPriority = new JComboBox<>(new String[]{"Any","LOW","MEDIUM","HIGH"});
    private JComboBox<String> cmbSeverity = new JComboBox<>(new String[]{"Any","BLOCKER","SEVERE","MAJOR","MINOR","COSMETIC"});
    private JComboBox<String> cmbCategory = new JComboBox<>(new String[]{"Any","FUNCTIONAL","GRAPHICAL","PERFORMANCE","TECHNICAL","SECURITY","COMPATIBILITY"});
    private JComboBox<String> cmbProject  = new JComboBox<>(new String[]{"Any"});
    private JTextField txtKeyword = new JTextField(14);
    private JButton btnApplyFilter = AppTheme.primaryButton("Filter");
    private JButton btnClearFilter = AppTheme.secondaryButton("Clear");
    private JButton btnShowClosed  = AppTheme.secondaryButton("Show Closed Projects");
    private boolean showingClosedProjects = false;

    // ── Table ────────────────────────────────────────────────────────────────────
    private JTable            tblBugs;
    private DefaultTableModel tableModel;
    private List<Bug>         tableBugs;

    // ── Action buttons ───────────────────────────────────────────────────────────
    private JButton btnNewBug       = AppTheme.primaryButton("+ New Bug");
    private JButton btnRead         = AppTheme.secondaryButton("Read");
    private JButton btnEditBug      = AppTheme.secondaryButton("Edit");
    private JButton btnMarkComplete = AppTheme.secondaryButton("Mark Complete");
    private JButton btnMarkProgress = AppTheme.secondaryButton("Mark In Progress");
    private JButton btnDelete       = AppTheme.dangerButton("Delete");
    private JButton btnRefresh      = AppTheme.secondaryButton("Refresh");

    // ── Cards ────────────────────────────────────────────────────────────────────
    private JPanel     centerCards;
    private CardLayout cardLayout = new CardLayout();
    public static final String CARD_TABLE   = "TABLE";
    public static final String CARD_NEW     = "NEW";
    public static final String CARD_EDIT    = "EDIT";
    public static final String CARD_READ    = "READ";
    public static final String CARD_USERS   = "USERS";
    public static final String CARD_PROJ_MGMT = "PROJ_MGMT";

    // ── Status bar ───────────────────────────────────────────────────────────────
    private JLabel lblStatus;

    // ── Sub-panels ───────────────────────────────────────────────────────────────
    private BugFormPanel        newBugPanel;
    private BugFormPanel        editBugPanel;
    private BugReadPanel        readBugPanel;
    private UserManagementPanel userManagementPanel;
    private ProjectManagementPanel projectManagementPanel;

    // ═══════════════════════════════════════════════════════════════════════════════
    public BugListView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        controller = new BugListController(this, loggedInUser);
        setTitle("Bug Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1800, 900));
        buildUI();
        pack();
        setLocationRelativeTo(null);
        controller.loadMyBugs();
    }

    private void buildUI() {
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildBody(),      BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Header ───────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(AppTheme.BG_HEADER);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR),
            new EmptyBorder(10,20,10,20)));

        JLabel logo = new JLabel("🐞 Bug Tracker");
        logo.setFont(AppTheme.FONT_HEADING);
        logo.setForeground(AppTheme.ACCENT);

        lblWelcome = new JLabel("Hello, " + loggedInUser.getFullname()
            + (loggedInUser.isAdmin() ? "  [Admin]" : ""));
        lblWelcome.setFont(AppTheme.FONT_BODY);
        lblWelcome.setForeground(AppTheme.TEXT_MUTED);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        btnMyBugs  = AppTheme.primaryButton("My Bugs");
        btnAllBugs = AppTheme.secondaryButton("All Bugs");
        btnMyBugs.addActionListener(e  -> { controller.loadMyBugs();  setMyBugsActive(true); });
        btnAllBugs.addActionListener(e -> { controller.loadAllBugs(); setMyBugsActive(false); });
        right.add(lblWelcome);
        right.add(Box.createHorizontalStrut(12));
        right.add(btnMyBugs);
        right.add(btnAllBugs);

        if (loggedInUser.isAdmin()) {
            btnAdminUsers = AppTheme.secondaryButton("User Management");
            btnAdminUsers.addActionListener(e -> showCard(CARD_USERS));
            right.add(Box.createHorizontalStrut(8));
            right.add(btnAdminUsers);

            btnProjectMgmt = AppTheme.secondaryButton("Project Management");
            btnProjectMgmt.addActionListener(e -> {
                if (projectManagementPanel != null) projectManagementPanel.reload();
                showCard(CARD_PROJ_MGMT);
            });
            right.add(Box.createHorizontalStrut(8));
            right.add(btnProjectMgmt);
        }

        // New Project button — between All Bugs area and Logout
        btnNewProject = AppTheme.primaryButton("+ New Project");
        btnNewProject.addActionListener(e -> controller.onNewProjectClick());
        right.add(Box.createHorizontalStrut(8));
        right.add(btnNewProject);

        right.add(Box.createHorizontalStrut(16));
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new java.awt.Dimension(1, 24));
        sep.setForeground(AppTheme.BORDER_COLOR);
        right.add(sep);
        right.add(Box.createHorizontalStrut(8));

        btnLogout = AppTheme.dangerButton("⏻  Log Out");
        btnLogout.addActionListener(e -> showLogoutDialog());
        right.add(btnLogout);

        h.add(logo,  BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ── Body ─────────────────────────────────────────────────────────────────────
    private JSplitPane buildBody() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildSidebar(), buildCenter());
        split.setDividerLocation(220);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setBackground(AppTheme.BG_DARK);
        return split;
    }

    // ── Sidebar ──────────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        sidebarList.setBackground(AppTheme.BG_SIDEBAR);
        sidebarList.setForeground(AppTheme.TEXT_PRIMARY);
        sidebarList.setFont(AppTheme.FONT_SMALL);
        sidebarList.setSelectionBackground(AppTheme.TABLE_SEL);
        sidebarList.setSelectionForeground(Color.WHITE);
        sidebarList.setFixedCellHeight(44);
        sidebarList.setBorder(null);
        sidebarList.setCellRenderer(new SidebarRenderer());

        sidebarList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && sidebarList.getSelectedIndex() >= 0) {
                int idx = sidebarList.getSelectedIndex();
                if (sidebarBugs != null && idx < sidebarBugs.size())
                    controller.onSidebarSelect(sidebarBugs.get(idx));
            }
        });

        JScrollPane scroll = new JScrollPane(sidebarList);
        scroll.setBorder(null);
        scroll.setBackground(AppTheme.BG_SIDEBAR);

        sidebarLabel = AppTheme.muted("  MY BUGS");
        sidebarLabel.setBorder(new EmptyBorder(10,10,6,10));

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.BG_SIDEBAR);
        p.add(sidebarLabel, BorderLayout.NORTH);
        p.add(scroll,       BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(220, 0));
        return p;
    }

    // ── Center ───────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        tableModel = new DefaultTableModel(
            new Object[]{"ID","Title","Status","Priority","Severity","Category","Project","Due Date","Submitted By"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        // DueDate=col7, Status=col2
        tblBugs = buildBugTableWithOverdue1(tableModel, 7, 2);

        newBugPanel  = new BugFormPanel(this, false);
        editBugPanel = new BugFormPanel(this, true);
        readBugPanel = new BugReadPanel(this);

        centerCards = new JPanel(cardLayout);
        centerCards.setBackground(AppTheme.BG_DARK);
        centerCards.add(buildTableCard(), CARD_TABLE);
        centerCards.add(newBugPanel,      CARD_NEW);
        centerCards.add(editBugPanel,     CARD_EDIT);
        centerCards.add(readBugPanel,     CARD_READ);

        if (loggedInUser.isAdmin()) {
            userManagementPanel = new UserManagementPanel(this);
            centerCards.add(userManagementPanel, CARD_USERS);

            projectManagementPanel = new ProjectManagementPanel(this);
            centerCards.add(projectManagementPanel, CARD_PROJ_MGMT);
        }
        cardLayout.show(centerCards, CARD_TABLE);
        return centerCards;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(AppTheme.BG_DARK);

        // Filter bar
        JPanel fb = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        fb.setBackground(AppTheme.BG_HEADER);
        fb.setBorder(BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR));
        styleCombo(cmbStatus); styleCombo(cmbPriority); styleCombo(cmbSeverity);
        styleCombo(cmbCategory); styleCombo(cmbProject);
        styleFilterField(txtKeyword);
        fb.add(AppTheme.muted("Status:"));   fb.add(cmbStatus);
        fb.add(Box.createHorizontalStrut(4));
        fb.add(AppTheme.muted("Priority:")); fb.add(cmbPriority);
        fb.add(Box.createHorizontalStrut(4));
        fb.add(AppTheme.muted("Severity:")); fb.add(cmbSeverity);
        fb.add(Box.createHorizontalStrut(4));
        fb.add(AppTheme.muted("Category:")); fb.add(cmbCategory);
        fb.add(Box.createHorizontalStrut(4));
        fb.add(AppTheme.muted("Project:"));  fb.add(cmbProject);
        fb.add(Box.createHorizontalStrut(4));
        fb.add(AppTheme.muted("Keyword:"));  fb.add(txtKeyword);
        fb.add(btnApplyFilter);
        fb.add(btnClearFilter);
        fb.add(Box.createHorizontalStrut(8));
        fb.add(btnShowClosed);
        btnApplyFilter.addActionListener(e -> controller.onApplyFilterClick());
        btnClearFilter.addActionListener(e -> controller.onClearFilterClick());
        btnShowClosed.addActionListener(e  -> controller.onToggleClosedProjects());

        JScrollPane scroll = new JScrollPane(tblBugs);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(AppTheme.BG_DARK);

        JPanel ab = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        ab.setBackground(AppTheme.BG_HEADER);
        ab.setBorder(BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER_COLOR));
        ab.add(btnNewBug); ab.add(btnRead); ab.add(btnEditBug);
        ab.add(btnMarkComplete); ab.add(btnMarkProgress);
        ab.add(btnDelete); ab.add(Box.createHorizontalStrut(8)); ab.add(btnRefresh);

        btnNewBug.addActionListener(e       -> controller.onAddButtonClick());
        btnRead.addActionListener(e         -> controller.onReadButtonClick());
        btnEditBug.addActionListener(e      -> controller.onEditButtonClick());
        btnMarkComplete.addActionListener(e -> controller.onMarkCompleteClick());
        btnMarkProgress.addActionListener(e -> controller.onMarkInProgressClick());
        btnDelete.addActionListener(e       -> controller.onDeleteClick());
        btnRefresh.addActionListener(e      -> controller.refreshCurrent());

        card.add(fb,     BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(ab,     BorderLayout.SOUTH);
        return card;
    }

    private void showLogoutDialog() {
        JDialog dlg = new JDialog(this, "Log Out", true);
        dlg.setUndecorated(true);
        dlg.setBackground(AppTheme.BG_PANEL);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG_PANEL);
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1),
            new EmptyBorder(28, 32, 24, 32)));

        JLabel icon = new JLabel("⏻", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        icon.setForeground(new Color(220, 60, 60));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Log out of Bug Tracker?", SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.BOLD, 15));
        msg.setForeground(AppTheme.TEXT_PRIMARY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("You'll be returned to the sign-in screen.", SwingConstants.CENTER);
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(icon);
        top.add(Box.createVerticalStrut(12));
        top.add(msg);
        top.add(Box.createVerticalStrut(6));
        top.add(sub);

        JButton btnYes = AppTheme.dangerButton("Yes, Log Out");
        JButton btnNo  = AppTheme.secondaryButton("Cancel");
        btnYes.setPreferredSize(new Dimension(130, 36));
        btnNo.setPreferredSize(new Dimension(100, 36));

        btnYes.addActionListener(e -> { dlg.dispose(); dispose(); new LoginView().setVisible(true); });
        btnNo.addActionListener(e  -> dlg.dispose());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btns.setOpaque(false);
        btns.add(btnNo);
        btns.add(btnYes);

        root.add(top,  BorderLayout.CENTER);
        root.add(btns, BorderLayout.SOUTH);
        ((JPanel) root.getComponent(1)).setBorder(new EmptyBorder(20, 0, 0, 0));

        dlg.setContentPane(root);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppTheme.BG_HEADER);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER_COLOR),
            new EmptyBorder(8,16,8,16)));
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        lblStatus.setForeground(AppTheme.TEXT_MUTED);
        bar.add(lblStatus, BorderLayout.WEST);
        return bar;
    }

    // ── Public API ────────────────────────────────────────────────────────────────

    public void displayBugs(List<Bug> bugs) {
        this.sidebarBugs = bugs;
        this.tableBugs   = bugs;
        tableModel.setRowCount(0);
        sidebarModel.clear();
        // Keep sidebar label in sync with My Bugs / All Bugs mode
        if (controller != null) {
            sidebarLabel.setText(controller.isShowingMyBugs() ? "  MY BUGS" : "  ALL BUGS");
        }
        for (Bug b : bugs) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getTitle(), b.getStatus(), b.getPriority(),
                b.getSeverity() != null ? b.getSeverity() : "",
                b.getCategory() != null ? b.getCategory() : "",
                b.getProject() != null ? b.getProject().getName() : "",
                b.getDueDate(), b.getUser().getFullname()
            });
            sidebarModel.addElement(b.getTitle());
        }
    }

    public void displayBugsInSidebar(List<Bug> bugs, String userName) {
        this.sidebarBugs = bugs;
        sidebarModel.clear();
        sidebarLabel.setText("  " + userName.toUpperCase() + "'S BUGS");
        for (Bug b : bugs) sidebarModel.addElement(b.getTitle());
    }

    public void restoreSidebar() {
        // Restore sidebar label to match current My Bugs / All Bugs mode
        sidebarLabel.setText(controller.isShowingMyBugs() ? "  MY BUGS" : "  ALL BUGS");
        sidebarModel.clear();
        if (sidebarBugs != null)
            for (Bug b : sidebarBugs) sidebarModel.addElement(b.getTitle());
    }

    /** Update header button styles and sidebar label when switching between My Bugs / All Bugs. */
    public void setMyBugsActive(boolean myBugs) {
        // Swap button appearance
        if (myBugs) {
            btnMyBugs.setBackground(AppTheme.ACCENT);
            btnMyBugs.setForeground(Color.WHITE);
            btnAllBugs.setBackground(AppTheme.BG_PANEL);
            btnAllBugs.setForeground(AppTheme.TEXT_PRIMARY);
        } else {
            btnAllBugs.setBackground(AppTheme.ACCENT);
            btnAllBugs.setForeground(Color.WHITE);
            btnMyBugs.setBackground(AppTheme.BG_PANEL);
            btnMyBugs.setForeground(AppTheme.TEXT_PRIMARY);
        }
        // Update sidebar label
        sidebarLabel.setText(myBugs ? "  MY BUGS" : "  ALL BUGS");
    }

    /** Refresh the project filter dropdown with given project names. */
    public void refreshProjectFilter(List<String> projectNames) {
        String current = (String) cmbProject.getSelectedItem();
        cmbProject.removeAllItems();
        cmbProject.addItem("Any");
        for (String name : projectNames) cmbProject.addItem(name);
        if (current != null) cmbProject.setSelectedItem(current);
    }

    public void showCard(String name) {
        if (CARD_USERS.equals(name) && userManagementPanel != null)
            userManagementPanel.loadDataIfNeeded();
        cardLayout.show(centerCards, name);
    }

    public void showNewBugForm()       { newBugPanel.clearForm(); showCard(CARD_NEW); }
    public void showEditBugForm(Bug b) { editBugPanel.populateForm(b); showCard(CARD_EDIT); }
    public void showReadBugPanel(Bug b){ readBugPanel.populate(b); showCard(CARD_READ); }

    public void setShowingClosedProjects(boolean val) {
        showingClosedProjects = val;
        btnShowClosed.setText(val ? "Hide Closed Projects" : "Show Closed Projects");
    }
    public boolean isShowingClosedProjects() { return showingClosedProjects; }

    public void showSuccess(String msg) {
        if (lblStatus == null) return;
        lblStatus.setForeground(AppTheme.SUCCESS);
        lblStatus.setText("✔  " + msg);
        lblStatus.getParent().setBackground(new Color(20, 50, 30));
    }
    public void showError(String msg) {
        if (lblStatus == null) return;
        lblStatus.setForeground(new Color(255, 100, 100));
        lblStatus.setText("✖  " + msg);
        lblStatus.getParent().setBackground(new Color(60, 18, 18));
    }
    public void showInfo(String msg) {
        if (lblStatus == null) return;
        lblStatus.setForeground(new Color(130, 170, 255));
        lblStatus.setText("ℹ  " + msg);
        lblStatus.getParent().setBackground(AppTheme.BG_HEADER);
    }

    // ── Getters ───────────────────────────────────────────────────────────────────
    public JTable             getTblBugs()         { return tblBugs; }
    public DefaultTableModel  getTableModel()      { return tableModel; }
    public List<Bug>          getTableBugs()       { return tableBugs; }
    public JComboBox<String>  getCmbStatus()       { return cmbStatus; }
    public JComboBox<String>  getCmbPriority()     { return cmbPriority; }
    public JComboBox<String>  getCmbSeverity()     { return cmbSeverity; }
    public JComboBox<String>  getCmbCategory()     { return cmbCategory; }
    public JComboBox<String>  getCmbProject()      { return cmbProject; }
    public JTextField         getTxtKeywordSearch(){ return txtKeyword; }
    public User               getLoggedInUser()    { return loggedInUser; }
    public BugListController  getController()      { return controller; }
    public BugFormPanel       getNewBugPanel()     { return newBugPanel; }
    public BugFormPanel       getEditBugPanel()    { return editBugPanel; }
    public UserManagementPanel getUserManagementPanel() { return userManagementPanel; }

    // ── Styling helpers ───────────────────────────────────────────────────────────
    static JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(AppTheme.TABLE_SEL);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? AppTheme.BG_PANEL : AppTheme.TABLE_ALT);
                    c.setForeground(AppTheme.TEXT_PRIMARY);
                }
                if (c instanceof JLabel) ((JLabel)c).setBorder(new EmptyBorder(4,10,4,10));
                return c;
            }
        };
        table.setBackground(AppTheme.BG_PANEL);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        JTableHeader hdr = table.getTableHeader();
        hdr.setBackground(AppTheme.BG_HEADER);
        hdr.setForeground(AppTheme.TEXT_MUTED);
        hdr.setFont(AppTheme.FONT_LABEL);
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR));
        hdr.setReorderingAllowed(false);
        return table;
    }

    /**
     * Builds the main bug table with full colour rendering:
     * closed+completed = black bg / green text, closed = black bg / red text,
     * completed = dark green bg / green text, overdue = dark red bg / red text.
     */
    JTable buildBugTableWithOverdue1(DefaultTableModel model, int dueDateCol, int statusCol) {
        BugListView self = this;
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(AppTheme.TABLE_SEL);
                    c.setForeground(Color.WHITE);
                } else {
                    boolean closedProject = false;
                    boolean completed = false;
                    boolean overdue = false;

                    // Check closed project and completed status via tableBugs list
                    List<Bug> bugs = self.tableBugs;
                    if (bugs != null && row < bugs.size()) {
                        Bug bug = bugs.get(row);
                        closedProject = bug.isInClosedProject();
                        completed = bug.getStatus() == BugStatus.COMPLETED;
                    }

                    if (!closedProject && !completed) {
                        Object statusVal = getValueAt(row, statusCol);
                        Object dueVal    = getValueAt(row, dueDateCol);
                        if (statusVal != null && !statusVal.toString().equals("COMPLETED") && dueVal != null) {
                            try {
                                LocalDate due = dueVal instanceof LocalDate
                                    ? (LocalDate) dueVal
                                    : LocalDate.parse(dueVal.toString());
                                overdue = due.isBefore(LocalDate.now());
                            } catch (Exception ignored) {}
                        }
                    }

                    if (closedProject && completed) {
                        // Closed project + completed: black background, green text
                        c.setBackground(new Color(0, 0, 0));
                        c.setForeground(new Color(80, 200, 120));
                    } else if (closedProject) {
                        // Closed project not completed: black background, red text
                        c.setBackground(new Color(0, 0, 0));
                        c.setForeground(new Color(255, 120, 120));
                    } else if (completed) {
                        // Completed (open project): green background, green text
                        c.setBackground(new Color(20, 60, 30));
                        c.setForeground(new Color(80, 200, 120));
                    } else if (overdue) {
                        c.setBackground(new Color(90, 20, 20));
                        c.setForeground(new Color(255, 120, 120));
                    } else {
                        c.setBackground(row % 2 == 0 ? AppTheme.BG_PANEL : AppTheme.TABLE_ALT);
                        c.setForeground(AppTheme.TEXT_PRIMARY);
                    }
                }
                if (c instanceof JLabel) ((JLabel)c).setBorder(new EmptyBorder(4,10,4,10));
                return c;
            }
        };
        table.setBackground(AppTheme.BG_PANEL);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        JTableHeader hdr = table.getTableHeader();
        hdr.setBackground(AppTheme.BG_HEADER);
        hdr.setForeground(AppTheme.TEXT_MUTED);
        hdr.setFont(AppTheme.FONT_LABEL);
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR));
        hdr.setReorderingAllowed(false);
        return table;
    }

    private void styleCombo(JComboBox<String> c) {
        c.setBackground(AppTheme.BG_PANEL);
        c.setForeground(AppTheme.TEXT_PRIMARY);
        c.setFont(AppTheme.FONT_BODY);
    }
    private void styleFilterField(JTextField f) {
        f.setBackground(AppTheme.BG_PANEL);
        f.setForeground(AppTheme.TEXT_PRIMARY);
        f.setCaretColor(AppTheme.TEXT_PRIMARY);
        f.setFont(AppTheme.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
            new EmptyBorder(4,8,4,8)));
    }

    private class SidebarRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean hasFocus) {
            JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
            l.setBorder(new EmptyBorder(10,14,10,10));
            l.setFont(AppTheme.FONT_SMALL);

            boolean closedProject = false;
            boolean completed = false;
            boolean overdue = false;
            if (sidebarBugs != null && index < sidebarBugs.size()) {
                Bug bug = sidebarBugs.get(index);
                closedProject = bug.isInClosedProject();
                completed = bug.getStatus() == model.BugStatus.COMPLETED;
                overdue = bug.isOverdue();
            }

            if (isSelected) {
                l.setBackground(AppTheme.TABLE_SEL);
                l.setForeground(Color.WHITE);
            } else if (closedProject && completed) {
                // Closed project + completed: black background, green text
                l.setBackground(new Color(0, 0, 0));
                l.setForeground(new Color(80, 200, 120));
            } else if (closedProject) {
                // Closed project not completed: black background, red text
                l.setBackground(new Color(0, 0, 0));
                l.setForeground(new Color(255, 120, 120));
            } else if (completed) {
                // Completed (open project): green background, green text
                l.setBackground(new Color(20, 60, 30));
                l.setForeground(new Color(80, 200, 120));
            } else if (overdue) {
                l.setBackground(new Color(90, 20, 20));
                l.setForeground(new Color(255, 120, 120));
            } else {
                l.setBackground(AppTheme.BG_SIDEBAR);
                l.setForeground(AppTheme.TEXT_PRIMARY);
            }
            return l;
        }
    }
}
