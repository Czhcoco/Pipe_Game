package controllers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import models.map.cells.Cell;
import models.pipes.Pipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static models.Config.TILE_SIZE;

/**
 * Helper class for render operations on a {@link Canvas}.
 */
public class Renderer {

    /**
     * Padding between two tiles in a queue.
     */
    private static final int QUEUE_TILE_PADDING = 8;

    /**
     * An image of a cell, with support for rotated images.
     */
    public static class CellImage {

        /**
         * Image of the cell.
         */
        @NotNull
        final Image image;
        /**
         * Rotation of the image.
         */
        final float rotation;

        /**
         * @param image    Image of the cell.
         * @param rotation Rotation of the image.
         */
        public CellImage(@NotNull Image image, float rotation) {
            this.image = image;
            this.rotation = rotation;
        }
    }

    /**
     * Sets the current rotation of a {@link GraphicsContext}.
     *
     * @param gc     Target Graphics Context.
     * @param angle  Angle to rotate the context by.
     * @param pivotX X-coordinate of the pivot point.
     * @param pivotY Y-coordinate of the pivot point.
     */
    private static void rotate(@NotNull GraphicsContext gc, double angle, double pivotX, double pivotY) {
        final var r = new Rotate(angle, pivotX, pivotY);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    /**
     * Draws a rotated image onto a {@link GraphicsContext}.
     *
     * @param gc    Target Graphics Context.
     * @param image Image to draw.
     * @param angle Angle to rotate the image by.
     * @param x     X-coordinate relative to the graphics context to draw the top-left of the image.
     * @param y     Y-coordinate relative to the graphics context to draw the top-left of the image.
     */
    private static void drawRotatedImage(@NotNull GraphicsContext gc, @NotNull Image image, double angle, double x, double y) {
        // TODO
        gc.save();
        rotate(gc, angle, x + image.getWidth()/2, y + image.getHeight()/2);
        gc.drawImage(image, x, y);
        gc.restore();
    }

    /**
     * Renders a map into a {@link Canvas}.
     *
     * @param canvas Canvas to render to.
     * @param map    Map to render.
     */
    public static void renderMap(@NotNull Canvas canvas, @NotNull Cell[][] map) {
        // TODO
        int row = map.length;
        int col = map[0].length;

        canvas.setHeight(row * TILE_SIZE);
        canvas.setWidth(col *TILE_SIZE);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                CellImage image = map[i][j].getImageRep();
                drawRotatedImage(gc, image.image, image.rotation, j*TILE_SIZE, i*TILE_SIZE);
            }
        }
    }

    /**
     * Renders a pipe queue into a {@link Canvas}.
     *
     * @param canvas    Canvas to render to.
     * @param pipeQueue Pipe queue to render.
     */
    public static void renderQueue(@NotNull Canvas canvas, @NotNull List<Pipe> pipeQueue) {
        // TODO
        int width = pipeQueue.size();
        canvas.setHeight(TILE_SIZE);
        canvas.setWidth(width * (TILE_SIZE + 3 * QUEUE_TILE_PADDING));

        GraphicsContext gc = canvas.getGraphicsContext2D();
        for(int i = 0; i < pipeQueue.size(); i++){
            CellImage image = pipeQueue.get(i).getImageRep();
            drawRotatedImage(gc, image.image, image.rotation, i * TILE_SIZE + (i + 3) * QUEUE_TILE_PADDING, 0);
        }
    }
}
