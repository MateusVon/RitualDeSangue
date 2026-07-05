package gui;

import model.Carta;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Representação visual de uma {@link Carta}: nome, ataque, vida e custo
 * de sangue. Usado tanto na mão do jogador quanto nos slots de campo e
 * na listagem do deck.
 */
public class CardComponent extends JPanel {

    private static final Dimension TAMANHO = new Dimension(118, 148);

    private final Carta carta;
    private final Border bordaNormal = BorderFactory.createLineBorder(Theme.BORDER, 1);
    private final Border bordaSelecionada = BorderFactory.createLineBorder(Theme.ACCENT, 2);

    public CardComponent(Carta carta) {
        this.carta = carta;

        setPreferredSize(TAMANHO);
        setMinimumSize(TAMANHO);
        setMaximumSize(TAMANHO);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(bordaNormal,
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        montarConteudo();
    }

    private void montarConteudo() {
        JLabel nome = Theme.label(carta.getNome(), Theme.FONT_BOLD, Theme.TEXT);
        nome.setAlignmentX(CENTER_ALIGNMENT);
        nome.setHorizontalAlignment(SwingConstants.CENTER);

        add(nome);
        add(Box.createVerticalStrut(6));
        add(centralizado(linha("ATK " + carta.getAtaque(), Theme.ATAQUE)));
        add(Box.createVerticalStrut(2));
        add(centralizado(linha("HP " + carta.getVida() + "/" + carta.getVidaMaxima(), Theme.VIDA)));
        add(Box.createVerticalStrut(2));
        add(centralizado(linha("Custo " + carta.getCustoSangue(), Theme.SANGUE)));
        add(Box.createVerticalGlue());

        if (carta.getTipo() != null || carta.getRaridade() != null) {
            String rodape = String.valueOf(carta.getTipo()) + " · " + carta.getRaridade();
            JLabel tipoLabel = Theme.label(rodape, Theme.FONT_SMALL, Theme.TEXT_MUTED);
            tipoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(centralizado(tipoLabel));
        }

        setToolTipText("<html><b>" + carta.getNome() + "</b><br>" + carta.getDescricao() + "</html>");
    }

    private JLabel linha(String texto, Color cor) {
        return Theme.label(texto, Theme.FONT_SMALL, cor);
    }

    private JPanel centralizado(JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.add(comp);
        p.setAlignmentX(CENTER_ALIGNMENT);
        return p;
    }

    public void setSelecionada(boolean selecionada) {
        setBorder(BorderFactory.createCompoundBorder(selecionada ? bordaSelecionada : bordaNormal,
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
    }

    public Carta getCarta() {
        return carta;
    }
}
