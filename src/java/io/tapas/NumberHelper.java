package io.tapas;

/**
 * @author Yoon
 */
public abstract class NumberHelper {

    public static int coinToCent(int coinAmount) {
        return (int) Math.round(coinAmount / 12.0);
    }

    public static int iapFees(int gross) {
        return (int) Math.ceil(gross * 0.3);
    }

    public static int tipNetAmount(int revenue) {
        return (int) Math.round(revenue * (85 / 100.0));
    }
}
