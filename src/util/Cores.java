package util;

/**
 * Códigos ANSI simples para colorir a saída no terminal. Se o terminal do
 * usuário não suportar cores ANSI, os códigos aparecem como texto
 * inofensivo, então isso é seguro de usar em qualquer ambiente.
 */
public class Cores {

    public static final String RESET = "\u001B[0m";
    public static final String NEGRITO = "\u001B[1m";

    public static final String VERMELHO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARELO = "\u001B[33m";
    public static final String AZUL = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CIANO = "\u001B[36m";
    public static final String BRANCO = "\u001B[37m";

    public static final String VERMELHO_CLARO = "\u001B[91m";
    public static final String VERDE_CLARO = "\u001B[92m";
    public static final String AMARELO_CLARO = "\u001B[93m";
    public static final String CIANO_CLARO = "\u001B[96m";

    private Cores() {
        // utilitário estático, não deve ser instanciado
    }

    public static String vida(String texto) {
        return VERMELHO_CLARO + texto + RESET;
    }

    public static String sangue(String texto) {
        return MAGENTA + texto + RESET;
    }

    public static String ataque(String texto) {
        return AMARELO + texto + RESET;
    }

    public static String pontos(String texto) {
        return VERDE_CLARO + texto + RESET;
    }

    public static String titulo(String texto) {
        return NEGRITO + CIANO_CLARO + texto + RESET;
    }

    public static String erro(String texto) {
        return VERMELHO + texto + RESET;
    }

    public static String sucesso(String texto) {
        return VERDE + texto + RESET;
    }
}
