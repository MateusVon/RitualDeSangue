package gui;

import model.Jogador;

import javax.swing.*;
import java.awt.*;

public class EstatisticasPanel extends JPanel {

    private final JLabel partidasLabel = Theme.label("", Theme.FONT_NORMAL, Theme.TEXT);
    private final JLabel vitoriasLabel = Theme.label("", Theme.FONT_NORMAL, Theme.TEXT);
    private final JLabel pontuacaoLabel = Theme.label("", Theme.FONT_NORMAL, Theme.PONTOS);
    private final JLabel aproveitamentoLabel = Theme.label("", Theme.FONT_NORMAL, Theme.TEXT_MUTED);

    public EstatisticasPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        Theme.aplicarFundo(this, Theme.BG);

        JPanel caixa = new JPanel();
        caixa.setLayout(new BoxLayout(caixa, BoxLayout.Y_AXIS));
        Theme.aplicarFundo(caixa, Theme.PANEL_BG);
        caixa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(36, 56, 36, 56)));

        JLabel titulo = Theme.label("ESTATÍSTICAS", Theme.FONT_TITLE, Theme.ACCENT);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        for (JLabel l : new JLabel[]{partidasLabel, vitoriasLabel, pontuacaoLabel, aproveitamentoLabel}) {
            l.setAlignmentX(CENTER_ALIGNMENT);
        }

        JButton voltar = Theme.button("Voltar");
        voltar.setAlignmentX(CENTER_ALIGNMENT);
        voltar.addActionListener(e -> frame.mostrarMenu());

        caixa.add(titulo);
        caixa.add(Box.createVerticalStrut(24));
        caixa.add(partidasLabel);
        caixa.add(Box.createVerticalStrut(8));
        caixa.add(vitoriasLabel);
        caixa.add(Box.createVerticalStrut(8));
        caixa.add(pontuacaoLabel);
        caixa.add(Box.createVerticalStrut(8));
        caixa.add(aproveitamentoLabel);
        caixa.add(Box.createVerticalStrut(24));
        caixa.add(voltar);

        add(caixa);
    }

    public void atualizar(Jogador jogador) {
        partidasLabel.setText("Partidas jogadas: " + jogador.getTotalPartidas());
        vitoriasLabel.setText("Vitórias: " + jogador.getTotalVitorias());
        pontuacaoLabel.setText("Pontuação total: " + jogador.getPontuacao());

        int partidas = jogador.getTotalPartidas();
        if (partidas > 0) {
            double taxa = (100.0 * jogador.getTotalVitorias()) / partidas;
            aproveitamentoLabel.setText(String.format("Aproveitamento: %.0f%%", taxa));
        } else {
            aproveitamentoLabel.setText("Aproveitamento: —");
        }
    }
}
