package auth;

import database.JogadorDAO;
import model.Jogador;

import java.util.Scanner;

public class AuthService {

  private JogadorDAO dao = new JogadorDAO();
  private Scanner sc;

  /**
   * Recebe o Scanner de fora (TerminalUI) em vez de criar o seu
   * próprio, evitando ter dois Scanners diferentes lendo de
   * System.in ao mesmo tempo, o que pode causar entradas perdidas.
   */
  public AuthService(Scanner sc) {
    this.sc = sc;
  }

  public Jogador telaLogin() {

    System.out.println("\n===== LOGIN =====");

    System.out.print("Email: ");
    String email = sc.nextLine();

    System.out.print("Senha: ");
    String senha = sc.nextLine();

    Jogador jogador = dao.login(email, senha);

    if (jogador == null) {
      System.out.println("\nEmail ou senha incorretos");
      return null;
    }

    System.out.println("\nBem-vindo " + jogador.getNome());
    return jogador;
  }

  public Jogador telaCadastro() {

    System.out.println("\n===== CADASTRO =====");

    System.out.print("Nome: ");
    String nome = sc.nextLine();

    System.out.print("Sobrenome: ");
    String sobrenome = sc.nextLine();

    System.out.print("Email: ");
    String email = sc.nextLine();

    System.out.print("Senha: ");
    String senha = sc.nextLine();

    boolean criado = dao.cadastrar(nome, sobrenome, email, senha);

    if (!criado) {
      System.out.println("\nErro ao criar conta");
      return null;
    }

    System.out.println("\nConta criada com sucesso!");
    return dao.login(email, senha);
  }
}
