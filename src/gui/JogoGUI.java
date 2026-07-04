package gui;

import javax.swing.*;

/**
 * Ponto de entrada da versão gráfica (Swing) do Ritual de Sangue.
 * A versão original em terminal (main.Jogo) continua funcionando
 * normalmente e não foi alterada.
 */
public class JogoGUI {

    public static void main(String[] args) {

        // Intercepta o System.out para que as mensagens que as classes de
        // jogo já imprimem (Jogador, partida, CombateManager, IA...)
        // apareçam também no "Registro da partida" da interface gráfica.
        GuiOutputStream.instalar();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Se o look and feel do sistema não estiver disponível, segue com o padrão do Swing.
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
