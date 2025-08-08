package net.villagerzock.stream;

import imgui.ImGui;
import net.villagerzock.Server;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TrackingPrintStream extends PrintStream {
    private TrackingDataOutputStream trackingDataOutputStream;

    @Override
    public void flush() {
        super.flush();
        trackingDataOutputStream.changed.set(false);
    }

    private TrackingPrintStream(TrackingDataOutputStream out) {
        super(out);
        trackingDataOutputStream = out;
    }
    public static TrackingPrintStream create(OutputStream outputStream){
        TrackingDataOutputStream dataOutputStream = new TrackingDataOutputStream(outputStream);
        return new TrackingPrintStream(dataOutputStream);
    }

    public boolean hasChanged() {
        return trackingDataOutputStream.changed.get();
    }

    private static class TrackingDataOutputStream extends DataOutputStream{

        /**
         * Creates a new data output stream to write data to the specified
         * underlying output stream. The counter {@code written} is
         * set to zero.
         *
         * @param out the underlying output stream, to be saved for later
         *            use.
         * @see FilterOutputStream#out
         */
        public TrackingDataOutputStream(OutputStream out) {
            super(out);
        }

        public AtomicBoolean changed = new AtomicBoolean(false);
        @Override
        public synchronized void write(int b) throws IOException {
            super.write(b);
            Server.originalPrintStream.println("Tracker Caught!");
            changed.set(true);
        }

        @Override
        public void write(byte[] b) throws IOException {
            super.write(b);
            Server.originalPrintStream.println("Tracker Caught!");
            changed.set(true);
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException {
            super.write(b, off, len);
            Server.originalPrintStream.println("Tracker Caught!");
            changed.set(true);
        }
    }
}
