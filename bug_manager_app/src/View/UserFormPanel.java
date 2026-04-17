package View;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.User;
import util.AppTheme;

public class UserFormPanel extends JPanel {

    private final UserManagementPanel parent;

    private JTextField     txtFullName = new JTextField();
    private JTextField     txtUsername = new JTextField();
    private JTextField     txtEmail    = new JTextField();
    private JTextField     txtJobTitle = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();
    private JCheckBox      chkAdmin    = new JCheckBox("Grant Admin privileges");
    private JLabel         lblPwdHint  = AppTheme.muted("Leave blank to keep current password (edit mode)");
    private JLabel         lblHeading  = new JLabel();

    private JButton btnSave, btnCancel;
    private User    currentUser;
    private boolean editMode;

    public UserFormPanel(UserManagementPanel parent) {
        this.parent = parent;
        buildUI();
    }

    private void buildUI() {
        setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(AppTheme.BG_HEADER);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR), new EmptyBorder(12,24,12,24)));
        lblHeading.setFont(AppTheme.FONT_HEADING); lblHeading.setForeground(AppTheme.TEXT_PRIMARY);
        hdr.add(lblHeading, BorderLayout.WEST);
        add(hdr, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(AppTheme.BORDER_PANEL, new EmptyBorder(24,36,24,36)));

        int row = 0;
        row = addRow(card, row, "Full Name *",  txtFullName);
        row = addRow(card, row, "Username *",   txtUsername);
        row = addRow(card, row, "Email *",      txtEmail);
        row = addRow(card, row, "Job Title",    txtJobTitle);
        row = addRow(card, row, "Password *",   txtPassword);

        GridBagConstraints hc = gbc(0, row, 2); hc.insets = new Insets(0,0,8,0);
        card.add(lblPwdHint, hc); row++;

        chkAdmin.setFont(AppTheme.FONT_BODY); chkAdmin.setForeground(AppTheme.TEXT_PRIMARY); chkAdmin.setOpaque(false);
        GridBagConstraints cc = gbc(0, row, 2); cc.insets = new Insets(4,0,16,0);
        card.add(chkAdmin, cc); row++;

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnSave   = AppTheme.primaryButton("Save");
        btnCancel = AppTheme.secondaryButton("Cancel");
        btnRow.add(btnCancel); btnRow.add(btnSave);
        GridBagConstraints bc = gbc(0, row, 2); bc.fill = GridBagConstraints.HORIZONTAL;
        card.add(btnRow, bc);

        btnSave.addActionListener(e   -> parent.getController().onSaveUserClick(editMode));
        btnCancel.addActionListener(e -> { parent.showInnerCard(UserManagementPanel.CARD_TABLE); parent.getParentView().showInfo("Cancelled."); });

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(AppTheme.BG_DARK);
        wrapper.setBorder(new EmptyBorder(24,40,24,40));
        wrapper.add(card, new GridBagConstraints(){{fill=BOTH;weightx=1;weighty=1;}});
        add(wrapper, BorderLayout.CENTER);
    }

    public void clearForm(boolean edit) {
        this.editMode = edit; this.currentUser = null;
        lblHeading.setText("Add User"); lblPwdHint.setVisible(false);
        txtFullName.setText(""); txtUsername.setText(""); txtEmail.setText("");
        txtJobTitle.setText(""); txtPassword.setText(""); chkAdmin.setSelected(false);
    }

    public void populateForm(User user) {
        this.editMode = true; this.currentUser = user;
        lblHeading.setText("Edit User"); lblPwdHint.setVisible(true);
        txtFullName.setText(user.getFullname());
        txtUsername.setText(user.getUsername());
        txtEmail.setText(user.getEmail());
        txtJobTitle.setText(user.getJobTitle() != null ? user.getJobTitle() : "");
        txtPassword.setText("");
        chkAdmin.setSelected(user.isAdmin());
    }

    public String  getFullName()   { return txtFullName.getText().trim(); }
    public String  getUsername()   { return txtUsername.getText().trim(); }
    public String  getEmail()      { return txtEmail.getText().trim(); }
    public String  getJobTitle()   { return txtJobTitle.getText().trim(); }
    public String  getPassword()   { return new String(txtPassword.getPassword()).trim(); }
    public boolean isAdmin()       { return chkAdmin.isSelected(); }
    public User    getCurrentUser(){ return currentUser; }

    private int addRow(JPanel p, int row, String labelText, JComponent field) {
        GridBagConstraints lc = gbc(0, row, 1); lc.insets = new Insets(8,0,2,0);
        p.add(fieldLabel(labelText), lc);
        styleField(field);
        GridBagConstraints fc = gbc(0, row+1, 2); fc.fill=GridBagConstraints.HORIZONTAL; fc.weightx=1; fc.insets=new Insets(0,0,4,0);
        p.add(field, fc);
        return row + 2;
    }
    private GridBagConstraints gbc(int x, int y, int w) {
        GridBagConstraints c = new GridBagConstraints(); c.gridx=x; c.gridy=y; c.gridwidth=w; c.anchor=GridBagConstraints.WEST; return c;
    }
    private JLabel fieldLabel(String t) { JLabel l=new JLabel(t); l.setFont(AppTheme.FONT_LABEL); l.setForeground(AppTheme.TEXT_MUTED); return l; }
    private void styleField(JComponent f) {
        f.setFont(AppTheme.FONT_BODY);
        if (f instanceof JTextField) { ((JTextField)f).setBackground(AppTheme.BG_DARK); ((JTextField)f).setForeground(AppTheme.TEXT_PRIMARY); ((JTextField)f).setCaretColor(AppTheme.TEXT_PRIMARY); }
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR), new EmptyBorder(6,10,6,10)));
    }
}
