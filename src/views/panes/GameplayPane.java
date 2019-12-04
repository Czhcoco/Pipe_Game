package views.panes;

import controllers.AudioManager;
import controllers.LevelManager;
import controllers.SceneManager;
import io.Deserializer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.FXGame;
import org.jetbrains.annotations.NotNull;
import views.BigButton;
import views.BigVBox;
import views.GameplayInfoPane;

import java.io.FileNotFoundException;

import static models.Config.TILE_SIZE;

/**
 * Pane for displaying the actual gameplay.
 */
public class GameplayPane extends GamePane {

    private HBox topBar = new HBox(20);
    private VBox canvasContainer = new BigVBox();
    private Canvas gameplayCanvas = new Canvas();
    private HBox bottomBar = new HBox(20);
    private Canvas queueCanvas = new Canvas();
    private Button quitToMenuButton = new BigButton("Quit to menu");

    private FXGame game;

    private final IntegerProperty ticksElapsed = new SimpleIntegerProperty();
    private GameplayInfoPane infoPane = null;

    public GameplayPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void connectComponents() {
        // TODO
        infoPane = new GameplayInfoPane(LevelManager.getInstance().getCurrentLevelProperty(), ticksElapsed, game.getNumOfSteps(), game.getNumOfUndo());

        topBar.getChildren().add(infoPane);
        canvasContainer.getChildren().add(gameplayCanvas);
        bottomBar.getChildren().addAll(queueCanvas, quitToMenuButton);

        this.setTop(topBar);
        this.setCenter(canvasContainer);
        this.setBottom(bottomBar);

        try {
            Deserializer des = new Deserializer(LevelManager.getInstance().getCurrentLevelPath());
            startGame(des.parseFXGame());
        } catch (FileNotFoundException e) {
            Alert box = new Alert(Alert.AlertType.WARNING);
            box.setHeaderText("Cannot open next map");
            box.setContentText("You will be returned to the Level Select Menu.");
            box.showAndWait();
            SceneManager.getInstance().showPane(LevelSelectPane.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        quitToMenuButton.setOnAction(e -> doQuitToMenuAction());
        gameplayCanvas.setOnMouseClicked(e -> onCanvasClicked(e));
        this.setOnKeyPressed(e -> onKeyPressed(e));
    }

    /**
     * Handles events when somewhere on the {@link GameplayPane#gameplayCanvas} is clicked.
     *
     * @param event Event to handle.
     */
    private void onCanvasClicked(MouseEvent event) {
        // TODO
        int col = ((int) event.getX()) / TILE_SIZE;
        int row = ((int) event.getY()) / TILE_SIZE;
        game.placePipe(row, col);
    }

    /**
     * Handles events when a key is pressed.
     *
     * @param event Event to handle.
     */
    private void onKeyPressed(KeyEvent event) {
        // TODO
        String key = event.getCharacter();
        if(key.equals("u"))
            game.undoStep();
        else if(key.equals("s"))
            game.skipPipe();
    }

    /**
     * Creates a popup which tells the player they have completed the map.
     */
    private void createWinPopup() {
        // TODO

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Level cleared!");

        ButtonType nextLevelButton = new ButtonType("Next level");
        ButtonType returnButton = new ButtonType("Return");

        if (LevelManager.getInstance().getAndSetNextLevel() != null) {
            alert.getButtonTypes().setAll(nextLevelButton, returnButton);
        } else {
            alert.getButtonTypes().setAll(returnButton);
        }

        var result = alert.showAndWait();
        if (!result.isPresent()) {
            if (System.getenv("CI") != null && System.getenv("CI").equals("true")) {
                System.out.println("This is normal in CI environment");
            } else {
                System.err.println("Should be impossible!");
            }
        } else if (result.get().getText().equals("Return")) {
            SceneManager.getInstance().showPane(LevelSelectPane.class);
        } else {
            loadNextMap();
        }
    }

    /**
     * Loads the next map in the series, or generate a new map if one is not available.
     */
    private void loadNextMap() {
        // TODO
        LevelManager manager = LevelManager.getInstance();
        String nextLevel = manager.getAndSetNextLevel();
        if (nextLevel != null) {
            try {
                Deserializer des = new Deserializer(manager.getCurrentLevelPath());
                startGame(des.parseFXGame());
            } catch (FileNotFoundException e) {
                Alert box = new Alert(Alert.AlertType.WARNING);
                box.setHeaderText("Cannot open next map");
                box.setContentText("You will be returned to the Level Select Menu.");
                box.showAndWait();
                SceneManager.getInstance().showPane(LevelSelectPane.class);
            }
        }
    }

    /**
     * Creates a popup which tells the player they have lost the map.
     */
    private void createLosePopup() {
        // TODO
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();

        var returnButtonType = new ButtonType("Return");

        alert.setTitle("Confirm");
        alert.setHeaderText("You Lose");
        alert.getButtonTypes().addAll(returnButtonType);

        var option = alert.showAndWait();
        if (option.isPresent()){
            if (option.get() == returnButtonType){
                SceneManager.getInstance().showPane(LevelSelectPane.class);
            }
        }

    }

    /**
     * Creates a popup which prompts the player whether they want to quit.
     */
    private void doQuitToMenuAction() {
        // TODO
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Return to menu?");
        alert.setContentText("Game progress will be lost.");
        alert.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);

        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.OK)){
            doQuitToMenu();
        }
    }

    /**
     * Go back to the Level Select scene.
     */
    private void doQuitToMenu() {
        // TODO
        SceneManager.getInstance().showPane(MainMenuPane.class);

    }

    /**
     * Starts a new game with the given name.
     *
     * @param game New game to start.
     */
    void startGame(@NotNull FXGame game) {
        // TODO
        this.game = game;
        this.game.startCountdown();
    }

    /**
     * Cleans up the currently bound game.
     */
    private void endGame() {
        // TODO
        this.game.stopCountdown();
    }
}
