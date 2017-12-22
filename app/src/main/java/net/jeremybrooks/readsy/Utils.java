package net.jeremybrooks.readsy;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * Some file handling utilities.
 */

public class Utils {
    public static void close(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public static void close(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }


    public static void close(Reader r) {
        if (r != null) {
            try {
                r.close();
            } catch (Exception e) {
                //ignore
            }
        }
    }
}
