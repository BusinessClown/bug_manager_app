package util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * Global UI palette. Call AppTheme.apply(ThemeDefinition) to switch themes.
 */
public final class AppTheme {

    private AppTheme() {}

    // ── Active theme ───────────────────────────────────────────────────────────
    private static ThemeDefinition currentTheme = ThemeDefinition.DARK();

    public static ThemeDefinition getCurrentTheme() { return currentTheme; }

    public static void apply(ThemeDefinition t) {
        currentTheme = t;
        BG_DARK      = t.bgDark;
        BG_PANEL     = t.bgPanel;
        BG_SIDEBAR   = t.bgSidebar;
        BG_HEADER    = t.bgHeader;
        ACCENT       = t.accent;
        ACCENT_HOVER = t.accentHover;
        DANGER       = t.danger;
        SUCCESS      = t.success;
        WARNING      = t.warning;
        TEXT_PRIMARY = t.textPrimary;
        TEXT_MUTED   = t.textMuted;
        BORDER_COLOR = t.borderColor;
        TABLE_ALT    = t.tableAlt;
        TABLE_SEL    = t.tableSel;
        refreshBorders();
    }

    // ── Palette (mutable) ──────────────────────────────────────────────────────
    public static Color BG_DARK      = new Color(28,  30,  38);
    public static Color BG_PANEL     = new Color(36,  39,  50);
    public static Color BG_SIDEBAR   = new Color(22,  24,  32);
    public static Color BG_HEADER    = new Color(20,  22,  30);
    public static Color ACCENT       = new Color(99, 132, 255);
    public static Color ACCENT_HOVER = new Color(120, 150, 255);
    public static Color DANGER       = new Color(220,  60,  60);
    public static Color SUCCESS      = new Color( 60, 190, 100);
    public static Color WARNING      = new Color(230, 160,  30);
    public static Color TEXT_PRIMARY = new Color(220, 222, 235);
    public static Color TEXT_MUTED   = new Color(130, 135, 160);
    public static Color BORDER_COLOR = new Color( 50,  53,  68);
    public static Color TABLE_ALT    = new Color( 40,  43,  56);
    public static Color TABLE_SEL    = new Color( 60,  80, 160);

    // ── Borders ────────────────────────────────────────────────────────────────
    public static Border BORDER_PANEL = BorderFactory.createLineBorder(BORDER_COLOR, 1);
    public static Border PADDING_SM   = BorderFactory.createEmptyBorder(6,  10,  6, 10);
    public static Border PADDING_MD   = BorderFactory.createEmptyBorder(12, 16, 12, 16);
    public static Border PADDING_LG   = BorderFactory.createEmptyBorder(20, 24, 20, 24);

    public static void refreshBorders() {
        BORDER_PANEL = BorderFactory.createLineBorder(BORDER_COLOR, 1);
    }

    // ── Fonts ──────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD,  12);

    // ── Button / label factories ───────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        return styledButton(text, ACCENT,    perceivedLight(ACCENT)  ? new Color(20,20,20) : java.awt.Color.WHITE);
    }
    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER,    java.awt.Color.WHITE);
    }
    public static JButton secondaryButton(String text) {
        return styledButton(text, BG_PANEL,  TEXT_PRIMARY);
    }

    private static JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return btn;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text); l.setFont(FONT_HEADING); l.setForeground(TEXT_PRIMARY); return l;
    }
    public static JLabel muted(String text) {
        JLabel l = new JLabel(text); l.setFont(FONT_SMALL); l.setForeground(TEXT_MUTED); return l;
    }

    // ── Utilities ──────────────────────────────────────────────────────────────
    public static boolean perceivedLight(Color c) {
        return (0.2126*c.getRed() + 0.7152*c.getGreen() + 0.0722*c.getBlue()) > 160;
    }
    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }
    public static Color fromHex(String hex, Color fallback) {
        try {
            String h = hex.startsWith("#") ? hex.substring(1) : hex;
            return new Color(
                Integer.parseInt(h.substring(0,2),16),
                Integer.parseInt(h.substring(2,4),16),
                Integer.parseInt(h.substring(4,6),16));
        } catch (Exception e) { return fallback; }
    }
}
