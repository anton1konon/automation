package org.example;

import org.example.annotations.InterfaceMethod;
import org.example.annotations.RunWithDouble;
import org.example.annotations.WithInterface;

@WithInterface(interfaceName = "MathInterface")
public class MathForDouble {

    private final double aDouble;

    public MathForDouble(double d) {
        aDouble = d;
    }

    @InterfaceMethod
    public double getRoot() {
        return Math.sqrt(aDouble);
    }

    @InterfaceMethod
    public double pow(int pow) {
        return Math.pow(aDouble,pow);
    }

    @InterfaceMethod
    public double sin() {
        return Math.sin(aDouble);
    }

    @RunWithDouble(doubleValue = 10.4)
    public double multiplyBy(double d) {
        return aDouble * d;
    }



}
