package com.vzome.core.generic;

import java.util.Comparator;

/**
 * @author David Hall
 */
public class ArrayComparator<T extends Comparable<T>> {
    
    public ContentFirstArrayComparator<T> getContentFirstArrayComparator() {
        return new ContentFirstArrayComparator<>();
    }

    public LengthFirstArrayComparator<T> getLengthFirstArrayComparator() {
        return new LengthFirstArrayComparator<>();
    }

    public static class ContentFirstArrayComparator<T extends Comparable<T>> implements Comparator<T[]>
    {
        @Override
        public int compare(T array1[], T array2[]) {
            Integer len1 = array1.length;
            Integer len2 = array2.length;
            int smaller = len1 < len2 ? len1 : len2;
            for (int i = 0; i < smaller; i++) {
                T element1 = array1[i];
                T element2 = array2[i];
                int comparison = element1.compareTo(element2);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return len1.compareTo(len2);
        }
    }

    public static class LengthFirstArrayComparator<T extends Comparable<T>> implements Comparator<T[]>
    {
        @Override
        public int compare(T array1[], T array2[]) {
            Integer len1 = array1.length;
            Integer len2 = array2.length;
            int comparison = len1.compareTo(len2);
            if (comparison != 0) {
                return comparison;
            }
            for (int i = 0; i < len1; i++) {
                T element1 = array1[i];
                T element2 = array2[i];
                comparison = element1.compareTo(element2);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return comparison;
        }
    }

}
