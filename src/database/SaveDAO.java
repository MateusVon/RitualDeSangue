package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.Carta;
import model.Deck;
import model.Jogador;
import model.partida;
import util.Cores;

/**
 * Responsável por salvar e restaurar o estado COMPLETO de uma partida em
 * andamento: turno, vida, sangue, mão, campo e deck restante de AMBOS os
 * lados (jogador e máquina). Antes, apenas o número do turno era salvo, o
 * que fazia a máquina "resetar" (vida cheia, campo vazio, deck novo) toda
 * vez que a partida era retomada.
 */
public class SaveDAO {

  public void salvar(partida partida) {

        Jogador jogador = partida.getJogador();
        Jogador maquina = partida.getMaquina();

        // Regra de negócio: uma partida perdida jamais pode ser salva —
        // essa checagem fica aqui (e não só na tela) para valer não
        // importa de onde salvar() seja chamado.
        if (jogador.perdeu()) {
            System.out.println(Cores.erro("\nVocê perdeu esta partida. Não é possível salvá-la."));
            return;
        }

        String sql = """
                INSERT INTO save_partida
                (id_jogador, turno,
                 vida_jogador, sangue_jogador, mao_jogador, campo_jogador, deck_jogador,
                 vida_maquina, sangue_maquina, mao_maquina, campo_maquina, deck_maquina)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                turno = ?,
                vida_jogador = ?, sangue_jogador = ?, mao_jogador = ?, campo_jogador = ?, deck_jogador = ?,
                vida_maquina = ?, sangue_maquina = ?, mao_maquina = ?, campo_maquina = ?, deck_maquina = ?
                """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

            String maoJogador = idsDaLista(jogador.getMao());
            String campoJogador = idsDoCampo(jogador.getCampo());
            String deckJogador = idsDaLista(jogador.getDeck().getCartas());

            String maoMaquina = idsDaLista(maquina.getMao());
            String campoMaquina = idsDoCampo(maquina.getCampo());
            String deckMaquina = idsDaLista(maquina.getDeck().getCartas());

            ps.setInt(1, jogador.getId());
            ps.setInt(2, partida.getTurno());
            ps.setInt(3, jogador.getVida());
            ps.setInt(4, jogador.getSangue());
            ps.setString(5, maoJogador);
            ps.setString(6, campoJogador);
            ps.setString(7, deckJogador);
            ps.setInt(8, maquina.getVida());
            ps.setInt(9, maquina.getSangue());
            ps.setString(10, maoMaquina);
            ps.setString(11, campoMaquina);
            ps.setString(12, deckMaquina);

            ps.setInt(13, partida.getTurno());
            ps.setInt(14, jogador.getVida());
            ps.setInt(15, jogador.getSangue());
            ps.setString(16, maoJogador);
            ps.setString(17, campoJogador);
            ps.setString(18, deckJogador);
            ps.setInt(19, maquina.getVida());
            ps.setInt(20, maquina.getSangue());
            ps.setString(21, maoMaquina);
            ps.setString(22, campoMaquina);
            ps.setString(23, deckMaquina);

      ps.executeUpdate();

      System.out.println("\nPartida salva!");

    } catch (Exception e) {
      System.out.println("Erro ao salvar: " + e.getMessage());
    }
  }

    /**
     * Restaura a partida salva do jogador informado, reconstruindo o
     * estado completo de AMBOS os lados (jogador e máquina): vida,
     * sangue, mão, campo (com dano já sofrido preservado) e o deck
     * restante, exatamente como estavam quando foi salva.
     */
    public partida carregar(Jogador jogador) {

        String sql = """
                SELECT *
                FROM save_partida
                WHERE id_jogador = ?
                """;

    try (
        Connection conn = Conexao.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, jogador.getId());

      ResultSet rs = ps.executeQuery();

      if (rs.next()) {

                CartaDAO cartaDAO = new CartaDAO();

                int turnoSalvo = rs.getInt("turno");

                jogador.setVida(rs.getInt("vida_jogador"));
                jogador.setSangue(rs.getInt("sangue_jogador"));
                jogador.setMao(cartasDaString(rs.getString("mao_jogador"), cartaDAO));
                jogador.setCampo(campoDaString(rs.getString("campo_jogador"), cartaDAO));
                jogador.setDeck(deckDaString(rs.getString("deck_jogador"), cartaDAO));

                Jogador maquina = new Jogador(0, "Máquina", "IA", "ia@game.com");
                maquina.setVida(rs.getInt("vida_maquina"));
                maquina.setSangue(rs.getInt("sangue_maquina"));
                maquina.setMao(cartasDaString(rs.getString("mao_maquina"), cartaDAO));
                maquina.setCampo(campoDaString(rs.getString("campo_maquina"), cartaDAO));
                maquina.setDeck(deckDaString(rs.getString("deck_maquina"), cartaDAO));

                partida partidaCarregada = new partida(jogador, maquina);
                partidaCarregada.definirTurno(turnoSalvo);

                return partidaCarregada;
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar partida: " + e.getMessage());
        }

        partidaCarregada.definirTurno(turnoSalvo);

        return partidaCarregada;
      }

    } catch (Exception e) {
      System.out.println("Erro ao carregar partida: " + e.getMessage());
    }

    /**
     * Remove o save de um jogador. Chamado assim que uma partida termina
     * (vitória ou derrota), para não deixar um save "fantasma" de uma
     * partida que já acabou.
     */
    public void removerSave(int idJogador) {

        String sql = "DELETE FROM save_partida WHERE id_jogador = ?";

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idJogador);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Erro ao remover save: " + e.getMessage());
        }
    }

    // =========================
    // SERIALIZAÇÃO AUXILIAR
    // =========================

    private String idsDaLista(ArrayList<Carta> cartas) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < cartas.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(cartas.get(i).getId());
        }

        return sb.toString();
    }

    private String idsDoCampo(Carta[] campo) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < campo.length; i++) {
            if (i > 0) {
                sb.append(",");
            }

            if (campo[i] == null) {
                sb.append("-1");
            } else {
                sb.append(campo[i].getId()).append(":").append(campo[i].getVida());
            }
        }

        return sb.toString();
    }

    private ArrayList<Carta> cartasDaString(String texto, CartaDAO dao) {

        ArrayList<Carta> lista = new ArrayList<>();

        if (texto == null || texto.isBlank()) {
            return lista;
        }

        for (String parte : texto.split(",")) {
            int id = Integer.parseInt(parte.trim());
            Carta carta = dao.buscarPorId(id);
            if (carta != null) {
                lista.add(carta);
            }
        }

        return lista;
    }

    private Carta[] campoDaString(String texto, CartaDAO dao) {

        Carta[] campo = new Carta[4];

        if (texto == null || texto.isBlank()) {
            return campo;
        }

        String[] partes = texto.split(",");

        for (int i = 0; i < partes.length && i < campo.length; i++) {

            String parte = partes[i].trim();

            if (parte.equals("-1")) {
                campo[i] = null;
                continue;
            }

            String[] idEVida = parte.split(":");
            int id = Integer.parseInt(idEVida[0].trim());
            int vidaSalva = Integer.parseInt(idEVida[1].trim());

            Carta carta = dao.buscarPorId(id);

            if (carta != null) {
                int dano = carta.getVidaMaxima() - vidaSalva;
                if (dano > 0) {
                    carta.receberDano(dano);
                }
                campo[i] = carta;
            }
        }

        return campo;
    }

    private Deck deckDaString(String texto, CartaDAO dao) {

        Deck deck = new Deck();

        if (texto == null || texto.isBlank()) {
            return deck;
        }

        for (String parte : texto.split(",")) {
            int id = Integer.parseInt(parte.trim());
            Carta carta = dao.buscarPorId(id);
            if (carta != null) {
                deck.adicionarCarta(carta);
            }
        }

        return deck;
    }
}
