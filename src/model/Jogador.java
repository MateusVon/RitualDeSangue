package model;

import java.util.ArrayList;

import util.Cores;

public class Jogador {

  private int id;
  private String nome;
  private String sobrenome;
  private String email;

  // Vida inicial de cada jogador (jogador e máquina).
  public static final int VIDA_INICIAL = 50;
  private int vida = VIDA_INICIAL;

  // Pontos de Sangue: recurso usado para colocar cartas em campo.
  // Toda partida OBRIGATORIAMENTE começa com 5 pontos de sangue.
  public static final int SANGUE_INICIAL = 5;
  private int sangue = SANGUE_INICIAL;

  // Pontuação acumulada do jogador (persistida entre partidas).
  private int pontuacao;

  private int totalPartidas;
  private int totalVitorias;

  // Limite máximo de cartas na mão (jogador e máquina).
  public static final int LIMITE_MAO = 5;

  private Deck deck = new Deck();
  private ArrayList<Carta> mao = new ArrayList<>();
  private Carta[] campo = new Carta[4];

  public Jogador(int id, String nome, String sobrenome, String email) {
    this.id = id;
    this.nome = nome;
    this.sobrenome = sobrenome;
    this.email = email;
  }

  public boolean perdeu() {
    return vida <= 0;
  }

  /**
   * @return true se a carta foi realmente colocada no campo, false se a
   *         jogada foi inválida (usado pelo GameManager para saber se deve
   *         disparar a resposta da IA e o combate).
   */
  public boolean jogarCarta(int indiceCarta, int posicao) {

    if (indiceCarta < 0 || indiceCarta >= mao.size()) {
      System.out.println("Carta inválida");
      return false;
    }

    if (posicao < 0 || posicao >= campo.length) {
      System.out.println("Posição inválida");
      return false;
    }

    if (campo[posicao] != null) {
      System.out.println("Posição ocupada");
      return false;
    }

    Carta carta = mao.get(indiceCarta);

    if (!gastarSangue(carta.getCustoSangue())) {
      System.out.println(Cores.erro("Sangue insuficiente! Você tem " + sangue
          + " e " + carta.getNome() + " custa " + carta.getCustoSangue() + "."));
      return false;
    }

    mao.remove(indiceCarta);
    campo[posicao] = carta;

    System.out.println(Cores.sucesso(nome + " jogou " + carta.getNome())
        + " (custou " + Cores.sangue(String.valueOf(carta.getCustoSangue())) + " de sangue, restam "
        + Cores.sangue(String.valueOf(sangue)) + ")");
    return true;
  }

  /**
   * Compra uma carta do deck, respeitando o limite máximo de cartas na
   * mão. Se a mão já estiver cheia, a compra é simplesmente ignorada
   * (a carta permanece no deck).
   */
  public void comprarCarta() {
    if (mao.size() >= LIMITE_MAO) {
      return;
    }

    Carta carta = deck.comprarCarta();
    if (carta != null) {
      mao.add(carta);
    }
  }

  /**
   * Adiciona pontos de Sangue (ex: recompensa por eliminar uma carta
   * inimiga ou ganho automático a cada turno).
   */
  public void adicionarSangue(int valor) {
    sangue += valor;
  }

  /**
   * Tenta gastar pontos de Sangue. Retorna false (sem gastar nada) se
   * o jogador não tiver sangue suficiente.
   */
  public boolean gastarSangue(int valor) {
    if (valor > sangue) {
      return false;
    }
    sangue -= valor;
    return true;
  }

  public void adicionarPontuacao(int valor) {
    pontuacao += valor;
  }

  /**
   * Reinicia o estado de uma partida (vida, sangue, mão e campo),
   * preservando estatísticas e pontuação acumuladas. Usado quando o
   * jogador decide continuar para a próxima partida.
   */
  public void resetParaNovaPartida() {
    this.vida = VIDA_INICIAL;
    this.sangue = SANGUE_INICIAL;
    this.mao = new ArrayList<>();
    this.campo = new Carta[4];
  }

  public void adicionarPartida() {
    totalPartidas++;
  }

  public void adicionarVitoria() {
    totalVitorias++;
  }

  public void receberDano(int dano) {
    vida -= dano;
  }

  public void mostrarMao() {
    System.out.println("\n===== SUA MÃO ===== (Sangue disponível: " + sangue + ")");

    if (mao.isEmpty()) {
      System.out.println("Nenhuma carta na mão.");
      return;
    }

    for (int i = 0; i < mao.size(); i++) {
      Carta c = mao.get(i);
      System.out.println(i + " - " + c.getNome()
          + " | ATK: " + c.getAtaque()
          + " | HP: " + c.getVida()
          + " | CUSTO: " + c.getCustoSangue());
    }
  }

  public void mostrarCampo() {
    System.out.println("\n===== CAMPO =====");

    for (int i = 0; i < campo.length; i++) {
      System.out.print("Posição " + i + ": ");

      if (campo[i] == null) {
        System.out.println("[Vazio]");
      } else {
        Carta c = campo[i];
        System.out.println(c.getNome()
            + " | ATK: " + c.getAtaque()
            + " | HP: " + c.getVida());
      }
    }
  }

  public int getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public String getEmail() {
    return email;
  }

  public int getVida() {
    return vida;
  }

  /**
   * Usado pelo SaveDAO para restaurar a vida exata de uma partida salva.
   */
  public void setVida(int vida) {
    this.vida = vida;
  }

  /**
   * Usado pelo SaveDAO para restaurar a mão exata de uma partida salva.
   */
  public void setMao(ArrayList<Carta> mao) {
    this.mao = mao;
  }

  /**
   * Usado pelo SaveDAO para restaurar o campo exato de uma partida salva.
   */
  public void setCampo(Carta[] campo) {
    this.campo = campo;
  }

  public int getSangue() {
    return sangue;
  }

  public void setSangue(int sangue) {
    this.sangue = sangue;
  }

  public int getPontuacao() {
    return pontuacao;
  }

  public void setPontuacao(int pontuacao) {
    this.pontuacao = pontuacao;
  }

  public void setDeck(Deck deck) {
    this.deck = deck;
  }

  public Deck getDeck() {
    return deck;
  }

  public ArrayList<Carta> getMao() {
    return mao;
  }

  public Carta[] getCampo() {
    return campo;
  }

  public int getTotalPartidas() {
    return totalPartidas;
  }

  public int getTotalVitorias() {
    return totalVitorias;
  }

  public void setTotalPartidas(int totalPartidas) {
    this.totalPartidas = totalPartidas;
  }

  public void setTotalVitorias(int totalVitorias) {
    this.totalVitorias = totalVitorias;
  }
}