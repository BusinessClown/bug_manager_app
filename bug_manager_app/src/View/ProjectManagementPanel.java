package View;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import dao.ProjectDao;
import model.Bug;
import model.Project;
import model.Project.ProjectStatus;
import service.BugService;
import dao.BugDaoImplentation;
import util.AppTheme;

/**
 * Admin-only panel: lists all projects. Clicking a project shows its bugs in
 * the left sidebar. Provides Close and Re-open buttons.
 */
public class ProjectManagementPanel extends JPanel {

    private final BugListView parent;
    private final ProjectDao  projectDao = new ProjectDao();
    private final BugService  bugService = new BugService(new BugDaoImplentation());

    private DefaultTableModel projTableModel;
    private JTable            tblProjects;
    private List<Project>     projectList;

    private JButton btnClose    = AppTheme.dangerButton("Close Project");
    private JButton btnReopen   = AppTheme.primaryButton("Re-open Project");
    private JButton btnBack     = AppTheme.secondaryButton("← Back");

    public ProjectManagementPanel(BugListView parent) {
        this.parent = parent;
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
        JLabel title = new JLabel("Project Management");
        title.setFont(AppTheme.FONT_HEADING);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        hdr.add(title, BorderLayout.WEST);
        add(hdr, BorderLayout.NORTH);

        // Table
        projTableModel = new DefaultTableModel(new Object[]{"ID","Name","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblProjects = buildProjectTable();

        tblProjects.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProjects.getSelectedRow() >= 0) {
                onProjectSelected();
            }
        });

        JScrollPane scroll = new JScrollPane(tblProjects);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(AppTheme.BG_DARK);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppTheme.BG_DARK);
        wrapper.setBorder(new EmptyBorder(20, 30, 0, 30));
        wrapper.add(scroll, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // Button bar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnBar.setBackground(AppTheme.BG_HEADER);
        btnBar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,AppTheme.BORDER_COLOR));
        btnBar.add(btnClose);
        btnBar.add(btnReopen);
        btnBar.add(Box.createHorizontalStrut(20));
        btnBar.add(btnBack);
        add(btnBar, BorderLayout.SOUTH);

        btnClose.addActionListener(e  -> onCloseProject());
        btnReopen.addActionListener(e -> onReopenProject());
        btnBack.addActionListener(e   -> parent.showCard(BugListView.CARD_TABLE));
    }

    public void reload() {
        projectList = projectDao.findAll();
        projTableModel.setRowCount(0);
        for (Project p : projectList) {
            projTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getStatus().name()});
        }
    }

    private void onProjectSelected() {
        int row = tblProjects.getSelectedRow();
        if (row < 0 || projectList == null || row >= projectList.size()) return;
        Project p = projectList.get(row);
        // Load this project's bugs into the sidebar
        List<Bug> bugs = bugService.getBugsByProject(p.getId());
        parent.displayBugsInSidebar(bugs, p.getName() + " project");
        parent.showInfo("Showing " + bugs.size() + " bug(s) for project: " + p.getName());
    }

    private Project getSelectedProject() {
        int row = tblProjects.getSelectedRow();
        if (row < 0 || projectList == null || row >= projectList.size()) return null;
        return projectList.get(row);
    }

    private void onCloseProject() {
        Project p = getSelectedProject();
        if (p == null) { parent.showError("Select a project first."); return; }
        if (p.isClosed()) { parent.showInfo("Project is already closed."); return; }
        projectDao.updateStatus(p.getId(), ProjectStatus.CLOSED);
        parent.showSuccess("Project \"" + p.getName() + "\" closed.");
        reload();
        parent.getController().refreshCurrent();
        parent.getController().refreshProjectFilter();
    }

    private void onReopenProject() {
        Project p = getSelectedProject();
        if (p == null) { parent.showError("Select a project first."); return; }
        if (!p.isClosed()) { parent.showInfo("Project is already open."); return; }
        projectDao.updateStatus(p.getId(), ProjectStatus.OPEN);
        parent.showSuccess("Project \"" + p.getName() + "\" re-opened.");
        reload();
        parent.getController().refreshCurrent();
        parent.getController().refreshProjectFilter();
    }

    private JTable buildProjectTable() {
        JTable table = new JTable(projTableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(AppTheme.TABLE_SEL);
                    c.setForeground(Color.WHITE);
                } else {
                    // Closed projects shown in dark style
                    boolean closed = false;
                    if (projectList != null && row < projectList.size())
                        closed = projectList.get(row).isClosed();
                    if (closed) {
                        c.setBackground(new Color(30, 30, 30));
                        c.setForeground(new Color(120, 120, 120));
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
}
