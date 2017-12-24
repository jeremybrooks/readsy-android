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

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by jeremyb on 10/9/17.
 */
public class BitHelperTest {
    @Test
    public void isRead() throws Exception {
        Date date = new Date();
        BitHelper bitHelper = new BitHelper("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0100000000000000000000");
        assertFalse(bitHelper.isRead(date));

        bitHelper = new BitHelper("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0300000000000000000000");
        assertTrue(bitHelper.isRead(date));
    }

    @Test
    public void setRead() throws Exception {

    }

    @Test
    public void whichByte() throws Exception {

    }

    @Test
    public void getDayOfYear() throws Exception {

    }

    @Test
    public void getUnreadItemCount() throws Exception {
        // this bit pattern is all days read up to dec 22, so there should be 9 unread days
        BitHelper bitHelper = new BitHelper("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0f00");
        GregorianCalendar dec31 = new GregorianCalendar();
        dec31.set(GregorianCalendar.YEAR, 2017);
        int currentDayOfYear = dec31.get(Calendar.DAY_OF_YEAR);
        dec31.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
        dec31.set(GregorianCalendar.DAY_OF_MONTH, 31);
        assertEquals(9, bitHelper.getUnreadItemCount(dec31.getTime(), "2017"));

        int unread = bitHelper.getUnreadItemCount(dec31.getTime(), "2017");
        float daysInYear = (float)dec31.get(Calendar.DAY_OF_YEAR);
        float read = daysInYear - unread;
        int pct = (int)((daysInYear - unread)/daysInYear*100);

        System.out.println(daysInYear - currentDayOfYear);
        int percentTarget = (int)((daysInYear - (daysInYear - currentDayOfYear))/daysInYear*100);
        System.out.println(pct);
        System.out.println(percentTarget);
    }


}