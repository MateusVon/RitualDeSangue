package gui;

import database.DeckDAO;
import database.SaveDAO;
import game.CombateManager;
import game.IA;
import model.Jogador;
import model.partida;

/**
 * Equivalente a {@link game.GameManager}, mas pensado para ser controlado
 * por cliques de botão em vez do loop bloqueante baseado em Scanner usado
 * na versão de terminal. Reaproveita exatamente as mesmas classes de
 * regra do jogo (Jogador, partida, CombateManager, IA, DeckDAO, SaveDAO),
 * então o comportamento da partida é idêntico ao da versão em texto.
 */
public class GameController {

    private final Jogador jogador;
    private Jogador maquina;
    private partida partida;

    private final IA ia = new IA();
    private final CombateManager combate = new CombateManager();

    public GameController(Jogador jogador) {
        this.jogador = jogador;
    }

    /** Começa uma partida nova do zero, com deck aleatório para os dois lados. */
    public void iniciarNovaPartida() {
        maquina = new Jogador(0, "Máquina", "IA", "ia@game.com");

        DeckDAO dao = new DeckDAO();
        jogador.setDeck(dao.gerarDeckAleatorio());
        maquina.setDeck(dao.gerarDeckAleatorio());

        partida = new partida(jogador, maquina);
        partida.iniciarPartida();
    }

    /**
     * Tenta carregar o save do jogador atual.
     *
     * @return true se havia um save e ele foi carregado; false se não
     *         havia nenhum save (nesse caso nada é alterado no controller).
     */
    public boolean carregarPartidaSalva() {
        SaveDAO save = new SaveDAO();
        partida carregada = save.carregar(jogador);

        if (carregada == null) {
            return false;
        }

        this.partida = carregada;
        this.maquina = carregada.getMaquina();
        return true;
    }

    /**
     * Joga uma carta da mão do jogador na posição indicada e, se der
     * certo, executa o ataque de todo o campo do jogador (mesma regra da
     * versão terminal: toda vez que uma carta é colocada, o campo inteiro
     * ataca).
     *
     * @return true se a carta foi jogada (custo pago e posição livre).
     */
    public boolean jogarCarta(int indiceCarta, int posicao) {
        boolean jogou = jogador.jogarCarta(indiceCarta, posicao);

        if (jogou) {
            combate.atacar(jogador, maquina);
            partida.verificarFimPartida();
        }

        return jogou;
    }

    /**
     * Executa o turno da máquina (ela joga cartas enquanto puder/quiser,
     * atacando a cada carta colocada) e, se a partida não tiver acabado,
     * avança para o próximo turno.
     */
    public void passarTurno() {
        turnoDaMaquina();

        if (!partida.acabou()) {
            partida.proximoTurno();
        }
    }

    private void turnoDaMaquina() {
        ia.iniciarTurno(maquina);

        while (true) {
            int posicaoJogada = ia.jogarMelhorCartaDisponivel(maquina);

            if (posicaoJogada == -1) {
                break;
            }

            combate.atacar(maquina, jogador);
            partida.verificarFimPartida();

            if (partida.acabou()) {
                return;
            }
        }
    }

    public void salvar() {
        new SaveDAO().salvar(partida);
    }

    /** Prepara uma nova rodada mantendo pontuação/estatísticas do jogador. */
    public void novaRodadaAposFim() {
        jogador.resetParaNovaPartida();
        iniciarNovaPartida();
    }

    public Jogador getJogador() {
        return jogador;
    }

    public Jogador getMaquina() {
        return maquina;
    }

    public partida getPartida() {
        return partida;
    }
}
