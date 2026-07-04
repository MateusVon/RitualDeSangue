package model;

import java.util.ArrayList;

public class Carta {

  private int id;
  private String nome;
  private int ataque;
  private int vida;
  private int vidaMaxima;
  private int custoSangue;
  private String descricao;
  private String raridade;
  private String tipo;
  private ArrayList<String> palavrasChave = new ArrayList<>();

  public Carta(int id, String nome, int ataque, int vida, int custoSangue,
      String descricao, String raridade, String tipo) {
    this.id = id;
    this.nome = nome;
    this.ataque = ataque;
    this.vida = vida;
    this.vidaMaxima = vida;
    this.custoSangue = custoSangue;
    this.descricao = descricao;
    this.raridade = raridade;
    this.tipo = tipo;
  }

  public void receberDano(int dano) {
    vida -= dano;
  }

  public void curar(int valor) {
    vida += valor;
    if (vida > vidaMaxima) {
      vida = vidaMaxima;
    }
  }

  public boolean morreu() {
    return vida <= 0;
  }

  public void aumentarAtaque(int valor) {
    ataque += valor;
  }

  public void aumentarVida(int valor) {
    vida += valor;
    vidaMaxima += valor;
  }

  public void adicionarPalavraChave(String habilidade) {
    palavrasChave.add(habilidade);
  }

  public boolean possuiPalavraChave(String habilidade) {
    return palavrasChave.contains(habilidade);
  }

  public void mostrarCarta() {
    System.out.println();
    System.out.println("==========");
    System.out.println(nome);
    System.out.println("ATK: " + ataque);
    System.out.println("HP: " + vida);
    System.out.println("Custo: " + custoSangue);
    System.out.println("Raridade: " + raridade);
    System.out.println("Tipo: " + tipo);
    System.out.println("Descrição: " + descricao);
    System.out.println("Habilidades: " + palavrasChave);
    System.out.println("==========");
  }

  public int getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public int getAtaque() {
    return ataque;
  }

  public int getVida() {
    return vida;
  }

  public int getVidaMaxima() {
    return vidaMaxima;
  }

  public int getCustoSangue() {
    return custoSangue;
  }

  public String getDescricao() {
    return descricao;
  }

  public String getRaridade() {
    return raridade;
  }

  public String getTipo() {
    return tipo;
  }

  public ArrayList<String> getPalavrasChave() {
    return palavrasChave;
  }
}
