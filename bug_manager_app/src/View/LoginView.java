package View;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.LoginController;
import util.AppTheme;

public class LoginView extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin, btnRegister;
    private JLabel         lblStatus;
    private LoginController controller;

    public LoginView() {
        controller = new LoginController(this);
        buildUI();
        setTitle("Bug Tracker — Sign In");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        getContentPane().setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());
        add(buildBranding(), BorderLayout.WEST);
        add(buildForm(),     BorderLayout.CENTER);
        getContentPane().setPreferredSize(new Dimension(700, 460));
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
        p.setPreferredSize(new Dimension(260, 0));
        p.setOpaque(false);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel icon = new JLabel("🐞");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Bug Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Find  ·  Hunt  ·  Squash");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(180, 190, 230));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(icon);
        inner.add(Box.createVerticalStrut(10));
        inner.add(title);
        inner.add(Box.createVerticalStrut(6));
        inner.add(tagline);
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
        form.setBorder(new EmptyBorder(10, 48, 10, 48));

        JLabel lblHeading = new JLabel("Welcome back");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeading.setForeground(AppTheme.TEXT_PRIMARY);
        lblHeading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sign in to continue");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(lblHeading);
        form.add(Box.createVerticalStrut(4));
        form.add(lblSub);
        form.add(Box.createVerticalStrut(28));

        // ── Grid for aligned label + field pairs ──────────────────────────────
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(0, 0, 4, 0); lc.fill = GridBagConstraints.NONE;

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = 0; fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1; fc.anchor = GridBagConstraints.WEST;

        // Username row
        lc.gridy = 0; grid.add(fieldLabel("Username or Email"), lc);
        txtUsername = styledField(22);
        fc.gridy = 1; fc.insets = new Insets(0, 0, 16, 0); grid.add(txtUsername, fc);

        // Password row
        lc.gridy = 2; lc.insets = new Insets(0, 0, 4, 0); grid.add(fieldLabel("Password"), lc);
        txtPassword = new JPasswordField(22);
        styleField(txtPassword);
        fc.gridy = 3; fc.insets = new Insets(0, 0, 0, 0); grid.add(txtPassword, fc);

        form.add(grid);
        form.add(Box.createVerticalStrut(10));

        // Status label — fixed height so it never causes the form to shift
        lblStatus = new JLabel(" ");
        lblStatus.setFont(AppTheme.FONT_SMALL);
        lblStatus.setForeground(AppTheme.DANGER);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatus.setMinimumSize(new Dimension(0, 18));
        lblStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        form.add(lblStatus);
        form.add(Box.createVerticalStrut(14));

        btnLogin = AppTheme.primaryButton("Sign In  →");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.addActionListener(e -> controller.onLoginButtonClick());
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(14));

        // "or" divider
        JPanel orRow = new JPanel(new GridBagLayout());
        orRow.setOpaque(false);
        orRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        JSeparator s1 = new JSeparator(); s1.setForeground(AppTheme.BORDER_COLOR);
        JSeparator s2 = new JSeparator(); s2.setForeground(AppTheme.BORDER_COLOR);
        JLabel orLbl = new JLabel(" or ", SwingConstants.CENTER);
        orLbl.setFont(AppTheme.FONT_SMALL); orLbl.setForeground(AppTheme.TEXT_MUTED);
        orRow.add(s1, gbc);
        gbc.weightx = 0; orRow.add(orLbl, gbc);
        gbc.weightx = 1; orRow.add(s2, gbc);
        form.add(orRow);
        form.add(Box.createVerticalStrut(14));

        btnRegister = AppTheme.secondaryButton("Create an Account");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnRegister.addActionListener(e -> controller.onRegisterButtonClick());
        form.add(btnRegister);

        txtUsername.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> controller.onLoginButtonClick());

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
            new EmptyBorder(8, 12, 8, 12)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.ACCENT, 1),
                    new EmptyBorder(8, 12, 8, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1),
                    new EmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public JTextField      getTxtUsername() { return txtUsername; }
    public JPasswordField  getTxtPassword() { return txtPassword; }
    public JLabel          getLblStatus()   { return lblStatus; }
}
