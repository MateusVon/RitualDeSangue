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

  /**
   * Retorna o id_deck já existente do jogador (o mais recente), ou cria um
   * novo registro em `deck` caso ele ainda não tenha nenhum. Isso existe
   * porque a tabela `partida` exige um id_deck válido (chave estrangeira)
   * para registrar o resultado de uma partida no histórico, mas o jogo
   * ainda não tem uma tela de "montar meu deck" — então usamos um deck
   * padrão por jogador só para satisfazer essa referência.
   */
  public int obterOuCriarDeckId(int idJogador) {

    String selecionar = "SELECT id_deck FROM deck WHERE id_jogador = ? ORDER BY data_criacao DESC LIMIT 1";
    String inserir = "INSERT INTO deck (id_jogador, nome) VALUES (?, 'Deck Padrão')";

    try (Connection conn = Conexao.conectar()) {

      try (PreparedStatement ps = conn.prepareStatement(selecionar)) {
        ps.setInt(1, idJogador);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
          return rs.getInt("id_deck");
        }
      }

      try (PreparedStatement ps = conn.prepareStatement(inserir, PreparedStatement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, idJogador);
        ps.executeUpdate();
        ResultSet chaves = ps.getGeneratedKeys();
        if (chaves.next()) {
          return chaves.getInt(1);
        }
      }

    } catch (Exception e) {
      System.out.println("Erro ao obter/criar deck do jogador: " + e.getMessage());
    }

    return 0;
  }
}