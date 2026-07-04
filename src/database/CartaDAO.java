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
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cartas.add(construirCarta(conn, rs));
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar cartas: " + e.getMessage());
        }

        return cartas;
    }

    /**
     * Monta um objeto Carta a partir de uma linha de ResultSet e tenta
     * carregar as palavras-chave associadas a ela. Reaproveitado por
     * CartaDAO e DeckDAO para manter a criação de Carta consistente
     * em todo o projeto.
     */
    public static Carta construirCarta(Connection conn, ResultSet rs) throws SQLException {

        Carta carta = new Carta(
                rs.getInt("id_carta"),
                rs.getString("nome"),
                rs.getInt("ataque"),
                rs.getInt("vida"),
                rs.getInt("custo_sangue"),
                rs.getString("descricao"),
                rs.getString("raridade"),
                rs.getString("tipo")
        );

        carregarPalavrasChave(conn, carta);

        return carta;
    }

    /**
     * ATENÇÃO: assume uma tabela "carta_palavra_chave" com as colunas
     * "id_carta" e "palavra_chave". Se o seu schema usa outro nome,
     * ajuste a query abaixo. Caso a tabela não exista, a falha é
     * silenciosa e a carta simplesmente fica sem palavras-chave —
     * isso não quebra o restante do sistema.
     */
    private static void carregarPalavrasChave(Connection conn, Carta carta) {

        String sql = "SELECT palavra_chave FROM carta_palavra_chave WHERE id_carta = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, carta.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                carta.adicionarPalavraChave(rs.getString("palavra_chave"));
            }

        } catch (Exception e) {
            // Tabela de palavras-chave ausente/diferente no schema atual.
            // Ignorado de propósito para não quebrar o carregamento das cartas.
        }
    }
}
