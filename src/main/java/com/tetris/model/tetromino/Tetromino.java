package com.tetris.model.tetromino;

import javafx.scene.paint.Color;

public abstract class Tetromino {

    // NOVO: Armazena todos os estados de rotação (ex: 4 matrizes 5x5)
    protected int[][][] shapes;
    // NOVO: Armazena o estado de rotação atual (0, 1, 2, ou 3)
    protected int currentState;

    protected Color color;
    protected int x, y;

    /**
     * NOVO Construtor: Agora recebe TODOS os estados da peça.
     */
    public Tetromino(int[][][] shapes, Color color) {
        this.shapes = shapes;
        this.color = color;
        this.currentState = 0; // Começa no primeiro estado (índice 0)
        this.x = 3;
        this.y = 0;
    }

    /**
     * NOVO MÉTODO rotate():
     * Em vez de calcular, apenas avança para o próximo estado.
     */
    public void rotate() {
        // Avança para o próximo estado (0 -> 1 -> 2 -> 3 -> 0)
        currentState = (currentState + 1) % shapes.length;
    }

    /**
     * MÉTODO getShape() MODIFICADO:
     * Agora retorna a matriz do estado de rotação ATUAL.
     */
    public int[][] getShape() {
        return shapes[currentState];
    }

    // Getters e Setters (maioria continua igual)
    public Color getColor() { return color; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // (Opcional) Para detecção de colisão, você pode precisar "olhar" a próxima rotação
    public int[][] getNextRotationShape() {
        int nextState = (currentState + 1) % shapes.length;
        return shapes[nextState];
    }
}