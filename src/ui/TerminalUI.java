package ui;

import java.util.Scanner;

import auth.AuthService;
import game.GameManager;
import model.Jogador;
import util.Cores;

public class TerminalUI {

    private Scanner scanner = new Scanner(System.in);

    // O mesmo Scanner é repassado para AuthService e GameManager,
    // em vez de cada classe criar o seu próprio (o que causava
    // disputa por System.in e entradas "perdidas").
    private AuthService auth = new AuthService(scanner);

    // =========================
    // MENU PRINCIPAL
    // =========================

    public void menuInicial() {

        int opcao;

        while (true) {

            System.out.println("\n" + Cores.titulo("=============================="));
            System.out.println(Cores.titulo("      RITUAL DE SANGUE"));
            System.out.println(Cores.titulo("=============================="));

            System.out.println("\n1 - Login");
            System.out.println("2 - Criar conta");
            System.out.println("3 - Sair");

            System.out.print("\nEscolha: ");

            opcao = lerInteiro();

            switch (opcao) {

                case 1:
                    fazerLogin();
                    break;

                case 2:
                    criarConta();
                    break;

                case 3:
                    System.out.println("\nEncerrando jogo...");
                    System.exit(0);
                    break;

                default:
                    System.out.println(Cores.erro("\nOpção inválida!"));
            }
        }
    }

    // =========================
    // LOGIN
    // =========================

    private void fazerLogin() {

        Jogador jogador = auth.telaLogin();

        if (jogador != null) {
            menuJogador(jogador);
        }
    }

    // =========================
    // CADASTRO
    // =========================

    private void criarConta() {
        auth.telaCadastro();
    }

    // =========================
    // MENU APÓS LOGIN
    // =========================

    public void menuJogador(Jogador jogador) {

        int opcao;

        while (true) {

            System.out.println("\n" + Cores.titulo("=============================="));
            System.out.println(Cores.NEGRITO + "Jogador: " + Cores.RESET + jogador.getNome());
            System.out.println("Vida: " + Cores.vida(String.valueOf(jogador.getVida())));
            System.out.println("Pontuação: " + Cores.pontos(String.valueOf(jogador.getPontuacao())));
            System.out.println(Cores.titulo("=============================="));

            System.out.println("\n1 - Novo jogo");
            System.out.println("2 - Continuar");
            System.out.println("3 - Meu deck");
            System.out.println("4 - Histórico");
            System.out.println("5 - Estatísticas");
            System.out.println("6 - Logout");

            System.out.print("\nEscolha: ");

            opcao = lerInteiro();

            switch (opcao) {

                case 1:
                    novoJogo(jogador);
                    break;

                case 2:
                    continuarJogo(jogador);
                    break;

                case 3:
                    mostrarDeck(jogador);
                    break;

                case 4:
                    mostrarHistorico();
                    break;

                case 5:
                    mostrarEstatisticas(jogador);
                    break;

                case 6:
                    System.out.println("\nLogout realizado.");
                    return;

                default:
                    System.out.println(Cores.erro("\nOpção inválida!"));
            }
        }
    }

    // =========================
    // NOVO JOGO
    // =========================

    private void novoJogo(Jogador jogador) {

        System.out.println("\nCriando nova partida...");

        GameManager jogo = new GameManager(jogador, scanner);
        jogo.iniciar();
    }

    // =========================
    // CONTINUAR
    // =========================

    private void continuarJogo(Jogador jogador) {

        System.out.println("\nCarregando save...");

        GameManager jogo = new GameManager(jogador, scanner);
        jogo.carregarPartida();
    }

    // =========================
    // DECK
    // =========================

    private void mostrarDeck(Jogador jogador) {

        System.out.println("\n" + Cores.titulo("===== SEU DECK ====="));
        jogador.getDeck().mostrarDeck();
    }

    // =========================
    // HISTÓRICO
    // =========================

    private void mostrarHistorico() {
        System.out.println("\nSistema ainda será implementado.");
    }

    // =========================
    // ESTATÍSTICAS
    // =========================

    private void mostrarEstatisticas(Jogador jogador) {

        System.out.println("\n" + Cores.titulo("===== ESTATÍSTICAS ====="));
        System.out.println("Partidas: " + jogador.getTotalPartidas());
        System.out.println("Vitórias: " + jogador.getTotalVitorias());
        System.out.println("Pontuação: " + Cores.pontos(String.valueOf(jogador.getPontuacao())));
    }

    private int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }
}
