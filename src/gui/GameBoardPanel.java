package gui;

import model.Carta;
import model.Jogador;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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
  // disparar uma nova ação.
  private boolean processando = false;

  // Cabeçalho: turno, cronômetro e status (nome + barras de vida/sangue)
  // de cada lado.
  private final JLabel turnoLabel = Theme.label("", Theme.FONT_SUBTITLE, Theme.TEXT);
  private final JLabel tempoLabel = Theme.label("00:00", Theme.FONT_BOLD, Theme.TEXT_MUTED);

  private final JLabel jogadorNomeLabel = Theme.label("", Theme.FONT_BOLD, Theme.TEXT);
  private final JLabel jogadorPontosLabel = Theme.label("", Theme.FONT_SMALL, Theme.PONTOS);
  private final StatusBar vidaJogadorBar = new StatusBar("Vida", Theme.BARRA_VIDA);
  private final StatusBar sangueJogadorBar = new StatusBar("Sangue", Theme.BARRA_SANGUE);

  private final JLabel maquinaNomeLabel = Theme.label("", Theme.FONT_BOLD, Theme.TEXT);
  private final StatusBar vidaMaquinaBar = new StatusBar("Vida", Theme.BARRA_VIDA);
  private final StatusBar sangueMaquinaBar = new StatusBar("Sangue", Theme.BARRA_SANGUE);

  // O sangue não tem um teto fixo (cresce a cada turno), então a barra usa
  // como "máximo" o maior valor de sangue já visto na partida atual: ela
  // se enche por completo no pico acumulado e esvazia visualmente
  // conforme o sangue é gasto, voltando a encher quando é reposto.
  private int maxSangueJogador = Jogador.SANGUE_INICIAL;
  private int maxSangueMaquina = Jogador.SANGUE_INICIAL;

  // Cronômetro simples: mostra o tempo decorrido desde que a tela de
  // jogo foi aberta (nova partida ou partida continuada).
  private javax.swing.Timer cronometro;
  private long inicioCronometro;

  // Áreas dinâmicas
  private final JPanel campoMaquinaPanel = new JPanel(new GridLayout(1, 4, 10, 10));
  private final JPanel campoJogadorPanel = new JPanel(new GridLayout(1, 4, 10, 10));
  private final JPanel maoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
  private final JLabel instrucaoLabel = Theme.label(" ", Theme.FONT_SMALL, Theme.TEXT_MUTED);

  // Registro da partida: usa um JTextPane (em vez de um JTextArea simples)
  // para poder colorir e destacar cada tipo de evento — turno, jogada,
  // ataque, morte, resultado final — em vez de despejar tudo como um
  // bloco único de texto monocromático.
  private final JTextPane logArea = new JTextPane();
  private Style estiloNormal;
  private Style estiloMudo;
  private Style estiloCabecalho;
  private Style estiloAtaque;
  private Style estiloMorte;
  private Style estiloJogada;
  private Style estiloDestaque;

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
    JPanel painel = new JPanel(new BorderLayout(0, 10));
    painel.setOpaque(false);

    JPanel linhaTopo = new JPanel(new BorderLayout());
    linhaTopo.setOpaque(false);

    turnoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    linhaTopo.add(turnoLabel, BorderLayout.CENTER);

    JPanel cronometroPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
    cronometroPanel.setOpaque(false);
    cronometroPanel.add(Theme.label("Tempo", Theme.FONT_NORMAL, Theme.TEXT_MUTED));
    cronometroPanel.add(tempoLabel);
    linhaTopo.add(cronometroPanel, BorderLayout.EAST);

    painel.add(linhaTopo, BorderLayout.NORTH);

    JPanel status = new JPanel(new GridLayout(1, 2, 24, 0));
    status.setOpaque(false);
    status.add(construirStatus(jogadorNomeLabel, vidaJogadorBar, sangueJogadorBar, jogadorPontosLabel,
        SwingConstants.LEFT));
    status.add(construirStatus(maquinaNomeLabel, vidaMaquinaBar, sangueMaquinaBar, null, SwingConstants.RIGHT));
    painel.add(status, BorderLayout.CENTER);

    return painel;
  }

  private JPanel construirStatus(JLabel nomeLabel, StatusBar vidaBar, StatusBar sangueBar, JLabel pontosLabel,
      int alinhamento) {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setOpaque(false);

    float alinhamentoEixoX = (alinhamento == SwingConstants.RIGHT) ? RIGHT_ALIGNMENT : LEFT_ALIGNMENT;

    nomeLabel.setHorizontalAlignment(alinhamento);
    nomeLabel.setAlignmentX(alinhamentoEixoX);
    vidaBar.setAlignmentX(alinhamentoEixoX);
    sangueBar.setAlignmentX(alinhamentoEixoX);

    p.add(nomeLabel);
    p.add(Box.createVerticalStrut(4));
    p.add(vidaBar);
    p.add(Box.createVerticalStrut(3));
    p.add(sangueBar);

    if (pontosLabel != null) {
      pontosLabel.setHorizontalAlignment(alinhamento);
      pontosLabel.setAlignmentX(alinhamentoEixoX);
      p.add(Box.createVerticalStrut(3));
      p.add(pontosLabel);
    }

    return p;
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
    logArea.setBackground(Theme.SLOT_BG);
    logArea.setForeground(Theme.TEXT);
    logArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    criarEstilosDoRegistro();

    JScrollPane scroll = new JScrollPane(logArea);
    scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

    painel.add(titulo, BorderLayout.NORTH);
    painel.add(scroll, BorderLayout.CENTER);
    return painel;
  }

  /**
   * Define, uma única vez, os estilos de texto usados para diferenciar
   * visualmente cada tipo de linha do registro da partida.
   */
  private void criarEstilosDoRegistro() {
    StyledDocument doc = logArea.getStyledDocument();

    Style base = doc.addStyle("base", null);
    StyleConstants.setFontFamily(base, Theme.FONT_MONO.getFamily());
    StyleConstants.setFontSize(base, Theme.FONT_MONO.getSize());
    StyleConstants.setForeground(base, Theme.TEXT);

    estiloNormal = doc.addStyle("normal", base);

    estiloMudo = doc.addStyle("mudo", base);
    StyleConstants.setForeground(estiloMudo, Theme.TEXT_MUTED);
    StyleConstants.setItalic(estiloMudo, true);

    estiloCabecalho = doc.addStyle("cabecalho", base);
    StyleConstants.setForeground(estiloCabecalho, Theme.ACCENT);
    StyleConstants.setBold(estiloCabecalho, true);

    estiloAtaque = doc.addStyle("ataque", base);
    StyleConstants.setForeground(estiloAtaque, Theme.ATAQUE);

    estiloMorte = doc.addStyle("morte", base);
    StyleConstants.setForeground(estiloMorte, Theme.VIDA);
    StyleConstants.setBold(estiloMorte, true);

    estiloJogada = doc.addStyle("jogada", base);
    StyleConstants.setForeground(estiloJogada, Theme.TEXT);

    estiloDestaque = doc.addStyle("destaque", base);
    StyleConstants.setForeground(estiloDestaque, Theme.PONTOS);
    StyleConstants.setBold(estiloDestaque, true);
  }

  // ================= Ciclo de vida da partida =================

  public void novaPartidaIniciada(GameController controller) {
    this.controller = controller;
    this.cartaSelecionada = -1;
    logArea.setText("");
    campoMaquinaPanel.removeAll();
    campoJogadorPanel.removeAll();
    maoPanel.removeAll();

    maxSangueJogador = Jogador.SANGUE_INICIAL;
    maxSangueMaquina = Jogador.SANGUE_INICIAL;

    setControlesHabilitados(true);
    atualizarTudo();
    iniciarCronometro();
  }

  private void adicionarLog(String linha) {
    if (linha == null || linha.isBlank()) {
      return;
    }
    SwingUtilities.invokeLater(() -> publicarNoRegistro(linha.trim()));
  }

  /**
   * Formata e insere uma linha vinda do motor de jogo (via LogBus) no
   * registro da partida, escolhendo o estilo visual de acordo com o tipo
   * de evento reconhecido no texto. Isso substitui o antigo despejo bruto
   * de texto por um registro dividido em blocos (um por turno) e com
   * destaque de cor para jogadas, ataques, mortes e o resultado final.
   */
  private void publicarNoRegistro(String linha) {
    StyledDocument doc = logArea.getStyledDocument();

    String textoFormatado;
    Style estilo;

    if (linha.contains("===== TURNO") || linha.contains("===== INÍCIO DA PARTIDA")) {
      String titulo = linha.replace("=", "").trim();
      textoFormatado = (doc.getLength() > 0 ? "\n" : "") + "-- " + titulo + " --\n";
      estilo = estiloCabecalho;
    } else if (linha.startsWith("Sangue acumulado")) {
      textoFormatado = "   " + linha + "\n";
      estilo = estiloMudo;
    } else if (linha.contains("morreu!")) {
      textoFormatado = "   [X] " + linha + "\n";
      estilo = estiloMorte;
    } else if (linha.contains(" causou ") || linha.contains(" atacou ")) {
      textoFormatado = "   > " + linha + "\n";
      estilo = estiloAtaque;
    } else if (linha.contains("jogou ")) {
      textoFormatado = "   * " + linha + "\n";
      estilo = estiloJogada;
    } else if (linha.contains("venceu!") || linha.contains("perdeu...") || linha.contains("pontos! Pontuação")) {
      textoFormatado = "\n" + linha + "\n";
      estilo = estiloDestaque;
    } else {
      textoFormatado = "   " + linha + "\n";
      estilo = estiloNormal;
    }

    try {
      doc.insertString(doc.getLength(), textoFormatado, estilo);
    } catch (BadLocationException ex) {
      // A posição inserida é sempre o fim do documento, portanto válida;
      // isso não deve ocorrer na prática.
    }
    logArea.setCaretPosition(doc.getLength());
  }

  public void reiniciarParaNovoJogo(model.Deck deckSelecionado) {
    if (this.controller != null) {
      // 1. Reseta o jogador atual para os status iniciais (limpa mão, campo,
      // vida, sangue)
      model.Jogador jogadorActual = this.controller.getJogador();
      jogadorActual.resetParaNovaPartida();

      // Se o deck selecionado veio customizado da tela de decks, atribui ele
      // ao jogador
      if (deckSelecionado != null) {
        jogadorActual.setDeck(deckSelecionado);
      }

      // 2. Força o controller a instanciar uma nova máquina e uma nova
      // partida zerada
      this.controller.iniciarNovaPartida();

      // 3. Reseta os parâmetros visuais do painel
      this.cartaSelecionada = -1;
      this.logArea.setText("");
      this.maxSangueJogador = Jogador.SANGUE_INICIAL;
      this.maxSangueMaquina = Jogador.SANGUE_INICIAL;
      setControlesHabilitados(true);

      // 4. Redesenha a tela limpa com os novos status
      atualizarTudo();
      iniciarCronometro();
    }
  }

  // ================= Cronômetro =================

  private void iniciarCronometro() {
    pararCronometro();
    inicioCronometro = System.currentTimeMillis();
    tempoLabel.setText("00:00");
    cronometro = new javax.swing.Timer(1000, e -> atualizarTempoDecorrido());
    cronometro.start();
  }

  private void pararCronometro() {
    if (cronometro != null) {
      cronometro.stop();
      cronometro = null;
    }
  }

  private void atualizarTempoDecorrido() {
    long decorridoSegundos = (System.currentTimeMillis() - inicioCronometro) / 1000;
    long minutos = decorridoSegundos / 60;
    long segundos = decorridoSegundos % 60;
    tempoLabel.setText(String.format("%02d:%02d", minutos, segundos));
  }

  // ================= Atualização visual =================

  private void atualizarTudo() {
    Jogador jogador = controller.getJogador();
    Jogador maquina = controller.getMaquina();

    turnoLabel.setText("Turno " + controller.getPartida().getTurno());

    jogadorNomeLabel.setText(jogador.getNome());
    jogadorPontosLabel.setText("Pontos " + jogador.getPontuacao());
    maquinaNomeLabel.setText(maquina.getNome());

    // O "máximo" da barra de sangue acompanha o maior valor já acumulado
    // na partida, para que a barra encolha visivelmente ao gastar sangue
    // e volte a crescer conforme ele é reposto.
    maxSangueJogador = Math.max(maxSangueJogador, jogador.getSangue());
    maxSangueMaquina = Math.max(maxSangueMaquina, maquina.getSangue());

    vidaJogadorBar.atualizar(jogador.getVida(), Jogador.VIDA_INICIAL);
    sangueJogadorBar.atualizar(jogador.getSangue(), maxSangueJogador);
    vidaMaquinaBar.atualizar(maquina.getVida(), Jogador.VIDA_INICIAL);
    sangueMaquinaBar.atualizar(maquina.getSangue(), maxSangueMaquina);

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
        pararCronometro();
        frame.mostrarMenu();
      }
    };
    worker.execute();
  }

  private void verificarFimDePartida() {
    if (!controller.getPartida().acabou()) {
      return;
    }

    pararCronometro();

    Jogador jogador = controller.getJogador();
    boolean venceu = !jogador.perdeu();

    String mensagem = (venceu ? "Você venceu!" : "Você perdeu!")
        + "\nPontuação total: " + jogador.getPontuacao()
        + "\nPartidas: " + jogador.getTotalPartidas() + "   Vitórias: " + jogador.getTotalVitorias();

    Object[] opcoes = { "Jogar próxima partida", "Voltar ao menu" };
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
          maxSangueJogador = Jogador.SANGUE_INICIAL;
          maxSangueMaquina = Jogador.SANGUE_INICIAL;
          atualizarTudo();
          iniciarCronometro();
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
