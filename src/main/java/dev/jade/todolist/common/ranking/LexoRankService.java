package dev.jade.todolist.common.ranking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * LexoRank implementation for ordering tasks/sections.
 * Provides string-based ranking similar to Jira's ordering system.
 * <p>
 * Key Features:
 * - Insert between any two items without reordering
 * - Lexicographically sorted (database-friendly)
 * - Supports rebalancing when ranks get too close
 * - Thread-safe rank generation
 */
@Slf4j
@Service
public class LexoRankService {

    // Base-36 character set for rank generation (0-9, a-z)
    private static final String BASE_36_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE_36_CHARS.length();

    // Initial rank for first item
    private static final String INITIAL_RANK = "n";

    // Minimum rank (lexicographically smallest)
    private static final String MIN_RANK = "0";

    // Maximum rank (lexicographically largest)
    private static final String MAX_RANK = "z";

    // Rebalancing threshold - trigger rebalance if ranks are this close
    private static final int MIN_RANK_DISTANCE = 2;

    /**
     * Generate initial rank for the first item in a list
     */
    public String getInitialRank() {
        return INITIAL_RANK;
    }

    /**
     * Generate rank for inserting before the first item
     */
    public String getRankBefore(String existingRank) {
        if (existingRank == null || existingRank.isEmpty()) {
            return INITIAL_RANK;
        }

        return calculateBetween(MIN_RANK, existingRank);
    }

    /**
     * Generate rank for inserting after the last item
     */
    public String getRankAfter(String existingRank) {
        if (existingRank == null || existingRank.isEmpty()) {
            return INITIAL_RANK;
        }

        return calculateBetween(existingRank, MAX_RANK);
    }

    /**
     * Generate rank for inserting between two items
     *
     * @param prevRank Rank of item before insertion point (can be null for start)
     * @param nextRank Rank of item after insertion point (can be null for end)
     * @return New rank that sorts between prevRank and nextRank
     */
    public String getRankBetween(String prevRank, String nextRank) {
        // Handle edge cases
        if (prevRank == null && nextRank == null) {
            return INITIAL_RANK;
        }
        if (prevRank == null) {
            return getRankBefore(nextRank);
        }
        if (nextRank == null) {
            return getRankAfter(prevRank);
        }

        // Validate order
        if (prevRank.compareTo(nextRank) >= 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid rank order: prevRank '%s' must be < nextRank '%s'",
                            prevRank, nextRank)
            );
        }

        return calculateBetween(prevRank, nextRank);
    }

    /**
     * Check if ranks are too close and need rebalancing
     */
    public boolean needsRebalancing(String rank1, String rank2) {
        if (rank1 == null || rank2 == null) {
            return false;
        }

        // Calculate lexicographic distance
        int distance = Math.abs(rank1.compareTo(rank2));
        return distance < MIN_RANK_DISTANCE;
    }

    /**
     * Generate evenly distributed ranks for a list of items
     * Used for rebalancing when ranks get too close
     *
     * @param count Number of ranks to generate
     * @return Array of evenly spaced ranks
     */
    public String[] rebalanceRanks(int count) {
        if (count <= 0) {
            return new String[0];
        }
        if (count == 1) {
            return new String[]{INITIAL_RANK};
        }

        String[] ranks = new String[count];

        // Calculate step size
        // Use base-36 arithmetic to divide the space evenly
        BigInteger start = decodeRank("0");
        BigInteger end = decodeRank("z");
        BigInteger range = end.subtract(start);
        BigInteger step = range.divide(BigInteger.valueOf(count + 1));

        for (int i = 0; i < count; i++) {
            BigInteger position = start.add(step.multiply(BigInteger.valueOf(i + 1)));
            ranks[i] = encodeRank(position);
        }

        return ranks;
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Calculate rank between two existing ranks
     */
    private String calculateBetween(String lower, String upper) {
        // Normalize lengths
        int maxLen = Math.max(lower.length(), upper.length());
        lower = padRank(lower, maxLen);
        upper = padRank(upper, maxLen);

        // Convert to BigInteger for arithmetic
        BigInteger lowerVal = decodeRank(lower);
        BigInteger upperVal = decodeRank(upper);

        // Calculate midpoint
        BigInteger midpoint = lowerVal.add(upperVal).divide(BigInteger.TWO);

        // If midpoint equals lower, we need to add precision
        if (midpoint.equals(lowerVal)) {
            // Extend precision by adding a character
            lower = lower + "0";
            upper = upper + "z";
            return calculateBetween(lower, upper);
        }

        return encodeRank(midpoint);
    }

    /**
     * Decode base-36 rank string to BigInteger
     */
    private BigInteger decodeRank(String rank) {
        BigInteger result = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(BASE);

        for (int i = 0; i < rank.length(); i++) {
            char c = rank.charAt(i);
            int digit = BASE_36_CHARS.indexOf(c);
            if (digit == -1) {
                throw new IllegalArgumentException("Invalid character in rank: " + c);
            }
            result = result.multiply(base).add(BigInteger.valueOf(digit));
        }

        return result;
    }

    /**
     * Encode BigInteger to base-36 rank string
     */
    private String encodeRank(BigInteger value) {
        if (value.equals(BigInteger.ZERO)) {
            return "0";
        }

        StringBuilder result = new StringBuilder();
        BigInteger base = BigInteger.valueOf(BASE);

        while (value.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divMod = value.divideAndRemainder(base);
            result.insert(0, BASE_36_CHARS.charAt(divMod[1].intValue()));
            value = divMod[0];
        }

        return result.toString();
    }

    /**
     * Pad rank string to specified length with '0'
     */
    private String padRank(String rank, int length) {
        if (rank.length() >= length) {
            return rank;
        }
        return rank + "0".repeat(length - rank.length());
    }

    /**
     * Trim trailing zeros from rank (optimization)
     */
    @SuppressWarnings("unused")
    private String trimRank(String rank) {
        if (rank.isEmpty()) {
            return rank;
        }
        int end = rank.length();
        while (end > 1 && rank.charAt(end - 1) == '0') {
            end--;
        }
        return rank.substring(0, end);
    }
}
