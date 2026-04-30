package View;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.SettingsController;
import model.User;
import util.AppTheme;
import util.ThemeDefinition;
import util.UserPreferences;

/**
 * Settings panel — three sections in the left nav:
 *   👤 User Settings  — read-only: ID, username, full name
 *                       editable:  email, job title
 *                       separate:  change password
 *   🎨 Theme          — built-in presets + accent chips + custom theme list
 *   🔧 Advanced       — grouped colour editor (UI chrome, row states), save/name custom themes
 */
public class SettingsPanel extends JPanel {

    private final BugListView        parent;
    private final SettingsController controller;

    // ── Nav ───────────────────────────────────────────────────────────────────
    private JButton btnUser, btnTheme, btnAdvanced;

    // ── Cards ─────────────────────────────────────────────────────────────────
    private final CardLayout innerCard  = new CardLayout();
    private final JPanel     innerPanel = new JPanel(innerCard);
    public  static final String CARD_USER     = "S_USER";
    public  static final String CARD_THEME    = "S_THEME";
    public  static final String CARD_ADVANCED = "S_ADVANCED";

    // ── User settings — editable fields ───────────────────────────────────────
    private JTextField     txtEmail      = new JTextField();
    private JTextField     txtJobTitle   = new JTextField();
    private JPasswordField txtCurrentPwd = new JPasswordField();
    private JPasswordField txtNewPwd     = new JPasswordField();
    private JPasswordField txtConfirmPwd = new JPasswordField();

    // ── User settings — read-only display labels ───────────────────────────────
    private JLabel lblRoId       = roValue("—");
    private JLabel lblRoUsername = roValue("—");
    private JLabel lblRoFullName = roValue("—");

    // ── Status labels ─────────────────────────────────────────────────────────
    private JLabel lblProfileStatus  = statusLabel();
    private JLabel lblPwdStatus      = statusLabel();
    private JLabel lblAdvancedStatus = statusLabel();

    // ── Advanced editor ───────────────────────────────────────────────────────
    private ThemeDefinition advancedDraft;
    private JTextField      txtThemeName;

    // ── UI Chrome swatches (14 colours, indices 0–13) ─────────────────────────
    private JButton[] chromeSwatches;
    private static final String[] CHROME_LABELS = {
        "Background — Main Window",    "Background — Panels & Cards",
        "Background — Sidebar",        "Background — Header & Action Bars",
        "Accent Colour",               "Accent Hover",
        "Danger (errors, deletes)",    "Success (confirmations)",
        "Warning",                     "Text — Primary",
        "Text — Muted / Labels",       "Border Lines",
        "Table — Alternate Row",       "Table — Selected Row"
    };

    // ── Row state swatches (6 colours, indices 0–5, 2 per state) ─────────────
    private JButton[] rowStateSwatches; // [closedBg, closedFg, overdueBg, overdueFg, completedBg, completedFg]

    // ═════════════════════════════════════════════════════════════════════════
    public SettingsPanel(BugListView parent) {
        this.parent     = parent;
        this.controller = new SettingsController(this, parent);
        buildUI();
    }

    private void buildUI() {
        setBackground(AppTheme.BG_DARK);
        setLayout(new BorderLayout());

        // Header bar
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(AppTheme.BG_HEADER);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER_COLOR),
            new EmptyBorder(14,24,14,24)));
        JLabel title = new JLabel("⚙  Settings");
        title.setFont(AppTheme.FONT_HEADING);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JButton btnBack = AppTheme.secondaryButton("← Back to Bugs");
        btnBack.addActionListener(e -> { parent.restoreSidebar(); parent.showCard(BugListView.CARD_TABLE); });
        hdr.add(title, BorderLayout.WEST);
        hdr.add(btnBack, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(AppTheme.BG_DARK);
        body.add(buildNav(),        BorderLayout.WEST);
        body.add(buildInnerCards(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    // ── Left nav ──────────────────────────────────────────────────────────────
    private JPanel buildNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(AppTheme.BG_SIDEBAR);
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,0,1,AppTheme.BORDER_COLOR),
            new EmptyBorder(16,0,16,0)));
        nav.setPreferredSize(new Dimension(190, 0));

        JLabel lbl = AppTheme.muted("  SETTINGS");
        lbl.setBorder(new EmptyBorder(0,14,10,14));
        nav.add(lbl);

        btnUser     = navBtn("👤  User Settings");
        btnTheme    = navBtn("🎨  Theme");
        btnAdvanced = navBtn("🔧  Advanced");

        btnUser.addActionListener(e     -> showInnerCard(CARD_USER));
        btnTheme.addActionListener(e    -> showInnerCard(CARD_THEME));
        btnAdvanced.addActionListener(e -> showInnerCard(CARD_ADVANCED));

        nav.add(btnUser);
        nav.add(btnTheme);
        nav.add(btnAdvanced);
        nav.add(Box.createVerticalGlue());
        setNavActive(btnUser);
        return nav;
    }

    private JButton navBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(AppTheme.FONT_BODY);
        b.setBackground(AppTheme.BG_SIDEBAR);
        b.setForeground(AppTheme.TEXT_PRIMARY);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(12,18,12,18));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void setNavActive(JButton active) {
        for (JButton b : new JButton[]{btnUser, btnTheme, btnAdvanced}) {
            if (b == null) continue;
            boolean on = (b == active);
            b.setBackground(on ? AppTheme.TABLE_SEL : AppTheme.BG_SIDEBAR);
            b.setForeground(on ? java.awt.Color.WHITE : AppTheme.TEXT_PRIMARY);
        }
    }

    public void showInnerCard(String card) {
        innerCard.show(innerPanel, card);
        setNavActive(switch (card) {
            case CARD_USER     -> btnUser;
            case CARD_ADVANCED -> btnAdvanced;
            default            -> btnTheme;
        });
    }

    // ── Inner cards ───────────────────────────────────────────────────────────
    private JPanel buildInnerCards() {
        innerPanel.setBackground(AppTheme.BG_DARK);
        innerPanel.add(buildUserCard(),     CARD_USER);
        innerPanel.add(buildThemeCard(),    CARD_THEME);
        innerPanel.add(buildAdvancedCard(), CARD_ADVANCED);
        innerCard.show(innerPanel, CARD_USER);
        return innerPanel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // USER SETTINGS CARD
    // ════════════════════════════════════════════════════════════════════════
    private JScrollPane buildUserCard() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_DARK);
        content.setBorder(new EmptyBorder(24,40,24,40));

        JPanel profileCard = sectionCard("Profile");

        JPanel roGrid = new JPanel(new GridBagLayout());
        roGrid.setOpaque(false);
        roGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        roGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        int rr = 0;
        rr = addRoRow(roGrid, rr, "User ID",    lblRoId);
        rr = addRoRow(roGrid, rr, "Username",   lblRoUsername);
             addRoRow(roGrid, rr, "Full Name",  lblRoFullName);

        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER_COLOR);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        profileCard.add(roGrid);
        profileCard.add(Box.createVerticalStrut(12));
        profileCard.add(sep);
        profileCard.add(Box.createVerticalStrut(12));

        addField(profileCard, "Email *",   txtEmail);
        addField(profileCard, "Job Title", txtJobTitle);

        lblProfileStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.add(Box.createVerticalStrut(8));
        profileCard.add(lblProfileStatus);

        JButton btnSave = AppTheme.primaryButton("Save Profile");
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.addActionListener(e -> controller.onSaveProfile());
        profileCard.add(Box.createVerticalStrut(6));
        profileCard.add(btnSave);

        JPanel pwdCard = sectionCard("Change Password");
        addField(pwdCard, "Current Password",    txtCurrentPwd);
        addField(pwdCard, "New Password",         txtNewPwd);
        addField(pwdCard, "Confirm New Password", txtConfirmPwd);

        lblPwdStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        pwdCard.add(Box.createVerticalStrut(8));
        pwdCard.add(lblPwdStatus);

        JButton btnPwd = AppTheme.primaryButton("Change Password");
        btnPwd.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPwd.addActionListener(e -> controller.onChangePassword());
        pwdCard.add(Box.createVerticalStrut(6));
        pwdCard.add(btnPwd);

        content.add(profileCard);
        content.add(Box.createVerticalStrut(20));
        content.add(pwdCard);
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(AppTheme.BG_DARK);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private int addRoRow(JPanel grid, int row, String labelText, JLabel value) {
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.gridy = row; lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(3,0,3,20);
        GridBagConstraints vc = new GridBagConstraints();
        vc.gridx = 1; vc.gridy = row; vc.anchor = GridBagConstraints.WEST;
        vc.fill = GridBagConstraints.HORIZONTAL; vc.weightx = 1;
        vc.insets = new Insets(3,0,3,0);
        grid.add(fieldLbl(labelText), lc);
        grid.add(value, vc);
        return row + 1;
    }

    // ════════════════════════════════════════════════════════════════════════
    // THEME PICKER CARD
    // ════════════════════════════════════════════════════════════════════════
    private JScrollPane buildThemeCard() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(AppTheme.BG_DARK);
        outer.setBorder(new EmptyBorder(24,40,24,40));

        JPanel themeCard = sectionCard("Choose Theme");

        JLabel lblBuiltIn = fieldLbl("Built-in Presets");
        lblBuiltIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblBuiltIn.setBorder(new EmptyBorder(0,0,8,0));
        themeCard.add(lblBuiltIn);

        JPanel presetGrid = new JPanel(new GridLayout(0, 3, 10, 10));
        presetGrid.setOpaque(false);
        presetGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (ThemeDefinition t : ThemeDefinition.builtIns()) presetGrid.add(makeThemeButton(t));
        themeCard.add(presetGrid);

        List<ThemeDefinition> customs = UserPreferences.loadCustomThemes();
        if (!customs.isEmpty()) {
            themeCard.add(Box.createVerticalStrut(20));
            JLabel lblCustom = fieldLbl("Saved Custom Themes");
            lblCustom.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblCustom.setBorder(new EmptyBorder(0,0,8,0));
            themeCard.add(lblCustom);

            JPanel customGrid = new JPanel(new GridLayout(0, 3, 10, 10));
            customGrid.setOpaque(false);
            customGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (ThemeDefinition c : customs) customGrid.add(makeCustomThemeButton(c));
            themeCard.add(customGrid);
        }

        themeCard.add(Box.createVerticalStrut(20));
        JLabel lblAccent = fieldLbl("Quick Accent  (Dark base, swap accent colour)");
        lblAccent.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAccent.setBorder(new EmptyBorder(0,0,8,0));
        themeCard.add(lblAccent);

        JPanel accentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        accentRow.setOpaque(false);
        accentRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (ThemeDefinition a : new ThemeDefinition[]{
                ThemeDefinition.ACCENT_PURPLE(), ThemeDefinition.ACCENT_TEAL(),
                ThemeDefinition.ACCENT_ROSE(),   ThemeDefinition.ACCENT_AMBER()})
            accentRow.add(makeAccentChip(a));
        themeCard.add(accentRow);

        JLabel note = AppTheme.muted("Theme applies immediately and is saved for next launch.  Right-click custom themes to edit or delete.");
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        note.setBorder(new EmptyBorder(16,0,0,0));
        themeCard.add(note);

        outer.add(themeCard, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(AppTheme.BG_DARK);
        return scroll;
    }

    private JButton makeThemeButton(ThemeDefinition t) {
        boolean active = t.name.equals(AppTheme.getCurrentTheme().name);
        Color[] sw = { t.bgDark, t.accent, t.bgPanel };
        JButton btn = new JButton(t.name) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int s = 9;
                for (int i = 0; i < 3; i++) { g2.setColor(sw[i]); g2.fillRect(i*s, 0, s, getHeight()); }
                g2.setColor(getBackground()); g2.fillRect(s*3, 0, getWidth()-s*3, getHeight()); g2.dispose();
                FontMetrics fm = getFontMetrics(getFont());
                g.setColor(getForeground()); g.setFont(getFont());
                g.drawString(getText(), s*3+12, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        styleThemeBtn(btn, active);
        btn.addActionListener(e -> controller.onSelectTheme(t));
        return btn;
    }

    private JButton makeCustomThemeButton(ThemeDefinition t) {
        boolean active = t.name.equals(AppTheme.getCurrentTheme().name);
        Color[] sw = { t.bgDark, t.accent, t.bgPanel };
        JButton btn = new JButton(t.name) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                int s = 9;
                for (int i = 0; i < 3; i++) { g2.setColor(sw[i]); g2.fillRect(i*s, 0, s, getHeight()); }
                g2.setColor(getBackground()); g2.fillRect(s*3, 0, getWidth()-s*3, getHeight()); g2.dispose();
                FontMetrics fm = getFontMetrics(getFont());
                g.setColor(getForeground()); g.setFont(getFont());
                g.drawString(getText(), s*3+12, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        styleThemeBtn(btn, active);

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(AppTheme.BG_PANEL);
        JMenuItem miApply  = menuItem("Apply",  AppTheme.TEXT_PRIMARY);
        JMenuItem miEdit   = menuItem("Edit",   AppTheme.ACCENT);
        JMenuItem miDelete = menuItem("Delete", AppTheme.DANGER);
        miApply.addActionListener(e  -> controller.onSelectTheme(t));
        miEdit.addActionListener(e   -> { loadDraftIntoAdvanced(t); showInnerCard(CARD_ADVANCED); });
        miDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete theme \"" + t.name + "\"?", "Delete Theme",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) controller.onDeleteCustomTheme(t);
        });
        menu.add(miApply); menu.add(miEdit); menu.addSeparator(); menu.add(miDelete);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) menu.show(btn, e.getX(), e.getY());
                else controller.onSelectTheme(t);
            }
        });
        return btn;
    }

    private JMenuItem menuItem(String text, Color fg) {
        JMenuItem mi = new JMenuItem(text);
        mi.setFont(AppTheme.FONT_BODY);
        mi.setBackground(AppTheme.BG_PANEL);
        mi.setForeground(fg);
        return mi;
    }

    private void styleThemeBtn(JButton btn, boolean active) {
        btn.setFont(AppTheme.FONT_BODY);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setPreferredSize(new Dimension(200, 46));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(active ? AppTheme.TABLE_SEL : AppTheme.BG_PANEL);
        btn.setForeground(active ? java.awt.Color.WHITE : AppTheme.TEXT_PRIMARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(active ? AppTheme.ACCENT : AppTheme.BORDER_COLOR, active ? 2 : 1),
            new EmptyBorder(6,6,6,12)));
    }

    private JButton makeAccentChip(ThemeDefinition t) {
        boolean active = t.name.equals(AppTheme.getCurrentTheme().name);
        JButton chip = new JButton();
        chip.setPreferredSize(new Dimension(36, 36));
        chip.setBackground(t.accent);
        chip.setOpaque(true); chip.setFocusPainted(false);
        chip.setBorder(BorderFactory.createLineBorder(
            active ? java.awt.Color.WHITE : AppTheme.BORDER_COLOR, active ? 2 : 1));
        chip.setToolTipText(t.name);
        chip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chip.addActionListener(e -> controller.onSelectTheme(t));
        return chip;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ADVANCED EDITOR CARD — grouped colour editor
    // ════════════════════════════════════════════════════════════════════════
    private JScrollPane buildAdvancedCard() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_DARK);
        content.setBorder(new EmptyBorder(24,40,24,40));

        // ── Theme Name ─────────────────────────────────────────────────────────
        JPanel nameCard = sectionCard("Theme Name");
        JPanel nameRow = new JPanel(new BorderLayout(10,0));
        nameRow.setOpaque(false);
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtThemeName = new JTextField();
        styleTextField(txtThemeName);
        nameRow.add(fieldLbl("Name:"), BorderLayout.WEST);
        nameRow.add(txtThemeName,      BorderLayout.CENTER);
        nameCard.add(nameRow);
        content.add(nameCard);
        content.add(Box.createVerticalStrut(16));

        // ── UI Chrome ──────────────────────────────────────────────────────────
        JPanel chromeCard = sectionCard("UI Chrome — Backgrounds, Accents & Text");
        chromeCard.add(AppTheme.muted("Controls the general look of windows, panels, buttons and text."));
        chromeCard.add(Box.createVerticalStrut(12));
        chromeSwatches = new JButton[CHROME_LABELS.length];
        chromeCard.add(buildSwatchGrid(chromeSwatches, CHROME_LABELS, this::pickChromeColor));
        content.add(chromeCard);
        content.add(Box.createVerticalStrut(16));

        // ── Row States ─────────────────────────────────────────────────────────
        rowStateSwatches = new JButton[6];

        // Closed-project rows
        JPanel closedCard = sectionCard("Closed Project Rows");
        closedCard.add(wrapMuted("Rows highlighted when a bug belongs to a project that has been closed."));
        closedCard.add(Box.createVerticalStrut(10));
        closedCard.add(buildPairRow(rowStateSwatches, 0, "Row Background", 1, "Row Text Colour", this::pickRowStateColor));
        closedCard.add(Box.createVerticalStrut(4));
        closedCard.add(buildStatePreviewRow(
            () -> rowStateSwatches[0].getBackground(),
            () -> rowStateSwatches[1].getBackground(),
            "Example: Bug Title [Project Closed]"));
        content.add(closedCard);
        content.add(Box.createVerticalStrut(12));

        // Overdue rows
        JPanel overdueCard = sectionCard("Overdue Bug Rows");
        overdueCard.add(wrapMuted("Rows highlighted when a bug's due date has passed and it is not yet completed."));
        overdueCard.add(Box.createVerticalStrut(10));
        overdueCard.add(buildPairRow(rowStateSwatches, 2, "Row Background", 3, "Row Text Colour", this::pickRowStateColor));
        overdueCard.add(Box.createVerticalStrut(4));
        overdueCard.add(buildStatePreviewRow(
            () -> rowStateSwatches[2].getBackground(),
            () -> rowStateSwatches[3].getBackground(),
            "Example: Bug Title [Overdue]"));
        content.add(overdueCard);
        content.add(Box.createVerticalStrut(12));

        // Completed rows
        JPanel completedCard = sectionCard("Completed Bug Rows");
        completedCard.add(wrapMuted("Rows highlighted when a bug's status is COMPLETED."));
        completedCard.add(Box.createVerticalStrut(10));
        completedCard.add(buildPairRow(rowStateSwatches, 4, "Row Background", 5, "Row Text Colour", this::pickRowStateColor));
        completedCard.add(Box.createVerticalStrut(4));
        completedCard.add(buildStatePreviewRow(
            () -> rowStateSwatches[4].getBackground(),
            () -> rowStateSwatches[5].getBackground(),
            "Example: Bug Title [Completed]"));
        content.add(completedCard);
        content.add(Box.createVerticalStrut(16));

        // ── Controls ──────────────────────────────────────────────────────────
        JPanel ctrlCard = sectionCard("Apply");

        // "Start from" preset row
        JPanel startRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        startRow.setOpaque(false);
        startRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        startRow.add(fieldLbl("Start from:"));
        for (ThemeDefinition b : ThemeDefinition.builtIns()) {
            JButton bBtn = smallBtn(b.name);
            bBtn.addActionListener(e -> loadDraftIntoAdvanced(b.clone()));
            startRow.add(bBtn);
        }
        ctrlCard.add(startRow);
        ctrlCard.add(Box.createVerticalStrut(14));

        lblAdvancedStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        ctrlCard.add(lblAdvancedStatus);
        ctrlCard.add(Box.createVerticalStrut(6));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnPreview = AppTheme.secondaryButton("▶  Preview");
        JButton btnSave    = AppTheme.primaryButton("Save & Apply");
        btnPreview.addActionListener(e -> previewDraft());
        btnSave.addActionListener(e    -> controller.onSaveCustomTheme(buildDraftFromUI(), true));
        btnRow.add(btnPreview);
        btnRow.add(btnSave);
        ctrlCard.add(btnRow);

        content.add(ctrlCard);
        content.add(Box.createVerticalGlue());

        // Seed draft from current theme
        advancedDraft = AppTheme.getCurrentTheme().clone();
        advancedDraft.builtIn = false;
        refreshAdvancedSwatches();

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(AppTheme.BG_DARK);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    // ── Build a 2-column swatch grid ──────────────────────────────────────────
    private JPanel buildSwatchGrid(JButton[] swatches, String[] labels, java.util.function.IntConsumer picker) {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < labels.length; i++) {
            int col = i % 2, row = i / 2;
            GridBagConstraints lc = new GridBagConstraints();
            lc.gridx = col*3; lc.gridy = row; lc.anchor = GridBagConstraints.WEST;
            lc.insets = new Insets(4, col == 1 ? 28 : 0, 4, 8);
            GridBagConstraints sc = new GridBagConstraints();
            sc.gridx = col*3+1; sc.gridy = row; sc.insets = new Insets(4, 0, 4, col == 0 ? 8 : 0);
            grid.add(fieldLbl(labels[i]), lc);
            final int idx = i;
            JButton swatch = makeSwatch(null, () -> picker.accept(idx));
            swatches[i] = swatch;
            grid.add(swatch, sc);
            // Spacer column so the two halves don't mash together
            if (col == 0) {
                GridBagConstraints sp = new GridBagConstraints();
                sp.gridx = 2; sp.gridy = row; sp.weightx = 0.05;
                grid.add(Box.createHorizontalStrut(20), sp);
            } else {
                GridBagConstraints wx = new GridBagConstraints();
                wx.gridx = col*3+2; wx.gridy = row; wx.weightx = 1;
                grid.add(Box.createHorizontalStrut(1), wx);
            }
        }
        return grid;
    }

    // ── Build a labelled pair of swatches (bg + fg) ───────────────────────────
    private JPanel buildPairRow(JButton[] swatches, int bgIdx, String bgLabel,
                                int fgIdx, String fgLabel,
                                java.util.function.IntConsumer picker) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(fieldLbl(bgLabel));
        JButton bgSwatch = makeSwatch(null, () -> picker.accept(bgIdx));
        swatches[bgIdx] = bgSwatch;
        row.add(bgSwatch);

        row.add(Box.createHorizontalStrut(20));
        row.add(fieldLbl(fgLabel));
        JButton fgSwatch = makeSwatch(null, () -> picker.accept(fgIdx));
        swatches[fgIdx] = fgSwatch;
        row.add(fgSwatch);

        return row;
    }

    /** A live preview strip showing text on background using the given colour suppliers. */
    private JPanel buildStatePreviewRow(java.util.function.Supplier<Color> bgSupplier,
                                        java.util.function.Supplier<Color> fgSupplier,
                                        String sampleText) {
        JPanel preview = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(bgSupplier.get());
                g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        preview.setAlignmentX(Component.LEFT_ALIGNMENT);
        preview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lbl = new JLabel("  " + sampleText) {
            @Override public void paintComponent(Graphics g) {
                setForeground(fgSupplier.get());
                super.paintComponent(g);
            }
        };
        lbl.setFont(AppTheme.FONT_BODY);
        preview.add(lbl, BorderLayout.CENTER);
        preview.setOpaque(false);
        return preview;
    }

    private JButton makeSwatch(Color initial, Runnable onClick) {
        JButton swatch = new JButton();
        if (initial != null) swatch.setBackground(initial);
        swatch.setPreferredSize(new Dimension(80, 28));
        swatch.setOpaque(true); swatch.setFocusPainted(false);
        swatch.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR));
        swatch.setToolTipText("Click to pick colour");
        swatch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        swatch.addActionListener(e -> onClick.run());
        return swatch;
    }

    // ── Colour picker handlers ────────────────────────────────────────────────
    private void pickChromeColor(int idx) {
        Color picked = JColorChooser.showDialog(this, "Pick: " + CHROME_LABELS[idx],
            chromeSwatches[idx].getBackground());
        if (picked != null) {
            chromeSwatches[idx].setBackground(picked);
            chromeSwatches[idx].setToolTipText(AppTheme.toHex(picked));
            applyChromeSwatchToDraft(idx, picked);
        }
    }

    private void pickRowStateColor(int idx) {
        String[] rowLabels = {
            "Closed Row — Background", "Closed Row — Text Colour",
            "Overdue Row — Background","Overdue Row — Text Colour",
            "Completed Row — Background","Completed Row — Text Colour"
        };
        Color picked = JColorChooser.showDialog(this, "Pick: " + rowLabels[idx],
            rowStateSwatches[idx].getBackground());
        if (picked != null) {
            rowStateSwatches[idx].setBackground(picked);
            rowStateSwatches[idx].setToolTipText(AppTheme.toHex(picked));
            applyRowStateSwatchToDraft(idx, picked);
            // Force preview panels to repaint
            SwingUtilities.invokeLater(() -> repaint());
        }
    }

    // ── Draft sync helpers ────────────────────────────────────────────────────
    public void loadDraftIntoAdvanced(ThemeDefinition t) {
        advancedDraft = t.clone();
        advancedDraft.builtIn = false;
        if (txtThemeName != null)
            txtThemeName.setText(t.builtIn ? "" : t.name);
        refreshAdvancedSwatches();
    }

    private void refreshAdvancedSwatches() {
        if (advancedDraft == null) return;
        if (chromeSwatches != null) {
            Color[] chrome = chromeDraftToArray(advancedDraft);
            for (int i = 0; i < chromeSwatches.length; i++) {
                chromeSwatches[i].setBackground(chrome[i]);
                chromeSwatches[i].setToolTipText(AppTheme.toHex(chrome[i]));
            }
        }
        if (rowStateSwatches != null) {
            Color[] rs = rowStateDraftToArray(advancedDraft);
            for (int i = 0; i < rowStateSwatches.length; i++) {
                rowStateSwatches[i].setBackground(rs[i]);
                rowStateSwatches[i].setToolTipText(AppTheme.toHex(rs[i]));
            }
        }
    }

    private void previewDraft() {
        controller.onPreviewTheme(buildDraftFromUI());
    }

    ThemeDefinition buildDraftFromUI() {
        if (chromeSwatches != null) {
            advancedDraft.bgDark      = chromeSwatches[0].getBackground();
            advancedDraft.bgPanel     = chromeSwatches[1].getBackground();
            advancedDraft.bgSidebar   = chromeSwatches[2].getBackground();
            advancedDraft.bgHeader    = chromeSwatches[3].getBackground();
            advancedDraft.accent      = chromeSwatches[4].getBackground();
            advancedDraft.accentHover = chromeSwatches[5].getBackground();
            advancedDraft.danger      = chromeSwatches[6].getBackground();
            advancedDraft.success     = chromeSwatches[7].getBackground();
            advancedDraft.warning     = chromeSwatches[8].getBackground();
            advancedDraft.textPrimary = chromeSwatches[9].getBackground();
            advancedDraft.textMuted   = chromeSwatches[10].getBackground();
            advancedDraft.borderColor = chromeSwatches[11].getBackground();
            advancedDraft.tableAlt    = chromeSwatches[12].getBackground();
            advancedDraft.tableSel    = chromeSwatches[13].getBackground();
        }
        if (rowStateSwatches != null) {
            advancedDraft.closedBg    = rowStateSwatches[0].getBackground();
            advancedDraft.closedFg    = rowStateSwatches[1].getBackground();
            advancedDraft.overdueBg   = rowStateSwatches[2].getBackground();
            advancedDraft.overdueFg   = rowStateSwatches[3].getBackground();
            advancedDraft.completedBg = rowStateSwatches[4].getBackground();
            advancedDraft.completedFg = rowStateSwatches[5].getBackground();
        }
        advancedDraft.name = (txtThemeName != null && !txtThemeName.getText().isBlank())
            ? txtThemeName.getText().trim() : "My Custom Theme";
        advancedDraft.builtIn = false;
        return advancedDraft;
    }

    private void applyChromeSwatchToDraft(int idx, Color c) {
        switch (idx) {
            case 0  -> advancedDraft.bgDark      = c;
            case 1  -> advancedDraft.bgPanel      = c;
            case 2  -> advancedDraft.bgSidebar    = c;
            case 3  -> advancedDraft.bgHeader     = c;
            case 4  -> advancedDraft.accent       = c;
            case 5  -> advancedDraft.accentHover  = c;
            case 6  -> advancedDraft.danger       = c;
            case 7  -> advancedDraft.success      = c;
            case 8  -> advancedDraft.warning      = c;
            case 9  -> advancedDraft.textPrimary  = c;
            case 10 -> advancedDraft.textMuted    = c;
            case 11 -> advancedDraft.borderColor  = c;
            case 12 -> advancedDraft.tableAlt     = c;
            case 13 -> advancedDraft.tableSel     = c;
        }
    }

    private void applyRowStateSwatchToDraft(int idx, Color c) {
        switch (idx) {
            case 0 -> advancedDraft.closedBg    = c;
            case 1 -> advancedDraft.closedFg    = c;
            case 2 -> advancedDraft.overdueBg   = c;
            case 3 -> advancedDraft.overdueFg   = c;
            case 4 -> advancedDraft.completedBg = c;
            case 5 -> advancedDraft.completedFg = c;
        }
    }

    private Color[] chromeDraftToArray(ThemeDefinition t) {
        return new Color[]{
            t.bgDark, t.bgPanel, t.bgSidebar, t.bgHeader,
            t.accent, t.accentHover,
            t.danger, t.success, t.warning,
            t.textPrimary, t.textMuted, t.borderColor,
            t.tableAlt, t.tableSel
        };
    }

    private Color[] rowStateDraftToArray(ThemeDefinition t) {
        Color[] def = {
            new Color(0,0,0), new Color(255,120,120),
            new Color(90,20,20), new Color(255,120,120),
            new Color(20,60,30), new Color(80,200,120)
        };
        return new Color[]{
            t.closedBg    != null ? t.closedBg    : def[0],
            t.closedFg    != null ? t.closedFg    : def[1],
            t.overdueBg   != null ? t.overdueBg   : def[2],
            t.overdueFg   != null ? t.overdueFg   : def[3],
            t.completedBg != null ? t.completedBg : def[4],
            t.completedFg != null ? t.completedFg : def[5]
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // Public populate
    // ════════════════════════════════════════════════════════════════════════
    public void populate(User user) {
        lblRoId.setText(String.valueOf(user.getId()));
        lblRoUsername.setText("@" + user.getUsername());
        lblRoFullName.setText(user.getFullname());
        txtEmail.setText(user.getEmail());
        txtJobTitle.setText(user.getJobTitle() != null ? user.getJobTitle() : "");
        txtCurrentPwd.setText(""); txtNewPwd.setText(""); txtConfirmPwd.setText("");
        lblProfileStatus.setText(" "); lblPwdStatus.setText(" ");
        if (advancedDraft == null) advancedDraft = AppTheme.getCurrentTheme().clone();
        if (txtThemeName != null && txtThemeName.getText().isBlank() && !AppTheme.getCurrentTheme().builtIn)
            txtThemeName.setText(AppTheme.getCurrentTheme().name);
        refreshAdvancedSwatches();
    }

    // ── Getters used by SettingsController ────────────────────────────────────
    public String getEmail()            { return txtEmail.getText().trim(); }
    public String getJobTitle()         { return txtJobTitle.getText().trim(); }
    public String getCurrentPwd()       { return new String(txtCurrentPwd.getPassword()).trim(); }
    public String getNewPwd()           { return new String(txtNewPwd.getPassword()).trim(); }
    public String getConfirmPwd()       { return new String(txtConfirmPwd.getPassword()).trim(); }
    public SettingsController getController() { return controller; }

    public void setProfileStatus(String msg, boolean ok) {
        lblProfileStatus.setForeground(ok ? AppTheme.SUCCESS : AppTheme.DANGER);
        lblProfileStatus.setText(ok ? "✔  " + msg : "✖  " + msg);
    }
    public void setPwdStatus(String msg, boolean ok) {
        lblPwdStatus.setForeground(ok ? AppTheme.SUCCESS : AppTheme.DANGER);
        lblPwdStatus.setText(ok ? "✔  " + msg : "✖  " + msg);
    }
    public void setAdvancedStatus(String msg, boolean ok) {
        lblAdvancedStatus.setForeground(ok ? AppTheme.SUCCESS : AppTheme.DANGER);
        lblAdvancedStatus.setText(ok ? "✔  " + msg : "✖  " + msg);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Layout / style helpers
    // ════════════════════════════════════════════════════════════════════════
    private JPanel sectionCard(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppTheme.BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
            new EmptyBorder(20,24,20,24)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel h = new JLabel(title);
        h.setFont(AppTheme.FONT_HEADING); h.setForeground(AppTheme.TEXT_PRIMARY);
        h.setAlignmentX(Component.LEFT_ALIGNMENT);
        h.setBorder(new EmptyBorder(0,0,14,0));
        p.add(h);
        return p;
    }

    private JLabel wrapMuted(String text) {
        JLabel l = AppTheme.muted(text);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void addField(JPanel card, String label, JComponent field) {
        JLabel lbl = fieldLbl(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(8,0,3,0));
        styleTextField(field);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(lbl);
        card.add(field);
    }

    private void styleTextField(JComponent f) {
        f.setFont(AppTheme.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
            new EmptyBorder(6,10,6,10)));
        if (f instanceof JTextField tf) {
            tf.setBackground(AppTheme.BG_DARK);
            tf.setForeground(AppTheme.TEXT_PRIMARY);
            tf.setCaretColor(AppTheme.TEXT_PRIMARY);
        }
        if (f instanceof JPasswordField pf) {
            pf.setBackground(AppTheme.BG_DARK);
            pf.setForeground(AppTheme.TEXT_PRIMARY);
            pf.setCaretColor(AppTheme.TEXT_PRIMARY);
        }
    }

    private JLabel fieldLbl(String t) {
        JLabel l = new JLabel(t); l.setFont(AppTheme.FONT_LABEL); l.setForeground(AppTheme.TEXT_MUTED); return l;
    }
    private static JLabel roValue(String t) {
        JLabel l = new JLabel(t); l.setFont(AppTheme.FONT_BODY); l.setForeground(AppTheme.TEXT_PRIMARY); return l;
    }
    private static JLabel statusLabel() {
        JLabel l = new JLabel(" "); l.setFont(AppTheme.FONT_SMALL); return l;
    }
    private JButton smallBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(AppTheme.FONT_SMALL);
        b.setBackground(AppTheme.BG_PANEL);
        b.setForeground(AppTheme.TEXT_MUTED);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
            new EmptyBorder(3,8,3,8)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}