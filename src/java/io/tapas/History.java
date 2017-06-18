package io.tapas;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Yoon
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class History {
    private Long id;
    private int amount;
    private int balance;
    private int coin_amount;
    private int fee;
    private int income;
    private int type;
    private int iap_coin_amount;
    private Integer tipper_id;
    private Timestamp created_date;

    public String aMonthAgo() {
        return ZonedDateTime.ofInstant(this.getCreated_date().toInstant(), ZoneId.of("UTC")).minusMonths(1).format(
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String now() {
        return ZonedDateTime.ofInstant(this.getCreated_date().toInstant(), ZoneId.of("UTC")).format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ZonedDateTime createdDate() {
        return ZonedDateTime.ofInstant(this.getCreated_date().toInstant(), ZoneId.of("UTC"));
    }
}
