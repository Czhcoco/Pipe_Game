package views;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Displays info about the current level being played by the user.
 */
public class GameplayInfoPane extends BigVBox {
    private final Label levelNameLabel = new Label();
    private final Label timerLabel = new Label();
    private final Label numMovesLabel = new Label();
    private final Label numUndoLabel = new Label();

    public GameplayInfoPane(StringProperty levelNameProperty, IntegerProperty timerProperty, IntegerProperty numMovesProperty, IntegerProperty numUndoProperty) {
        // TODO
//        bindTo(levelNameProperty, timerProperty, numMovesProperty, numUndoProperty);
        this.getChildren().addAll(levelNameLabel, timerLabel, numMovesLabel, numUndoLabel);
    }

    /**
     * @param s Seconds duration
     * @return A string that formats the duration stopwatch style
     */
    private static String format(int s) {
        final var d = Duration.of(s, SECONDS);

        int seconds = d.toSecondsPart();
        int minutes = d.toMinutesPart();

        return String.format("%02d:%02d", minutes, seconds);
        // Uncomment next line for JDK 8
//        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }

    /**
     * Binds all properties to their respective UI elements.
     *
     * @param levelNameProperty Level Name Property
     * @param timerProperty Timer Property
     * @param numMovesProperty Number of Moves Property
     * @param numUndoProperty Number of Undoes Property
     */
    public void bindTo(StringProperty levelNameProperty, IntegerProperty timerProperty, IntegerProperty numMovesProperty, IntegerProperty numUndoProperty) {
        // TODO
        levelNameLabel.textProperty().bind(Bindings.createStringBinding(()->"Level: " + levelNameProperty.get(),levelNameProperty));
        timerLabel.textProperty().bind(Bindings.createStringBinding(()->"Time: " + format(timerProperty.get()),timerProperty));
        numMovesLabel.textProperty().bind(Bindings.createStringBinding(()->"Moves: "+ numMovesProperty.get(),numMovesProperty));
        numUndoLabel.textProperty().bind(Bindings.createStringBinding(()->"Undo Count: "+ numUndoProperty.get(),numMovesProperty));

    }
}

