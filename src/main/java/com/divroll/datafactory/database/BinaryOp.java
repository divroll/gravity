package com.divroll.datafactory.database;

/**
 * This enumeration defines the binary operators that can be used in a condition.
 *
 * <p>The enum values include:</p>
 * <ol>
 *  <li>{@code INTERSECT}: Represents the INTERSECT binary operator.</li>
 *  <li>{@code UNION}: Represents the UNION binary operator.</li>
 *  <li>{@code MINUS}: Represents the MINUS binary operator.</li>
 *  <li>{@code CONCAT}: Represents the CONCAT binary operator.</li>
 * </ol>
 */

public enum BinaryOp {
    /**
     * Represents intersection of two sets.
     */
    INTERSECT,
    /**
     * Represents union of two sets.
     */
    UNION,
    /**
     * Represents difference of two sets.
     */
    MINUS,
    /**
     * Represents concatenation of two sets.
     */
    CONCAT
}
