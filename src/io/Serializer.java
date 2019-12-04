package io;

import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * A serializer for converting {@link GameProperties} into a map file.
 */
public class Serializer {

    /**
     * Path to the map to serialize to.
     */
    @NotNull
    private Path path;

    public Serializer(@NotNull final Path path) {
        this.path = path;
    }

    /**
     * Serializes a {@link GameProperties} object and saves it into a file.
     *
     * @param prop {@link GameProperties} object to serialize and save.
     * @throws IOException if an I/O exception has occurred.
     */
    public void serializeGameProp(@NotNull final GameProperties prop) throws IOException {
        // TODO
        PrintWriter b = new PrintWriter(path.toString());

        b.print("# rows");
        b.println();
        b.print(prop.rows);
        b.println();

        b.print("# cols");
        b.println();
        b.print(prop.cols);
        b.println();

        b.print("# delay before first flow");
        b.println();
        b.print(prop.delay);
        b.println();

        b.print("# map");
        b.println();
        for(int i = 0; i < prop.cells.length; i++){
            for(int j = 0; j < prop.cells[0].length; j++){
                b.print(prop.cells[i][j].toSerializedRep());
            }
            b.println();
        }

        b.flush();

    }
}
