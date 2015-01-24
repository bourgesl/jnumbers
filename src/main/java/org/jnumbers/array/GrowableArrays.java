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
package org.jnumbers.array;

import java.util.Arrays;

/**
 * Growable Arrays for primitive types
 * 
 * @author Laurent Bourges
 */
public final class GrowableArrays {

    /**
     * initial capacity for arrays
     */
    private final static int INITIAL_CAPACITY = 64 * 1024;

    /**
     * large array threshold to grow slowly (1/4 instead of double size)
     */
    private final static int LARGE_THRESHOLD = 8 * 1024 * 1024;

    /**
     * Forbidden constructor
     */
    private GrowableArrays() {
        super();
    }

    static int getGrowSize(final int length) {
        if (length <= LARGE_THRESHOLD) {
            return length << 1; // double size
        }
		// add only 1/4 (25%)
        // TODO: check overflow ie MAX_INT ?
        return length + (length >> 2);
    }

    static final class ADouble {

        static ADouble[] create(final int nDims) {
            final ADouble[] arrays = new ADouble[nDims];
            for (int i = 0; i < nDims; i++) {
                arrays[i] = new ADouble();
            }
            return arrays;
        }

        static void fill(final ADouble[] arrays, final double[] values) {
            // skip dimension checks:
            for (int i = 0; i < arrays.length; i++) {
                arrays[i].add(values[i]);
            }
        }

        static double[][] copy(final ADouble[] arrays) {
            final double[][] data = new double[arrays.length][];
            for (int i = 0; i < arrays.length; i++) {
                data[i] = arrays[i].copyData();
                // free memory:
                arrays[i] = null;
            }
            return data;
        }

        private int size;
        private double[] array;

        ADouble() {
            this(INITIAL_CAPACITY);
        }

        ADouble(final int capacity) {
            array = new double[(capacity >= INITIAL_CAPACITY) ? capacity
                    : INITIAL_CAPACITY];
        }

        void reset() {
            size = 0;
        }

        void add(final double value) {
            double[] a = array;
            if (size + 1 > a.length) {
                // grow ie double current size:
                array = a = Arrays.copyOf(a, getGrowSize(a.length));
            }
            a[size++] = value;
        }

        double[] copyData() {
            return Arrays.copyOf(array, size);
        }
    }

    static final class AFloat {

        static AFloat[] create(final int nDims) {
            final AFloat[] arrays = new AFloat[nDims];
            for (int i = 0; i < nDims; i++) {
                arrays[i] = new AFloat();
            }
            return arrays;
        }

        static void fill(final AFloat[] arrays, final float[] values) {
            // skip dimension checks:
            for (int i = 0; i < arrays.length; i++) {
                arrays[i].add(values[i]);
            }
        }

        static float[][] copy(final AFloat[] arrays) {
            final float[][] data = new float[arrays.length][];
            for (int i = 0; i < arrays.length; i++) {
                data[i] = arrays[i].copyData();
                // free memory:
                arrays[i] = null;
            }
            return data;
        }

        private int size;
        private float[] array;

        AFloat() {
            this(INITIAL_CAPACITY);
        }

        AFloat(final int capacity) {
            array = new float[(capacity >= INITIAL_CAPACITY) ? capacity
                    : INITIAL_CAPACITY];
        }

        void reset() {
            size = 0;
        }

        void add(final float value) {
            float[] a = array;
            if (size + 1 > a.length) {
                // grow ie float current size:
                array = a = Arrays.copyOf(a, getGrowSize(a.length));
            }
            a[size++] = value;
        }

        float[] copyData() {
            return Arrays.copyOf(array, size);
        }
    }

    static final class ALong {

        static ALong[] create(final int nDims) {
            final ALong[] arrays = new ALong[nDims];
            for (int i = 0; i < nDims; i++) {
                arrays[i] = new ALong();
            }
            return arrays;
        }

        static void fill(final ALong[] arrays, final long[] values) {
			// optimize array dimensions checks
            // size + 1 > a.length
            // skip dimension checks:
            for (int i = 0; i < arrays.length; i++) {
                arrays[i].add(values[i]);
            }
        }

        static long[][] copy(final ALong[] arrays) {
            final long[][] data = new long[arrays.length][];
            for (int i = 0; i < arrays.length; i++) {
                data[i] = arrays[i].copyData();
                // free memory:
                arrays[i] = null;
            }
            return data;
        }

        private int size;
        private long[] array;

        ALong() {
            this(INITIAL_CAPACITY);
        }

        ALong(final int capacity) {
            array = new long[(capacity >= INITIAL_CAPACITY) ? capacity
                    : INITIAL_CAPACITY];
        }

        void reset() {
            size = 0;
        }

        void add(final long value) {
            long[] a = array;
            if (size + 1 > a.length) {
                // grow ie long current size:
                array = a = Arrays.copyOf(a, getGrowSize(a.length));
            }
            a[size++] = value;
        }

        long[] copyData() {
            return Arrays.copyOf(array, size);
        }
    }

    static final class AInteger {

        static AInteger[] create(final int nDims) {
            final AInteger[] arrays = new AInteger[nDims];
            for (int i = 0; i < nDims; i++) {
                arrays[i] = new AInteger();
            }
            return arrays;
        }

        static void fill(final AInteger[] arrays, final int[] values) {
			// optimize array dimensions checks
            // size + 1 > a.length
            // skip dimension checks:
            for (int i = 0; i < arrays.length; i++) {
                arrays[i].add(values[i]);
            }
        }

        static int[][] copy(final AInteger[] arrays) {
            final int[][] data = new int[arrays.length][];
            for (int i = 0; i < arrays.length; i++) {
                data[i] = arrays[i].copyData();
                // free memory:
                arrays[i] = null;
            }
            return data;
        }

        private int size;
        private int[] array;

        AInteger() {
            this(INITIAL_CAPACITY);
        }

        AInteger(final int capacity) {
            array = new int[(capacity >= INITIAL_CAPACITY) ? capacity
                    : INITIAL_CAPACITY];
        }

        void reset() {
            size = 0;
        }

        void add(final int value) {
            int[] a = array;
            if (size + 1 > a.length) {
                // grow ie int current size:
                array = a = Arrays.copyOf(a, getGrowSize(a.length));
            }
            a[size++] = value;
        }

        int[] copyData() {
            return Arrays.copyOf(array, size);
        }
    }

    static final class AChar {

        static AChar[] create(final int nDims) {
            final AChar[] arrays = new AChar[nDims];
            for (int i = 0; i < nDims; i++) {
                arrays[i] = new AChar();
            }
            return arrays;
        }

        static void fill(final AChar[] arrays, final char[] values) {
			// optimize array dimensions checks
            // size + 1 > a.length
            // skip dimension checks:
            for (int i = 0; i < arrays.length; i++) {
                arrays[i].add(values[i]);
            }
        }

        static char[][] copy(final AChar[] arrays) {
            final char[][] data = new char[arrays.length][];
            for (int i = 0; i < arrays.length; i++) {
                data[i] = arrays[i].copyData();
                // free memory:
                arrays[i] = null;
            }
            return data;
        }

        private int size;
        private char[] array;

        AChar() {
            this(INITIAL_CAPACITY);
        }

        AChar(final int capacity) {
            array = new char[(capacity >= INITIAL_CAPACITY) ? capacity
                    : INITIAL_CAPACITY];
        }

        void reset() {
            size = 0;
        }

        void add(final char value) {
            char[] a = array;
            if (size + 1 > a.length) {
                // grow ie int current size:
                array = a = Arrays.copyOf(a, getGrowSize(a.length));
            }
            a[size++] = value;
        }

        char[] copyData() {
            return Arrays.copyOf(array, size);
        }
    }

    static final class AShort {

        static AShort[] create(final int nDims) {
            final AShort[] arrays = new AShort[nDims];
            for (int i = 0; i < nDims; i++) {
                arrays[i] = new AShort();
            }
            return arrays;
        }

        static void fill(final AShort[] arrays, final short[] values) {
			// optimize array dimensions checks
            // size + 1 > a.length
            // skip dimension checks:
            for (int i = 0; i < arrays.length; i++) {
                arrays[i].add(values[i]);
            }
        }

        static short[][] copy(final AShort[] arrays) {
            final short[][] data = new short[arrays.length][];
            for (int i = 0; i < arrays.length; i++) {
                data[i] = arrays[i].copyData();
                // free memory:
                arrays[i] = null;
            }
            return data;
        }

        private int size;
        private short[] array;

        AShort() {
            this(INITIAL_CAPACITY);
        }

        AShort(final int capacity) {
            array = new short[(capacity >= INITIAL_CAPACITY) ? capacity
                    : INITIAL_CAPACITY];
        }

        void reset() {
            size = 0;
        }

        void add(final short value) {
            short[] a = array;
            if (size + 1 > a.length) {
                // grow ie short current size:
                array = a = Arrays.copyOf(a, getGrowSize(a.length));
            }
            a[size++] = value;
        }

        short[] copyData() {
            return Arrays.copyOf(array, size);
        }
    }

    static final class AString {

        static AString[] create(final int nDims) {
            final AString[] arrays = new AString[nDims];
            for (int i = 0; i < nDims; i++) {
                arrays[i] = new AString();
            }
            return arrays;
        }

        static void fill(final AString[] arrays, final String[] values) {
			// optimize array dimensions checks
            // size + 1 > a.length
            // skip dimension checks:
            for (int i = 0; i < arrays.length; i++) {
                arrays[i].add(values[i]);
            }
        }

        static String[][] copy(final AString[] arrays) {
            final String[][] data = new String[arrays.length][];
            for (int i = 0; i < arrays.length; i++) {
                data[i] = arrays[i].copyData();
                // free memory:
                arrays[i] = null;
            }
            return data;
        }

        private int size;
        private String[] array;

        AString() {
            this(INITIAL_CAPACITY);
        }

        AString(final int capacity) {
            array = new String[(capacity >= INITIAL_CAPACITY) ? capacity
                    : INITIAL_CAPACITY];
        }

        void reset() {
            size = 0;
        }

        void add(final String value) {
            String[] a = array;
            if (size + 1 > a.length) {
                // grow ie Object current size:
                array = a = Arrays.copyOf(a, getGrowSize(a.length));
            }
            a[size++] = value;
        }

        String[] copyData() {
            return Arrays.copyOf(array, size);
        }
    }

}
