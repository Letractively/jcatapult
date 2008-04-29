package org.jcatapult.example;

import static org.junit.Assert.*;
import org.junit.Test;

public class CalculatorTest {
    @Test
    public void testAdd() {
        Calculator calc = new Calculator(2, 2);
        assertEquals(4, calc.add());
    }
}