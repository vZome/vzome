package com.vzome.core.edits;

import java.util.function.Function;

import com.vzome.core.construction.Color;

/**
 * @author David Hall
 */
public class ColorMappers {

    public interface ColorMapper<T> extends Function<T, Color> {

        @Override
        public Color apply(T src);

        public default boolean requiresOrderedSelection() {
            return false;
        }

        /**
         * We had a default implementation here, using reflection, but that gave me problems
         * when transpiled to JavaScript with JSweet.  Now each class simply implements the method.
         * @return
         */
        public String getName();
    }
}
