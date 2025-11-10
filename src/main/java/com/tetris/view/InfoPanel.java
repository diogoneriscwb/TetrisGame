package com.tetris.view;

import com.tetris.controller.GameEngine;
import com.tetris.model.tetromino.Tetromino;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

// Import extra que vamos precisar para os "wrappers" de preview
import javafx.scene.layout.StackPane;

import com.tetris.model.PieceType;
import javafx.scene.image.Image;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects; // (Para o Objects.requireNonNull)
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.BlurType;

/**
 * Painel dedicado à exibição de informações (HUD Holográfico).
 * Contém pontuação, nível, linhas, pré-visualizações e botões.
 */
public class InfoPanel extends VBox {

    private static final int PREVIEW_BLOCK_SIZE = 20;
    private final Canvas nextPieceCanvas;
    private final Canvas holdPieceCanvas;
    // O campo 'gamePanel' foi removido pois não era lido (limpeza de código)

    // O "Mapa" que vai guardar suas 7 imagens de gradiente
    private Map<PieceType, Image> textureMap;
    // A imagem para a sua ideia de "peça travada cinza"
    private Image lockedTexture;

    // O "Pincel" do efeito de botão 3D (Sombra escura)
    private final InnerShadow buttonShadowEffect = new InnerShadow(
            7.0, 3.0, 3.0, Color.web("0x00000088"));

    // O "Pincel" do efeito de botão 3D (Destaque claro)
    private final InnerShadow buttonHighlightEffect = new InnerShadow(
            5.0, -2.0, -2.0, Color.web("0xFFFFFF55")
    );

    public InfoPanel(GameEngine gameEngine, GamePanel gamePanel) {

        setId("info-panel"); // Aplica o CSS do painel holográfico
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(200);
        loadTextures();

        // --- 1. CRIA OS PLACARES (Layout VBox - um em cima do outro) ---

        // --- Pontuação (Score) ---
        Label scoreTextLabel = new Label("SCORE");
        scoreTextLabel.getStyleClass().add("hud-label");

        Label scoreDataLabel = new Label("0");
        scoreDataLabel.getStyleClass().add("hud-data");
        scoreDataLabel.textProperty().bind(gameEngine.scoreProperty().asString("%d"));

        // --- Nível (Level) ---
        Label levelTextLabel = new Label("LEVEL");
        levelTextLabel.getStyleClass().add("hud-label");

        Label levelDataLabel = new Label("1");
        levelDataLabel.getStyleClass().add("hud-data");
        levelDataLabel.textProperty().bind(gameEngine.levelProperty().asString("%d"));

        // --- Linhas (Lines) ---
        Label linesTextLabel = new Label("LINES");
        linesTextLabel.getStyleClass().add("hud-label");

        Label linesDataLabel = new Label("0");
        linesDataLabel.getStyleClass().add("hud-data");
        linesDataLabel.textProperty().bind(gameEngine.linesClearedProperty().asString("%d"));

        // --- 2. CRIA AS "JANELAS" DE PREVIEW (Sua 4ª ideia) ---

        // --- Peça "Hold" ---
        Label holdPieceLabel = new Label("HOLD:");
        holdPieceLabel.getStyleClass().add("hud-label");
        holdPieceCanvas = new Canvas(5 * PREVIEW_BLOCK_SIZE, 5 * PREVIEW_BLOCK_SIZE);

        // "Envelopa" o canvas em um StackPane
        StackPane holdWrapper = new StackPane(holdPieceCanvas);
        holdWrapper.getStyleClass().add("hud-preview-box"); // Aplica o CSS de "janela"

        // --- Peça "Next" ---
        Label nextPieceLabel = new Label("NEXT");
        nextPieceLabel.getStyleClass().add("hud-label");
        nextPieceCanvas = new Canvas(5 * PREVIEW_BLOCK_SIZE, 5 * PREVIEW_BLOCK_SIZE);

        // "Envelopa" o canvas em um StackPane
        StackPane nextWrapper = new StackPane(nextPieceCanvas);
        nextWrapper.getStyleClass().add("hud-preview-box"); // Aplica o CSS de "janela"

        // --- 3. LISTENERS (Ouvintes) ---
        gameEngine.nextPieceProperty().addListener((__, ___, newPiece) -> {
            drawPieceOnCanvas(newPiece, nextPieceCanvas);
        });

        gameEngine.heldPieceProperty().addListener((__, ___, newPiece) -> {
            drawPieceOnCanvas(newPiece, holdPieceCanvas);
        });

        // --- 4. Botões ---
        Button pauseButton = new Button("Pause / Resume");
        pauseButton.setOnAction(e -> {
            gameEngine.togglePause();
            gamePanel.requestFocus();
        });

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> {
            gameEngine.start();
            gamePanel.requestFocus();
        });

        // --- 5. Adiciona tudo à tela (NOVA ORDEM) ---
        // (Sem os HBoxes, os labels agora ficam um em cima do outro)
        getChildren().addAll(
                scoreTextLabel, scoreDataLabel,
                levelTextLabel, levelDataLabel,
                linesTextLabel, linesDataLabel,
                holdPieceLabel, holdWrapper, // <-- Adiciona o "wrapper"
                nextPieceLabel, nextWrapper, // <-- Adiciona o "wrapper"
                pauseButton, restartButton
        );

    }

    private void loadTextures() {
        textureMap = new HashMap<>();
        try {
            textureMap.put(PieceType.I, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_I.png"))));
            textureMap.put(PieceType.J, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_J.png"))));
            textureMap.put(PieceType.L, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_L.png"))));
            textureMap.put(PieceType.O, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_O.png"))));
            textureMap.put(PieceType.S, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_S.png"))));
            textureMap.put(PieceType.T, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_T.png"))));
            textureMap.put(PieceType.Z, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/gradiente_Z.png"))));

            // Carrega a textura da peça travada
            lockedTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/bloco_cinza.png")));
        }catch (Exception e) {
            // Log robusto (sem stack trace!)
            System.err.println("[ERRO FATAL][InfoPanel] Falha ao carregar texturas de preview!");
            System.err.println("Causa: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * MÉTODO ANTIGO 'createInfoLabel' FOI REMOVIDO (LIMPEZA)
     * O CSS agora cuida da estilização dos labels.
     */

    /**
     * NOVO MÉTODO UNIFICADO (CÓDIGO LIMPO)
     * Desenha uma peça (Hold ou Next) em um canvas específico.
     * Substitui os antigos 'drawNextPiece' e 'drawHoldPiece' que eram duplicados.
     */
    /**
     * MÉTODO ATUALIZADO (Refatorado para PieceType e Texturas 3D)
     * Desenha uma peça (Hold ou Next) com a textura de gradiente
     * e o efeito de "botão 3D".
     */
    private void drawPieceOnCanvas(Tetromino piece, Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 1. Limpa o canvas (DEIXANDO TRANSPARENTE)
        // (Isso permite que o fundo ".hud-preview-box" do wrapper apareça)
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (piece == null) {
            return; // Peça nula (ex: "Hold" vazio), não desenha nada.
        }

        // --- INÍCIO DA NOVA LÓGICA DE DESENHO ---

        // 2. Pega o TIPO (não a cor)
        PieceType type = piece.getType();
        int[][] shape = piece.getShape();

        // 3. Pega a textura correta do Mapa
        Image texture = textureMap.get(type);

        // (Lógica para peças travadas, se aplicável no preview)
        if (type == PieceType.LOCKED) {
            texture = lockedTexture;
        }

        // Define o tamanho (do preview)
        double blockSize = PREVIEW_BLOCK_SIZE * 0.8;

        // Centraliza a peça
        double startX = (canvas.getWidth() - (shape[0].length * blockSize)) / 2;
        double startY = (canvas.getHeight() - (shape[0].length * blockSize)) / 2;

        // 4. Desenha os blocos
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {

                    double px = startX + x * blockSize;
                    double py = startY + y * blockSize;

                    if (texture != null) {
                        // 5. Desenha a IMAGEM do gradiente
                        gc.drawImage(texture, px, py, blockSize, blockSize);
                    } else {
                        // Plano B (se a imagem falhar em carregar)
                        gc.setFill(Color.MAGENTA); // Cor de erro óbvia
                        gc.fillRect(px, py, blockSize, blockSize);
                    }

                    // --- 6. A MÁGICA DO "BOTÃO 3D" (Cópia do GamePanel) ---
                    gc.save();

                    // Aplica o efeito de SOMBRA
                    gc.setEffect(buttonShadowEffect);
                    gc.setFill(Color.TRANSPARENT);
                    gc.fillRect(px, py, blockSize, blockSize);

                    // Aplica o efeito de DESTAQUE
                    gc.setEffect(buttonHighlightEffect);
                    gc.fillRect(px, py, blockSize, blockSize);

                    gc.restore(); // Limpa os efeitos

                    // 7. Contorno final
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(px, py, blockSize, blockSize);
                }
            }
        }
    }

    /**
     * MÉTODO ANTIGO 'drawNextPiece' FOI REMOVIDO (COMBINADO ACIMA)
     */

    /**
     * MÉTODO ANTIGO 'drawHoldPiece' FOI REMOVIDO (COMBINADO ACIMA)
     */
}