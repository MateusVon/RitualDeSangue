package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pequeno mecanismo de publicação/assinatura usado para encaminhar linhas
 * de log (capturadas do System.out original do jogo) até quem estiver
 * interessado em exibi-las — no caso, o painel do tabuleiro.
 */
public final class LogBus {

    private static final List<Consumer<String>> ouvintes = new ArrayList<>();

    private LogBus() {
    }

    public static void assinar(Consumer<String> ouvinte) {
        ouvintes.add(ouvinte);
    }

    public static void publicar(String linha) {
        for (Consumer<String> ouvinte : new ArrayList<>(ouvintes)) {
            ouvinte.accept(linha);
        }
    }
}
