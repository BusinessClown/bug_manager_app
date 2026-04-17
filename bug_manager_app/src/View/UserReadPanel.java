package View;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import model.Bug;
import model.User;
import util.AppTheme;

public class UserReadPanel extends JPanel {

    private final UserManagementPanel parent;

    private JLabel lblId       = val("");
    private JLabel lblUsername = val("");
    private JLabel lblFullName = val("");
    private JLabel lblEmail    = val("");
    private JLabel lblJobTitle = val("");
    private JLabel lblAdmin    = val("");

    private DefaultTableModel bugTableModel;
    private JTable            bugTable;
    private List<Bug>         currentBugs = new java.util.ArrayList<>();

    public UserReadPanel(UserManagementPanel parent) {
        this.parent = parent;
        buildUI();
    }

    private void buildUI() {
        setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(AppTheme.BG_HEADER);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR), new EmptyBorder(14,24,14,24)));
        JLabel heading = new JLabel("User Details");
        heading.setFont(AppTheme.FONT_HEADING); heading.setForeground(AppTheme.TEXT_PRIMARY);
        JButton btnBack = AppTheme.secondaryButton("← Back to Users");
        btnBack.addActionListener(e -> { parent.showInnerCard(UserManagementPanel.CARD_TABLE); parent.getParentView().restoreSidebar(); });
        hdr.add(heading, BorderLayout.WEST); hdr.add(btnBack, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0,16));
        content.setBackground(AppTheme.BG_DARK);
        content.setBorder(new EmptyBorder(20,40,20,40));

        // Info card
        JPanel info = new JPanel(new GridBagLayout());
        info.setBackground(AppTheme.BG_PANEL);
        info.setBorder(BorderFactory.createCompoundBorder(AppTheme.BORDER_PANEL, new EmptyBorder(16,24,16,24)));
        int row = 0;
        row = addRow(info, row, "User ID",    lblId);
        row = addRow(info, row, "Username",   lblUsername);
        row = addRow(info, row, "Full Name",  lblFullName);
        row = addRow(info, row, "Email",      lblEmail);
        row = addRow(info, row, "Job Title",  lblJobTitle);
        addRow(info,  row, "Admin",      lblAdmin);
        content.add(info, BorderLayout.NORTH);

        // Bugs table — with overdue highlighting
        JLabel bugsHeading = AppTheme.heading("Bugs Submitted by This User");
        bugsHeading.setBorder(new EmptyBorder(8,0,6,0));

        bugTableModel = new DefaultTableModel(new Object[]{"ID","Title","Status","Due Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        // Full color renderer: completed=green, closed+completed=green text,
        // closed only=black bg/red text, overdue=dark red
        bugTable = new JTable(bugTableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(AppTheme.TABLE_SEL);
                    c.setForeground(Color.WHITE);
                } else {
                    boolean closedProject = false;
                    boolean completed = false;
                    boolean overdue = false;
                    if (row < currentBugs.size()) {
                        Bug bug = currentBugs.get(row);
                        closedProject = bug.isInClosedProject();
                        completed = bug.getStatus() == model.BugStatus.COMPLETED;
                        overdue = bug.isOverdue();
                    }
                    if (closedProject && completed) {
                        c.setBackground(new Color(0, 0, 0));
                        c.setForeground(new Color(80, 200, 120));
                    } else if (closedProject) {
                        c.setBackground(new Color(0, 0, 0));
                        c.setForeground(new Color(255, 120, 120));
                    } else if (completed) {
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
                if (c instanceof JLabel) ((JLabel) c).setBorder(new EmptyBorder(4, 10, 4, 10));
                return c;
            }
        };
        bugTable.setBackground(AppTheme.BG_PANEL);
        bugTable.setForeground(AppTheme.TEXT_PRIMARY);
        bugTable.setFont(AppTheme.FONT_BODY);
        bugTable.setShowGrid(false);
        bugTable.setIntercellSpacing(new Dimension(0, 1));
        bugTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bugTable.setFillsViewportHeight(true);
        JTableHeader bugHdr = bugTable.getTableHeader();
        bugHdr.setBackground(AppTheme.BG_HEADER);
        bugHdr.setForeground(AppTheme.TEXT_MUTED);
        bugHdr.setFont(AppTheme.FONT_LABEL);
        bugHdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_COLOR));
        bugHdr.setReorderingAllowed(false);
        bugTable.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(bugTable);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
        scroll.setPreferredSize(new Dimension(0, 200));

        JPanel bugsSection = new JPanel(new BorderLayout(0,4));
        bugsSection.setBackground(AppTheme.BG_DARK);
        bugsSection.add(bugsHeading, BorderLayout.NORTH);
        bugsSection.add(scroll, BorderLayout.CENTER);
        content.add(bugsSection, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    public void populate(User user, List<Bug> bugs) {
        lblId.setText(String.valueOf(user.getId()));
        lblUsername.setText("@" + user.getUsername());
        lblFullName.setText(user.getFullname());
        lblEmail.setText(user.getEmail());
        lblJobTitle.setText(user.getJobTitle() != null && !user.getJobTitle().isEmpty() ? user.getJobTitle() : "—");
        lblAdmin.setText(user.isAdmin() ? "Yes" : "No");
        lblAdmin.setForeground(user.isAdmin() ? AppTheme.ACCENT : AppTheme.TEXT_PRIMARY);

        currentBugs = bugs;
        bugTableModel.setRowCount(0);
        for (Bug b : bugs) {
            bugTableModel.addRow(new Object[]{
                b.getId(), b.getTitle(), b.getStatus().name(),
                b.getDueDate() != null ? b.getDueDate() : ""
            });
        }
    }

    private int addRow(JPanel p, int row, String labelText, JLabel valueLabel) {
        GridBagConstraints lc = gbc(0, row); lc.insets = new Insets(6,0,6,24);
        p.add(lbl(labelText), lc);
        GridBagConstraints vc = gbc(1, row); vc.fill=GridBagConstraints.HORIZONTAL; vc.weightx=1; vc.insets=new Insets(6,0,6,0);
        p.add(valueLabel, vc);
        return row + 1;
    }
    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints c = new GridBagConstraints(); c.gridx=x; c.gridy=y; c.anchor=GridBagConstraints.WEST; return c;
    }
    private JLabel lbl(String t) { JLabel l=new JLabel(t); l.setFont(AppTheme.FONT_LABEL); l.setForeground(AppTheme.TEXT_MUTED); return l; }
    private JLabel val(String t) { JLabel l=new JLabel(t); l.setFont(AppTheme.FONT_BODY);  l.setForeground(AppTheme.TEXT_PRIMARY); return l; }
}
