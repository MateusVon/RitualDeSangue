package gui;

import database.JogadorDAO;
import model.Jogador;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final MainFrame frame;
    private final JTextField campoEmail = new JTextField();
    private final JPasswordField campoSenha = new JPasswordField();
    private final JLabel status = new JLabel(" ");
    private final JButton botaoEntrar;

    public LoginPanel(MainFrame frame) {
        this.frame = frame;

        setLayout(new GridBagLayout());
        Theme.aplicarFundo(this, Theme.BG);

        JPanel caixa = new JPanel();
        caixa.setLayout(new BoxLayout(caixa, BoxLayout.Y_AXIS));
        Theme.aplicarFundo(caixa, Theme.PANEL_BG);
        caixa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(36, 44, 36, 44)));

        JLabel titulo = Theme.label("RITUAL DE SANGUE", Theme.FONT_TITLE, Theme.ACCENT);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitulo = Theme.label("Entre com sua conta", Theme.FONT_NORMAL, Theme.TEXT_MUTED);
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);

        caixa.add(titulo);
        caixa.add(Box.createVerticalStrut(4));
        caixa.add(subtitulo);
        caixa.add(Box.createVerticalStrut(28));
        caixa.add(campoComRotulo("Email", campoEmail));
        caixa.add(Box.createVerticalStrut(14));
        caixa.add(campoComRotulo("Senha", campoSenha));
        caixa.add(Box.createVerticalStrut(18));

        status.setFont(Theme.FONT_SMALL);
        status.setForeground(Theme.VIDA);
        status.setAlignmentX(CENTER_ALIGNMENT);
        caixa.add(status);
        caixa.add(Box.createVerticalStrut(10));

        botaoEntrar = Theme.accentButton("Entrar");
        botaoEntrar.addActionListener(e -> tentarLogin());

        JButton botaoCadastro = Theme.button("Criar conta");
        botaoCadastro.addActionListener(e -> {
            limpar();
            frame.mostrarCadastro();
        });

        caixa.add(botaoEntrar);
        caixa.add(Box.createVerticalStrut(10));
        caixa.add(botaoCadastro);

        getRootPaneAcao();

        add(caixa);
    }

    private void getRootPaneAcao() {
        // Enter no campo de senha também tenta logar.
        campoSenha.addActionListener(e -> tentarLogin());
    }

    private JPanel campoComRotulo(String rotulo, JTextField campo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel l = Theme.label(rotulo, Theme.FONT_SMALL, Theme.TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);

        campo.setMaximumSize(new Dimension(280, 34));
        campo.setPreferredSize(new Dimension(280, 34));
        campo.setBackground(Theme.SLOT_BG);
        campo.setForeground(Theme.TEXT);
        campo.setCaretColor(Theme.TEXT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        campo.setFont(Theme.FONT_NORMAL);
        campo.setAlignmentX(LEFT_ALIGNMENT);

        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(campo);
        return p;
    }

    private void tentarLogin() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarStatus("Preencha email e senha.", Theme.VIDA);
            return;
        }

        botaoEntrar.setEnabled(false);
        mostrarStatus("Entrando...", Theme.TEXT_MUTED);

        SwingWorker<Jogador, Void> worker = new SwingWorker<>() {
            @Override
            protected Jogador doInBackground() {
                return new JogadorDAO().login(email, senha);
            }

            @Override
            protected void done() {
                botaoEntrar.setEnabled(true);
                try {
                    Jogador jogador = get();
                    if (jogador == null) {
                        mostrarStatus("Email ou senha incorretos.", Theme.VIDA);
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
        campoEmail.setText("");
        campoSenha.setText("");
        status.setText(" ");
    }
}
