package io.tapas;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Yoon
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class HistorySum {
    private int amount;
    private int balance;
    private int coin_amount;
    private int iap_coin_amount;
    private int total_tip_amount;
}
