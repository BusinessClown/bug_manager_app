import View.LoginView;
import util.AppTheme;
import util.ThemeDefinition;
import util.UserPreferences;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Load any saved custom themes first, then resolve active theme
        List<ThemeDefinition> customs = UserPreferences.loadCustomThemes();
        ThemeDefinition active = UserPreferences.loadActiveTheme(customs);
        AppTheme.apply(active);

        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }
}