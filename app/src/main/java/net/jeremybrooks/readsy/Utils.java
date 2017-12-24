/*
 * readsy - read something new every day <http://jeremybrooks.net/readsy>
 *
 * Copyright (c) 2017  Jeremy Brooks
 *
 * This file is part of readsy for Android.
 *
 * readsy for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * readsy for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with readsy for Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
