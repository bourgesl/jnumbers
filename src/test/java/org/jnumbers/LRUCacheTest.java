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

import java.util.Arrays;
import junit.framework.TestCase;

public class LRUCacheTest extends TestCase {

    enum Mode {

        SINGLE,
        UNIQUE,
        RANDOM,
        REPEAT_M,
        RANDOM_M,
        REPEAT_N,
        RANDOM_N
    };

    public void testCache() throws Exception {
        System.out.println("Test");

        final int offset = 1024 * 1024;
        final int M = 4096;
        final int N = 2048;

//		final int capacity = Math.max(M, N);
        final int capacity = Math.max(M, N) >> 4; // 1/16

        final LRUCache<String, String> _lruCache = new LRUCache<String, String>(capacity);

        // Hard refs to avoid dead code elimination:
        final String[] values = new String[M];
        int x;
        String value;

        for (Mode mode : Mode.values()) {
            _lruCache.clear();

            for (int i = 0; i < M; i++) {
                for (int j = 0; j < N; j++) {
                    x = offset;
                    switch (mode) {
                        case UNIQUE:
                            x += (i * j);
                            break;
                        case RANDOM:
                            x += (int) ((N * M) * Math.random());
                            break;
                        case REPEAT_M:
                            x += i;
                            break;
                        case REPEAT_N:
                            x += j;
                            break;
                        case RANDOM_M:
                            x += (int) (M * Math.random());
                            break;
                        case RANDOM_N:
                            x += (int) (N * Math.random());
                            break;
                        case SINGLE:
                        default:
                    }

                    value = Integer.toString(x);
                    // deduplication:
                    value = _lruCache.getOrPutMissing(value, value);

                    values[i] = value;
                }
            }

            System.out.println("---------------------------------------");
            System.out.println("Mode : " + mode);
            System.out.println("M : " + M);
            System.out.println("N : " + N);
            System.out.println("---");
            _lruCache.showStats();
            System.out.println("---");
            System.out.println("values: " + Arrays.toString(Arrays.copyOf(values, 100)));
            System.out.println("---------------------------------------");
        }
    }

}
