package com.tetris.model.tetromino;

import com.tetris.model.PieceType;

public class JPiece extends Tetromino {

    // Define os 4 estados de rotação para a 'J'
    private static final int[][][] SHAPES = new int[][][]{
            { // Estado 0 (Inicial)
                    {0, 0, 0, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 1, 1, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            { // Estado 1 (90 graus)
                    {0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 0},
                    {0, 1, 1, 1, 0},
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            { // Estado 2 (180 graus)
                    {0, 0, 0, 0, 0},
                    {0, 0, 1, 1, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            { // Estado 3 (270 graus)
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 0},
                    {0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0}
            }
    };

    /**
     * Construtor: Passa as 4 formas e o TIPO 'J' para a classe-mãe.
     */
    public JPiece() {
        super(SHAPES, PieceType.J);
    }
}