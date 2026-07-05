package game;

import model.Carta;
import model.Jogador;
import util.Cores;

public class CombateManager {

  // A cada carta eliminada, quem eliminou ganha 10 pontos.
  private static final int PONTOS_POR_ELIMINACAO = 10;

  /**
   * Faz TODAS as cartas presentes no campo do atacante atacarem a mesma
   * posição no campo do defensor (ou causarem dano direto ao jogador
   * defensor, se a posição estiver vazia). Chamado a cada vez que uma
   * carta nova é colocada em campo — ou seja, cartas já presentes no
   * tabuleiro atacam de novo a cada novo posicionamento.
   */
  public void atacar(Jogador atacanteJogador, Jogador defensorJogador) {

    Carta[] campoAtacante = atacanteJogador.getCampo();
    Carta[] campoDefensor = defensorJogador.getCampo();

    for (int i = 0; i < campoAtacante.length; i++) {

      Carta atacante = campoAtacante[i];

      if (atacante == null) {
        continue;
      }

      Carta defensor = campoDefensor[i];

      if (defensor != null) {

        defensor.receberDano(atacante.getAtaque());

        System.out.println("  " + Cores.CIANO + atacante.getNome() + Cores.RESET
            + " atacou " + Cores.CIANO + defensor.getNome() + Cores.RESET
            + " (HP restante: " + Cores.vida(String.valueOf(Math.max(defensor.getVida(), 0))) + ")");

        if (defensor.morreu()) {
          processarEliminacao(defensor, atacanteJogador);
          campoDefensor[i] = null;
        }

      } else {

        defensorJogador.receberDano(atacante.getAtaque());

        System.out.println("  " + Cores.CIANO + atacante.getNome() + Cores.RESET
            + " causou " + Cores.vida(String.valueOf(atacante.getAtaque())) + " de dano direto em "
            + defensorJogador.getNome() + " (vida restante: "
            + Cores.vida(String.valueOf(Math.max(defensorJogador.getVida(), 0))) + ")");
      }
    }
  }

  /**
   * Concede a recompensa por eliminar uma carta: metade inteira (divisão
   * inteira) do custo de sangue da carta eliminada, mais pontos fixos de
   * pontuação, para o dono da carta que causou a eliminação.
   */
  private void processarEliminacao(Carta cartaEliminada, Jogador responsavel) {

    int recompensaSangue = cartaEliminada.getCustoSangue() / 2;

    responsavel.adicionarSangue(recompensaSangue);
    responsavel.adicionarPontuacao(PONTOS_POR_ELIMINACAO);

    System.out.println("  " + Cores.erro(cartaEliminada.getNome() + " morreu!") + " "
        + responsavel.getNome() + " ganhou " + Cores.sangue("+" + recompensaSangue + " sangue")
        + " e " + Cores.pontos("+" + PONTOS_POR_ELIMINACAO + " pontos") + ".");
  }
}
