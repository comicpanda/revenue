package io.tapas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        Long[] ids = new Long[]{2320L, 2506L, 201L, 769L, 393L, 672L, 200L, 756L,
                2122L, 2784L, 861L, 1403L, 3003L, 5513L, 3439L, 5309L, 1717L};

        for (Long id : ids) {
            System.out.println("[ " + id + " ]---------------------------------------------------------------");
            String s1 = String.format(
                    "select * from other_revenue_history where other_revenue_id = %d and type = 2 order by id desc limit 2",
                    id);
            String s2 = "select sum(amount) as amount, sum(balance) as balance, sum(coin_amount) as coin_amount, sum(iap_coin_amount) as iap_coin_amount, sum(if(type = 4, amount, 0)) as total_tip_amount   from other_revenue_history where other_revenue_id = %d and id";
            String s3 = "select * from other_revenue_history where other_revenue_id = %d and description = 'Tip-related Fees' order by id desc limit 1";
            String s4 = "select sum(this_.amount) as amount, sum(this_.coin_amount) as coin_amount, sum(this_.iap_coin_amount) as iap_coin_amount from other_revenue_history this_ where this_.other_revenue_id=%d and this_.type=4 and this_.created_date between '%s' and '%s'";
            List<History> list1 = jdbcTemplate.query(s1, new BeanPropertyRowMapper(History.class));

            if (list1.size() == 1) {
                Long hId = list1.get(0).getId();
                s2 = String.format(s2 + " < %d and description <> 'Tip-related Fees'", id, hId);
            } else {
                Long hId = list1.get(0).getId();
                Long hId1 = list1.get(1).getId();
                s2 = String.format(s2 + " between %d and %d and description <> 'Tip-related Fees' and type <> 2",
                        id,
                        hId1,
                        hId);
            }
            History history = list1.get(0);
            HistorySum sum = jdbcTemplate.query(s2, new BeanPropertyRowMapper<>(HistorySum.class)).get(0);
            History tipHistory = jdbcTemplate.query(String.format(s3, id), new BeanPropertyRowMapper<>(History.class))
                    .get(0);

            HistorySum thresholdCoin = jdbcTemplate.query(String.format(s4, id, history.aMonthAgo(), history.now()),
                    new BeanPropertyRowMapper<>(HistorySum.class)).get(0);
            IntegerTriple it = new IntegerTriple(thresholdCoin.getAmount(),
                    thresholdCoin.getCoin_amount(),
                    thresholdCoin.getIap_coin_amount());
            OtherRevenue otherRevenue = new OtherRevenue();
            otherRevenue.setTotalAmount(sum.getAmount());
            otherRevenue.setTotalBalance(sum.getBalance());
            otherRevenue.setTotalCoinAmount(sum.getCoin_amount());
            otherRevenue.setTotalIapCoinAmount(sum.getIap_coin_amount());
            otherRevenue.setTotalTipAmount(sum.getTotal_tip_amount());
            int balance = Cal.oldBalance(it, otherRevenue);
            RevenueDetails details = Cal.revenueDetails(it, otherRevenue);
            System.out.println("payout history : " + history);
            System.out.println("sum  : " + sum);
            System.out.println("tip fees  : " + tipHistory);
            System.out.println("threshold  : " + thresholdCoin);
            System.out.println("real: " + details);
            System.out.println("balance : " + balance);
            System.out.println("totalRevenue : " + details.totalRevenue());
            System.out.println("totalBalance : " + details.totalBalance());

            String s5 = "update other_revenue orr set \n" +
                    "total_amount = (select sum(amount) + %d from other_revenue_history where id > %d and description <> 'Tip-related Fees' and other_revenue_id = orr.id),\n" +
                    "total_balance = (select sum(balance) +%d from other_revenue_history where id > %d and description <> 'Tip-related Fees' and other_revenue_id = orr.id),\n" +
                    "total_tip_amount = (select sum(if(type = 4, amount, 0)) + %d from other_revenue_history where id > %d and description <> 'Tip-related Fees' and other_revenue_id = orr.id),\n" +
                    "total_coin_amount = (select sum(coin_amount) + %d from other_revenue_history where id > %d and description <> 'Tip-related Fees' and other_revenue_id = orr.id),\n" +
                    "total_iap_coin_amount = (select sum(iap_coin_amount) + %d from other_revenue_history where id > %d and description <> 'Tip-related Fees' and other_revenue_id = orr.id)\n" +
                    "where orr.id = %d ;";
            if (tipHistory.createdDate()
                    .isAfter(ZonedDateTime.of(2017, 6, 7, 00, 00, 00, 00, ZoneId.systemDefault()))) {
                System.out.println(String.format("update other_revenue_history set " +
                                "coin_amount = -%d , iap_coin_amount = -%d where id = %d; /* %d */",
                        details.getTipTotalCoinAmount(),
                        details.getTipIapCoinAmount(),
                        history.getId(), thresholdCoin.getAmount()));
                System.out.println(String.format(s5,
                        thresholdCoin.getAmount(), history.getId(),
                        thresholdCoin.getAmount(), history.getId(),
                        thresholdCoin.getAmount(), history.getId(),
                        thresholdCoin.getCoin_amount(), history.getId(),
                        thresholdCoin.getIap_coin_amount(), history.getId(),
                        id
                ));
            } else {
                System.out.println(String.format("update other_revenue_history set " +
                                "coin_amount = -%d , iap_coin_amount = -%d where id = %d; /*-  %d */",
                        0, 0,
                        history.getId(), sum.getTotal_tip_amount()));
                System.out.println(String.format(s5,
                        sum.getTotal_tip_amount(), history.getId(),
                        sum.getBalance() - details.getAdRevenue(), history.getId(),
                        sum.getTotal_tip_amount(), history.getId(),
                        sum.getCoin_amount(), history.getId(),
                        sum.getIap_coin_amount(), history.getId(),
                        id
                ));

            }
            if ((details.totalRevenue() != Math.abs(history.getBalance() + tipHistory.getBalance()))) {
                System.out.println("============" + id);
            }
            System.out.println("---------------------------------------------------------------");
        }
    }
}
// threshold.amount - > TotalTipAmount and totalAmount and totalBalance
// threshold.coins_amount -> TotalCoinAmount +
// threshold.iap_coin_amount -> TotalInAppCoinAmount +


