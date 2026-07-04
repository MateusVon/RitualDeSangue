package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.Jogador;
import model.partida;

public class SaveDAO {

  public void salvar(partida partida) {

    String sql = """
        INSERT INTO save_partida
        (id_jogador, turno)
        VALUES (?, ?)
        ON DUPLICATE KEY UPDATE
        turno = ?
        """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, partida.getJogador().getId());
      ps.setInt(2, partida.getTurno());
      ps.setInt(3, partida.getTurno());

      ps.executeUpdate();

      System.out.println("\nPartida salva!");

    } catch (Exception e) {
      System.out.println("Erro ao salvar: " + e.getMessage());
    }
  }

  /**
   * Restaura uma partida salva para o jogador informado.
   *
   * Observação importante: a tabela save_partida atual só guarda o
   * número do turno, não o estado do campo/mão de cada jogador.
   * Por isso o "carregamento" recria o tabuleiro (deck novo, mão
   * inicial) e avança os turnos até o valor salvo, para o jogo
   * continuar de forma consistente. Para persistir campo/mão de
   * verdade, seria necessário adicionar tabelas extras (ex:
   * carta_em_campo, carta_na_mao) e salvar/carregar esse estado aqui.
   */
  public partida carregar(Jogador jogador) {

    String sql = """
        SELECT turno
        FROM save_partida
        WHERE id_jogador = ?
        """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, jogador.getId());

      ResultSet rs = ps.executeQuery();

      if (rs.next()) {

        int turnoSalvo = rs.getInt("turno");

        Jogador maquina = new Jogador(0, "Máquina", "IA", "ia@game.com");

        DeckDAO deckDAO = new DeckDAO();
        jogador.setDeck(deckDAO.gerarDeckAleatorio());
        maquina.setDeck(deckDAO.gerarDeckAleatorio());

        partida partidaCarregada = new partida(jogador, maquina);
        partidaCarregada.iniciarPartida();

        for (int i = 1; i < turnoSalvo; i++) {
          jogador.comprarCarta();
          maquina.comprarCarta();
        }

        partidaCarregada.definirTurno(turnoSalvo);

        return partidaCarregada;
      }

    } catch (Exception e) {
      System.out.println("Erro ao carregar partida: " + e.getMessage());
    }

    return null;
  }
}
