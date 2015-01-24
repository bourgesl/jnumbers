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
package org.jnumbers.text;

import org.jnumbers.LRUCache;

/**
 * Basic String deduplication using the LRU cache implementation (NOT Thread-safe)
 * 
 * @author Laurent Bourges
 */
public final class StringDeduplicator {

    /** default maximum size of the String instance cache (LRU) */
    private final static int DEFAULT_LRU_CAPACITY = 16 * 1024;

    /** String instance cache (LRU) */
    private final LRUCache<CharSequenceKey, String> _lruCache;
    private final CharSequenceKey _sKey = new CharSequenceKey();

    public StringDeduplicator() {
        this(DEFAULT_LRU_CAPACITY);
    }

    public StringDeduplicator(final int size) {
        _lruCache = new LRUCache<CharSequenceKey, String>(size);
    }

    /**
     * use LRU cache to reduce instance counts (deduplication)
     * @param value char sequence (String or StringBuilder instance) to look up
     * @return String instance (deduplicated in this cache)
     */
    public String deduplicate(final CharSequence value) {

        // 1. check hashcode with the given value
        _sKey.update(value);

        // Get existing value:
        String val = _lruCache.get(_sKey);
        if (val == null) {
            // 2. add missing String:
            val = value.toString();

            _lruCache.put(new CharSequenceKey(_sKey.hashCode(), val), val);
        }
        return val;
    }

    public void showStringCacheStats() {
        System.out.println("StringDeduplicator statistics:");
        _lruCache.showStats();
    }

}
