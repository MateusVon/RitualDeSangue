package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Paleta e estilos centralizados da interface gráfica. Mantém o visual
 * consistente em todas as telas: fundo escuro neutro, um único tom de
 * destaque (vermelho, remetendo a "sangue") e tipografia simples, sem
 * elementos decorativos.
 */
public final class Theme {

    private Theme() {
    }

    public static final Color BG = new Color(0x1c, 0x1c, 0x1e);
    public static final Color PANEL_BG = new Color(0x25, 0x25, 0x28);
    public static final Color CARD_BG = new Color(0x2d, 0x2d, 0x31);
    public static final Color SLOT_BG = new Color(0x20, 0x20, 0x23);
    public static final Color BORDER = new Color(0x3c, 0x3c, 0x40);

    public static final Color TEXT = new Color(0xe8, 0xe8, 0xea);
    public static final Color TEXT_MUTED = new Color(0x9a, 0x9a, 0xa0);

    public static final Color ACCENT = new Color(0xb7, 0x1c, 0x1c);
    public static final Color ACCENT_HOVER = new Color(0xd3, 0x2f, 0x2f);

    public static final Color VIDA = new Color(0xef, 0x53, 0x50);
    public static final Color SANGUE = new Color(0xab, 0x47, 0xbc);
    public static final Color ATAQUE = new Color(0xff, 0xca, 0x28);
    public static final Color PONTOS = new Color(0x66, 0xbb, 0x6a);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);

    public static JButton button(String texto) {
        JButton b = new JButton(texto);
        estilizarBotao(b, PANEL_BG, TEXT, BORDER);
        return b;
    }

    public static JButton accentButton(String texto) {
        JButton b = new JButton(texto);
        estilizarBotao(b, ACCENT, Color.WHITE, ACCENT);
        return b;
    }

    private static void estilizarBotao(JButton b, Color bg, Color fg, Color borda) {
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(FONT_BOLD);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borda, 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    }

    public static JLabel label(String texto, Font fonte, Color cor) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte);
        l.setForeground(cor);
        return l;
    }

    public static void aplicarFundo(JComponent c, Color cor) {
        c.setOpaque(true);
        c.setBackground(cor);
    }

    /**
     * Monta um rótulo + campo de texto no padrão visual das telas de
     * autenticação. Centralizado aqui para não ficar duplicado em cada
     * tela que tem formulário (LoginPanel, CadastroPanel, ...).
     */
    public static JPanel campoComRotulo(String rotulo, JTextField campo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel l = label(rotulo, FONT_SMALL, TEXT_MUTED);
        l.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        campo.setMaximumSize(new Dimension(280, 34));
        campo.setPreferredSize(new Dimension(280, 34));
        campo.setBackground(SLOT_BG);
        campo.setForeground(TEXT);
        campo.setCaretColor(TEXT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        campo.setFont(FONT_NORMAL);
        campo.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(campo);
        return p;
    }
}