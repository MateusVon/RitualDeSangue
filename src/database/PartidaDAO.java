package database;

import model.RegistroPartida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Consulta a tabela `partida`, que guarda o resultado consolidado de cada
 * partida já finalizada (resultado, pontuação, data e duração). Essa
 * tabela já existe no schema do banco, mas ainda não era usada em
 * nenhum lugar do código — o TerminalUI tinha inclusive um método
 * `mostrarHistorico()` com o comentário "ainda será implementado".
 */
public class PartidaDAO {

  public ArrayList<RegistroPartida> buscarHistorico(int idJogador) {

    ArrayList<RegistroPartida> registros = new ArrayList<>();

    String sql = """
        SELECT resultado, pontuacao, data_partida, duracao_segundos
        FROM partida
        WHERE id_jogador = ?
        ORDER BY data_partida DESC
        LIMIT 50
        """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, idJogador);

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        registros.add(new RegistroPartida(
            rs.getString("resultado"),
            rs.getInt("pontuacao"),
            rs.getTimestamp("data_partida").toLocalDateTime(),
            rs.getInt("duracao_segundos")));
      }

    } catch (Exception e) {
      System.out.println("Erro ao buscar histórico de partidas: " + e.getMessage());
    }

    return registros;
  }
}
