package gui;

import model.Carta;
import model.Jogador;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DeckPanel extends JPanel {

    // Grade fixa de 5 linhas x 4 colunas (20 cartas), em vez do antigo
    // FlowLayout, que ia empilhando as cartas soltas lado a lado e
    // quebrando linha de forma imprevisível conforme a largura da janela.
    // Com a grade fixa, o deck aparece sempre organizado no mesmo formato
    // 4x5, célula por célula.
    private static final int COLUNAS = 4;
    private static final int LINHAS = 5;

    private final MainFrame frame;
    private final JPanel grade = new JPanel(new GridLayout(LINHAS, COLUNAS, 14, 14));
    private final JLabel contagemLabel = Theme.label("", Theme.FONT_NORMAL, Theme.TEXT_MUTED);

    public DeckPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        Theme.aplicarFundo(this, Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JLabel titulo = Theme.label("MEU DECK", Theme.FONT_TITLE, Theme.ACCENT);
        JButton voltar = Theme.button("Voltar");
        voltar.addActionListener(e -> frame.mostrarMenu());

        topo.add(titulo, BorderLayout.WEST);
        topo.add(voltar, BorderLayout.EAST);

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);
        cabecalho.add(topo);
        cabecalho.add(Box.createVerticalStrut(6));
        cabecalho.add(contagemLabel);
        cabecalho.add(Box.createVerticalStrut(12));

        grade.setOpaque(false);
        JScrollPane scroll = new JScrollPane(grade);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(cabecalho, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void atualizar(Jogador jogador) {
        grade.removeAll();

        ArrayList<Carta> cartas = jogador.getDeck().getCartas();
        contagemLabel.setText(cartas.size() + " carta(s) no deck");

        for (Carta carta : cartas) {
            grade.add(new CardComponent(carta));
        }

        grade.revalidate();
        grade.repaint();
    }
}
