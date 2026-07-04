package game;

import model.Carta;
import model.Jogador;

import java.util.ArrayList;

public class IA {

    /**
     * Compra a carta do turno para a máquina. Chamado uma única vez no
     * início do turno da máquina (antes de qualquer jogada).
     */
    public void iniciarTurno(Jogador maquina) {
        maquina.comprarCarta();
    }

    /**
     * Tenta jogar a melhor carta possível da mão da máquina, respeitando
     * o sangue disponível e as posições livres no campo. Pode ser chamado
     * repetidamente dentro do mesmo turno: enquanto a máquina tiver
     * sangue suficiente, cartas na mão e espaço em campo, ela continua
     * colocando cartas.
     *
     * @return a posição em que a máquina jogou a carta, ou -1 se ela não
     *         conseguiu (ou não quis) jogar mais nenhuma carta.
     */
    public int jogarMelhorCartaDisponivel(Jogador maquina) {

        if (maquina.getMao().isEmpty()) {
            return -1;
        }

        Carta melhorCarta = escolherMelhorCarta(maquina.getMao(), maquina.getSangue());

        if (melhorCarta == null) {
            return -1;
        }

        int posicao = procurarPosicaoLivre(maquina);

        if (posicao == -1) {
            return -1;
        }

        int indice = maquina.getMao().indexOf(melhorCarta);

        boolean jogou = maquina.jogarCarta(indice, posicao);

        return jogou ? posicao : -1;
    }

    /**
     * Escolhe a carta de maior ataque dentre as que a máquina consegue
     * pagar com o sangue disponível. Retorna null se nenhuma carta da mão
     * for viável.
     */
    private Carta escolherMelhorCarta(ArrayList<Carta> mao, int sangueDisponivel) {

        Carta melhor = null;

        for (Carta carta : mao) {

            if (carta.getCustoSangue() > sangueDisponivel) {
                continue;
            }

            if (melhor == null || carta.getAtaque() > melhor.getAtaque()) {
                melhor = carta;
            }
        }

        return melhor;
    }

    private int procurarPosicaoLivre(Jogador maquina) {

        Carta[] campo = maquina.getCampo();

        for (int i = 0; i < campo.length; i++) {
            if (campo[i] == null) {
                return i;
            }
        }

        return -1;
    }
}
