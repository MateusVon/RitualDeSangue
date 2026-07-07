package database;

import model.Carta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CartaDAO {

  public ArrayList<Carta> buscarTodasCartas() {

    ArrayList<Carta> cartas = new ArrayList<>();

    String sql = """
        SELECT
            c.id_carta,
            c.nome,
            c.ataque,
            c.vida,
            c.custo_sangue,
            c.descricao,
            r.nome AS raridade,
            t.nome AS tipo
        FROM carta c
        INNER JOIN raridade r ON c.id_raridade = r.id_raridade
        INNER JOIN tipo_carta t ON c.id_tipo = t.id_tipo
        """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        cartas.add(construirCarta(conn, rs));
      }

    } catch (Exception e) {
      System.out.println("Erro ao buscar cartas: " + e.getMessage());
    }

    return cartas;
  }


  public Carta buscarPorId(int idCarta) { 

    String sql = """
        SELECT
            c.id_carta,
            c.nome,
            c.ataque,
            c.vida,
            c.custo_sangue,
            c.descricao,
            r.nome AS raridade,
            t.nome AS tipo
        FROM carta c
        INNER JOIN raridade r ON c.id_raridade = r.id_raridade
        INNER JOIN tipo_carta t ON c.id_tipo = t.id_tipo
        WHERE c.id_carta = ?
        """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, idCarta);

      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        return construirCarta(conn, rs);
      }

    } catch (Exception e) {
      System.out.println("Erro ao buscar carta por id: " + e.getMessage());
    }

    return null;
  }

 
  public static Carta construirCarta(Connection conn, ResultSet rs) throws SQLException {

    Carta carta = new Carta(
        rs.getInt("id_carta"),
        rs.getString("nome"),
        rs.getInt("ataque"),
        rs.getInt("vida"),
        rs.getInt("custo_sangue"),
        rs.getString("descricao"),
        rs.getString("raridade"),
        rs.getString("tipo"));

    carregarPalavrasChave(conn, carta);

    return carta;
  }

 
  private static void carregarPalavrasChave(Connection conn, Carta carta) {

    String sql = "SELECT palavra_chave FROM carta_palavra_chave WHERE id_carta = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, carta.getId());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        carta.adicionarPalavraChave(rs.getString("palavra_chave"));
      }

    } catch (Exception e) {
     
    }
  }
}