package io.tapas;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Yoon
 */
@Getter
@Builder
@ToString
public class RevenueDetails {
    private int adRevenue;
    private int adRevenueAmount;
    private int tipRevenue;
    private int iapFees;
    private int tapasFees;
    private int tipTotalCoinAmount;
    private int tipIapCoinAmount;

    public int tipFees() {
        return this.iapFees + tapasFees;
    }

    public static RevenueDetails empty() {
        return new RevenueDetails(0, 0, 0, 0, 0, 0, 0);
    }

    public int tipNetRevenue() {
        return Math.max(this.tipRevenue - this.tipFees(), 0);
    }

    public int availableBalance() {
        return Math.max(this.getAdRevenue() + this.tipNetRevenue(), 0);
    }

    public int totalRevenue() {
        return this.adRevenueAmount + this.tipRevenue;
    }

    public int totalBalance() {
        return this.adRevenue + this.tipRevenue;
    }

    public int tipAmount() {
        return NumberHelper.coinToCent(this.tipTotalCoinAmount);
    }
}
