package views.panes;

import controllers.LevelManager;
import controllers.SceneManager;
import io.Deserializer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import models.FXGame;
import views.BigButton;
import views.BigVBox;
import views.SideMenuVBox;

import java.io.File;
import java.io.FileNotFoundException;

public class LevelSelectPane extends GamePane {

    private SideMenuVBox leftContainer = new SideMenuVBox();
    private BigButton returnButton = new BigButton("Return");
    private BigButton playButton = new BigButton("Play");
    private BigButton playRandom = new BigButton("Generate Map and Play");
    private BigButton chooseMapDirButton = new BigButton("Choose map directory");
    private ListView<String> levelsListView = new ListView<>(LevelManager.getInstance().getLevelNames());
    private BigVBox centerContainer = new BigVBox();
    private Canvas levelPreview = new Canvas();

    public LevelSelectPane() {
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
        leftContainer.getChildren().addAll(
                returnButton,
                chooseMapDirButton,
                levelsListView,
                playButton,
                playRandom
        );

        centerContainer.getChildren().addAll(
                levelPreview
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
        playButton.setVisible(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        returnButton.setOnAction(e -> SceneManager.getInstance().showPane(MainMenuPane.class));
        chooseMapDirButton.setOnAction(e -> promptUserForMapDirectory());
        playButton.setOnAction(e -> startGame(false));
        playRandom.setOnAction(e -> startGame(true));
        levelsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
        {
            public void changed(ObservableValue<? extends String> obs,
                                final String oldValue, final String newValue)
            {
                onMapSelected(obs, oldValue, newValue);
            }
        });
    }

    /**
     * Starts the game.
     *
     * <p>
     * This method should do everything that is required to initialize and start the game, including loading/generating
     * maps, switching scenes, etc.
     * </p>
     *
     * @param generateRandom Whether to use a generated map.
     */
    private void startGame(final boolean generateRandom)  {
        // TODO
        LevelManager manager = LevelManager.getInstance();
        Deserializer newGame;
        FXGame newFXGame;
        try {
            if (generateRandom) {
                newGame = new Deserializer(manager.getCurrentLevelPath());
                newFXGame = newGame.parseFXGame();
                newFXGame.startCountdown();
            } else {
                manager.setLevel(levelsListView.getSelectionModel().getSelectedItem());
                newGame = new Deserializer(manager.getCurrentLevelPath());
                newFXGame = newGame.parseFXGame();
                newFXGame.startCountdown();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SceneManager.getInstance().showPane(GameplayPane.class);

    }

    /**
     * Listener method that executes when a map on the list is selected.
     *
     * @param observable Observable value.
     * @param oldValue   Original value.
     * @param newValue   New value.
     */
    private void onMapSelected(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // TODO
        if (newValue == null || levelsListView.getItems().stream().noneMatch(it -> it.equals(newValue))) {
            levelPreview.setWidth(0);
            levelPreview.setHeight(0);
            return;
        }
        LevelManager.getInstance().setLevel(newValue);
    }

    /**
     * Prompts the user for a map directory.
     *
     * <p>
     * Hint:
     * Use {@link DirectoryChooser} to display a folder selection prompt.
     * </p>
     */
    private void promptUserForMapDirectory() {
        // TODO
        DirectoryChooser chooser = new DirectoryChooser();
        File folder = chooser.showDialog(null);

        if (folder != null) {
            commitMapDirectoryChange(folder);
        }
    }

    /**
     * Actually changes the current map directory.
     *
     * @param dir New directory to change to.
     */
    private void commitMapDirectoryChange(File dir) {
        // TODO
        levelsListView.getSelectionModel().clearSelection();
        LevelManager.getInstance().setMapDirectory(dir.toPath());
    }
}
