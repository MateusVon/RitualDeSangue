package database;

import model.Deck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeckDAO {

  public Deck carregarDeck(int idDeck) {

    Deck deck = new Deck();

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
        FROM deck_carta dc
        INNER JOIN carta c ON dc.id_carta = c.id_carta
        INNER JOIN raridade r ON c.id_raridade = r.id_raridade
        INNER JOIN tipo_carta t ON c.id_tipo = t.id_tipo
        WHERE dc.id_deck = ?
        """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, idDeck);

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        deck.adicionarCarta(CartaDAO.construirCarta(conn, rs));
      }

    } catch (Exception e) {
      System.out.println("Erro ao carregar deck: " + e.getMessage());
    }

    deck.embaralhar();
    return deck;
  }

  public Deck gerarDeckAleatorio() {

    Deck deck = new Deck();

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
        ORDER BY RAND()
        LIMIT 20
        """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        deck.adicionarCarta(CartaDAO.construirCarta(conn, rs));
      }

    } catch (Exception e) {
      System.out.println("Erro ao gerar deck aleatório: " + e.getMessage());
    }

    deck.embaralhar();
    return deck;
  }
}
