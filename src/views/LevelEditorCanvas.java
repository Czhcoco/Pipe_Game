package views;

import controllers.Renderer;
import io.Deserializer;
import io.GameProperties;
import io.Serializer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import models.exceptions.InvalidMapException;
import models.map.cells.Cell;
import models.map.cells.FillableCell;
import models.map.cells.TerminationCell;
import models.map.cells.Wall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static models.Config.TILE_SIZE;

public class LevelEditorCanvas extends Canvas {

    private static final String MSG_MISSING_SOURCE = "Source tile is missing!";
    private static final String MSG_MISSING_SINK = "Sink tile is missing!";
    private static final String MSG_BAD_DIMS = "Map size must be at least 2x2!";
    private static final String MSG_BAD_DELAY = "Delay must be a positive value!";
    private static final String MSG_SOURCE_TO_WALL = "Source tile is blocked by a wall!";
    private static final String MSG_SINK_TO_WALL = "Sink tile is blocked by a wall!";

    private GameProperties gameProp;

    @Nullable
    private TerminationCell sourceCell;
    @Nullable
    private TerminationCell sinkCell;

    public LevelEditorCanvas(int rows, int cols, int delay) {
        super();

        resetMap(rows, cols, delay);
    }

    /**
     * Changes the attributes of this canvas.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    public void changeAttributes(int rows, int cols, int delay) {
        resetMap(rows, cols, delay);
    }

    /**
     * Resets the map with the given attributes.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    private void resetMap(int rows, int cols, int delay) {
        // TODO
        gameProp.rows = rows;
        gameProp.cols = cols;
        gameProp.delay = delay;
    }

    /**
     * Renders the canvas.
     */
    private void renderCanvas() {
        Platform.runLater(() -> Renderer.renderMap(this, gameProp.cells));
    }

    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You may need to check/compute some attribute in order to create the new {@link Cell} object.
     *
     * @param sel Selected {@link CellSelection}.
     * @param x   X-coordinate relative to the canvas.
     * @param y   Y-coordinate relative to the canvas.
     */
    public void setTile(@NotNull CellSelection sel, double x, double y) {
        // TODO
        int row = (int)y / TILE_SIZE;
        int col = (int)x / TILE_SIZE;
        Coordinate coord = new Coordinate(row, col);
        switch(sel.text){
            case "Wall":
                setTileByMapCoord(new Wall(coord));
                break;
            case "Cell":
                if(!((row == 0 && col == 0) ||
                        (row == 0 && col == gameProp.cols - 1) ||
                        (row == gameProp.rows - 1 && col == 0) ||
                        (row == gameProp.rows - 1 && col == gameProp.cols - 1)))
                    setTileByMapCoord(new FillableCell(coord));
                break;
            case "Source/Sink":
                if((row == 0 && col == 0) ||
                        (row == 0 && col == gameProp.cols - 1) ||
                        (row == gameProp.rows - 1 && col == 0) ||
                        (row == gameProp.rows - 1 && col == gameProp.cols-1))
                    break;
                if(row == 0)
                    setTileByMapCoord(new TerminationCell(coord, Direction.UP, TerminationCell.Type.SINK));
                else if(row == gameProp.rows - 1)
                    setTileByMapCoord(new TerminationCell(coord, Direction.DOWN, TerminationCell.Type.SINK));
                else if(col == 0)
                    setTileByMapCoord(new TerminationCell(coord, Direction.LEFT, TerminationCell.Type.SINK));
                else if(col == gameProp.cols - 1)
                    setTileByMapCoord(new TerminationCell(coord, Direction.RIGHT, TerminationCell.Type.SINK));
                else
                    setTileByMapCoord(new TerminationCell(coord, Direction.UP, TerminationCell.Type.SOURCE));
        }
    }

    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You will need to make sure that there is only one source/sink cells in the map.
     *
     * @param cell The {@link Cell} object to set.
     */
    private void setTileByMapCoord(@NotNull Cell cell) {
        // TODO
        gameProp.cells[cell.coord.row][cell.coord.col] = cell;
    }

    /**
     * Toggles the rotation of the source tile clockwise.
     */
    public void toggleSourceTileRotation() {
        // TODO
        for(int i = 1; i < gameProp.rows - 1; i++){
            for(int j = 1; j < gameProp.cols - 1; j++){
                if(gameProp.cells[i][j] instanceof TerminationCell) {
                    Direction old = ((TerminationCell)gameProp.cells[i][j]).pointingTo;
                    Direction now = null;
                    switch (old){
                        case UP:
                            now = Direction.RIGHT; break;
                        case RIGHT:
                            now = Direction.DOWN; break;
                        case DOWN:
                            now = Direction.LEFT; break;
                        case LEFT:
                            now = Direction.UP; break;
                    }
                    gameProp.cells[i][j] = new TerminationCell(new Coordinate(i, j), now, TerminationCell.Type.SOURCE);
                }
            }
        }
    }

    /**
     * Loads a map from a file.
     * <p>
     * Prompts the player if they want to discard the changes, displays the file chooser prompt, and loads the file.
     *
     * @return {@code true} if the file is loaded successfully.
     */
    public boolean loadFromFile() {
        // TODO
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Load a map from file?");
        alert.setContentText("Current map contents will be lost.");

        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.OK)){
            return loadFromFile(getTargetLoadFile().toPath());
        } else {
            return false;
        }
    }

    /**
     * Prompts the user for the file to load.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to load, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetLoadFile() {
        // TODO
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter mapExFilter = new FileChooser.ExtensionFilter("map files (.map)", "*.map");
        fc.getExtensionFilters().add(mapExFilter);
        fc.setSelectedExtensionFilter(mapExFilter);
        File file = fc.showOpenDialog(null);
        return file;
    }

    /**
     * Loads the file from the given path and replaces the current {@link LevelEditorCanvas#gameProp}.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from loading in this method.
     *
     * @param path Path to load the file from.
     * @return {@code true} if the file is loaded successfully, {@code false} otherwise.
     */
    private boolean loadFromFile(@NotNull Path path) {
        // TODO
        try {
            Deserializer des = new Deserializer(path);
            gameProp = des.parseGameFile();
            return true;
        } catch (InvalidMapException e) {
            return false;
        }
        catch (FileNotFoundException e){
            return false;
        }
    }

    /**
     * Checks the validity of the map, prompts the player for the target save directory, and saves the file.
     */
    public void saveToFile() {
        // TODO
        if (checkValidity().isPresent()) {
            return;
        }

        File file = getTargetSaveDirectory();
        if (file != null) {
            exportToFile(file.toPath());
        }
    }

    /**
     * Prompts the user for the directory and filename to save as.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to save to, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetSaveDirectory() {
        // TODO
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Map");
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("map files (.map)", Collections.singletonList("*.map")));

        return chooser.showSaveDialog(null);
    }

    /**
     * Exports the current map to a file.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from saving in this method.
     *
     * @param p Path to export to.
     */
    private void exportToFile(@NotNull Path p) {
        // TODO
        try {
            Serializer serializer = new Serializer(p);
            serializer.serializeGameProp(gameProp);
        }
        catch (IOException e){
            System.err.println("Unable to write data!");
            e.printStackTrace();
        }

    }

    /**
     * Checks whether the current map and its properties are valid.
     * <p>
     * Hint:
     * You should check for the following conditions:
     * <ul>
     * <li>Source cell is present</li>
     * <li>Sink cell is present</li>
     * <li>Minimum map size is 2x2</li>
     * <li>Flow delay is at least 1</li>
     * <li>Source/Sink tiles are not blocked by walls</li>
     * </ul>
     *
     * @return {@link Optional} containing the error message, or an empty {@link Optional} if the map is valid.
     */
    private Optional<String> checkValidity() {
        // TODO
        int sinkCount = 0;
        int sourceCount = 0;
        Cell sinkCell = null;
        Cell sourceCell = null;

        for (int i = 0; i < gameProp.rows - 1; i++) {
            if (gameProp.cells[i][0] instanceof TerminationCell) {
                sinkCount++;
                sinkCell = gameProp.cells[i][0];
            }
            if (gameProp.cells[i][gameProp.cols - 1] instanceof TerminationCell){
                sinkCount++;
                sinkCell = gameProp.cells[i][gameProp.cols - 1];
            }
        }

        for (int i = 0; i < gameProp.cols - 1; i++) {
            if (gameProp.cells[0][i] instanceof TerminationCell) {
                sinkCount++;
                sinkCell = gameProp.cells[0][i];
            }
            if (gameProp.cells[gameProp.rows - 1][i] instanceof TerminationCell){
                sinkCount++;
                sinkCell = gameProp.cells[gameProp.rows-1][i];
            }
        }

        for (int i = 1; i < gameProp.rows - 2; i++) {
            for (int j = 1; j < gameProp.cols - 2; j++)
                if (gameProp.cells[i][j] instanceof TerminationCell) {
                    sourceCount++;
                    sourceCell = gameProp.cells[i][j];
                }
        }

        if(sinkCount == 0)
            return Optional.of(MSG_MISSING_SINK);
        else if(sourceCount == 0)
            return Optional.of(MSG_MISSING_SOURCE);
        else if(gameProp.rows < 2 || gameProp.cols < 2)
            return Optional.of(MSG_BAD_DIMS);
        else if(gameProp.delay <= 0)
            return Optional.of(MSG_BAD_DELAY);
        else{
            Coordinate sourceNext = sourceCell.coord.add(((TerminationCell)sourceCell).pointingTo.getOffset());
            Coordinate sinkNext = sinkCell.coord.add(((TerminationCell)sinkCell).pointingTo.getOpposite().getOffset());
            if(gameProp.cells[sourceNext.row][sourceNext.col] instanceof Wall)
                return Optional.of(MSG_SOURCE_TO_WALL);
            else if(gameProp.cells[sinkNext.row][sinkNext.col] instanceof Wall)
                return Optional.of(MSG_SINK_TO_WALL);
        }

        return Optional.empty();
    }

    public int getNumOfRows() {
        return gameProp.rows;
    }

    public int getNumOfCols() {
        return gameProp.cols;
    }

    public int getAmountOfDelay() {
        return gameProp.delay;
    }

    public void setAmountOfDelay(int delay) {
        gameProp.delay = delay;
    }

    public enum CellSelection {
        WALL("Wall"),
        CELL("Cell"),
        TERMINATION_CELL("Source/Sink");

        private String text;

        CellSelection(@NotNull String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
