package com.tetris.model.tetromino;

import javafx.scene.paint.Color;

public class IPiece extends Tetromino {

    // NOVO: Define os 4 estados de rotação para a 'L'
    private static final int[][][] SHAPES = new int[][][]{
            { // Estado 0 (Inicial)
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            { // Estado 1 (90 graus)
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            { // Estado 2 (180 graus)
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            { // Estado 3 (270 graus)
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0}
            }
    };

    /**
     * NOVO Construtor: Passa todas as 4 formas para a classe-mãe.
     */
    public IPiece() {
        super(SHAPES, Color.BLUE);
    }
}