package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Barra de status horizontal (usada para Vida e Sangue no
 * {@link GameBoardPanel}): um rótulo, uma barra colorida cujo
 * preenchimento é proporcional a "atual / máximo" e o valor numérico
 * sobreposto ao centro. A barra encolhe conforme o valor atual cai (vida
 * perdida, sangue gasto) e volta a crescer quando o valor sobe.
 */
public class StatusBar extends JPanel {

  private final Color corPreenchimento;
  private final String rotulo;

  private int atual;
  private int maximo = 1;

  public StatusBar(String rotulo, Color corPreenchimento) {
    this.rotulo = rotulo;
    this.corPreenchimento = corPreenchimento;
    setOpaque(false);
    setPreferredSize(new Dimension(190, 20));
    setMinimumSize(new Dimension(90, 20));
    setMaximumSize(new Dimension(260, 20));
  }

  /**
   * Atualiza os valores exibidos e repinta a barra. {@code maximo} é
   * sempre tratado como pelo menos 1, para nunca dividir por zero
   * (ex.: sangue no primeiríssimo instante da partida).
   */
  public void atualizar(int atual, int maximo) {
    this.atual = Math.max(0, atual);
    this.maximo = Math.max(1, maximo);
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int largura = getWidth();
    int altura = getHeight();
    int raio = altura;

    // Fundo (trilho) da barra.
    g2.setColor(Theme.SLOT_BG);
    g2.fillRoundRect(0, 0, largura, altura, raio, raio);

    // Preenchimento proporcional ao valor atual.
    double proporcao = Math.min(1.0, (double) atual / (double) maximo);
    int larguraPreenchida = (int) Math.round(largura * proporcao);
    if (larguraPreenchida > 0) {
      g2.setColor(corPreenchimento);
      g2.fillRoundRect(0, 0, larguraPreenchida, altura, raio, raio);
    }

    // Borda.
    g2.setColor(Theme.BORDER);
    g2.setStroke(new BasicStroke(1.2f));
    g2.drawRoundRect(1, 1, largura - 3, altura - 3, raio, raio);

    // Texto centralizado: "Vida  35/50".
    String texto = rotulo + "  " + atual + "/" + maximo;
    g2.setFont(Theme.FONT_SMALL);
    g2.setColor(Theme.TEXT);
    FontMetrics fm = g2.getFontMetrics();
    int textoX = (largura - fm.stringWidth(texto)) / 2;
    int textoY = (altura - fm.getHeight()) / 2 + fm.getAscent();
    g2.drawString(texto, textoX, textoY);

    g2.dispose();
  }
}
