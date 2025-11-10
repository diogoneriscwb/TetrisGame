package com.tetris.model.tetromino;

import com.tetris.model.PieceType;

public class OPiece extends Tetromino {

    // Define o ÚNICO estado do Quadrado
    private static final int[][] SHAPE = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 1, 1, 0, 0},
            {0, 1, 1, 0, 0},
            {0, 0, 0, 0, 0}
    };

    // Repete o único estado 4 vezes para o construtor da classe-mãe
    private static final int[][][] SHAPES = new int[][][]{
            SHAPE,
            SHAPE,
            SHAPE,
            SHAPE
    };

    /**
     * Construtor: Passa as 4 formas (iguais) e o TIPO 'O' para a classe-mãe.
     */
    public OPiece() {
        super(SHAPES, PieceType.O);
    }

    /**
     * Sobrescreve (overrides) o método de rotação.
     * Como a peça 'O' não gira, este método fica vazio.
     * (O 'rotate' da classe-mãe ainda funcionaria, mas isso é mais limpo).
     */
    @Override
    public void rotate() {
        // Não faz nada. O quadrado não gira.
    }
}