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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import si.pele.microbench.TestRunner;
import static si.pele.microbench.TestRunner.doTest;

/**
 * Benchmark parseDouble, parseInt, parseFastDate, parseDMS ...
 * @author Laurent Bourges
 */
public class NumberParserBenchmark extends TestRunner {

    final static String[] values_int;

    final static String[] values_ats = new String[]{
        "-02:26:56.84901360", "+0050:35:47.11817040",
        "+0050:35:47.11210080"};

    final static String[] values_date = new String[]{
        "2015-01-02:23:59:59.999",
        "2015-01-01:00:00:59.500",
        "2015-01-01:00:59:59.500",
        "2015-01-01:19:45:45.500",
        "2015-01-01:11:23:59.500",
        "2015-01-01:00:00:49.1",
        "2015-01-01:00:00:49.1",
        "2015-01-01:00:00:49.1",
        "2015-01-01:00:00:49.1",
        "2015-01-01:00:00:49.1",
        "2015-01-01:00:00:59.0",
        "2015-01-01:00:00:59.0",
        "2015-01-01:00:00:59.0",
        "2015-01-01:00:00:59.0",
        "2015-01-01:00:00:59.0",
        "2015-01-01:00:00:49.123456789",
        "2015-01-01:23:59:59.999",
        "2015-01-01:23:59:59.999",
        "2015-01-01:23:59:59.999",
        "2015-01-01:23:59:59.999",
        "2015-01-01:23:59:59.999",
        "2015-01-01:23:59:59.999"
    /* , "2015/01/01-23:59:59.999" // bad format */
    };

    final static String[] values_dbl;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Locale.setDefault(Locale.ENGLISH);

        final int[] ints = new int[4096];
        ints[0] = Integer.MAX_VALUE;
        ints[1] = Integer.MIN_VALUE;
        ints[2] = -123456789;
        ints[3] = -123;
        ints[4] = -1;
        ints[5] = 0;
        ints[6] = 1;
        ints[7] = 123;
        ints[8] = 123456789;

        for (int i = 9; i < ints.length; i++) {
            ints[i] = nextInt();
        }
        values_int = new String[ints.length];

        for (int i = 0; i < ints.length; i++) {
            values_int[i] = Integer.toString(ints[i]);
        }

        /*, Long.toString(Long.MAX_VALUE) */
        final double[] dbls = new double[2048];
        dbls[0] = Double.MAX_VALUE;
        dbls[1] = Double.MIN_VALUE;
        dbls[2] = 0;
        dbls[3] = Double.NaN;
        dbls[4] = Double.POSITIVE_INFINITY;
        dbls[5] = Double.NEGATIVE_INFINITY;
        dbls[6] = 1.1794462202530568E-20;
        dbls[7] = 2.2250738585072012e-308; // java bug http://www.exploringbinary.com/a-closer-look-at-the-java-2-2250738585072012e-308-bug/

        for (int i = 8; i < dbls.length; i++) {
            dbls[i] = nextDouble();
        }

        values_dbl = new String[dbls.length];

        for (int i = 0; i < dbls.length; i++) {
            values_dbl[i] = Double.toString(dbls[i]);
        }
    }

    /** 
     * Compare two double precision floats for equality within a margin of error. 
     * 
     * @param v1 The expected value. 
     * @param v2 The actual value. 
     * @see Utils#compareFloatEquals(float, float, int)
     */
    public static long getDiffUlps(double v1, double v2) {
        final long expBits = Double.doubleToLongBits(v1);
        final long actBits = Double.doubleToLongBits(v2);
        long expectedBits = (expBits < 0) ? 0x8000000000000000L - expBits : expBits;
        long actualBits = (actBits < 0) ? 0x8000000000000000L - actBits : actBits;
        long difference = (expectedBits > actualBits) ? (expectedBits - actualBits) : (actualBits - expectedBits);
        return difference;
    }

    private static int nextInt() {
        return (int) (9.99999999999999999999 * (Math.random() - 0.5) * Math.pow(2.0, 30.0 * Math.random()));
    }

    private static double nextDouble() {
        /*
         Double.MIN_VALUE = 4.9E-324
         Double.MAX_VALUE = 1.7976931348623157E308
         */
        return 9.99999999999999999999 * (Math.random() - 0.5) * Math.pow(10.0, 308.0 * (Math.random() - 0.5));
    }

    public static void main(String[] args) throws Exception {
        final boolean trace = false;
        final double th = 2e-16;

        if (true) {
            for (int i = 0; i < values_int.length; i++) {
                String value = values_int[i];
                System.out.println("test[" + value + "] = ");
                int v1, v2;
                v1 = v2 = 0;
                try {
                    v1 = parseInt(value);
                    if (trace) {
                        System.out.println("parseInt   = " + v1);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                try {
                    v2 = NumberParser.getIntegerUnsafe(value); // TODO: test
                    if (trace) {
                        System.out.println("NumberParser.getInteger = " + v2);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                if (v1 != v2) {
                    System.out.println("Bad case : " + v1 + " <> " + v2);
                }
            }
        }

        if (true) {
            for (int i = 0; i < values_int.length; i++) {
                String value = values_int[i];
                System.out.println("test[" + value + "] = ");
                double v1, v2;
                v1 = v2 = Double.NaN;
                try {
                    v1 = parseDouble(value);
                    if (trace) {
                        System.out.println("parseDouble   = " + v1);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                try {
                    v2 = NumberParser.getDouble(value);
                    if (trace) {
                        System.out.println("DoubleParser.getDouble = " + v2);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                double relDiff = (Math.abs(v2 - v1) / v1);
                if (relDiff > th) {
                    System.out.println("DoubleParser.getDouble: Bad case\t" + v1 + "\t<> " + v2 + "\tdiff: " + relDiff + " ulps= " + getDiffUlps(v1, v2));
                }
            }
            for (int i = 0; i < values_dbl.length; i++) {
                String value = values_dbl[i];
                System.out.println("test[" + value + "] = ");
                double v1, v2;
                v1 = v2 = Double.NaN;
                try {
                    v1 = parseDouble(value);
                    if (trace) {
                        System.out.println("parseDouble   = " + v1);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                try {
                    v2 = NumberParser.getDouble(value);
                    if (trace) {
                        System.out.println("DoubleParser.getDouble = " + v2);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                double relDiff = (Math.abs(v2 - v1) / v1);
                if (relDiff > th) {
                    // TODO: check number of digits = max precision = 1/10 or 1/100 ?
                    System.out.println("DoubleParser.getDouble: Bad case:\t" + v1 + "\t<> " + v2 + "\tdiff: " + relDiff + " ulps= " + getDiffUlps(v1, v2));
                }
            }
            // TODO: test perf ints, fractional only, full double ...
        }
        if (false) {
            final long duration = 100l * 60l * 1000l * 1000l * 1000l;
            System.out.println("duration = " + duration);

            /*
             DoubleParser.getDouble: Max:	1.689312691545249E-226	<> 1.6893126915452496E-226	diff: 4.4387780350002214E-16
             DoubleParser.getDouble: MAX:	1.705938634162947E-111	<> 1.7059386341629476E-111	diff: 4.329805742310811E-16 ulps= 2
             */
            double maxDiff = 0.0;
            double maxValue = Double.NaN;

            long maxUlp = 0l;
            double maxUlpValue = Double.NaN;

            final long startTime = System.nanoTime();

            for (; (System.nanoTime() - startTime) < duration;) {

                double dbl = nextDouble();
                String value = Double.toString(dbl);

                // check fraction length
                int pos = value.indexOf('E');
                if (pos == -1) {
                    pos = value.length();
                }
                if (value.charAt(0) == '-') {
                    pos--;
                }

                if (pos < 17) {
                    if (trace) {
                        System.out.println("test[" + value + "] = skipped, too short !");
                    }
                    continue;
                }
                if (trace) {
                    System.out.println("test[" + value + "] = ");
                }
                double v1, v2;
                v1 = v2 = Double.NaN;
                try {
                    v1 = parseDouble(value);
                    if (trace) {
                        System.out.println("parseDouble   = " + v1);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                try {
                    v2 = NumberParser.getDouble(value);
                    if (trace) {
                        System.out.println("DoubleParser.getDouble = " + v2);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                double relDiff = (Math.abs(v2 - v1) / v1);
                if (trace && (relDiff > th)) {
                    System.out.println("DoubleParser.getDouble: Bad case:\t" + v1 + "\t<> " + v2 + "\tdiff: " + relDiff + " ulps= " + getDiffUlps(v1, v2));
                }
                if (relDiff > maxDiff) {
                    maxDiff = relDiff;
                    maxValue = dbl;
                    System.out.println("DoubleParser.getDouble: MAX:\t" + v1 + "\t<> " + v2 + "\tdiff: " + relDiff + " ulps= " + getDiffUlps(v1, v2));
                }
                long ulpDiff = getDiffUlps(v1, v2);
                if (ulpDiff > maxUlp) {
                    maxUlp = ulpDiff;
                    maxUlpValue = dbl;
                    System.out.println("DoubleParser.getDouble: ULP:\t" + v1 + "\t<> " + v2 + "\tdiff: " + relDiff + " ulps= " + getDiffUlps(v1, v2));
                }
            }
            System.out.println("DoubleParser.getDouble: max relative error = " + maxDiff + " for value = " + maxValue);
            System.out.println("DoubleParser.getDouble: max ULP = " + maxUlp + " for value = " + maxUlpValue);
        }
        if (true) {
            for (int i = 0; i < values_ats.length; i++) {
                String value = values_ats[i];
                System.out.println("test[" + value + "] = ");
                double v1, v2;
                v1 = v2 = Double.NaN;
                try {
                    v1 = DMSConverter.parseDMS_REF(value);
                    if (trace) {
                        System.out.println("parseDMS_OLD   = " + v1);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                try {
                    v2 = DMSConverter.parseDMS(value);
                    if (trace) {
                        System.out.println("parseDMS2 = " + v2);
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Error : " + nfe);
                }
                double relDiff = (Math.abs(v2 - v1) / v1);
                if (relDiff > th) {
                    System.out.println("ATSConverter.parseDMS: Bad case:\t" + v1 + "\t<> " + v2 + "\tdiff: " + relDiff + " ulps= " + getDiffUlps(v1, v2));
                }
            }
        }
        if (true) {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
            Locale.setDefault(Locale.ENGLISH);
            for (int i = 0; i < values_date.length; i++) {
                String value = values_date[i];
                System.out.println("test[" + value + "] = ");
                long v1, v2;

                v1 = parseDate_REF(value);
                if (trace) {
                    System.out.println("parseDate   = " + v1);
                }
                v2 = DateParser.parseFastDate(value);
                if (trace) {
                    System.out.println("parseDate   = " + v2);
                }
                if (v1 != v2) {
                    System.out.println("Bad case : " + v1 + " <> " + v2);
                }
            }
            if (false) {
                System.exit(1);
            }

        }
        System.out.println(">> JVM START: " + System.getProperty("java.version") + " [" + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + "]");

        /*
         * JVM settings:
         * -XX:+PrintCommandLineFlags -XX:-PrintFlagsFinal -Xms128m  -Xmx128m -XX:-TieredCompilation
         * 
         */
        final long testDuration = 30000L;

        final int nTh = 8;

        if (false) {
            doTest(MicroTest2.class, testDuration, nTh, nTh, 1);
            doTest(MicroTest1.class, testDuration, nTh, nTh, 1);
        }

        if (false) {
            // parse ints:
            doTest(ParseIntUnsafeParserTest.class, testDuration, 1, nTh, 1);
            doTest(ParseIntParserTest.class, testDuration, 1, nTh, 1);
            doTest(ParseIntTest.class, testDuration, 1, nTh, 1);
        }

        if (false) {
            // parse doubles
            doTest(ParseDblParserTest.class, testDuration, 1, nTh, 1);
            doTest(ParseDblTest.class, testDuration, 1, nTh, 1);
        }

        if (false) {
            // parse ATS (DMS):
            doTest(ParseATSTest.class, testDuration, nTh, nTh, 1);
            doTest(ParseATSOLDTest.class, testDuration, nTh, nTh, 1);
        }

        if (true) {
			// parse dates
            // single thread:
            doTest(ParseDateTest.class, testDuration, 1, 1, 1);
            doTest(ParseDateOLDTest.class, testDuration, 1, 1, 1);
            doTest(ParseDateREFTest.class, testDuration, 1, 1, 1);
        }
        System.out.println("<< JVM END");

        System.exit(0);
    }

    public static final class ParseATSOLDTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_ats;

            double res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0.0;
                for (int i = 0; i < values.length; i++) {
                    res += DMSConverter.parseDMS_OLD(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class ParseATSTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_ats;

            double res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0.0;
                for (int i = 0; i < values.length; i++) {
                    res += DMSConverter.parseDMS(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class ParseIntTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_int;

            int res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0;
                for (int i = 0; i < values.length; i++) {
                    res += parseInt(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class ParseIntParserTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_int;

            int res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0;
                for (int i = 0; i < values.length; i++) {
                    res += NumberParser.getInteger(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class ParseIntUnsafeParserTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_int;

            int res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0;
                for (int i = 0; i < values.length; i++) {
                    res += NumberParser.getIntegerUnsafe(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class ParseDblTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_dbl;

            double res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0.0;
                for (int i = 0; i < values.length; i++) {
                    res += parseDouble(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class ParseDblParserTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_dbl;

            double res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0.0;
                for (int i = 0; i < values.length; i++) {
                    res += NumberParser.getDouble(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class ParseDateREFTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_date;

            long res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0l;
                for (int i = 0; i < values.length; i++) {
                    res += parseDate_REF(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class ParseDateTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_date;

            long res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0l;
                for (int i = 0; i < values.length; i++) {
                    res += DateParser.parseFastDate(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class ParseDateOLDTest extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final String[] values = values_date;

            long res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0l;
                for (int i = 0; i < values.length; i++) {
                    res += DateParser.parseFastDate(values[i]);
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class MicroTest1 extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final double value = 123.567E7;

            double res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0.0;
                for (int i = 0; i < 4096; i++) {

                    boolean sign = (i % 17) == 0;

                    res += (sign) ? value : -value;
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static final class MicroTest2 extends TestRunner.Test {

        @Override
        protected void doLoop(TestRunner.Loop loop, TestRunner.DevNull devNull1, TestRunner.DevNull devNull2, TestRunner.DevNull devNull3, TestRunner.DevNull devNull4, TestRunner.DevNull devNull5) {

            final double value = 123.567E7;

            double res;
            // work on arrays:
            while (loop.nextIteration()) {
                res = 0.0;
                for (int i = 0; i < 4096; i++) {

                    double sign = ((i % 17) == 0) ? 1.0 : -1.0;

                    res += value * sign;
                }

                // use array:
                devNull1.yield(res);
            }
        }
    }

    public static int parseInt(final String value) {
        return Integer.parseInt(value);
    }

    public static double parseDouble(final String value) {
        return Double.parseDouble(value);
    }

    /** date formatter */
    private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss.SSS"); // "yyyy/MM/dd-HH:mm:ss"

    public static long parseDate_REF(final CharSequence date) {
        try {
            return df.parse(date.toString()).getTime();
        } catch (ParseException pe) {
            // ignore
        }

        return NumberUtils.NULL_LONG;
    }
}
