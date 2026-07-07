package model;

import database.JogadorDAO;
import database.PartidaDAO;
import util.Cores;

public class partida {

  private Jogador jogador;
  private Jogador maquina;
  private int turno;
  private boolean partidaFinalizada;


  private int idDeckJogador;

  
  private final long inicioMillis = System.currentTimeMillis();

  
  private static final int GANHO_SANGUE_POR_TURNO = 3;

  private static final int PONTOS_POR_VITORIA = 100;

  public partida(Jogador jogador, Jogador maquina) {
    this.jogador = jogador;
    this.maquina = maquina;
    this.turno = 1;
    this.partidaFinalizada = false;
  }

  public void iniciarPartida() {
    System.out.println("\n===== INÍCIO DA PARTIDA =====");
    System.out.println("Cada lado começa com " + Jogador.SANGUE_INICIAL + " pontos de Sangue.");

    jogador.getDeck().embaralhar();
    maquina.getDeck().embaralhar();

    for (int i = 0; i < 3; i++) {
      jogador.comprarCarta();
      maquina.comprarCarta();
    }
  }

  public void proximoTurno() {
    turno++;

    System.out.println("\n===== TURNO " + turno + " =====");
    jogador.adicionarSangue(GANHO_SANGUE_POR_TURNO);
    maquina.adicionarSangue(GANHO_SANGUE_POR_TURNO);

    System.out.println("Sangue acumulado: " + jogador.getNome() + " = " + jogador.getSangue()
        + " | " + maquina.getNome() + " = " + maquina.getSangue());

    jogador.comprarCarta();
    maquina.comprarCarta();

    verificarFimPartida();
  }

  public void verificarFimPartida() {
    if (jogador.perdeu()) {
      partidaFinalizada = true;
      System.out.println("\n" + Cores.erro("Sua vida chegou a ZERO! Você perdeu..."));
      salvarResultado();
    } else if (maquina.perdeu()) {
      partidaFinalizada = true;
      System.out.println("\n" + Cores.sucesso("A vida da máquina chegou a ZERO! Você venceu!"));
      salvarResultado();
    }
  }

  private void salvarResultado() {
    JogadorDAO dao = new JogadorDAO();

    jogador.adicionarPartida();

    int pontosGanhos = 0;

    if (!jogador.perdeu()) {
      jogador.adicionarVitoria();
      pontosGanhos = PONTOS_POR_VITORIA;
      jogador.adicionarPontuacao(pontosGanhos);
      System.out.println("+" + PONTOS_POR_VITORIA + " pontos! Pontuação total: " + jogador.getPontuacao());
    }

    dao.atualizarEstatisticas(jogador);

    int duracaoSegundos = (int) ((System.currentTimeMillis() - inicioMillis) / 1000);
    String resultado = jogador.perdeu() ? "DERROTA" : "VITORIA";

    new PartidaDAO().registrarPartida(jogador.getId(), idDeckJogador, resultado, pontosGanhos, duracaoSegundos);
  }

  public boolean acabou() {
    return partidaFinalizada;
  }

  public Jogador getJogador() {
    return jogador;
  }

  public Jogador getMaquina() {
    return maquina;
  }

  public int getTurno() {
    return turno;
  }

  public void definirTurno(int turno) {
    this.turno = turno;
  }

  
  public void definirIdDeckJogador(int idDeck) {
    this.idDeckJogador = idDeck;
  }
}