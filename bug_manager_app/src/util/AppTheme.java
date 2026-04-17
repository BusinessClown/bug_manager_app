package util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * Centralised colour palette, fonts, and factory helpers for the Bug Tracker UI.
 * All UI classes pull constants from here so the look stays consistent.
 */
public final class AppTheme {

    private AppTheme() {}

    // ── Palette ────────────────────────────────────────────────────────────────
    public static final Color BG_DARK      = new Color(28,  30,  38);   // main window bg
    public static final Color BG_PANEL     = new Color(36,  39,  50);   // card / panel bg
    public static final Color BG_SIDEBAR   = new Color(22,  24,  32);   // left list bg
    public static final Color BG_HEADER    = new Color(20,  22,  30);   // top bar
    public static final Color ACCENT       = new Color(99, 132, 255);   // primary blue-purple
    public static final Color ACCENT_HOVER = new Color(120, 150, 255);
    public static final Color DANGER       = new Color(220,  60,  60);   // delete / error
    public static final Color SUCCESS      = new Color( 60, 190, 100);   // success green
    public static final Color WARNING      = new Color(230, 160,  30);   // warning amber
    public static final Color TEXT_PRIMARY = new Color(220, 222, 235);
    public static final Color TEXT_MUTED   = new Color(130, 135, 160);
    public static final Color BORDER_COLOR = new Color( 50,  53,  68);
    public static final Color TABLE_ALT    = new Color( 40,  43,  56);   // alternate row
    public static final Color TABLE_SEL    = new Color( 60,  80, 160);   // selected row

    // ── Fonts ───────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD,  12);

    // ── Borders ─────────────────────────────────────────────────────────────────
    public static final Border BORDER_PANEL  = BorderFactory.createLineBorder(BORDER_COLOR, 1);
    public static final Border PADDING_SM    = BorderFactory.createEmptyBorder(6,  10,  6, 10);
    public static final Border PADDING_MD    = BorderFactory.createEmptyBorder(12, 16, 12, 16);
    public static final Border PADDING_LG    = BorderFactory.createEmptyBorder(20, 24, 20, 24);

    // ── Button factory ──────────────────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        return styledButton(text, ACCENT, Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER, Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        return styledButton(text, BG_PANEL, TEXT_PRIMARY);
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

    // ── Label factory ───────────────────────────────────────────────────────────
    public static JLabel heading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel muted(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }
}
