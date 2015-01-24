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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU cache implementation (NOT Thread-safe)
 * 
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author Laurent Bourges
 */
public final class LRUCache<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;

	private static final boolean doStats = true;

	/* members */
	/** maximum size */
	private final int size;
	/** accesses */
	private long accesses;
	/** misses */
	private long misses;

	/**
	 * Create a new LRU cache with the given maximum size (= capacity)
	 *
	 * @param size maximum size
	 */
	public LRUCache(final int size) {
		super(size * 2, 0.75f, true);
		this.size = size;
	}

	/**
	 * Returns <tt>true</tt> if this map should remove its eldest entry.
	 *
	 * @param eldest The least recently inserted entry in the map, or if this is an access-ordered map, the least
	 * recently accessed entry. This is the entry that will be removed it this method returns
	 * <tt>true</tt>.
	 * @return   <tt>true</tt> if the eldest entry should be removed from the map; <tt>false</tt> if it should be
	 * retained.
	 */
	@Override
	protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
		return (size() > size);
	}

	@Override
	public void clear() {
		super.clear();
		if (doStats) {
			accesses = misses = 0;
		}
	}

	public V getOrPutMissing(K key, V value) {
		final V ref = get(key);
		if (ref == null) {
			// misses:
			if (doStats) {
				accesses++;
				misses++;
			}
			put(key, value);
			return value;
		}
		if (doStats) {
			accesses++;
		}
		return ref;
	}

	@Override
	public V get(Object key) {
		final V ref = super.get(key);
		if (ref == null) {
			// misses:
			if (doStats) {
				accesses++;
				misses++;
			}
			return null;
		}
		if (doStats) {
			accesses++;
		}
		return ref;
	}

	public long getAccesses() {
		return accesses;
	}

	public long getMisses() {
		return misses;
	}

	public double getHitRatio() {
		return (accesses == 0l) ? 0.0 : ((double) (accesses - misses)) / accesses;
	}

	public double getMissRatio() {
		return (accesses == 0l) ? 0.0 : ((double) misses) / accesses;
	}

	public void showStats() {
		System.out.println("  MapSize:\t" + size());
		System.out.println("  Accesses:\t" + getAccesses());
		System.out.println("  Misses:\t" + getMisses());
		System.out.println("  HitRatio:\t" + (100.0 * getHitRatio()) + " %");
		System.out.println("  MissRatio:\t" + (100.0 * getMissRatio()) + " %");
	}
}
