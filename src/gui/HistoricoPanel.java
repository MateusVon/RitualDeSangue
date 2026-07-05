package gui;

import model.Jogador;
import model.RegistroPartida;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HistoricoPanel extends JPanel {

  private final MainFrame frame;
  private final JPanel lista = new JPanel();
  private final JLabel statusLabel = Theme.label("", Theme.FONT_NORMAL, Theme.TEXT_MUTED);
  private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  public HistoricoPanel(MainFrame frame) {
    this.frame = frame;

    setLayout(new BorderLayout());
    Theme.aplicarFundo(this, Theme.BG);
    setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

    JPanel topo = new JPanel(new BorderLayout());
    topo.setOpaque(false);

    JLabel titulo = Theme.label("HISTÓRICO DE PARTIDAS", Theme.FONT_TITLE, Theme.ACCENT);
    JButton voltar = Theme.button("Voltar");
    voltar.addActionListener(e -> frame.mostrarMenu());

    topo.add(titulo, BorderLayout.WEST);
    topo.add(voltar, BorderLayout.EAST);

    JPanel cabecalho = new JPanel();
    cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
    cabecalho.setOpaque(false);
    cabecalho.add(topo);
    cabecalho.add(Box.createVerticalStrut(6));
    cabecalho.add(statusLabel);
    cabecalho.add(Box.createVerticalStrut(12));

    lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
    lista.setOpaque(false);

    JScrollPane scroll = new JScrollPane(lista);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    scroll.getViewport().setOpaque(false);
    scroll.setOpaque(false);
    scroll.getVerticalScrollBar().setUnitIncrement(16);

    add(cabecalho, BorderLayout.NORTH);
    add(scroll, BorderLayout.CENTER);
  }

  public void atualizar(Jogador jogador) {
    lista.removeAll();
    statusLabel.setText("Carregando...");
    revalidate();
    repaint();

    SwingWorker<ArrayList<RegistroPartida>, Void> worker = new SwingWorker<>() {
      @Override
      protected ArrayList<RegistroPartida> doInBackground() {
        return frame.getPartidaDAO().buscarHistorico(jogador.getId());
      }

      @Override
      protected void done() {
        try {
          preencher(get());
        } catch (Exception ex) {
          statusLabel.setText("Erro ao carregar histórico.");
        }
      }
    };
    worker.execute();
  }

  private void preencher(ArrayList<RegistroPartida> registros) {
    lista.removeAll();

    if (registros.isEmpty()) {
      statusLabel.setText("Nenhuma partida registrada ainda.");
    } else {
      statusLabel.setText(registros.size() + " partida(s) mais recente(s)");
      for (RegistroPartida r : registros) {
        lista.add(linhaHistorico(r));
        lista.add(Box.createVerticalStrut(8));
      }
    }

    lista.revalidate();
    lista.repaint();
  }

  private JPanel linhaHistorico(RegistroPartida r) {
    JPanel linha = new JPanel(new BorderLayout(16, 0));
    linha.setBackground(Theme.PANEL_BG);
    linha.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Theme.BORDER, 1),
        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
    linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    linha.setAlignmentX(LEFT_ALIGNMENT);

    Color corResultado = r.isVitoria() ? Theme.PONTOS : Theme.VIDA;
    JLabel resultado = Theme.label(r.isVitoria() ? "VITÓRIA" : "DERROTA", Theme.FONT_BOLD, corResultado);
    resultado.setPreferredSize(new Dimension(90, 20));

    JLabel data = Theme.label(r.getData().format(FORMATO_DATA), Theme.FONT_NORMAL, Theme.TEXT_MUTED);

    JLabel detalhes = Theme.label(
        "Pontuação " + r.getPontuacao() + "   ·   Duração " + r.getDuracaoFormatada(),
        Theme.FONT_NORMAL, Theme.TEXT);
    detalhes.setHorizontalAlignment(SwingConstants.RIGHT);

    linha.add(resultado, BorderLayout.WEST);
    linha.add(data, BorderLayout.CENTER);
    linha.add(detalhes, BorderLayout.EAST);
    return linha;
  }
}