package model;

import java.time.LocalDateTime;

public class RegistroPartida {

  private final String resultado;
  private final int pontuacao;
  private final LocalDateTime data;
  private final int duracaoSegundos;

  public RegistroPartida(String resultado, int pontuacao, LocalDateTime data, int duracaoSegundos) {
    this.resultado = resultado;
    this.pontuacao = pontuacao;
    this.data = data;
    this.duracaoSegundos = duracaoSegundos;
  }

  public String getResultado() {
    return resultado;
  }

  public boolean isVitoria() {
    return "VITORIA".equalsIgnoreCase(resultado);
  }

  public int getPontuacao() {
    return pontuacao;
  }

  public LocalDateTime getData() {
    return data;
  }

  public int getDuracaoSegundos() {
    return duracaoSegundos;
  }

  /** Formata a duração como mm:ss, mais fácil de ler do que segundos crus. */
  public String getDuracaoFormatada() {
    int minutos = duracaoSegundos / 60;
    int segundos = duracaoSegundos % 60;
    return String.format("%d:%02d", minutos, segundos);
  }
}
