package md.leonis.tivi.admin.utils;

import lombok.NoArgsConstructor;

import java.io.OutputStream;

@NoArgsConstructor
public class NullOutputStream extends OutputStream {

    public void write (final int i) {}

    public void write (final byte[] bytes) {}

    public void write (final byte[] bytes, final int off, final int len) {}

}