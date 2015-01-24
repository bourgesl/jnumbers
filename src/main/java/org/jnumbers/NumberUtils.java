/* 
 Copyright (c) 2015, Laurent Bourges. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 - Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jnumbers;

/**
 * TODO: 
 * -log exceptions in debug ... with threshold !
 *
 * @author Laurent Bourges
 */
public final class NumberUtils {

    private final static boolean USE_FAST_NUMBER_PARSER = true;

    public final static char NULL_CHAR = 0;
    public final static short NULL_SHORT = Short.MIN_VALUE;
    public final static int NULL_INT = Integer.MIN_VALUE;
    public final static long NULL_LONG = Long.MIN_VALUE;
    public final static float NULL_FLOAT = Float.NaN;
    public final static double NULL_DOUBLE = Double.NaN;

    /**
     * Parses the given value as a double.
     * 
     * @param value
     *            The string representation of the double value.
     * @return The parsed value or {@link #NULL_DOUBLE} if the
     *         value cannot be parsed.
     */
    public static double parseDouble(final CharSequence value) {
        try {
            if (USE_FAST_NUMBER_PARSER) {
                return NumberParser.getDouble(value, 0, value.length());
            }
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException nfe) {
            // ignore
        }
        return NULL_DOUBLE;
    }

    /**
     * Parses the given value as a float.
     * 
     * @param value
     *            The string representation of the float value.
     * @return The parsed value or {@link #NULL_FLOAT} if the value
     *         cannot be parsed.
     */
    public static float parseFloat(final CharSequence value) {
        try {
            if (USE_FAST_NUMBER_PARSER) {
                final double val = NumberParser.getDouble(value, 0, value.length());
                if (Double.isNaN(val)) {
                    return NULL_FLOAT;
                }
                if (val == Double.POSITIVE_INFINITY) {
                    return Float.POSITIVE_INFINITY;
                }
                if (val == Double.NEGATIVE_INFINITY) {
                    return Float.NEGATIVE_INFINITY;
                }
                if (val <= Float.MIN_VALUE) {
                    return NULL_FLOAT;
                }
                if (val >= Float.MAX_VALUE) {
                    return NULL_FLOAT;
                }
                return (float) val;
            }
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException nfe) {
            // ignore
        }
        return NULL_FLOAT;
    }

    /**
     * Parses the given value as an int.
     * 
     * @param value
     *            The string representation of the int value.
     * @return The parsed value or {@link #NULL_INT} if the value
     *         cannot be parsed.
     */
    public static int parseInt(final CharSequence value) {
        try {
            if (USE_FAST_NUMBER_PARSER) {
                return NumberParser.getInteger(value);
            }
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException nfe) {
            // ignore
        }
        return NULL_INT;
    }

    /**
     * Parses the given value as a long.
     * 
     * @param value
     *            The string representation of the long value.
     * @return The parsed value or {@link #NULL_LONG} if the value
     *         cannot be parsed.
     */
    public static long parseLong(final CharSequence value) {
        try {
            if (USE_FAST_NUMBER_PARSER) {
                return NumberParser.getLong(value);
            }
            return Long.parseLong(value.toString());
        } catch (NumberFormatException nfe) {
            // ignore
        }
        return NULL_LONG;
    }

    /**
     * Parses the given value as a short.
     * 
     * @param value
     *            The string representation of the short value.
     * @return The parsed value or {@link #NULL_SHORT} if the value
     *         cannot be parsed.
     */
    public static short parseShort(final CharSequence value) {
        try {
            if (USE_FAST_NUMBER_PARSER) {
                final int val = NumberParser.getInteger(value);
                if (val <= Short.MIN_VALUE) {
                    return Short.MIN_VALUE;
                }
                if (val >= Short.MAX_VALUE) {
                    return Short.MAX_VALUE;
                }
                return (short) val;
            }
            return Short.parseShort(value.toString());
        } catch (NumberFormatException nfe) {
            // ignore
        }
        return NULL_SHORT;
    }

    private NumberUtils() {
        // utility class
    }

}
