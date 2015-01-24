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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Fast Date parser
 *
 * @author Laurent Bourges
 */
public final class DateParser {

    /** Per-thread GregorianCalendar instance */
    private static final ThreadLocal<GregorianCalendar> calStateThreadLocal = new ThreadLocal<GregorianCalendar>() {
        @Override
        protected GregorianCalendar initialValue() {
            return new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        }
    };

    public static long parseFastDate_OLD(final CharSequence date) throws NumberFormatException {
        // Fast DATE format "yyyy/mm/dd-hh:mm:ss.SSS"
        // efficient formatter for “yyyyXmmXddXhhXmmXss.sss” where X is any separator character (except a digit).

        final int length = date.length();

        if (length < 19) {
            throwFormatException(date);
        }

        // yyyyX
        final int year = NumberParser.getInteger(date, 0, 4);

        // XmmX
        final int month = NumberParser.getInteger(date, 5, 7);

        // XddX
        final int day = NumberParser.getInteger(date, 8, 10);

        // XhhX
        final int hour = NumberParser.getInteger(date, 11, 13);

        // XmmX
        final int min = NumberParser.getInteger(date, 14, 16);

        // Xss.
        final int sec = NumberParser.getInteger(date, 17, 19);

        // optional milliseconds:
        final int millis;
        if (length > 20) {
            millis = NumberParser.getInteger(date, 20, length);
        } else {
            millis = 0;
        }

        // Check valid date ?
        final GregorianCalendar cal = calStateThreadLocal.get();
        cal.set(year, month - 1, day, hour, min, sec);
        cal.set(Calendar.MILLISECOND, millis);

        return cal.getTimeInMillis();
    }

    public static long parseFastDate(final CharSequence date) throws NumberFormatException {
        // Fast DATE format "yyyy/mm/dd-hh:mm:ss.SSS"
        // efficient formatter for “yyyyXmmXddXhhXmmXss.sss” where X is any separator character (except a digit).

        final int length = date.length();

        if (length < 19) {
            throwFormatException(date);
        }

        // yyyyX
        final int year = NumberParser.getPositiveIntegerUnsafe(date, 0, 4);

        // XmmX
        final int month = NumberParser.getPositiveIntegerUnsafe(date, 5, 7) - 1; // for calendar [0 = january]

        // XddX
        final int day = NumberParser.getPositiveIntegerUnsafe(date, 8, 10);

        // XhhX
        final int hour = NumberParser.getPositiveIntegerUnsafe(date, 11, 13);

        // XmmX
        final int min = NumberParser.getPositiveIntegerUnsafe(date, 14, 16);

        // Xss.
        final int sec = NumberParser.getPositiveIntegerUnsafe(date, 17, 19);

        // optional milliseconds:
        final int millis;
        if (length > 20) {
            millis = NumberParser.getPositiveIntegerUnsafe(date, 20, length);
        } else {
            millis = 0;
        }

        final GregorianCalendar cal = calStateThreadLocal.get();

        // Same date or same date+hour (depends on required precision) ?
        if ((cal.get(Calendar.YEAR) == year)
                && (cal.get(Calendar.MONTH) == month)
                && (cal.get(Calendar.DATE) == day)
                && (cal.get(Calendar.HOUR_OF_DAY) == hour)) {

            // same hour:
            return cal.getTimeInMillis() + ((min * 60 + sec) * 1000) + millis;
        }

        // Check previous date in case of duplicates ?
        if (cal.get(Calendar.YEAR) != year) {
            cal.set(Calendar.YEAR, year);
        }
        if (cal.get(Calendar.MONTH) != month) {
            cal.set(Calendar.MONTH, month);
        }
        if (cal.get(Calendar.DATE) != day) {
            cal.set(Calendar.DATE, day);
        }
        if (cal.get(Calendar.HOUR_OF_DAY) != hour) {
            cal.set(Calendar.HOUR_OF_DAY, hour);
        }
        if (cal.get(Calendar.MINUTE) != 0) {
            cal.set(Calendar.MINUTE, 0);
        }
        if (cal.get(Calendar.SECOND) != 0) {
            cal.set(Calendar.SECOND, 0);
        }
        if (cal.get(Calendar.MILLISECOND) != 0) {
            cal.set(Calendar.MILLISECOND, 0);
        }
        // cal is good hour:

        return cal.getTimeInMillis() + ((min * 60 + sec) * 1000) + millis;
    }

    public static long parseFullDate(final CharSequence date) throws NumberFormatException {
        // Fast DATE format "yyyy/mm/dd-hh:mm:ss.SSS"
        // efficient formatter for “yyyyXmmXddXhhXmmXss.sss” where X is any separator character (except a digit).

        final int length = date.length();

        if (length < 19) {
            throwFormatException(date);
        }

        // yyyyX
        int p1 = 0;
        int p2 = NumberParser.indexOfNotDigit(date, p1, length);

        if (p2 == -1) {
            throwFormatException(date);
        }
        final int year = NumberParser.getInteger(date, p1, p2);
        p1 = ++p2;

        // XmmX
        p2 = NumberParser.indexOfNotDigit(date, p1, length);
        if (p2 == -1) {
            throwFormatException(date);
        }
        final int month = NumberParser.getInteger(date, p1, p2);
        p1 = ++p2;

        // XddX
        p2 = NumberParser.indexOfNotDigit(date, p1, length);
        if (p2 == -1) {
            throwFormatException(date);
        }
        final int day = NumberParser.getInteger(date, p1, p2);
        p1 = ++p2;

        // XhhX
        p2 = NumberParser.indexOfNotDigit(date, p1, length);
        if (p2 == -1) {
            throwFormatException(date);
        }
        final int hour = NumberParser.getInteger(date, p1, p2);
        p1 = ++p2;

        // XmmX
        p2 = NumberParser.indexOfNotDigit(date, p1, length);
        if (p2 == -1) {
            throwFormatException(date);
        }
        final int min = NumberParser.getInteger(date, p1, p2);
        p1 = ++p2;

        // Xss.
        p2 = NumberParser.indexOfNotDigit(date, p1, length);
        if (p2 == -1) {
            // only seconds: Xss"
            p2 = length;
        }
        final int sec = NumberParser.getInteger(date, p1, p2);
        p1 = ++p2;

        // optional milliseconds:
        p2 = NumberParser.indexOfNotDigit(date, p1, length);
        if (p2 == -1) {
            // only millisseconds: no white spaces or other char at end
            p2 = length;
        }
        final int millis = (p2 >= p1) ? NumberParser.getInteger(date, p1, p2) : 0;

        // Check valid date ?
        final GregorianCalendar cal = calStateThreadLocal.get();
        cal.set(year, month - 1, day, hour, min, sec);
        cal.set(Calendar.MILLISECOND, millis);

        return cal.getTimeInMillis();
    }

    private static void throwFormatException(final CharSequence value) throws NumberFormatException {
        throw new NumberFormatException("Invalid date format [yyyy/mm/dd-hh:mm:ss.SSS]: " + value);
    }

    private DateParser() {
        // utility class
    }
}
