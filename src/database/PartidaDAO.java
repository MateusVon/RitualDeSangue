package database;

import model.RegistroPartida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


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

 
  public void registrarPartida(int idJogador, int idDeck, String resultado, int pontuacao, int duracaoSegundos) {

    String sql = """
        INSERT INTO partida (id_jogador, id_deck, resultado, pontuacao, duracao_segundos)
        VALUES (?, ?, ?, ?, ?)
        """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, idJogador);
      ps.setInt(2, idDeck);
      ps.setString(3, resultado);
      ps.setInt(4, pontuacao);
      ps.setInt(5, duracaoSegundos);

      ps.executeUpdate();

    } catch (Exception e) {
      System.out.println("Erro ao registrar partida no histórico: " + e.getMessage());
    }
  }
}