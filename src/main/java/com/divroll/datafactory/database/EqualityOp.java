package com.divroll.datafactory.database;
/**
 * The Enum representing various equality operations.
 * This could include operations like EQUAL, STARTS_WITH etc.
 */
public enum EqualityOp {

    /** Representing exact match. */
    EQUAL,

    /** Used to check if a string begins with a specified sequence of characters. */
    STARTS_WITH,

    /** Utilized to search if a string contains a certain sequence of characters. */
    CONTAINS,

    /** Used to check if a value falls within a certain range. */
    IN_RANGE,

    /** Used to confirm that a string does not begin with a certain sequence of characters. */
    NOT_STARTS_WITH,

    /** Verifying that two values are not identical. */
    NOT_EQUAL
}
