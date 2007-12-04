package org.jcatapult.example;

public class Calculator {
    private int one;
    private int two;

    public Calculator(int one, int two) {
        this.one = one;
        this.two = two;
    }

    public int add() {
        return one + two;
    }
}