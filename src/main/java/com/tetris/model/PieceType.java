package com.tetris.model;

/**
 * Define os "tipos" de blocos que podem existir no tabuleiro.
 * Isso substitui o sistema antigo de 'Color'.
 * * Agora, o Modelo (Board) só se preocupa com o *tipo* da peça,
 * e a Visão (GamePanel) decide como *desenhar* esse tipo
 * (ex: com a textura de gradiente vermelha, azul, etc.).
 */
public enum PieceType {
    // Os 7 tipos de peças que caem
    I,
    J,
    L,
    O,
    S,
    T,
    Z,

    // (Opcional, mas BÔNUS para suas ideias futuras)

    /** Um bloco que já travou no tabuleiro (para a sua ideia de "peças cinzas") */
    LOCKED,

    /** A "Peça Fantasma" (para a feature de Ghost Piece) */
    GHOST
}