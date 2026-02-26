package com.lendwise.transactionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * MerchantDatasetGenerator
 * ═══════════════════════════════════════════════════════════════════
 * Generates synthetic Fonepay QR transaction history for merchants,
 * mirroring the Python generate_merchant_transactions() function used
 * to train the LendWise XGBoost credit scoring model.
 *
 * Risk Tier Profiles (must match Python training data exactly):
 *   LOW    → cr_prob=0.92, txnRange=(70–100), amountMultiplier=1.4
 *   MEDIUM → cr_prob=0.85, txnRange=(50–80),  amountMultiplier=1.0
 *   HIGH   → cr_prob=0.70, txnRange=(20–50),  amountMultiplier=0.6
 *
 * Usage:
 *   List<Transaction> txns = MerchantDatasetGenerator.generateMerchantTransactions(
 *       2,       // months
 *       50, 89,  // txnPerDay min/max
 *       0.88,    // crProb
 *       1.0,     // amountMultiplier
 *       42       // seed
 *   );
 */
public class MerchantDatasetGenerator {

    // ── Constants (mirror Python exactly) ────────────────────────────────────

    private static final List<String> BANKS = Arrays.asList(
            "Nabil", "NIC Asia", "Global IME", "Siddhartha",
            "Sanima", "Prabhu", "Machhapuchchhre", "Everest"
    );

    private static final List<String> DR_REMARKS = Arrays.asList(
            "Settlement Withdrawal", "QR Reversal", "Wallet Transfer",
            "Self Withdrawal", "Adjustment Debit"
    );

    // ── Risk Tier Profile ─────────────────────────────────────────────────────

    public enum RiskTier {
        LOW   (0.92, 70, 100, 1.4,  0),
        MEDIUM(0.85, 50,  80, 1.0,  1000),
        HIGH  (0.70, 20,  50, 0.6,  2000);

        public final double crProb;
        public final int    txnMin;
        public final int    txnMax;
        public final double amountMultiplier;
        public final int    seedOffset;

        RiskTier(double crProb, int txnMin, int txnMax,
                 double amountMultiplier, int seedOffset) {
            this.crProb           = crProb;
            this.txnMin           = txnMin;
            this.txnMax           = txnMax;
            this.amountMultiplier = amountMultiplier;
            this.seedOffset       = seedOffset;
        }
    }

    // ── Transaction DTO ───────────────────────────────────────────────────────

    public static class Transaction {
        public String transType;   // "CR" or "DR"
        public String amount;      // string float e.g. "250.50"
        public String date;        // ISO datetime e.g. "2026-01-15T08:32:10"
        public String remarks;     // e.g. "Fonepay QR-Nabil-9812345678"

        public Transaction(String transType, String amount,
                           String date, String remarks) {
            this.transType = transType;
            this.amount    = amount;
            this.date      = date;
            this.remarks   = remarks;
        }

        @Override
        public String toString() {
            return String.format(
                "{transType='%s', amount='%s', date='%s', remarks='%s'}",
                transType, amount, date, remarks
            );
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Core Generator  (mirrors Python generate_merchant_transactions())
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Generates raw transaction list for a single merchant.
     *
     * @param months            Number of months of history to generate (default 2)
     * @param txnPerDayMin      Minimum transactions per day
     * @param txnPerDayMax      Maximum transactions per day
     * @param crProb            Probability a transaction is a credit (0.0–1.0)
     * @param amountMultiplier  Scales all amounts (1.0 = normal, 1.4 = high, 0.6 = low)
     * @param seed              Random seed for reproducibility
     * @return List of Transaction objects ready to insert into MongoDB
     */
    public static List<Transaction> generateMerchantTransactions(
            int    months,
            int    txnPerDayMin,
            int    txnPerDayMax,
            double crProb,
            double amountMultiplier,
            long   seed
    ) {
        Random rng = new Random(seed);
        List<Transaction> txns = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(months * 30L);

        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {

            // Mirror Python: rng.randint(*txn_per_day_range)
            int perDay = txnPerDayMin + rng.nextInt(txnPerDayMax - txnPerDayMin + 1);

            for (int i = 0; i < perDay; i++) {

                boolean isCR   = rng.nextDouble() < crProb;
                String  type   = isCR ? "CR" : "DR";
                String  amount = randomAmount(rng, amountMultiplier);
                String  time   = randomPeakTime(rng);
                String  dtStr  = d + "T" + time;
                String  remark = isCR
                        ? fonepayCreditRemark(rng)
                        : DR_REMARKS.get(rng.nextInt(DR_REMARKS.size()));

                txns.add(new Transaction(type, amount, dtStr, remark));
            }
        }

        return txns;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Dataset Generator  (mirrors Python generate_dataset())
    // Generates labelled merchants across LOW / MEDIUM / HIGH risk tiers.
    // ══════════════════════════════════════════════════════════════════════════

    public static class MerchantDataset {
        public int              merchantId;
        public RiskTier         riskTier;
        public List<Transaction> transactions;

        public MerchantDataset(int merchantId, RiskTier riskTier,
                               List<Transaction> transactions) {
            this.merchantId   = merchantId;
            this.riskTier     = riskTier;
            this.transactions = transactions;
        }
    }

    /**
     * Generates a full labelled dataset of N merchants across all three
     * risk tiers (mirrors Python generate_dataset()).
     *
     * Distribution:
     *   LOW    → 40% of merchants
     *   MEDIUM → 35% of merchants
     *   HIGH   → 25% of merchants
     *
     * @param nMerchants Total number of merchants to generate
     * @return List of MerchantDataset (each has merchantId, riskTier, transactions)
     */
    public static List<MerchantDataset> generateDataset(int nMerchants) {

        int nLow    = (int) (nMerchants * 0.40);
        int nMedium = (int) (nMerchants * 0.35);
        int nHigh   = nMerchants - nLow - nMedium;

        Map<RiskTier, Integer> counts = new LinkedHashMap<>();
        counts.put(RiskTier.LOW,    nLow);
        counts.put(RiskTier.MEDIUM, nMedium);
        counts.put(RiskTier.HIGH,   nHigh);

        List<MerchantDataset> dataset = new ArrayList<>();
        int merchantId = 1;

        for (Map.Entry<RiskTier, Integer> entry : counts.entrySet()) {
            RiskTier tier  = entry.getKey();
            int      count = entry.getValue();

            for (int i = 0; i < count; i++) {
                long seed = tier.seedOffset + i;

                List<Transaction> txns = generateMerchantTransactions(
                        2,                   // months
                        tier.txnMin,         // txnPerDayMin
                        tier.txnMax,         // txnPerDayMax
                        tier.crProb,         // crProb
                        tier.amountMultiplier, // amountMultiplier
                        seed                 // seed
                );

                dataset.add(new MerchantDataset(merchantId, tier, txns));
                merchantId++;
            }
        }

        return dataset;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Private Helpers  (mirror Python helpers exactly)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Small-ticket heavy amount distribution.
     * Mirrors Python:
     *   if r < 0.70 → 20–499
     *   if r < 0.90 → 500–1999
     *   else        → 2000–5999
     */
    private static String randomAmount(Random rng, double multiplier) {
        double r = rng.nextDouble();
        double base;

        if (r < 0.70) {
            base = 20 + rng.nextInt(480);          // 20–499
        } else if (r < 0.90) {
            base = 500 + rng.nextInt(1500);        // 500–1999
        } else {
            base = 2000 + rng.nextInt(4000);       // 2000–5999
        }

        return BigDecimal.valueOf(base * multiplier)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    /**
     * Peak hour pattern.
     * Mirrors Python:
     *   bucket 0 → 08–10 (morning rush)
     *   bucket 1 → 12–14 (lunch)
     *   bucket 2 → 17–20 (evening)
     */
    private static String randomPeakTime(Random rng) {
        int bucket = rng.nextInt(3);
        int hour;

        switch (bucket) {
            case 0:  hour = 8  + rng.nextInt(3); break;  // 8,9,10
            case 1:  hour = 12 + rng.nextInt(3); break;  // 12,13,14
            default: hour = 17 + rng.nextInt(4); break;  // 17,18,19,20
        }

        int minute = rng.nextInt(60);
        int second = rng.nextInt(60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * Fonepay-style credit remark.
     * Mirrors Python: f"Fonepay QR-{bank}-{mobile}"
     */
    private static String fonepayCreditRemark(Random rng) {
        String bank   = BANKS.get(rng.nextInt(BANKS.size()));
        String mobile = "98" + (1000000 + rng.nextInt(9000000));
        return "Fonepay QR-" + bank + "-" + mobile;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Main — demo output
    // ══════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // ── Demo 1: single merchant (default profile) ─────────────────────────
        System.out.println("=== Single Merchant (default profile, seed=42) ===");
        List<Transaction> singleMerchant = generateMerchantTransactions(
                2,      // months
                50, 89, // txnPerDay range
                0.88,   // crProb
                1.0,    // amountMultiplier
                42L     // seed
        );
        System.out.println("Total transactions: " + singleMerchant.size());
        System.out.println("First 3 records:");
        System.out.println(mapper.writeValueAsString(singleMerchant.subList(0, 3)));

        // ── Demo 2: full dataset (600 merchants) ──────────────────────────────
        System.out.println("\n=== Full Dataset (600 merchants) ===");
        List<MerchantDataset> dataset = generateDataset(600);

        long lowCount    = dataset.stream().filter(m -> m.riskTier == RiskTier.LOW).count();
        long mediumCount = dataset.stream().filter(m -> m.riskTier == RiskTier.MEDIUM).count();
        long highCount   = dataset.stream().filter(m -> m.riskTier == RiskTier.HIGH).count();

        System.out.println("Total merchants : " + dataset.size());
        System.out.println("LOW    merchants: " + lowCount);
        System.out.println("MEDIUM merchants: " + mediumCount);
        System.out.println("HIGH   merchants: " + highCount);

        // Print first merchant's first 2 transactions
        MerchantDataset first = dataset.get(0);
        System.out.println("\nFirst merchant:");
        System.out.println("  merchantId : " + first.merchantId);
        System.out.println("  riskTier   : " + first.riskTier);
        System.out.println("  txn count  : " + first.transactions.size());
        System.out.println("  first 2 txns:");
        System.out.println(mapper.writeValueAsString(first.transactions.subList(0, 2)));
    }
}