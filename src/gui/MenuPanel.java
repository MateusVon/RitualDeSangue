package gui;

import model.Jogador;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    private final MainFrame frame;
    private final JLabel nomeLabel = Theme.label("", Theme.FONT_SUBTITLE, Theme.TEXT);
    private final JLabel vidaLabel = Theme.label("", Theme.FONT_NORMAL, Theme.VIDA);
    private final JLabel pontosLabel = Theme.label("", Theme.FONT_NORMAL, Theme.PONTOS);

    private final JButton btnNovo;
    private final JButton btnContinuar;
    private final JButton btnDeck;
    private final JButton btnStats;
    private final JButton btnLogout;

    public MenuPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new GridBagLayout());
        Theme.aplicarFundo(this, Theme.BG);

        JPanel caixa = new JPanel();
        caixa.setLayout(new BoxLayout(caixa, BoxLayout.Y_AXIS));
        Theme.aplicarFundo(caixa, Theme.PANEL_BG);
        caixa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(36, 56, 36, 56)));

        JLabel titulo = Theme.label("RITUAL DE SANGUE", Theme.FONT_TITLE, Theme.ACCENT);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        nomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        vidaLabel.setAlignmentX(CENTER_ALIGNMENT);
        pontosLabel.setAlignmentX(CENTER_ALIGNMENT);

        caixa.add(titulo);
        caixa.add(Box.createVerticalStrut(18));
        caixa.add(nomeLabel);
        caixa.add(Box.createVerticalStrut(4));
        caixa.add(pontosLabel);
        caixa.add(Box.createVerticalStrut(28));

        btnNovo = Theme.accentButton("Novo jogo");
        btnContinuar = Theme.button("Continuar partida salva");
        btnDeck = Theme.button("Meu deck");
        btnStats = Theme.button("Estatísticas");
        btnLogout = Theme.button("Logout");

        for (JButton b : new JButton[]{btnNovo, btnContinuar, btnDeck, btnStats}) {
            b.setAlignmentX(CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(260, 40));
            caixa.add(b);
            caixa.add(Box.createVerticalStrut(10));
        }

        caixa.add(Box.createVerticalStrut(16));
        btnLogout.setAlignmentX(CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(260, 40));
        caixa.add(btnLogout);

        btnNovo.addActionListener(e -> iniciarNovoJogo());
        btnContinuar.addActionListener(e -> continuarJogo());
        btnDeck.addActionListener(e -> frame.mostrarDeck());
        btnStats.addActionListener(e -> frame.mostrarEstatisticas());
        btnLogout.addActionListener(e -> frame.logout());

        add(caixa);
    }

    public void atualizar(Jogador jogador) {
        nomeLabel.setText(jogador.getNome());
        vidaLabel.setText("Vida " + jogador.getVida());
        pontosLabel.setText("Pontuação: " + jogador.getPontuacao()
                + "   ·   Partidas: " + jogador.getTotalPartidas()
                + "   ·   Vitórias: " + jogador.getTotalVitorias());
    }

    private void iniciarNovoJogo() {
        setBotoesHabilitados(false);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                frame.getController().iniciarNovaPartida();
                return null;
            }

            @Override
            protected void done() {
                setBotoesHabilitados(true);
                frame.abrirTabuleiro();
            }
        };
        worker.execute();
    }

    private void continuarJogo() {
        setBotoesHabilitados(false);
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return frame.getController().carregarPartidaSalva();
            }

            @Override
            protected void done() {
                setBotoesHabilitados(true);
                try {
                    boolean carregou = get();
                    if (carregou) {
                        frame.abrirTabuleiro();
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Nenhum save encontrado. Iniciando novo jogo...",
                                "Continuar", JOptionPane.INFORMATION_MESSAGE);
                        iniciarNovoJogo();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Erro ao carregar partida salva.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void setBotoesHabilitados(boolean habilitado) {
        btnNovo.setEnabled(habilitado);
        btnContinuar.setEnabled(habilitado);
        btnDeck.setEnabled(habilitado);
        btnStats.setEnabled(habilitado);
        btnLogout.setEnabled(habilitado);
    }
}
