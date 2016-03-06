package com.jrisch.lynx;

/**
 * Immutable pair used when reading from a file.
 */
public class LynxPair<T,V> {
    final T first;
    final V second;

    public LynxPair(T first, V second) {
        this.first = first;
        this.second = second;
    }
}
