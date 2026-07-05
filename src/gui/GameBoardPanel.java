package gui;

import model.Carta;
import model.Jogador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.Consumer;

public class GameBoardPanel extends JPanel {

    private final MainFrame frame;
    private GameController controller;

    private int cartaSelecionada = -1;

    // Trava única e central da tela: enquanto uma jogada está sendo
    // processada em background (SwingWorker), nenhum clique — nem nos
    // botões, nem nos slots do campo, nem nas cartas da mão — deve
    // disparar uma nova ação. Antes, só os botões eram desabilitados,
    // deixando os cliques no campo/mão "soltos" (descentralizados) e
    // sujeitos a corrida de eventos.
    private boolean processando = false;

    // Cabeçalho
    private final JLabel turnoLabel = Theme.label("", Theme.FONT_SUBTITLE, Theme.TEXT);
    private final JLabel jogadorInfoLabel = Theme.label("", Theme.FONT_NORMAL, Theme.TEXT);
    private final JLabel maquinaInfoLabel = Theme.label("", Theme.FONT_NORMAL, Theme.TEXT);

    // Áreas dinâmicas
    private final JPanel campoMaquinaPanel = new JPanel(new GridLayout(1, 4, 10, 10));
    private final JPanel campoJogadorPanel = new JPanel(new GridLayout(1, 4, 10, 10));
    private final JPanel maoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private final JLabel instrucaoLabel = Theme.label(" ", Theme.FONT_SMALL, Theme.TEXT_MUTED);

    private final JTextArea logArea = new JTextArea();

    private final JButton btnPassarTurno = Theme.accentButton("Passar turno");
    private final JButton btnSalvarSair = Theme.button("Salvar e sair");

    private final Consumer<String> ouvinteLog = this::adicionarLog;

    public GameBoardPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout(0, 12));
        Theme.aplicarFundo(this, Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(construirCabecalho(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, construirAreaDeJogo(), construirAreaDeLog());
        split.setResizeWeight(0.72);
        split.setBorder(null);
        split.setOpaque(false);
        split.setDividerSize(8);
        add(split, BorderLayout.CENTER);

        LogBus.assinar(ouvinteLog);
    }

    // ================= Construção da UI =================

    private JPanel construirCabecalho() {
        JPanel painel = new JPanel(new GridLayout(1, 3));
        painel.setOpaque(false);

        turnoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jogadorInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        maquinaInfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        painel.add(jogadorInfoLabel);
        painel.add(turnoLabel);
        painel.add(maquinaInfoLabel);
        return painel;
    }

    private JPanel construirAreaDeJogo() {
        JPanel area = new JPanel();
        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));
        area.setOpaque(false);

        area.add(secaoTitulo("Campo da máquina"));
        campoMaquinaPanel.setOpaque(false);
        area.add(envolverAltura(campoMaquinaPanel));

        area.add(Box.createVerticalStrut(16));

        area.add(secaoTitulo("Seu campo"));
        campoJogadorPanel.setOpaque(false);
        area.add(envolverAltura(campoJogadorPanel));

        area.add(Box.createVerticalStrut(10));
        instrucaoLabel.setAlignmentX(LEFT_ALIGNMENT);
        area.add(instrucaoLabel);

        area.add(Box.createVerticalStrut(12));
        area.add(secaoTitulo("Sua mão"));

        maoPanel.setOpaque(false);
        JScrollPane scrollMao = new JScrollPane(maoPanel);
        scrollMao.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        scrollMao.setPreferredSize(new Dimension(100, 178));
        scrollMao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 178));
        scrollMao.getViewport().setOpaque(false);
        scrollMao.setOpaque(false);
        scrollMao.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollMao.setAlignmentX(LEFT_ALIGNMENT);
        area.add(scrollMao);

        area.add(Box.createVerticalStrut(14));
        area.add(construirControles());

        return area;
    }

    private JPanel envolverAltura(JPanel painelSlots) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(10, 160));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        wrapper.add(painelSlots, BorderLayout.CENTER);
        wrapper.setAlignmentX(LEFT_ALIGNMENT);
        return wrapper;
    }

    private JLabel secaoTitulo(String texto) {
        JLabel l = Theme.label(texto, Theme.FONT_BOLD, Theme.TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel construirControles() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);

        btnPassarTurno.addActionListener(e -> passarTurno());
        btnSalvarSair.addActionListener(e -> salvarESair());

        p.add(btnPassarTurno);
        p.add(btnSalvarSair);
        return p;
    }

    private JPanel construirAreaDeLog() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setOpaque(false);

        JLabel titulo = Theme.label("Registro da partida", Theme.FONT_BOLD, Theme.TEXT_MUTED);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(Theme.FONT_MONO);
        logArea.setBackground(Theme.SLOT_BG);
        logArea.setForeground(Theme.TEXT);
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // ================= Ciclo de vida da partida =================

    public void novaPartidaIniciada(GameController controller) {
        this.controller = controller;
        this.cartaSelecionada = -1;
        logArea.setText("");
        setControlesHabilitados(true);
        atualizarTudo();
    }

    private void adicionarLog(String linha) {
        if (linha == null || linha.isBlank()) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            logArea.append(linha + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // ================= Atualização visual =================

    private void atualizarTudo() {
        Jogador jogador = controller.getJogador();
        Jogador maquina = controller.getMaquina();

        turnoLabel.setText("Turno " + controller.getPartida().getTurno());

        jogadorInfoLabel.setText("<html><b>" + jogador.getNome() + "</b> &nbsp; "
                + "Vida " + jogador.getVida() + " &nbsp; Sangue " + jogador.getSangue()
                + " &nbsp; Pontos " + jogador.getPontuacao() + "</html>");

        maquinaInfoLabel.setText("<html><b>" + maquina.getNome() + "</b> &nbsp; "
                + "Vida " + maquina.getVida() + " &nbsp; Sangue " + maquina.getSangue() + "</html>");

        renderCampo(campoMaquinaPanel, maquina.getCampo(), false);
        renderCampo(campoJogadorPanel, jogador.getCampo(), true);
        renderMao();

        instrucaoLabel.setText(cartaSelecionada == -1
                ? "Selecione uma carta da sua mão e depois clique em um slot vazio do seu campo para jogá-la."
                : "Carta selecionada. Clique em um slot vazio do seu campo para jogá-la (ou clique nela de novo para cancelar).");
    }

    private void renderCampo(JPanel painel, Carta[] campo, boolean interativo) {
        painel.removeAll();

        for (int i = 0; i < campo.length; i++) {
            final int pos = i;
            Carta carta = campo[i];

            JPanel slot = new JPanel(new BorderLayout());
            slot.setBackground(Theme.SLOT_BG);
            slot.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

            if (carta == null) {
                JLabel vazio = Theme.label("vazio", Theme.FONT_SMALL, Theme.TEXT_MUTED);
                vazio.setHorizontalAlignment(SwingConstants.CENTER);
                slot.add(vazio, BorderLayout.CENTER);

                if (interativo) {
                    slot.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    slot.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            jogarNaPosicao(pos);
                        }
                    });
                }
            } else {
                slot.add(new CardComponent(carta), BorderLayout.CENTER);
            }

            painel.add(slot);
        }

        painel.revalidate();
        painel.repaint();
    }

    private void renderMao() {
        maoPanel.removeAll();

        ArrayList<Carta> mao = controller.getJogador().getMao();

        for (int i = 0; i < mao.size(); i++) {
            final int idx = i;
            CardComponent cc = new CardComponent(mao.get(i));
            cc.setSelecionada(idx == cartaSelecionada);
            cc.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cc.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selecionarCarta(idx);
                }
            });
            maoPanel.add(cc);
        }

        if (mao.isEmpty()) {
            maoPanel.add(Theme.label("(nenhuma carta na mão)", Theme.FONT_SMALL, Theme.TEXT_MUTED));
        }

        maoPanel.revalidate();
        maoPanel.repaint();
    }

    private void selecionarCarta(int idx) {
        if (processando) {
            return;
        }
        cartaSelecionada = (cartaSelecionada == idx) ? -1 : idx;
        atualizarTudo();
    }

    // ================= Ações =================

    private void jogarNaPosicao(int pos) {
        if (processando || cartaSelecionada == -1) {
            return;
        }

        final int idxCarta = cartaSelecionada;
        cartaSelecionada = -1;
        setControlesHabilitados(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return controller.jogarCarta(idxCarta, pos);
            }

            @Override
            protected void done() {
                setControlesHabilitados(true);
                atualizarTudo();
                verificarFimDePartida();
            }
        };
        worker.execute();
    }

    private void passarTurno() {
        if (processando) {
            return;
        }
        setControlesHabilitados(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                controller.passarTurno();
                return null;
            }

            @Override
            protected void done() {
                setControlesHabilitados(true);
                atualizarTudo();
                verificarFimDePartida();
            }
        };
        worker.execute();
    }

    private void salvarESair() {
        if (processando) {
            return;
        }
        setControlesHabilitados(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                controller.salvar();
                return null;
            }

            @Override
            protected void done() {
                setControlesHabilitados(true);
                frame.mostrarMenu();
            }
        };
        worker.execute();
    }

    private void verificarFimDePartida() {
        if (!controller.getPartida().acabou()) {
            return;
        }

        Jogador jogador = controller.getJogador();
        boolean venceu = !jogador.perdeu();

        String mensagem = (venceu ? "Você venceu!" : "Você perdeu!")
                + "\nPontuação total: " + jogador.getPontuacao()
                + "\nPartidas: " + jogador.getTotalPartidas() + "   Vitórias: " + jogador.getTotalVitorias();

        Object[] opcoes = {"Jogar próxima partida", "Voltar ao menu"};
        int escolha = JOptionPane.showOptionDialog(this, mensagem, "Fim de partida",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, opcoes, opcoes[0]);

        if (escolha == 0) {
            setControlesHabilitados(false);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    controller.novaRodadaAposFim();
                    return null;
                }

                @Override
                protected void done() {
                    setControlesHabilitados(true);
                    logArea.setText("");
                    atualizarTudo();
                }
            };
            worker.execute();
        } else {
            frame.mostrarMenu();
        }
    }

    private void setControlesHabilitados(boolean habilitado) {
        processando = !habilitado;
        btnPassarTurno.setEnabled(habilitado);
        btnSalvarSair.setEnabled(habilitado);
    }
}