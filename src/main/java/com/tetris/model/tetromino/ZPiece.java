package com.tetris.model.tetromino;

import com.tetris.model.PieceType;

public class ZPiece extends Tetromino {

    // Define os 2 estados de rotação para a 'Z'
    private static final int[][] STATE_0 = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 1, 1, 0, 0},
            {0, 0, 1, 1, 0},
            {0, 0, 0, 0, 0}
    };

    private static final int[][] STATE_1 = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0},
            {0, 0, 1, 1, 0},
            {0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0}
    };

    // Repete os 2 estados
    private static final int[][][] SHAPES = new int[][][]{
            STATE_0,
            STATE_1,
            STATE_0,
            STATE_1
    };

    /**
     * Construtor: Passa as 4 formas e o TIPO 'Z' para a classe-mãe.
     */
    public ZPiece() {
        super(SHAPES, PieceType.Z);
    }
}