package io.tapas;

import static io.tapas.NumberHelper.coinToCent;
import static io.tapas.NumberHelper.iapFees;
import static io.tapas.NumberHelper.tipNetAmount;

/**
 * @author Yoon
 */
public class Cal {
    public static void main(String[] args) {
        IntegerTriple threshold = new IntegerTriple(9476,116504,35783);
        int totalIapCoinAmount = 22292;
        int totalCoinAmount = 62447;
        int totalBalance = 60439;

        int iapTotalCoinAmount = Math.max(totalIapCoinAmount - threshold.getValue2(), 0);
        int totalCoinRevenue = coinToCent(Math.max(totalCoinAmount - threshold.getValue1(), 0));
        int iapRevenue = coinToCent(iapTotalCoinAmount);
        int tapasRevenue = totalCoinRevenue - iapRevenue;

        int iapFees = iapFees(iapRevenue);
        int totalRevenue = tapasRevenue + (iapRevenue - iapFees);

        int tipShareFees = totalRevenue - tipNetAmount(totalRevenue);

        int b = Math.max(totalBalance - threshold.getValue0() - iapFees - tipShareFees, 0);
        System.out.println(b);
    }

    public static int oldBalance(IntegerTriple threshold, OtherRevenue otherRevenue) {
        int iapTotalCoinAmount = otherRevenue.getTotalIapCoinAmount() - threshold.getValue2();
        int totalCoinRevenue = coinToCent(otherRevenue.getTotalCoinAmount() - threshold.getValue1());
        int iapRevenue = coinToCent(iapTotalCoinAmount);
        int tapasRevenue = totalCoinRevenue - iapRevenue;

        int iapFees = iapFees(iapRevenue);
        int totalRevenue = tapasRevenue + (iapRevenue - iapFees);

        int tipShareFees = totalRevenue - tipNetAmount(totalRevenue);

        return Math.max(otherRevenue.getTotalBalance() - threshold.getValue0() - iapFees - tipShareFees, 0);
    }

    public static RevenueDetails revenueDetails(IntegerTriple threshold, OtherRevenue otherRevenue) {
        int adRevenueAmount = Math.max(otherRevenue.getTotalAmount() - otherRevenue.getTotalTipAmount(), 0);
        int adRevenue = Math.max(otherRevenue.getTotalBalance() - otherRevenue.getTotalTipAmount(), 0);
        int tipRevenue = Math.max(otherRevenue.getTotalTipAmount() - threshold.getValue0(), 0);
        int iapFees = 0;
        int tapasFees = 0;
        int totalCoinAmount = 0;
        int totalIapCoinAmount = 0;

        if (tipRevenue > 0) {
            totalIapCoinAmount = Math.max(otherRevenue.getTotalIapCoinAmount() - threshold.getValue2(), 0);
            int iapTotalRevenue = coinToCent(totalIapCoinAmount);
            iapFees = iapFees(iapTotalRevenue);

            totalCoinAmount = Math.max(otherRevenue.getTotalCoinAmount() - threshold.getValue1(), 0);
            int totalCoinRevenue = coinToCent(totalCoinAmount);
            int totalRevenue = Math.max(totalCoinRevenue - iapFees, 0);
            tapasFees = totalRevenue - tipNetAmount(totalRevenue);
        }

        return RevenueDetails.builder()
                .adRevenue(adRevenue)
                .adRevenueAmount(adRevenueAmount)
                .tipRevenue(tipRevenue)
                .iapFees(iapFees)
                .tapasFees(tapasFees)
                .tipTotalCoinAmount(totalCoinAmount)
                .tipIapCoinAmount(totalIapCoinAmount)
                .build();
    }

}
