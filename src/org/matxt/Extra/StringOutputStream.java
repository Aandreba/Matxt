package org.matxt.Extra;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {
    final private StringBuilder builder;

    public StringOutputStream () {
        this.builder = new StringBuilder();
    }

    @Override
    public void write (int b) throws IOException {
        builder.append((char) b);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
