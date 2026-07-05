package gui;

import model.Jogador;

import javax.swing.*;
import java.awt.*;

public class CadastroPanel extends JPanel {

    private final MainFrame frame;
    private final JTextField campoNome = new JTextField();
    private final JTextField campoSobrenome = new JTextField();
    private final JTextField campoEmail = new JTextField();
    private final JPasswordField campoSenha = new JPasswordField();
    private final JLabel status = new JLabel(" ");
    private final JButton botaoCadastrar;

    public CadastroPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new GridBagLayout());
        Theme.aplicarFundo(this, Theme.BG);

        JPanel caixa = new JPanel();
        caixa.setLayout(new BoxLayout(caixa, BoxLayout.Y_AXIS));
        Theme.aplicarFundo(caixa, Theme.PANEL_BG);
        caixa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(36, 44, 36, 44)));

        JLabel titulo = Theme.label("CRIAR CONTA", Theme.FONT_TITLE, Theme.ACCENT);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        caixa.add(titulo);
        caixa.add(Box.createVerticalStrut(24));
        caixa.add(Theme.campoComRotulo("Nome", campoNome));
        caixa.add(Box.createVerticalStrut(12));
        caixa.add(Theme.campoComRotulo("Sobrenome", campoSobrenome));
        caixa.add(Box.createVerticalStrut(12));
        caixa.add(Theme.campoComRotulo("Email", campoEmail));
        caixa.add(Box.createVerticalStrut(12));
        caixa.add(Theme.campoComRotulo("Senha", campoSenha));
        caixa.add(Box.createVerticalStrut(18));

        status.setFont(Theme.FONT_SMALL);
        status.setForeground(Theme.VIDA);
        status.setAlignmentX(CENTER_ALIGNMENT);
        caixa.add(status);
        caixa.add(Box.createVerticalStrut(10));

        botaoCadastrar = Theme.accentButton("Cadastrar");
        botaoCadastrar.addActionListener(e -> tentarCadastrar());

        JButton botaoVoltar = Theme.button("Voltar para login");
        botaoVoltar.addActionListener(e -> {
            limpar();
            frame.mostrarLogin();
        });

        caixa.add(botaoCadastrar);
        caixa.add(Box.createVerticalStrut(10));
        caixa.add(botaoVoltar);

        add(caixa);
    }

    private void tentarCadastrar() {
        String nome = campoNome.getText().trim();
        String sobrenome = campoSobrenome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (nome.isEmpty() || sobrenome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            mostrarStatus("Preencha todos os campos.", Theme.VIDA);
            return;
        }

        botaoCadastrar.setEnabled(false);
        mostrarStatus("Criando conta...", Theme.TEXT_MUTED);

        SwingWorker<Jogador, Void> worker = new SwingWorker<>() {
            @Override
            protected Jogador doInBackground() {
                boolean criado = frame.getJogadorDAO().cadastrar(nome, sobrenome, email, senha);
                if (!criado) {
                    return null;
                }
                return frame.getJogadorDAO().login(email, senha);
            }

            @Override
            protected void done() {
                botaoCadastrar.setEnabled(true);
                try {
                    Jogador jogador = get();
                    if (jogador == null) {
                        mostrarStatus("Não foi possível criar a conta (email já existe?).", Theme.VIDA);
                    } else {
                        limpar();
                        frame.loginRealizado(jogador);
                    }
                } catch (Exception ex) {
                    mostrarStatus("Erro ao conectar ao banco de dados.", Theme.VIDA);
                }
            }
        };
        worker.execute();
    }

    private void mostrarStatus(String texto, Color cor) {
        status.setForeground(cor);
        status.setText(texto);
    }

    public void limpar() {
        campoNome.setText("");
        campoSobrenome.setText("");
        campoEmail.setText("");
        campoSenha.setText("");
        status.setText(" ");
    }
}