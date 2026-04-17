package View;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.RegisterController;
import util.AppTheme;

public class RegisterView extends JFrame {

    private final RegisterController controller;

    private JTextField     txtFullName, txtUsername, txtEmail;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton        btnLogin, btnRegister;
    private JLabel         lblStatus;

    public RegisterView() {
        controller = new RegisterController(this);
        setTitle("Bug Tracker — Create Account");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        buildUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());
        add(buildBranding(), BorderLayout.WEST);
        add(buildForm(),     BorderLayout.CENTER);
        getContentPane().setPreferredSize(new Dimension(700, 560));
    }

    // ── Left branding strip ───────────────────────────────────────────────────
    private JPanel buildBranding() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 50, 100), 0, getHeight(), new Color(20, 22, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(99, 132, 255, 30));
                g2.fillOval(-40, -40, 200, 200);
                g2.setColor(new Color(99, 132, 255, 20));
                g2.fillOval(30, getHeight() - 160, 220, 220);
                g2.dispose();
            }
        };
        p.setLayout(new GridBagLayout());
        p.setPreferredSize(new Dimension(240, 0));
        p.setOpaque(false);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel icon = new JLabel("🐞");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Bug Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel step = new JLabel("Create your account");
        step.setFont(AppTheme.FONT_SMALL);
        step.setForeground(new Color(160, 175, 220));
        step.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bullets = new JPanel();
        bullets.setLayout(new BoxLayout(bullets, BoxLayout.Y_AXIS));
        bullets.setOpaque(false);
        bullets.setBorder(new EmptyBorder(18, 10, 0, 10));
        for (String s : new String[]{"✔  Track bugs easily", "✔  Assign priorities", "✔  Team collaboration"}) {
            JLabel b = new JLabel(s);
            b.setFont(AppTheme.FONT_SMALL);
            b.setForeground(new Color(160, 175, 220));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            bullets.add(b);
            bullets.add(Box.createVerticalStrut(6));
        }

        inner.add(icon);
        inner.add(Box.createVerticalStrut(10));
        inner.add(title);
        inner.add(Box.createVerticalStrut(6));
        inner.add(step);
        inner.add(bullets);
        p.add(inner);
        return p;
    }

    // ── Right form panel ──────────────────────────────────────────────────────
    private JPanel buildForm() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(AppTheme.BG_PANEL);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 44, 20, 44));

        JLabel lblHeading = new JLabel("Create Account");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(AppTheme.TEXT_PRIMARY);
        lblHeading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Fill in the details below to get started");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(lblHeading);
        form.add(Box.createVerticalStrut(4));
        form.add(lblSub);
        form.add(Box.createVerticalStrut(20));

        // ── Aligned grid: label above, field below, consistent spacing ────────
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        //grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        //grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        // Label constraint
        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.fill   = GridBagConstraints.NONE;
        lc.insets = new Insets(0, 0, 4, 0);

        // Field constraint
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill    = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1;
        fc.anchor  = GridBagConstraints.WEST;

        // Row 0 labels: Full Name | Username
        lc.gridx = 0; lc.gridy = 0; lc.gridwidth = 1; lc.insets = new Insets(0, 0, 4, 6);
        grid.add(fieldLabel("Full Name"), lc);
        lc.gridx = 1; lc.insets = new Insets(0, 6, 4, 0);
        grid.add(fieldLabel("Username"), lc);

        // Row 1 fields: Full Name | Username
        txtFullName = styledField(10);
        fc.gridx = 0; fc.gridy = 1; fc.insets = new Insets(0, 0, 14, 6);
        grid.add(txtFullName, fc);
        txtUsername = styledField(10);
        fc.gridx = 1; fc.insets = new Insets(0, 6, 14, 0);
        grid.add(txtUsername, fc);

        // Row 2 label: Email (full width)
        lc.gridx = 0; lc.gridy = 2; lc.gridwidth = 2; lc.insets = new Insets(0, 0, 4, 0);
        grid.add(fieldLabel("Email Address"), lc);

        // Row 3 field: Email (full width)
        txtEmail = styledField(22);
        fc.gridx = 0; fc.gridy = 3; fc.gridwidth = 2; fc.insets = new Insets(0, 0, 14, 0);
        grid.add(txtEmail, fc);

        // Row 4 labels: Password | Confirm Password
        lc.gridwidth = 1;
        lc.gridx = 0; lc.gridy = 4; lc.insets = new Insets(0, 0, 4, 6);
        grid.add(fieldLabel("Password"), lc);
        lc.gridx = 1; lc.insets = new Insets(0, 6, 4, 0);
        grid.add(fieldLabel("Confirm Password"), lc);

        // Row 5 fields: Password | Confirm Password
        txtPassword = new JPasswordField(10);
        styleField(txtPassword);
        fc.gridwidth = 1;
        fc.gridx = 0; fc.gridy = 5; fc.insets = new Insets(0, 0, 0, 6);
        grid.add(txtPassword, fc);
        txtConfirmPassword = new JPasswordField(10);
        styleField(txtConfirmPassword);
        fc.gridx = 1; fc.insets = new Insets(0, 6, 0, 0);
        grid.add(txtConfirmPassword, fc);

        form.add(grid);
        form.add(Box.createVerticalStrut(10));

        // Status label — fixed height so it never bunches the layout
        lblStatus = new JLabel(" ");
        lblStatus.setFont(AppTheme.FONT_SMALL);
        lblStatus.setForeground(AppTheme.DANGER);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        //lblStatus.setPreferredSize(new Dimension(Integer.MAX_VALUE, 18));
        lblStatus.setMinimumSize(new Dimension(0, 18));
        lblStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        form.add(lblStatus);
        form.add(Box.createVerticalStrut(14));

        btnRegister = AppTheme.primaryButton("Create Account  →");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.addActionListener(e -> controller.onRegisterButtionClick());
        form.add(btnRegister);
        form.add(Box.createVerticalStrut(12));

        btnLogin = AppTheme.secondaryButton("← Back to Sign In");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnLogin.addActionListener(e -> controller.onLoginButtionClick());
        form.add(btnLogin);

        outer.add(form);
        return outer;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_LABEL);
        lbl.setForeground(AppTheme.TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        styleField(f);
        return f;
    }

    private void styleField(JTextField field) {
        field.setFont(AppTheme.FONT_BODY);
        field.setBackground(AppTheme.BG_DARK);
        field.setForeground(AppTheme.TEXT_PRIMARY);
        field.setCaretColor(AppTheme.ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1),
            new EmptyBorder(7, 10, 7, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.ACCENT, 1),
                    new EmptyBorder(7, 10, 7, 10)));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1),
                    new EmptyBorder(7, 10, 7, 10)));
            }
        });
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public JTextField      getTxtFullName()        { return txtFullName; }
    public JTextField      getTxtUsername()        { return txtUsername; }
    public JTextField      getTxtEmail()           { return txtEmail; }
    public JPasswordField  getTxtPassword()        { return txtPassword; }
    public JPasswordField  getTxtConfirmPassword() { return txtConfirmPassword; }
    public JLabel          getLblStatus()          { return lblStatus; }
}
