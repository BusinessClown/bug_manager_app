package View;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.*;
import util.AppTheme;

public class BugReadPanel extends JPanel {

    private final BugListView parent;

    private JLabel lblId        = val("");
    private JLabel lblTitle     = val("");
    private JLabel lblSubmitted = val("");
    private JLabel lblStatus    = val("");
    private JLabel lblPriority  = val("");
    private JLabel lblSeverity  = val("");
    private JLabel lblCategory  = val("");
    private JLabel lblProject   = val("");
    private JLabel lblCreated   = val("");
    private JLabel lblDue       = val("");
    private JTextArea txtDesc   = new JTextArea();

    private JButton btnBack, btnEdit;

    public BugReadPanel(BugListView parent) {
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
        JLabel heading = new JLabel("Bug Details");
        heading.setFont(AppTheme.FONT_HEADING); heading.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setOpaque(false);
        btnEdit = AppTheme.primaryButton("Edit This Bug");
        btnBack = AppTheme.secondaryButton("← Back to List");
        btnBar.add(btnBack); btnBar.add(btnEdit);
        btnBack.addActionListener(e -> { parent.restoreSidebar(); parent.showCard(BugListView.CARD_TABLE); });
        btnEdit.addActionListener(e -> parent.getController().onEditFromReadPanel());
        hdr.add(heading, BorderLayout.WEST); hdr.add(btnBar, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(AppTheme.BORDER_PANEL, new EmptyBorder(24,36,24,36)));

        int row = 0;
        row = addRow(card, row, "Bug ID",       lblId);
        row = addRow(card, row, "Title",        lblTitle);
        row = addRow(card, row, "Submitted By", lblSubmitted);
        row = addRow(card, row, "Status",       lblStatus);
        row = addRow(card, row, "Priority",     lblPriority);
        row = addRow(card, row, "Severity",     lblSeverity);
        row = addRow(card, row, "Category",     lblCategory);
        row = addRow(card, row, "Project",      lblProject);
        row = addRow(card, row, "Created",      lblCreated);
        row = addRow(card, row, "Due Date",     lblDue);

        GridBagConstraints dlc = gbc(0, row, 1); dlc.insets = new Insets(16,0,4,0);
        card.add(lbl("Description"), dlc); row++;

        txtDesc.setFont(AppTheme.FONT_BODY); txtDesc.setBackground(AppTheme.BG_DARK);
        txtDesc.setForeground(AppTheme.TEXT_PRIMARY); txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true); txtDesc.setEditable(false);
        txtDesc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR), new EmptyBorder(8,10,8,10)));
        JScrollPane ds = new JScrollPane(txtDesc);
        ds.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
        ds.setPreferredSize(new Dimension(600, 120));
        GridBagConstraints dfc = gbc(0, row, 2);
        dfc.fill=GridBagConstraints.BOTH; dfc.weighty=0.3;
        card.add(ds, dfc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(AppTheme.BG_DARK);
        wrapper.setBorder(new EmptyBorder(24,40,24,40));
        wrapper.add(card, new GridBagConstraints(){{fill=BOTH;weightx=1;weighty=1;}});
        add(wrapper, BorderLayout.CENTER);
    }

    public void populate(Bug bug) {
        lblId.setText("#" + bug.getId());
        lblTitle.setText(bug.getTitle());
        lblSubmitted.setText(bug.getUser().getFullname() + " (@" + bug.getUser().getUsername() + ")");
        lblPriority.setText(bug.getPriority().name());
        lblSeverity.setText(bug.getSeverity() != null ? bug.getSeverity().name() : "—");
        lblCategory.setText(bug.getCategory() != null ? bug.getCategory().name() : "—");
        lblCreated.setText(bug.getCreateDate() != null ? bug.getCreateDate().toString() : "—");

        // Project — show name and flag if closed
        if (bug.getProject() != null) {
            String projName = bug.getProject().getName();
            if (bug.getProject().isClosed()) {
                lblProject.setText(projName + "  [CLOSED]");
                lblProject.setForeground(new Color(160, 100, 100));
            } else {
                lblProject.setText(projName);
                lblProject.setForeground(AppTheme.ACCENT);
            }
        } else {
            lblProject.setText("—");
            lblProject.setForeground(AppTheme.TEXT_MUTED);
        }

        // Due date — red if overdue
        String dueText = bug.getDueDate() != null ? bug.getDueDate().toString() : "—";
        lblDue.setText(dueText);
        boolean overdue = bug.isOverdue();
        lblDue.setForeground(overdue ? AppTheme.DANGER : AppTheme.TEXT_PRIMARY);

        lblStatus.setText(bug.getStatus().name());
        switch (bug.getStatus()) {
            case OPEN        -> lblStatus.setForeground(AppTheme.WARNING);
            case IN_PROGRESS -> lblStatus.setForeground(AppTheme.ACCENT);
            case COMPLETED   -> lblStatus.setForeground(AppTheme.SUCCESS);
        }
        switch (bug.getPriority()) {
            case HIGH   -> lblPriority.setForeground(AppTheme.DANGER);
            case MEDIUM -> lblPriority.setForeground(AppTheme.WARNING);
            case LOW    -> lblPriority.setForeground(AppTheme.SUCCESS);
        }
        if (bug.getSeverity() != null) {
            switch (bug.getSeverity()) {
                case BLOCKER  -> lblSeverity.setForeground(new Color(255,60,60));
                case SEVERE   -> lblSeverity.setForeground(AppTheme.DANGER);
                case MAJOR    -> lblSeverity.setForeground(AppTheme.WARNING);
                case MINOR    -> lblSeverity.setForeground(AppTheme.ACCENT);
                case COSMETIC -> lblSeverity.setForeground(AppTheme.SUCCESS);
            }
        }

        txtDesc.setText(bug.getDescription() != null ? bug.getDescription() : "");
        txtDesc.setCaretPosition(0);
    }

    private int addRow(JPanel p, int row, String labelText, JLabel valueLabel) {
        GridBagConstraints lc = gbc(0, row, 1); lc.insets = new Insets(8,0,2,24);
        p.add(lbl(labelText), lc);
        GridBagConstraints vc = gbc(1, row, 1); vc.fill=GridBagConstraints.HORIZONTAL; vc.weightx=1; vc.insets=new Insets(8,0,2,0);
        p.add(valueLabel, vc);
        return row + 1;
    }
    private GridBagConstraints gbc(int x, int y, int w) {
        GridBagConstraints c = new GridBagConstraints(); c.gridx=x; c.gridy=y; c.gridwidth=w; c.anchor=GridBagConstraints.WEST; return c;
    }
    private JLabel lbl(String t) { JLabel l=new JLabel(t); l.setFont(AppTheme.FONT_LABEL); l.setForeground(AppTheme.TEXT_MUTED); return l; }
    private JLabel val(String t) { JLabel l=new JLabel(t); l.setFont(AppTheme.FONT_BODY);  l.setForeground(AppTheme.TEXT_PRIMARY); return l; }
}
