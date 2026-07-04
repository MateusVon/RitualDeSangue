package gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Todas as classes de jogo originais (Jogador, partida, CombateManager,
 * IA, GameManager...) foram escritas para imprimir suas mensagens com
 * System.out.println. Em vez de reescrever essas classes para a GUI,
 * interceptamos a saída padrão: cada linha impressa continua indo para o
 * console (útil para depuração) e também é publicada no {@link LogBus},
 * que o painel do tabuleiro usa para mostrar o "registro da partida".
 */
public class GuiOutputStream extends OutputStream {

    private final PrintStream original;
    private final StringBuilder linhaAtual = new StringBuilder();

    public GuiOutputStream(PrintStream original) {
        this.original = original;
    }

    @Override
    public void write(int b) throws IOException {
        original.write(b);

        char c = (char) b;

        if (c == '\n') {
            publicarLinha();
        } else if (c != '\r') {
            linhaAtual.append(c);
        }
    }

    private void publicarLinha() {
        String semCores = linhaAtual.toString().replaceAll("\u001B\\[[;\\d]*m", "");
        linhaAtual.setLength(0);
        LogBus.publicar(semCores);
    }

    /**
     * Instala a interceptação da saída padrão. Deve ser chamado uma única
     * vez, no início da aplicação (main).
     */
    public static void instalar() {
        PrintStream original = System.out;
        PrintStream interceptado = new PrintStream(new GuiOutputStream(original), true,
                java.nio.charset.StandardCharsets.UTF_8);
        System.setOut(interceptado);
    }
}
