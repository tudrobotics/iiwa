package org.tud.kuka.util;

public class NumberUtil {

	public static double round(final double value, final int frac) { 
        return Math.round(Math.pow(10.0, frac) * value) / Math.pow(10.0, frac); 
    }
}
