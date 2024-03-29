package models;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer for handling flow events.
 */
public class FlowTimer {

    /**
     * Default delay before the water starts flowing.
     */
    private static int defaultDelay = 2;

    /**
     * Default duration between each water flow.
     */
    private static int defaultFlowDuration = 3;

    /**
     * Backing timer.
     */
    @NotNull
    private final Timer flowTimer = new Timer(true);

    /**
     * Current value of the flow timer.
     */
    private final IntegerProperty currentValue = new SimpleIntegerProperty();

    /**
     * List of callbacks to execute when a tick has passed.
     */
    private final List<Runnable> onTickCallbacks = new ArrayList<>();
    /**
     * List of callbacks to execute when the water flows an additional tile.
     */
    private final List<Runnable> onFlowCallbacks = new ArrayList<>();

    private int ticksElapsed;

    /**
     * Sets the default delay of all {@link FlowTimer}.
     *
     * @param delay New default delay.
     */
    public static void setDefaultDelay(int delay) {
        defaultDelay = delay;
    }

    /**
     * @return Current default delay of all {@link FlowTimer}.
     */
    public static int getDefaultDelay() {
        return defaultDelay;
    }

    /**
     * Sets the default duration between each flow event.
     *
     * @param duration New default duration.
     */
    public static void setDefaultFlowDuration(int duration) {
        defaultFlowDuration = duration;
    }

    /**
     * @return Current default duration between flow events of all {@link FlowTimer}.
     */
    public static int getDefaultFlowDuration() {
        return defaultFlowDuration;
    }

    /**
     * Creates an instance with default delay.
     *
     * <p>
     * This constructor should also register a callback to increment current value on flow.
     * </p>
     */
    FlowTimer() {
        this(defaultDelay);
    }

    /**
     * Creates an instance with custom delay.
     *
     * <p>
     * This constructor should also register a callback to increment current value on flow.
     * </p>
     *
     * @param initialValue Initial delay value.
     */
    FlowTimer(int initialValue) {
        // TODO
        currentValue.set(-1);
        ticksElapsed = 0;
        defaultDelay = initialValue;
        registerFlowCallback(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()-> {
                    currentValue.set(currentValue.get() + 1);
                    System.out.println(currentValue.getValue());
                });
            }
        });

        registerTickCallback(new TimerTask() {
             @Override
             public void run() {
                 Platform.runLater(() -> {
                     ticksElapsed++;
//                     System.out.println(ticksElapsed);
                 });
             }
         });
    }

    /**
     * Registers a callback to be run when the water flow into an additional tile.
     *
     * @param cb Callback to run.
     */
    void registerFlowCallback(@NotNull final Runnable cb) {
        onFlowCallbacks.add(cb);
    }

    /**
     * Registers a callback to be run when a tick has passed.
     *
     * @param cb Callback to run.
     */
    void registerTickCallback(@NotNull final Runnable cb) {
        onTickCallbacks.add(cb);
    }

    /**
     * Starts the timer.
     *
     * <p>
     * The timer should tick down every one second, with a delay of one second. The water should flow an additional tile
     * every {@link FlowTimer#defaultFlowDuration} seconds.
     * </p>
     */
    void start() {
        // TODO
        onTickCallbacks.forEach((t) -> {
            System.out.println("tick");
            flowTimer.scheduleAtFixedRate((TimerTask) t, 1000, 1000);
        });

        onFlowCallbacks.forEach((t) -> {
            flowTimer.scheduleAtFixedRate((TimerTask) t, 1000 * defaultDelay, 1000 * defaultFlowDuration);
        });

//        flowTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                    ticksElapsed++;
//                    if (ticksElapsed % defaultFlowDuration == 0)
//                        currentValue.set(currentValue.get() + 1);
//            }
//        }, 1000, 1000);
    }

    void restart(int restartDistance) {
        currentValue.set(restartDistance);
        onTickCallbacks.forEach((t) -> {
            flowTimer.scheduleAtFixedRate((TimerTask) t, 1000, 1000);
        });

        onFlowCallbacks.forEach((t) -> {
            flowTimer.scheduleAtFixedRate((TimerTask) t, 0, 1000 * defaultFlowDuration);
        });
    }

    /**
     * Stops the timer.
     */
    void stop() {
        // TODO
        flowTimer.cancel();
    }

    /**
     * @return Current distance of the water flow. If this value is negative, no pipe should be filled yet.
     */
    int distance() {
        return currentValue.get();
    }
}
