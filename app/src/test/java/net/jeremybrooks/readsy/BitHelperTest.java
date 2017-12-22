package net.jeremybrooks.readsy;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

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

    }


}