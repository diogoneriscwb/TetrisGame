package com.tetris.model.tetromino;

import com.tetris.model.PieceType;

public class IPiece extends Tetromino {

    // Define os estados de rotação para a 'I' (só tem 2 estados)
    private static final int[][][] SHAPES = new int[][][]{
            { // Estado 0 (Vertical)
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            { // Estado 1 (Horizontal)
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            { // Estado 2 (Vertical - Repete o Estado 0)
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            { // Estado 3 (Horizontal - Repete o Estado 1)
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0}
            }
    };

    /**
     * Construtor: Passa as 4 formas e o TIPO 'I' para a classe-mãe.
     */
    public IPiece() {
        super(SHAPES, PieceType.I);
    }
}