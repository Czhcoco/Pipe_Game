package controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Singleton class responsible for loading and managing levels from the filesystem.
 */
public class LevelManager {

    /**
     * Singleton instance.
     */
    @NotNull
    private static final LevelManager INSTANCE = new LevelManager();

    /**
     * List of all loaded level names.
     */
    @NotNull
    private final ObservableList<String> levelNames = FXCollections.observableArrayList();
    /**
     * The JavaFX property housing the current level name.
     * <p>
     * The {@link String} backing the property may be {@code null} if no level is loaded from the filesystem.
     */
    @NotNull
    private final StringProperty curLevelNameProperty = new SimpleStringProperty();

    /**
     * Path to the map directory. Defaults to the current working directory.
     */
    @NotNull
    private Path mapDirectory = Paths.get("");

    private LevelManager() {
        setMapDirectory(mapDirectory);
    }

    /**
     * @return Singleton instance of this class.
     */
    @NotNull
    public static LevelManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the current map directory, and loads all maps from the newly set directory.
     *
     * @param mapDirectory New map directory to load maps from.
     */
    public void setMapDirectory(@NotNull Path mapDirectory) {
        // TODO
        this.mapDirectory = mapDirectory;
        loadLevelNamesFromDisk();
    }

    /**
     * Loads all level names from the currently set {@link LevelManager#mapDirectory}.
     *
     * <p>
     * Hint:
     * <ul>
     * <li>Use try-with-resources to automatically manage the lifetime of {@link Stream}.</li>
     * <li>Use {@link Files#walk(Path, int, FileVisitOption...)} to retrieve all files inside a given directory. Check
     * the documentation to see what to put for the second parameter!</li>
     * <li>Use {@link Stream} to filter, map and sort the loaded filenames.</li>
     * </ul>
     * </p>
     */
    private void loadLevelNamesFromDisk() {
        // TODO
        levelNames.clear();
        try (Stream<Path> stream = Files.walk(Paths.get(String.valueOf(mapDirectory)), 1)) {
            List<String> files = stream
                    .filter(f -> f.toFile().isFile())
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(it -> it.endsWith(".map"))
                    .sorted(String::compareTo)
                    .collect(Collectors.toList());
                levelNames.clear();
                levelNames.addAll(files);
        } catch (IOException e) {
            levelNames.clear();

            Alert box = new Alert(Alert.AlertType.WARNING);
            box.setHeaderText("Cannot open folder");
            box.setContentText("Check if you have the permission to access this folder.");
            Platform.runLater(box::showAndWait);
        }

    }

    @NotNull
    public ObservableList<String> getLevelNames() {
        return levelNames;
    }

    /**
     * @return Full path to the currently selected level.
     */
    @NotNull
    public Path getCurrentLevelPath() {
        // TODO
        Path path = Paths.get(String.valueOf(mapDirectory), curLevelNameProperty.get());
        return path;
    }

    /**
     * Sets the currently selected level.
     *
     * @param levelName Name of the newly selected level, or {@code null} if a level is not loaded.
     * @throws IllegalArgumentException When the level name is blank.
     */
    public void setLevel(@Nullable String levelName) {
        // TODO
        if (levelName == null || levelName.isEmpty()) {
            throw new IllegalArgumentException("Invalid level name");
        }

        this.curLevelNameProperty.setValue(levelName);
    }

    /**
     * Retrieves and sets the next level.
     * <p>
     * If there is a "next map" in the directory, set the level to that map. Otherwise, set the level to {@code null}.
     * </p>
     * <p>
     * Hint:
     * <ul>
     * <li>Remember to check whether the string in {@code curLevelNameProperty} is empty or not.</li>
     * </ul>
     * </p>
     *
     * @return Name of the next map if present, {@code null} otherwise.
     */
    @Nullable
    public String getAndSetNextLevel() {
        // TODO
//        if (curLevelNameProperty.getName().isEmpty())
//            throw new IllegalArgumentException("curLevelNameProperty is empty");
        int current = levelNames.indexOf(curLevelNameProperty.getValue());
        System.out.println(current);
        if (current == -1) {
            throw new IllegalStateException("Current Level Name is not in Level Names list!");
        }

        if (current < levelNames.size() - 1) {
            this.curLevelNameProperty.setValue(levelNames.get(current + 1));
            return levelNames.get(current + 1);
        } else {
            return null;
        }
    }

    @NotNull
    public StringProperty getCurrentLevelProperty() {
        return curLevelNameProperty;
    }
}
