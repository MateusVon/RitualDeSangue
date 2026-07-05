package gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Paleta e estilos centralizados da interface gráfica. Mantém o visual
 * consistente em todas as telas: fundo escuro neutro, um único tom de
 * destaque (vermelho, remetendo a "sangue") e tipografia simples, sem
 * elementos decorativos.
 */
public final class Theme {

  private Theme() {
  }

  public static final Color BG = new Color(0x1a, 0x1a, 0x1c);
  public static final Color PANEL_BG = new Color(0x24, 0x24, 0x27);
  public static final Color CARD_BG = new Color(0x2d, 0x2d, 0x31);
  public static final Color SLOT_BG = new Color(0x1e, 0x1e, 0x21);
  public static final Color BORDER = new Color(0x3c, 0x3c, 0x42);

  // Botões secundários usam um fundo mais claro que os painéis, para se
  // destacar visualmente do cartão escuro atrás deles.
  public static final Color BUTTON_BG = new Color(0x3a, 0x3a, 0x40);
  public static final Color BUTTON_BG_HOVER = new Color(0x48, 0x48, 0x50);
  public static final Color BUTTON_BORDER = new Color(0x5a, 0x5a, 0x64);

  public static final Color TEXT = new Color(0xf2, 0xf2, 0xf4);
  public static final Color TEXT_MUTED = new Color(0xa8, 0xa8, 0xb0);

  public static final Color ACCENT = new Color(0xc4, 0x1e, 0x1e);
  public static final Color ACCENT_HOVER = new Color(0xe0, 0x33, 0x33);

  public static final Color VIDA = new Color(0xef, 0x53, 0x50);
  public static final Color SANGUE = new Color(0xab, 0x47, 0xbc);
  public static final Color ATAQUE = new Color(0xff, 0xca, 0x28);
  public static final Color PONTOS = new Color(0x66, 0xbb, 0x6a);

  public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 26);
  public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 16);
  public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
  public static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
  public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
  public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);

  private static final int RAIO_BOTAO = 10;

  public static JButton button(String texto) {
    return criarBotao(texto, BUTTON_BG, BUTTON_BG_HOVER, TEXT, BUTTON_BORDER);
  }

  public static JButton accentButton(String texto) {
    return criarBotao(texto, ACCENT, ACCENT_HOVER, Color.WHITE, ACCENT_HOVER);
  }

  /**
   * Cria um botão que se pinta inteiramente sozinho (fundo arredondado,
   * borda e texto), em vez de depender do "chrome" nativo do sistema
   * operacional. Isso é necessário porque, em alguns Windows
   * (principalmente com temas de alto contraste/acessibilidade do
   * sistema ativos), o Look and Feel nativo ignora completamente as
   * cores que definimos via setBackground/setForeground e pinta o botão
   * com as cores padrão do sistema — o que deixava os botões brancos e
   * o texto quase invisível. Assumindo o controle total da pintura,
   * garantimos a mesma aparência (e os mesmos cantos arredondados) em
   * qualquer sistema operacional.
   */
  private static JButton criarBotao(String texto, Color bg, Color bgHover, Color fg, Color borda) {
    JButton botao = new JButton(texto) {
      private boolean hover = false;

      {
        addMouseListener(new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent e) {
            hover = isEnabled();
            repaint();
          }

          @Override
          public void mouseExited(MouseEvent e) {
            hover = false;
            repaint();
          }
        });
      }

      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(!isEnabled() ? bg.darker() : (hover ? bgHover : bg));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), RAIO_BOTAO, RAIO_BOTAO);
        g2.dispose();
        super.paintComponent(g);
      }

      @Override
      protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borda);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, RAIO_BOTAO, RAIO_BOTAO);
        g2.dispose();
      }
    };

    botao.setUI(new BasicButtonUI());
    botao.setContentAreaFilled(false);
    botao.setOpaque(false);
    botao.setFocusPainted(false);
    botao.setBackground(bg);
    botao.setForeground(fg);
    botao.setFont(FONT_BOLD);
    botao.setHorizontalAlignment(SwingConstants.CENTER);
    botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    botao.setAlignmentX(JComponent.CENTER_ALIGNMENT);
    return botao;
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
    p.setAlignmentX(Component.CENTER_ALIGNMENT);
    p.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));

    JLabel l = label(rotulo, FONT_SMALL, TEXT_MUTED);
    l.setAlignmentX(Component.LEFT_ALIGNMENT);

    campo.setMaximumSize(new Dimension(280, 36));
    campo.setPreferredSize(new Dimension(280, 36));
    campo.setBackground(SLOT_BG);
    campo.setForeground(TEXT);
    campo.setCaretColor(TEXT);
    campo.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER, 1),
        BorderFactory.createEmptyBorder(4, 10, 4, 10)));
    campo.setFont(FONT_NORMAL);
    campo.setAlignmentX(Component.LEFT_ALIGNMENT);

    p.add(l);
    p.add(Box.createVerticalStrut(4));
    p.add(campo);
    return p;
  }
}