package com.vzome.core.generic;

import java.util.function.Predicate;

/**
 * @author David Hall
 */
public class SubClassIterator<T, S extends T> extends FilteredIterator<T, S> {
    
    private final Class<S> subClass;

    @Override
    public boolean preFilter(T element) {
        return (element != null && subClass.isAssignableFrom(element.getClass()))
                ? super.preFilter(element)
                : false;
    }
    
    @Override
    protected S apply(T element) {
        return subClass.cast(element);
    }
    
    public SubClassIterator(Class<S> subClass, Predicate<T> preTest, Iterable<T> iterable, Predicate<S> postTest) {
        super(preTest, iterable, postTest);
        this.subClass = subClass;
    }
}
