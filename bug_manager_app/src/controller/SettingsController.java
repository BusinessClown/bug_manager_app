package controller;

import javax.swing.SwingUtilities;

import View.BugListView;
import View.SettingsPanel;
import dao.UserDaoImplementation;
import model.User;
import service.UserService;
import util.AppTheme;
import util.PasswordUtil;
import util.ThemeDefinition;
import util.UserPreferences;

public class SettingsController {

    private final SettingsPanel         panel;
    private final BugListView           parentView;
    private final UserDaoImplementation userDao     = new UserDaoImplementation();
    private final UserService           userService = new UserService(userDao);

    public SettingsController(SettingsPanel panel, BugListView parentView) {
        this.panel      = panel;
        this.parentView = parentView;
    }

    // ── Profile (email + job title only — full name / username are read-only) ──
    public void onSaveProfile() {
        User   user  = parentView.getLoggedInUser();
        String email = panel.getEmail();
        String job   = panel.getJobTitle();

        if (email.isEmpty()) {
            panel.setProfileStatus("Email is required.", false); return;
        }
        if (!email.matches("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$")) {
            panel.setProfileStatus("Email must be in the format: example@domain.com", false); return;
        }

        // Pass the existing immutable fields through unchanged
        String error = userService.adminUpdateUser(
            user,
            user.getFullname(),   // read-only — unchanged
            user.getUsername(),   // read-only — unchanged
            email,
            user.isAdmin(),
            job);

        if (error != null) { panel.setProfileStatus(error, false); return; }

        panel.setProfileStatus("Profile updated.", true);
    }

    // ── Password ───────────────────────────────────────────────────────────────
    public void onChangePassword() {
        User   user    = parentView.getLoggedInUser();
        String current = panel.getCurrentPwd();
        String newPwd  = panel.getNewPwd();
        String confirm = panel.getConfirmPwd();

        if (current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
            panel.setPwdStatus("All password fields are required.", false); return;
        }
        if (!PasswordUtil.verify(current, user.getPassword())) {
            panel.setPwdStatus("Current password is incorrect.", false); return;
        }
        if (newPwd.length() < 8) {
            panel.setPwdStatus("New password must be at least 8 characters.", false); return;
        }
        if (!newPwd.chars().anyMatch(Character::isUpperCase)) {
            panel.setPwdStatus("Must contain at least one capital letter.", false); return;
        }
        if (!newPwd.chars().anyMatch(Character::isDigit)) {
            panel.setPwdStatus("Must contain at least one number.", false); return;
        }
        if (!newPwd.equals(confirm)) {
            panel.setPwdStatus("New passwords do not match.", false); return;
        }

        String hashed = PasswordUtil.hash(newPwd);
        userDao.updatePassword(user.getId(), hashed);
        user.setPassword(hashed); // keep in-session object in sync
        panel.setPwdStatus("Password changed successfully.", true);
    }

    // ── Theme selection ────────────────────────────────────────────────────────
    public void onSelectTheme(ThemeDefinition theme) {
        UserPreferences.saveActiveTheme(theme);
        rebuildWithTheme(theme, SettingsPanel.CARD_THEME);
    }

    // ── Custom theme CRUD ──────────────────────────────────────────────────────
    public void onSaveCustomTheme(ThemeDefinition draft, boolean isNew) {
        if (draft.name == null || draft.name.isBlank()) {
            panel.setAdvancedStatus("Theme name cannot be empty.", false); return;
        }
        draft.builtIn = false;
        UserPreferences.saveCustomTheme(draft);
        UserPreferences.saveActiveTheme(draft);
        panel.setAdvancedStatus("Theme \"" + draft.name + "\" saved.", true);
        rebuildWithTheme(draft, SettingsPanel.CARD_THEME);
    }

    public void onDeleteCustomTheme(ThemeDefinition theme) {
        UserPreferences.deleteCustomTheme(theme.name);
        ThemeDefinition fallback = ThemeDefinition.DARK();
        UserPreferences.saveActiveTheme(fallback);
        rebuildWithTheme(fallback, SettingsPanel.CARD_THEME);
    }

    // ── Rebuild window with new theme ──────────────────────────────────────────
    public void rebuildWithTheme(ThemeDefinition t, String openCard) {
        AppTheme.apply(t);
        User user = parentView.getLoggedInUser();
        SwingUtilities.invokeLater(() -> {
            parentView.dispose();
            BugListView fresh = new BugListView(user);
            fresh.setVisible(true);
            fresh.showSettingsPanel(openCard);
        });
    }
}