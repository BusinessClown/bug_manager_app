package util;

import java.awt.Color;
import java.io.*;
import java.util.*;

/**
 * Saves and loads theme preferences + custom theme definitions
 * to ~/.bugtracker_prefs.properties between sessions.
 *
 * Custom themes are serialised as:
 *   custom.N.name       = My Theme
 *   custom.N.bgDark     = #1C1E26
 *   custom.N.bgPanel    = ...  (14 colour keys)
 *
 * Active theme is stored as:
 *   theme.active = Dark              (built-in name)
 *   theme.active = custom:My Theme   (custom theme)
 */
public class UserPreferences {

    private static final String FILE =
        System.getProperty("user.home") + File.separator + ".bugtracker_prefs.properties";

    private static final Properties PROPS = new Properties();

    // Keys that map to ThemeDefinition colour fields (in order)
    private static final String[] COLOR_KEYS = {
        "bgDark","bgPanel","bgSidebar","bgHeader",
        "accent","accentHover",
        "danger","success","warning",
        "textPrimary","textMuted",
        "borderColor","tableAlt","tableSel"
    };

    static { load(); }

    // ── Persist ────────────────────────────────────────────────────────────────

    private static void load() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (FileInputStream in = new FileInputStream(f)) { PROPS.load(in); }
        catch (IOException ignored) {}
    }

    private static void save() {
        try (FileOutputStream out = new FileOutputStream(FILE)) {
            PROPS.store(out, "Bug Tracker preferences");
        } catch (IOException ignored) {}
    }

    // ── Active theme ───────────────────────────────────────────────────────────

    /** Save whichever theme is now active. */
    public static void saveActiveTheme(ThemeDefinition t) {
        if (t.builtIn) {
            PROPS.setProperty("theme.active", t.name);
        } else {
            PROPS.setProperty("theme.active", "custom:" + t.name);
        }
        if (!t.builtIn) saveCustomTheme(t);
        save();
    }

    /**
     * Reconstruct the saved active theme.
     * Falls back to Dark if anything is missing / corrupt.
     */
    public static ThemeDefinition loadActiveTheme(List<ThemeDefinition> customThemes) {
        String active = PROPS.getProperty("theme.active", "Dark");
        if (active.startsWith("custom:")) {
            String cname = active.substring(7);
            for (ThemeDefinition c : customThemes)
                if (c.name.equals(cname)) return c;
        }
        // Search built-ins by name
        for (ThemeDefinition b : ThemeDefinition.builtIns())
            if (b.name.equals(active)) return b;
        return ThemeDefinition.DARK();
    }

    // ── Custom themes ──────────────────────────────────────────────────────────

    public static List<ThemeDefinition> loadCustomThemes() {
        List<ThemeDefinition> list = new ArrayList<>();
        int i = 0;
        while (PROPS.containsKey("custom." + i + ".name")) {
            String prefix = "custom." + i + ".";
            ThemeDefinition t = new ThemeDefinition();
            t.name    = PROPS.getProperty(prefix + "name", "Custom " + i);
            t.builtIn = false;
            t.bgDark      = hex(prefix + "bgDark",      new Color(28,30,38));
            t.bgPanel     = hex(prefix + "bgPanel",     new Color(36,39,50));
            t.bgSidebar   = hex(prefix + "bgSidebar",   new Color(22,24,32));
            t.bgHeader    = hex(prefix + "bgHeader",    new Color(20,22,30));
            t.accent      = hex(prefix + "accent",      new Color(99,132,255));
            t.accentHover = hex(prefix + "accentHover", new Color(120,150,255));
            t.danger      = hex(prefix + "danger",      new Color(220,60,60));
            t.success     = hex(prefix + "success",     new Color(60,190,100));
            t.warning     = hex(prefix + "warning",     new Color(230,160,30));
            t.textPrimary = hex(prefix + "textPrimary", new Color(220,222,235));
            t.textMuted   = hex(prefix + "textMuted",   new Color(130,135,160));
            t.borderColor = hex(prefix + "borderColor", new Color(50,53,68));
            t.tableAlt    = hex(prefix + "tableAlt",    new Color(40,43,56));
            t.tableSel    = hex(prefix + "tableSel",    new Color(60,80,160));
            list.add(t);
            i++;
        }
        return list;
    }

    public static void saveCustomTheme(ThemeDefinition t) {
        // Find or assign slot
        int slot = findCustomSlot(t.name);
        String prefix = "custom." + slot + ".";
        PROPS.setProperty(prefix + "name",        t.name);
        PROPS.setProperty(prefix + "bgDark",      AppTheme.toHex(t.bgDark));
        PROPS.setProperty(prefix + "bgPanel",     AppTheme.toHex(t.bgPanel));
        PROPS.setProperty(prefix + "bgSidebar",   AppTheme.toHex(t.bgSidebar));
        PROPS.setProperty(prefix + "bgHeader",    AppTheme.toHex(t.bgHeader));
        PROPS.setProperty(prefix + "accent",      AppTheme.toHex(t.accent));
        PROPS.setProperty(prefix + "accentHover", AppTheme.toHex(t.accentHover));
        PROPS.setProperty(prefix + "danger",      AppTheme.toHex(t.danger));
        PROPS.setProperty(prefix + "success",     AppTheme.toHex(t.success));
        PROPS.setProperty(prefix + "warning",     AppTheme.toHex(t.warning));
        PROPS.setProperty(prefix + "textPrimary", AppTheme.toHex(t.textPrimary));
        PROPS.setProperty(prefix + "textMuted",   AppTheme.toHex(t.textMuted));
        PROPS.setProperty(prefix + "borderColor", AppTheme.toHex(t.borderColor));
        PROPS.setProperty(prefix + "tableAlt",    AppTheme.toHex(t.tableAlt));
        PROPS.setProperty(prefix + "tableSel",    AppTheme.toHex(t.tableSel));
        save();
    }

    public static void deleteCustomTheme(String name) {
        int slot = findCustomSlot(name);
        String prefix = "custom." + slot + ".";
        if (!PROPS.containsKey(prefix + "name")) return;
        // Remove all keys for this slot then compact slots above it
        int i = slot;
        while (true) {
            String next = "custom." + (i+1) + ".";
            if (!PROPS.containsKey(next + "name")) {
                // Remove current slot
                String cur = "custom." + i + ".";
                for (String k : COLOR_KEYS)          PROPS.remove(cur + k);
                PROPS.remove(cur + "name");
                break;
            }
            // Shift next slot down
            String cur = "custom." + i + ".";
            PROPS.setProperty(cur + "name", PROPS.getProperty(next + "name"));
            for (String k : COLOR_KEYS)
                PROPS.setProperty(cur + k, PROPS.getProperty(next + k, ""));
            i++;
        }
        save();
    }

    private static int findCustomSlot(String name) {
        int i = 0;
        while (PROPS.containsKey("custom." + i + ".name")) {
            if (PROPS.getProperty("custom." + i + ".name").equals(name)) return i;
            i++;
        }
        return i; // new slot
    }

    private static Color hex(String key, Color fallback) {
        String v = PROPS.getProperty(key);
        if (v == null) return fallback;
        return AppTheme.fromHex(v, fallback);
    }
}