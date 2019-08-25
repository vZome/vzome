package com.vzome.core.edits;

import com.vzome.core.render.Color;
import java.util.function.Function;

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

        public default String getName() {
            Class<?> cls = getClass();
            if(cls.isAnonymousClass()) {
                throw new IllegalStateException("Anonymous implementations must override "
                        + ColorMapper.class.getSimpleName()
                        + ".getName() so that the result is not derived from the class name.");
            }
            return cls.getSimpleName();
        }
    }
}
