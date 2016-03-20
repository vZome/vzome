package com.vzome.core.generic;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * @author David Hall
 * Based on http://stackoverflow.com/questions/5474893/how-to-implement-this-filteringiterator
 */
public abstract class FilteredIterator<T, R> implements Iterator<R>, Iterable<R> {

    private final Iterator<T> wrappedIterator;
    private final Predicate<T> preFilter;
    private final Predicate<R> postFilter;
    private R nextElement = null;
    private Boolean hasNext = null; // determined on first use

    /**
     * Elements must match this filter before conversion
     * @param element
     */
    protected boolean preFilter(T element) {
        return preFilter == null 
                ? true
                : preFilter.test(element);
    }

    /**
     * Elements must match this filter after conversion
     * @param element
     */
    protected boolean postFilter(R element) {
        return postFilter == null 
                ? true
                : postFilter.test(element);
    }
    
    /**
     * Elements are converted from T to R. 
     * T and R may be identical, related (e.g. sub-classed) or completely unrelated.
     * @param element
     */
    protected abstract R apply(T element);

    protected FilteredIterator(Iterable<T> iterable) {
        this(null, iterable, null);    
    }

    protected FilteredIterator(Predicate<T> preTest, Iterable<T> iterable) {
        this(preTest, iterable, null);    
    }

    protected FilteredIterator(Iterable<T> iterable, Predicate<R> postTest) {
        this(null, iterable, postTest);    
    }

    /**
     * Creates a new FilteredIterator by wrapping the iterator 
     *  and returning only converted elements matching the filters.
     * 
     * @param preTest may be null to skip preTest;
     * @param iterable
     * @param postTest may be null to skip postTest;
     */
    protected FilteredIterator(Predicate<T> preTest, Iterable<T> iterable, Predicate<R> postTest) {
        this.wrappedIterator = iterable.iterator();
        preFilter = preTest;
        postFilter = postTest;
        // can't call nextMatch in any c'tor, because derived classes 
        // may not be fully initialized yet and their filters may not work.this(preTest, iterable.iterator(), postTest);    
    }

    @Override
    public Iterator<R> iterator() {
        return this; // careful, don't return wrappedIterator
    }

    @Override
    public boolean hasNext() {
        if(hasNext == null) {
            // first call after c'tor
            nextMatch();
        }
        return hasNext;
    }

    @Override
    public R next() {
        if(hasNext == null) {
            // first call after c'tor
            nextMatch();
        }
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextMatch();
    }
    
    private R nextMatch() {
        R lastMatch = nextElement;
        while (wrappedIterator.hasNext()) {
            T next = wrappedIterator.next();
            if (preFilter(next)) {
                R converted = apply(next);
                if(postFilter(converted)) {
                    nextElement = converted;
                    hasNext = true;
                    return lastMatch;
                }
            }
        }
        hasNext = false;
        return lastMatch;
    }
    
    @Override
    public void remove() {
        wrappedIterator.remove();
    }

    public static class Filters {
        private Filters() {}; // no public c'tor

        /**
         * A static convenience function that may be passed as a preFilter parameter
         * @param <T>
         * @param element
         * @return {@code true} if element is not null
         */
        public static <T> boolean elementNotNull(T element) {
            return element != null;
        }

        /**
         * A static convenience function that may be passed as a postFilter parameter
         * @param <R>
         * @param result
         * @return {@code true} if result is not null
         */
        public static <R> boolean resultNotNull(R result) {
            return result != null;
        }

        /**
         * A static convenience function that may be used with another predicate
         * to be passed as a preFilter or postFilter parameter
         * @param <B>
         * @param predicate The predicate to be negated.
         * @param arg The parameter to be passed to the predicate.
         * @return The opposite of what the predicate returns.
         */
        public static <B> boolean not(Predicate<B> predicate, B arg) {
            return predicate.negate().test(arg);
        }

        /**
         * A static convenience function that may be used to combine two other predicates
         * to be passed as a preFilter or postFilter parameter
         * @param <B>
         * @param check1 The 1st predicate to be evaluated.
         * @param check2 The 2nd predicate to be evaluated.
         * @param arg The parameter to be passed to the predicates.
         * @return {@code true} only if both predicates are true.
         */
        public static <B> boolean and(Predicate<B> check1, Predicate<B> check2, B arg) {
            return check1.and(check2).test(arg);
        }

        /**
         * A static convenience function that may be used to combine two other predicates
         * to be passed as a preFilter or postFilter parameter
         * @param <B>
         * @param check1 The 1st predicate to be evaluated.
         * @param check2 The 2nd predicate to be evaluated.
         * @param arg The parameter to be passed to the predicates.
         * @return {@code true} if either predicate is true.
         */
        public static <B> boolean or(Predicate<B> check1, Predicate<B> check2, B arg) {
            return check1.or(check2).test(arg);
        }

    }

}
