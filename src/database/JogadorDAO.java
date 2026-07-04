package database;

import model.Jogador;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JogadorDAO {

    public boolean emailExiste(String email) {

        String sql = """
                SELECT email
                FROM jogador
                WHERE email = ?
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("Erro ao verificar email: " + e.getMessage());
            return false;
        }
    }

    public boolean cadastrar(String nome, String sobrenome, String email, String senha) {

        // Verifica duplicidade ANTES de abrir uma conexão para o INSERT.
        if (emailExiste(email)) {
            System.out.println("Email já cadastrado");
            return false;
        }

        String sql = """
                INSERT INTO jogador
                (primeiro_nome, sobrenome, email, senha_hash)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, nome);
            ps.setString(2, sobrenome);
            ps.setString(3, email);
            ps.setString(4, hashSenha(senha));

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar jogador: " + e.getMessage());
            return false;
        }
    }

    public Jogador login(String email, String senha) {

        String sql = """
                SELECT *
                FROM jogador
                WHERE email = ?
                AND senha_hash = ?
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, email);
            ps.setString(2, hashSenha(senha));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Jogador jogador = new Jogador(
                        rs.getInt("id_jogador"),
                        rs.getString("primeiro_nome"),
                        rs.getString("sobrenome"),
                        rs.getString("email")
                );

                jogador.setTotalPartidas(rs.getInt("total_partidas"));
                jogador.setTotalVitorias(rs.getInt("total_vitorias"));
                jogador.setPontuacao(rs.getInt("pontuacao"));

                // O jogador precisa de um deck assim que loga, senão o menu
                // "Meu deck" ficaria sempre vazio antes da primeira partida.
                DeckDAO deckDAO = new DeckDAO();
                jogador.setDeck(deckDAO.gerarDeckAleatorio());

                System.out.println("Usuário encontrado!");
                return jogador;
            }

            System.out.println("Login não encontrado");

        } catch (Exception e) {
            System.out.println("Erro no login: " + e.getMessage());
        }

        return null;
    }

    public void atualizarEstatisticas(Jogador jogador) {

        String sql = """
                UPDATE jogador
                SET total_partidas = ?,
                    total_vitorias = ?,
                    pontuacao = ?
                WHERE id_jogador = ?
                """;

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, jogador.getTotalPartidas());
            ps.setInt(2, jogador.getTotalVitorias());
            ps.setInt(3, jogador.getPontuacao());
            ps.setInt(4, jogador.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Erro ao atualizar estatísticas: " + e.getMessage());
        }
    }

    /**
     * Gera o hash SHA-256 da senha em texto hexadecimal.
     * A coluna do banco chama-se "senha_hash", então nunca gravamos
     * nem comparamos senha em texto puro.
     */
    private String hashSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar hash da senha", e);
        }
    }
}
