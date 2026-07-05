package gui;

import database.JogadorDAO;
import database.PartidaDAO;
import model.Jogador;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal da aplicação e ÚNICO ponto central do sistema GUI.
 * <p>
 * Duas responsabilidades ficam concentradas aqui, e só aqui:
 * <ul>
 *   <li><b>Navegação</b>: usa {@link CardLayout} para alternar entre as
 *   telas (login, cadastro, menu, deck, estatísticas, histórico,
 *   tabuleiro) dentro de uma única janela.</li>
 *   <li><b>Acesso a dados/estado da sessão</b>: mantém o jogador
 *   logado, o {@link GameController} da partida em andamento e as
 *   instâncias dos DAOs usados pela interface gráfica
 *   ({@link JogadorDAO}, {@link PartidaDAO}). Nenhuma tela deve
 *   instanciar um DAO diretamente — todas pedem ao MainFrame através
 *   de {@link #getJogadorDAO()} / {@link #getPartidaDAO()}. Isso evita
 *   que o acesso ao banco fique espalhado (descentralizado) entre
 *   LoginPanel, CadastroPanel, HistoricoPanel etc.</li>
 * </ul>
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);

    private final LoginPanel loginPanel;
    private final CadastroPanel cadastroPanel;
    private MenuPanel menuPanel;
    private DeckPanel deckPanel;
    private EstatisticasPanel estatisticasPanel;
    private HistoricoPanel historicoPanel;
    private GameBoardPanel gameBoardPanel;

    private Jogador jogadorAtual;
    private GameController controller;

    // Acesso a dados centralizado: uma única instância de cada DAO
    // usada por todas as telas da GUI (em vez de cada painel criar a sua).
    private final JogadorDAO jogadorDAO = new JogadorDAO();
    private final PartidaDAO partidaDAO = new PartidaDAO();

    public MainFrame() {
        super("Ritual de Sangue");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 780);
        setMinimumSize(new Dimension(950, 680));
        setLocationRelativeTo(null);

        container.setBackground(Theme.BG);
        getContentPane().setBackground(Theme.BG);

        loginPanel = new LoginPanel(this);
        cadastroPanel = new CadastroPanel(this);

        container.add(loginPanel, "login");
        container.add(cadastroPanel, "cadastro");

        setContentPane(container);
        mostrarLogin();
    }

    // ================= Navegação =================

    public void mostrarLogin() {
        cardLayout.show(container, "login");
    }

    public void mostrarCadastro() {
        cardLayout.show(container, "cadastro");
    }

    public void loginRealizado(Jogador jogador) {
        this.jogadorAtual = jogador;
        this.controller = new GameController(jogador);

        if (menuPanel == null) {
            menuPanel = new MenuPanel(this);
            container.add(menuPanel, "menu");
        }

        mostrarMenu();
    }

    public void logout() {
        jogadorAtual = null;
        controller = null;

        if (menuPanel != null) {
            container.remove(menuPanel);
            menuPanel = null;
        }
        if (deckPanel != null) {
            container.remove(deckPanel);
            deckPanel = null;
        }
        if (estatisticasPanel != null) {
            container.remove(estatisticasPanel);
            estatisticasPanel = null;
        }
        if (historicoPanel != null) {
            container.remove(historicoPanel);
            historicoPanel = null;
        }
        if (gameBoardPanel != null) {
            container.remove(gameBoardPanel);
            gameBoardPanel = null;
        }

        loginPanel.limpar();
        mostrarLogin();
    }

    public void mostrarMenu() {
        menuPanel.atualizar(jogadorAtual);
        cardLayout.show(container, "menu");
    }

    public void mostrarDeck() {
        if (deckPanel == null) {
            deckPanel = new DeckPanel(this);
            container.add(deckPanel, "deck");
        }
        deckPanel.atualizar(jogadorAtual);
        cardLayout.show(container, "deck");
    }

    public void mostrarEstatisticas() {
        if (estatisticasPanel == null) {
            estatisticasPanel = new EstatisticasPanel(this);
            container.add(estatisticasPanel, "stats");
        }
        estatisticasPanel.atualizar(jogadorAtual);
        cardLayout.show(container, "stats");
    }

    public void mostrarHistorico() {
        if (historicoPanel == null) {
            historicoPanel = new HistoricoPanel(this);
            container.add(historicoPanel, "historico");
        }
        historicoPanel.atualizar(jogadorAtual);
        cardLayout.show(container, "historico");
    }

    /** Chamado depois que o GameController já preparou a partida (nova ou carregada). */
    public void abrirTabuleiro() {
        if (gameBoardPanel == null) {
            gameBoardPanel = new GameBoardPanel(this);
            container.add(gameBoardPanel, "jogo");
        }
        gameBoardPanel.novaPartidaIniciada(controller);
        cardLayout.show(container, "jogo");
    }

    // ================= Acesso ao estado =================

    public GameController getController() {
        return controller;
    }

    public Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    /** Acesso centralizado ao DAO de jogador. Usado por Login e Cadastro. */
    public JogadorDAO getJogadorDAO() {
        return jogadorDAO;
    }

    /** Acesso centralizado ao DAO de histórico de partidas. Usado pela tela de Histórico. */
    public PartidaDAO getPartidaDAO() {
        return partidaDAO;
    }
}