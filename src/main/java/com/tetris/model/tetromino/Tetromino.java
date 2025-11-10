package com.tetris.model.tetromino;

// Importa o nosso novo "coração"
import com.tetris.model.PieceType;

// O 'import javafx.scene.paint.Color;' foi REMOVIDO!

/**
 * Classe abstrata que representa um Tetrominó (uma peça do Tetris).
 * (Versão 2 - Refatorada para usar PieceType em vez de Color)
 */
public abstract class Tetromino {

    protected int[][][] shapes;
    protected int currentState;

    // --- A GRANDE MUDANÇA ESTÁ AQUI ---
    // protected Color color; // <-- ISSO FOI REMOVIDO
    protected PieceType type; // <-- ISSO FOI ADICIONADO
    // ---------------------------------

    protected int x, y;

    /**
     * Construtor atualizado.
     * @param shapes As 4 matrizes de rotação.
     * @param type O *tipo* da peça (ex: PieceType.L), não mais a cor.
     */
    public Tetromino(int[][][] shapes, PieceType type) { // <-- MUDOU AQUI
        this.shapes = shapes;
        this.type = type; // <-- MUDOU AQUI
        this.currentState = 0;
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

    // --- O GETTER DE COR FOI REMOVIDO ---

    /**
     * @return O Tipo (lógico) desta peça (I, J, L, etc.)
     */
    public PieceType getType() { // <-- GETTER NOVO
        return type;
    }

    // Getters e Setters de Posição (continuam iguais)
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