package io;

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
        BufferedWriter b = new BufferedWriter(new PrintWriter(path.toFile()));
        b.write("# rows");
        b.newLine();
        b.write(prop.rows);

        b.newLine();
        b.newLine();

        b.write("# cols");
        b.newLine();
        b.write(prop.cols);

        b.newLine();
        b.newLine();

        b.write("# delay before first flow");
        b.newLine();
        b.write(prop.delay);

        b.newLine();
        b.newLine();

        b.write("# map");
        b.newLine();
        for(int i = 0; i < prop.cells.length; i++){
            for(int j = 0; j < prop.cells[0].length; j++){
                b.write(prop.cells[i][j].toSingleChar());
            }
            b.newLine();
        }

        b.newLine();
        b.write("# optional: list of pipes to start with");
        b.newLine();
        b.write("# TR: Top-Right, TL: Top-Left, BL: Bottom-Left, BR: Bottom-Right, CR: Cross");
        b.newLine();
        for(int i = 0; i < prop.pipes.size(); i++){
            b.write(prop.pipes.get(i).toString());
            if(i != prop.pipes.size()-1)
                b.write(", ");
        }
        b.newLine();
    }
}
