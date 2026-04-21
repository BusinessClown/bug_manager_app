package util;

import java.awt.Color;

/**
 * A complete set of UI colours that defines one theme.
 * Built-in presets are constants; custom themes can be created freely.
 */
public class ThemeDefinition implements Cloneable {

    // ── Identity ───────────────────────────────────────────────────────────────
    public String  name;
    public boolean builtIn; // built-ins can't be deleted

    // ── Background family ──────────────────────────────────────────────────────
    public Color bgDark;      // main window background
    public Color bgPanel;     // card / panel background
    public Color bgSidebar;   // left list background
    public Color bgHeader;    // top bar / action bars

    // ── Accent family ──────────────────────────────────────────────────────────
    public Color accent;      // primary interactive colour
    public Color accentHover;

    // ── Semantic colours ───────────────────────────────────────────────────────
    public Color danger;
    public Color success;
    public Color warning;

    // ── Text ───────────────────────────────────────────────────────────────────
    public Color textPrimary;
    public Color textMuted;

    // ── Chrome ─────────────────────────────────────────────────────────────────
    public Color borderColor;
    public Color tableAlt;   // alternate table row
    public Color tableSel;   // selected table row

    public ThemeDefinition() {}

    public ThemeDefinition(String name, boolean builtIn,
            Color bgDark, Color bgPanel, Color bgSidebar, Color bgHeader,
            Color accent, Color accentHover,
            Color danger, Color success, Color warning,
            Color textPrimary, Color textMuted,
            Color borderColor, Color tableAlt, Color tableSel) {
        this.name        = name;
        this.builtIn     = builtIn;
        this.bgDark      = bgDark;
        this.bgPanel     = bgPanel;
        this.bgSidebar   = bgSidebar;
        this.bgHeader    = bgHeader;
        this.accent      = accent;
        this.accentHover = accentHover;
        this.danger      = danger;
        this.success     = success;
        this.warning     = warning;
        this.textPrimary = textPrimary;
        this.textMuted   = textMuted;
        this.borderColor = borderColor;
        this.tableAlt    = tableAlt;
        this.tableSel    = tableSel;
    }

    @Override
    public ThemeDefinition clone() {
        try { return (ThemeDefinition) super.clone(); }
        catch (CloneNotSupportedException e) { throw new RuntimeException(e); }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Built-in presets
    // ═══════════════════════════════════════════════════════════════════════════

    public static ThemeDefinition DARK() {
        return new ThemeDefinition("Dark", true,
            c(28,30,38),    c(36,39,50),    c(22,24,32),    c(20,22,30),
            c(99,132,255),  c(120,150,255),
            c(220,60,60),   c(60,190,100),  c(230,160,30),
            c(220,222,235), c(130,135,160),
            c(50,53,68),    c(40,43,56),    c(60,80,160));
    }

    public static ThemeDefinition LIGHT() {
        return new ThemeDefinition("Light", true,
            c(245,246,250), c(255,255,255), c(237,239,246), c(228,230,242),
            c(80,110,230),  c(60,90,210),
            c(200,50,50),   c(40,160,80),   c(200,130,20),
            c(30,32,50),    c(110,115,145),
            c(200,202,215), c(242,244,252), c(180,200,255));
    }

    public static ThemeDefinition MIDNIGHT() {
        return new ThemeDefinition("Midnight Blue", true,
            c(8,12,30),     c(14,20,48),    c(6,9,24),      c(5,8,20),
            c(80,200,255),  c(110,220,255),
            c(255,80,80),   c(60,220,130),  c(255,190,50),
            c(210,230,255), c(100,130,180),
            c(30,45,90),    c(12,18,42),    c(30,80,150));
    }

    public static ThemeDefinition FOREST() {
        return new ThemeDefinition("Forest", true,
            c(16,26,20),    c(22,36,28),    c(12,20,16),    c(10,18,14),
            c(80,200,120),  c(100,220,140),
            c(220,80,60),   c(60,210,100),  c(200,180,40),
            c(210,230,215), c(110,150,120),
            c(40,65,48),    c(20,32,24),    c(40,90,55));
    }

    public static ThemeDefinition CRIMSON() {
        return new ThemeDefinition("Crimson", true,
            c(24,14,14),    c(36,20,22),    c(18,10,12),    c(16,8,10),
            c(220,100,100), c(240,120,120),
            c(255,60,60),   c(80,190,100),  c(230,160,40),
            c(235,215,215), c(160,120,120),
            c(70,35,38),    c(30,17,19),    c(90,30,35));
    }

    public static ThemeDefinition SUNSET() {
        return new ThemeDefinition("Sunset", true,
            c(28,18,24),    c(42,26,34),    c(22,14,18),    c(18,12,16),
            c(255,140,80),  c(255,160,100),
            c(220,60,80),   c(80,200,140),  c(255,200,60),
            c(255,235,220), c(170,130,110),
            c(80,45,55),    c(36,22,28),    c(100,50,60));
    }

    public static ThemeDefinition OCEAN() {
        return new ThemeDefinition("Ocean", true,
            c(10,20,35),    c(16,30,52),    c(8,16,28),     c(6,14,24),
            c(40,190,220),  c(60,210,240),
            c(240,80,80),   c(50,210,130),  c(230,180,40),
            c(200,230,245), c(90,140,170),
            c(25,50,80),    c(12,24,44),    c(20,70,120));
    }

    // ── Accent-only variants (Dark base + swapped accent) ───────────────────────
    public static ThemeDefinition ACCENT_PURPLE() {
        ThemeDefinition t = DARK().clone();
        t.name = "Purple Accent"; t.builtIn = true;
        t.accent = c(170,100,255); t.accentHover = c(190,130,255); t.tableSel = c(90,40,170);
        return t;
    }
    public static ThemeDefinition ACCENT_TEAL() {
        ThemeDefinition t = DARK().clone();
        t.name = "Teal Accent"; t.builtIn = true;
        t.accent = c(40,210,190); t.accentHover = c(70,230,210); t.tableSel = c(20,90,80);
        return t;
    }
    public static ThemeDefinition ACCENT_ROSE() {
        ThemeDefinition t = DARK().clone();
        t.name = "Rose Accent"; t.builtIn = true;
        t.accent = c(255,100,150); t.accentHover = c(255,130,170); t.tableSel = c(120,30,60);
        return t;
    }
    public static ThemeDefinition ACCENT_AMBER() {
        ThemeDefinition t = DARK().clone();
        t.name = "Amber Accent"; t.builtIn = true;
        t.accent = c(255,185,40); t.accentHover = c(255,205,70); t.tableSel = c(110,70,10);
        return t;
    }

    // ── All built-ins in order ──────────────────────────────────────────────────
    public static ThemeDefinition[] builtIns() {
        return new ThemeDefinition[]{
            DARK(), LIGHT(), MIDNIGHT(), FOREST(), CRIMSON(), SUNSET(), OCEAN(),
            ACCENT_PURPLE(), ACCENT_TEAL(), ACCENT_ROSE(), ACCENT_AMBER()
        };
    }

    private static Color c(int r, int g, int b) { return new Color(r,g,b); }
}