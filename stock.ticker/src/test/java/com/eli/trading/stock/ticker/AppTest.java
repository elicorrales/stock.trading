package com.eli.trading.stock.ticker;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{

    @Test
    public void shouldAnswerWithTrue() throws IOException
    {
        Fidelity fidelity = new Fidelity();
        fidelity.doIt(null);
    }

}
