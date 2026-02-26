package com.lendwise.transactionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;

public class FonepayMockGenerator {

    private static final Random random = new Random();

    private static final List<String> BANKS = Arrays.asList(
            "Nabil", "NIC Asia", "Global IME", "Siddhartha",
            "Sanima", "Prabhu", "Machhapuchchhre", "Everest"
    );

    private static final List<String> DR_REMARKS = Arrays.asList(
            "Settlement Withdrawal", "QR Reversal", "Wallet Transfer",
            "Self Withdrawal", "Adjustment Debit"
    );

    public static void main(String[] args) throws Exception {

        List<Transaction> txns = generate();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println(mapper.writeValueAsString(txns));
    }

    public static List<Transaction> generate() {
        List<Transaction> list = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusMonths(2);

        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {

            int perDay = 50 + random.nextInt(40); // 50–89

            for (int i = 0; i < perDay; i++) {

                boolean isCR = random.nextDouble() < 0.88; // merchant inflow heavy

                String type = isCR ? "CR" : "DR";

                BigDecimal amount = randomAmount()
                        .setScale(2, RoundingMode.HALF_UP);

                LocalDateTime dateTime = LocalDateTime.of(d, randomPeakTime());

                String remarks = isCR ? fonepayCreditRemarks() :
                        DR_REMARKS.get(random.nextInt(DR_REMARKS.size()));

                list.add(new Transaction(type, amount.toString(), dateTime.toString(), remarks));
            }
        }
        return list;
    }

    // Small-ticket heavy distribution
    private static BigDecimal randomAmount() {
        double r = random.nextDouble();

        if (r < 0.7) return BigDecimal.valueOf(20 + random.nextInt(500));
        if (r < 0.9) return BigDecimal.valueOf(500 + random.nextInt(1500));
        return BigDecimal.valueOf(2000 + random.nextInt(4000));
    }

    // Peak hour pattern
    private static LocalTime randomPeakTime() {
        int bucket = random.nextInt(3);

        if (bucket == 0) return LocalTime.of(8 + random.nextInt(3), random.nextInt(60), random.nextInt(60));
        if (bucket == 1) return LocalTime.of(12 + random.nextInt(3), random.nextInt(60), random.nextInt(60));
        return LocalTime.of(17 + random.nextInt(4), random.nextInt(60), random.nextInt(60));
    }

    // Fonepay style credit remarks
    private static String fonepayCreditRemarks() {
        String bank = BANKS.get(random.nextInt(BANKS.size()));
        String mobile = "98" + (1000000 + random.nextInt(8999999));
        return "Fonepay QR-" + bank + "-" + mobile;
    }

    static class Transaction {
        public String transType;
        public String amount;
        public String date;
        public String remarks;

        public Transaction(String t, String a, String d, String r) {
            transType = t;
            amount = a;
            date = d;
            remarks = r;
        }
    }
}