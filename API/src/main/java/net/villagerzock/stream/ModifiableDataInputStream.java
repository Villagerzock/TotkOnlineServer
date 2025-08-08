package net.villagerzock.stream;

import java.io.DataInputStream;
import java.io.InputStream;

public class ModifiableDataInputStream extends DataInputStream {
    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public ModifiableDataInputStream(InputStream in) {
        super(in);
    }
    public void setIn(InputStream in){
        this.in = in;
    }
}
