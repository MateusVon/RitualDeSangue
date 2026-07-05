package game;

import java.util.ArrayList;
import java.util.Scanner;

import model.partida;
import model.Jogador;
import model.Carta;
import database.DeckDAO;
import database.SaveDAO;
import util.Cores;

public class GameManager {

  private Jogador jogador;
  private Jogador maquina;
  private partida partida;
  private Scanner scanner;

  private final IA ia = new IA();
  private final CombateManager combate = new CombateManager();

  public GameManager(Jogador jogador, Scanner scanner) {

    this.jogador = jogador;
    this.scanner = scanner;

<<<<<<< HEAD
        // Garante que todo novo jogo comece com vida, sangue, mão e campo
        // zerados — mesmo que este mesmo objeto Jogador já tenha sido
        // usado em outra partida nesta sessão. Pontuação e estatísticas
        // não são afetadas (resetParaNovaPartida não mexe nelas). Se o
        // jogador estiver na verdade continuando uma partida salva, esse
        // reset é sobrescrito logo em seguida por SaveDAO.carregar().
        this.jogador.resetParaNovaPartida();

        this.maquina = new Jogador(0, "Máquina", "IA", "ia@game.com");
=======
    this.maquina = new Jogador(0, "Máquina", "IA", "ia@game.com");
>>>>>>> e291dddf94841a7dd4fde314cd3059d29973c537

    DeckDAO dao = new DeckDAO();
    jogador.setDeck(dao.gerarDeckAleatorio());
    maquina.setDeck(dao.gerarDeckAleatorio());
  }

  public void iniciar() {

    partida = new partida(jogador, maquina);
    partida.iniciarPartida();

    loopPartida();
  }

  public void carregarPartida() {

    SaveDAO save = new SaveDAO();

    partida carregada = save.carregar(jogador);

    if (carregada == null) {
      System.out.println(Cores.erro("\nNenhum save encontrado. Iniciando novo jogo..."));
      iniciar();
      return;
    }

    partida = carregada;

    System.out.println(Cores.sucesso("\nPartida carregada! (turno " + partida.getTurno() + ")"));

    loopPartida();
  }

  private void loopPartida() {

    while (!partida.acabou()) {

      exibirTabuleiro();

      System.out.println("\n" + Cores.NEGRITO + "1" + Cores.RESET + " - Jogar carta");
      System.out.println(Cores.NEGRITO + "2" + Cores.RESET + " - Passar turno");
      System.out.println(Cores.NEGRITO + "3" + Cores.RESET + " - Salvar e sair");
      System.out.print("\nEscolha: ");

      int opcao = lerInteiro();

      switch (opcao) {

        case 1:
          jogarCartaDaMao();
          break;

        case 2:
          executarTurnoMaquinaEAvancar();
          break;

        case 3:
          SaveDAO dao = new SaveDAO();
          dao.salvar(partida);
          return;

        default:
          System.out.println(Cores.erro("\nOpção inválida!"));
      }
    }

    // A partida terminou naturalmente (vitória ou derrota): pergunta
    // se o jogador quer continuar para a próxima partida ou sair.
    perguntarProximaPartida();
  }

  // =========================
  // PAINEL DE INFORMAÇÕES
  // =========================

  private void exibirTabuleiro() {

    String linha = "════════════════════════════════════════════════════";

<<<<<<< HEAD
        partida = carregada;
        this.maquina = partida.getMaquina();
=======
    System.out.println("\n" + Cores.titulo(linha));
    System.out.println(Cores.titulo("  RITUAL DE SANGUE  —  Turno " + partida.getTurno()));
    System.out.println(Cores.titulo(linha));
>>>>>>> e291dddf94841a7dd4fde314cd3059d29973c537

    System.out.println(
        Cores.NEGRITO + jogador.getNome() + Cores.RESET
            + "   Vida: " + Cores.vida(String.valueOf(jogador.getVida()))
            + "   Sangue: " + Cores.sangue(String.valueOf(jogador.getSangue()))
            + "   Pontos: " + Cores.pontos(String.valueOf(jogador.getPontuacao())));

    System.out.println(
        Cores.NEGRITO + maquina.getNome() + Cores.RESET
            + "   Vida: " + Cores.vida(String.valueOf(maquina.getVida()))
            + "   Sangue: " + Cores.sangue(String.valueOf(maquina.getSangue())));

    System.out.println(Cores.titulo("\n-- Seu campo --"));
    exibirCampo(jogador);

    System.out.println(Cores.titulo("\n-- Campo da máquina --"));
    exibirCampo(maquina);

    System.out.println(Cores.titulo("\n-- Sua mão -- (sangue disponível: " + jogador.getSangue() + ")"));
    exibirMao(jogador);
  }

  private void exibirCampo(Jogador dono) {

    Carta[] campo = dono.getCampo();

    for (int i = 0; i < campo.length; i++) {

      if (campo[i] == null) {
        System.out.println("  [" + i + "] vazio");
      } else {
        Carta c = campo[i];
        System.out.println("  [" + i + "] " + Cores.NEGRITO + c.getNome() + Cores.RESET
            + "  ATK:" + Cores.ataque(String.valueOf(c.getAtaque()))
            + "  HP:" + Cores.vida(c.getVida() + "/" + c.getVidaMaxima()));
      }
    }
  }

  private void exibirMao(Jogador dono) {

    ArrayList<Carta> mao = dono.getMao();

    if (mao.isEmpty()) {
      System.out.println("  (nenhuma carta na mão)");
      return;
    }

    for (int i = 0; i < mao.size(); i++) {
      Carta c = mao.get(i);
      System.out.println("  " + i + " - " + Cores.NEGRITO + c.getNome() + Cores.RESET
          + "  ATK:" + Cores.ataque(String.valueOf(c.getAtaque()))
          + "  HP:" + Cores.vida(String.valueOf(c.getVida()))
          + "  CUSTO:" + Cores.sangue(String.valueOf(c.getCustoSangue())));
    }
  }

  // =========================
  // AÇÕES DO JOGADOR
  // =========================

  /**
   * O jogador pode colocar QUANTAS cartas quiser em campo no mesmo
   * turno, uma de cada vez, contanto que tenha sangue suficiente e
   * espaço livre no campo. A cada carta colocada, TODAS as cartas já
   * presentes no seu campo atacam (não só a que acabou de entrar).
   */
  private void jogarCartaDaMao() {

    System.out.print("Carta: ");
    int carta = lerInteiro();

    System.out.print("Posição (0-3): ");
    int pos = lerInteiro();

    boolean jogou = jogador.jogarCarta(carta, pos);

<<<<<<< HEAD
                case 1:
                    jogarCartaDaMao();
                    break;

                case 2:
                    executarTurnoMaquinaEAvancar();
                    break;

                case 3:
                    SaveDAO dao = new SaveDAO();
                    dao.salvar(partida);
                    return;

                default:
                    System.out.println(Cores.erro("\nOpção inválida!"));
            }
        }

        // A partida terminou naturalmente (vitória ou derrota): remove
        // qualquer save "fantasma" dessa partida já finalizada e pergunta
        // se o jogador quer continuar para a próxima partida ou sair.
        new SaveDAO().removerSave(jogador.getId());
        perguntarProximaPartida();
=======
    if (!jogou) {
      return;
>>>>>>> e291dddf94841a7dd4fde314cd3059d29973c537
    }

    System.out.println(Cores.titulo("\n-- Seu campo ataca! --"));
    combate.atacar(jogador, maquina);
    partida.verificarFimPartida();
  }

  // =========================
  // TURNO DA MÁQUINA
  // =========================

  /**
   * A máquina compra sua carta do turno e então continua colocando
   * cartas em campo enquanto tiver sangue suficiente, cartas na mão e
   * espaço livre. A cada carta colocada, todo o campo dela ataca.
   */
  private void turnoDaMaquina() {

    System.out.println(Cores.titulo("\n===== Turno Máquina ====="));

    ia.iniciarTurno(maquina);

    boolean jogouAlgumaCarta = false;

    while (true) {

      int posicaoJogada = ia.jogarMelhorCartaDisponivel(maquina);

      if (posicaoJogada == -1) {
        break;
      }

      jogouAlgumaCarta = true;

      Carta cartaDaMaquina = maquina.getCampo()[posicaoJogada];
      System.out.println(maquina.getNome() + " colocou " + Cores.NEGRITO
          + cartaDaMaquina.getNome() + Cores.RESET + " em campo!");

      System.out.println(Cores.titulo("-- Campo da máquina ataca! --"));
      combate.atacar(maquina, jogador);
      partida.verificarFimPartida();

      if (partida.acabou()) {
        return;
      }
    }

    if (!jogouAlgumaCarta) {
      System.out.println(maquina.getNome() + " não jogou nenhuma carta neste turno.");
    }
  }

  private void executarTurnoMaquinaEAvancar() {

    turnoDaMaquina();

    if (!partida.acabou()) {
      partida.proximoTurno();
    }
  }

  // =========================
  // FIM DE PARTIDA
  // =========================

  /**
   * Chamado assim que uma partida termina (vitória ou derrota). Pergunta
   * se o jogador quer seguir para a próxima partida (mantendo a
   * pontuação e as estatísticas acumuladas) ou salvar e encerrar.
   */
  private void perguntarProximaPartida() {

    System.out.println(Cores.titulo("\n===== FIM DE PARTIDA ====="));
    System.out.println("Pontuação total: " + Cores.pontos(String.valueOf(jogador.getPontuacao())));
    System.out.println("Partidas jogadas: " + jogador.getTotalPartidas()
        + " | Vitórias: " + jogador.getTotalVitorias());

    System.out.println("\n1 - Jogar próxima partida");
    System.out.println("2 - Salvar e sair");
    System.out.print("\nEscolha: ");

    int opcao = lerInteiro();

    if (opcao == 1) {
      novaRodada();
    } else {
      System.out.println(Cores.sucesso("\nProgresso salvo. Até a próxima!"));
    }
  }

  /**
   * Prepara uma nova partida reaproveitando o mesmo jogador (mantendo
   * pontuação e estatísticas), reiniciando vida/sangue/mão/campo e
   * sorteando um novo deck e uma nova máquina para enfrentar.
   */
  private void novaRodada() {

    jogador.resetParaNovaPartida();

    maquina = new Jogador(0, "Máquina", "IA", "ia@game.com");

    DeckDAO dao = new DeckDAO();
    jogador.setDeck(dao.gerarDeckAleatorio());
    maquina.setDeck(dao.gerarDeckAleatorio());

    iniciar();
  }

  private int lerInteiro() {
    try {
      return Integer.parseInt(scanner.nextLine().trim());
    } catch (Exception e) {
      return -1;
    }
<<<<<<< HEAD
}
=======
  }
}
>>>>>>> e291dddf94841a7dd4fde314cd3059d29973c537
