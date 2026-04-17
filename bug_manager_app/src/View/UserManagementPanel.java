package View;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import controller.UserManagementController;
import model.User;
import util.AppTheme;

public class UserManagementPanel extends JPanel {

    private final BugListView parent;
    private UserManagementController controller;

    // ── Table ─────────────────────────────────────────────────────────────────────
    private JTable            tblUsers;
    private DefaultTableModel tableModel;

    // ── Search/filter bar ─────────────────────────────────────────────────────────
    private JTextField     txtSearch    = new JTextField(18);
    private JComboBox<String> cmbAdminFilter = new JComboBox<>(new String[]{"All Users","Admins Only","Non-Admins"});
    private JButton        btnSearch    = AppTheme.primaryButton("Search");
    private JButton        btnClearSearch = AppTheme.secondaryButton("Clear");

    // ── Action buttons ────────────────────────────────────────────────────────────
    private JButton btnAdd     = AppTheme.primaryButton("+ Add User");
    private JButton btnRead    = AppTheme.secondaryButton("Read");
    private JButton btnEdit    = AppTheme.secondaryButton("Edit");
    private JButton btnDelete  = AppTheme.dangerButton("Delete");
    private JButton btnRefresh = AppTheme.secondaryButton("Refresh");

    // ── Inner card switcher ───────────────────────────────────────────────────────
    private CardLayout innerCards = new CardLayout();
    private JPanel     innerPanel = new JPanel(innerCards);

    public static final String CARD_TABLE = "U_TABLE";
    public static final String CARD_FORM  = "U_FORM";
    public static final String CARD_READ  = "U_READ";

    private UserFormPanel formPanel;
    private UserReadPanel readPanel;

    private boolean dataLoaded = false;

    public UserManagementPanel(BugListView parent) {
        this.parent = parent;
        controller  = new UserManagementController(this, parent);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_DARK);

        // Outer header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(AppTheme.BG_HEADER);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR), new EmptyBorder(14,24,14,24)));
        JLabel heading = new JLabel("User Management");
        heading.setFont(AppTheme.FONT_HEADING); heading.setForeground(AppTheme.TEXT_PRIMARY);
        JButton btnBack = AppTheme.secondaryButton("← Back to Bugs");
        btnBack.addActionListener(e -> { parent.restoreSidebar(); parent.showCard(BugListView.CARD_TABLE); });
        hdr.add(heading, BorderLayout.WEST); hdr.add(btnBack, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        // Table card
        tableModel = new DefaultTableModel(
            new Object[]{"ID","Username","Full Name","Email","Job Title","Admin"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsers = BugListView.buildStyledTable(tableModel);

        // Search/filter bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchBar.setBackground(AppTheme.BG_HEADER);
        searchBar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR));
        styleField(txtSearch);
        styleCombo(cmbAdminFilter);
        searchBar.add(AppTheme.muted("Search:"));     searchBar.add(txtSearch);
        searchBar.add(Box.createHorizontalStrut(6));
        searchBar.add(AppTheme.muted("Show:"));       searchBar.add(cmbAdminFilter);
        searchBar.add(btnSearch); searchBar.add(btnClearSearch);
        btnSearch.addActionListener(e     -> controller.onSearchClick());
        btnClearSearch.addActionListener(e-> controller.onClearSearchClick());
        // Also search on Enter in text field
        txtSearch.addActionListener(e -> controller.onSearchClick());

        JScrollPane scroll = new JScrollPane(tblUsers);
        scroll.setBorder(null); scroll.getViewport().setBackground(AppTheme.BG_DARK);

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        actionBar.setBackground(AppTheme.BG_HEADER);
        actionBar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER_COLOR));
        actionBar.add(btnAdd); actionBar.add(btnRead); actionBar.add(btnEdit);
        actionBar.add(btnDelete); actionBar.add(btnRefresh);

        btnAdd.addActionListener(e     -> controller.onAddClick());
        btnRead.addActionListener(e    -> controller.onReadClick());
        btnEdit.addActionListener(e    -> controller.onEditClick());
        btnDelete.addActionListener(e  -> controller.onDeleteClick());
        btnRefresh.addActionListener(e -> controller.loadUsers());

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(AppTheme.BG_DARK);
        tableCard.add(searchBar, BorderLayout.NORTH);
        tableCard.add(scroll,    BorderLayout.CENTER);
        tableCard.add(actionBar, BorderLayout.SOUTH);

        formPanel = new UserFormPanel(this);
        readPanel = new UserReadPanel(this);

        innerPanel.setBackground(AppTheme.BG_DARK);
        innerPanel.add(tableCard, CARD_TABLE);
        innerPanel.add(formPanel, CARD_FORM);
        innerPanel.add(readPanel, CARD_READ);
        innerCards.show(innerPanel, CARD_TABLE);

        add(innerPanel, BorderLayout.CENTER);
    }

    // ── Lazy load ─────────────────────────────────────────────────────────────────
    public void loadDataIfNeeded() {
        if (!dataLoaded) { dataLoaded = true; controller.loadUsers(); }
    }

    // ── Public API ────────────────────────────────────────────────────────────────
    public void displayUsers(List<User> users) {
        tableModel.setRowCount(0);
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getFullname(), u.getEmail(),
                u.getJobTitle() != null ? u.getJobTitle() : "",
                u.isAdmin() ? "Yes" : "No"
            });
        }
    }

    public void showInnerCard(String card)              { innerCards.show(innerPanel, card); }
    public void showAddForm()                           { formPanel.clearForm(false); showInnerCard(CARD_FORM); }
    public void showEditForm(User user)                 { formPanel.populateForm(user); showInnerCard(CARD_FORM); }
    public void showReadPanel(User user, List<model.Bug> bugs) { readPanel.populate(user, bugs); showInnerCard(CARD_READ); }
    public void refresh()                               { controller.loadUsers(); showInnerCard(CARD_TABLE); }

    public JTable                  getTblUsers()       { return tblUsers; }
    public DefaultTableModel       getTableModel()     { return tableModel; }
    public UserFormPanel           getFormPanel()      { return formPanel; }
    public UserManagementController getController()    { return controller; }
    public BugListView             getParentView()     { return parent; }
    public String                  getSearchText()     { return txtSearch.getText().trim(); }
    public String                  getAdminFilter()    { return (String) cmbAdminFilter.getSelectedItem(); }
    public void                    clearSearchFields() { txtSearch.setText(""); cmbAdminFilter.setSelectedIndex(0); }

    private void styleField(JTextField f) {
        f.setFont(AppTheme.FONT_BODY); f.setBackground(AppTheme.BG_PANEL);
        f.setForeground(AppTheme.TEXT_PRIMARY); f.setCaretColor(AppTheme.TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR), new EmptyBorder(4,8,4,8)));
    }
    private void styleCombo(JComboBox<String> c) {
        c.setBackground(AppTheme.BG_PANEL); c.setForeground(AppTheme.TEXT_PRIMARY); c.setFont(AppTheme.FONT_BODY);
    }
}
