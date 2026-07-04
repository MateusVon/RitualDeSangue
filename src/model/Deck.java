package model;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Carta> cartas = new ArrayList<>();
    private final int LIMITE = 20;

    public void adicionarCarta(Carta carta) {
        if (cartas.size() >= LIMITE) {
            System.out.println("Deck cheio!");
            return;
        }
        cartas.add(carta);
    }

    public void removerCarta(Carta carta) {
        cartas.remove(carta);
    }

    public Carta comprarCarta() {
        if (cartas.isEmpty()) {
            return null;
        }
        return cartas.remove(0);
    }

    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    public boolean vazio() {
        return cartas.isEmpty();
    }

    public int tamanho() {
        return cartas.size();
    }

    public void mostrarDeck() {
        System.out.println("\n=== DECK ===");

        if (cartas.isEmpty()) {
            System.out.println("Deck vazio.");
            return;
        }

        for (int i = 0; i < cartas.size(); i++) {
            Carta c = cartas.get(i);
            System.out.println((i + 1) + " - " + c.getNome()
                    + " | ATK: " + c.getAtaque()
                    + " | HP: " + c.getVida());
        }
    }

    public ArrayList<Carta> getCartas() {
        return cartas;
    }
}
