/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.generic {
    /**
     * @author David Hall
     * Based on http://stackoverflow.com/questions/5474893/how-to-implement-this-filteringiterator
     * @class
     */
    export abstract class FilteredIterator<T, R> implements java.util.Iterator<R>, java.lang.Iterable<R> {
        /*private*/ wrappedIterator: java.util.Iterator<T>;

        /*private*/ __preFilter: (p1: T) => boolean;

        /*private*/ __postFilter: (p1: R) => boolean;

        /*private*/ nextElement: R;

        /*private*/ __hasNext: boolean;

        /**
         * Elements must match this filter before conversion
         * @param {*} element
         * @return {boolean}
         */
        preFilter(element: T): boolean {
            return this.__preFilter == null ? true : (target => (typeof target === 'function') ? target(element) : (<any>target).test(element))(this.__preFilter);
        }

        /**
         * Elements must match this filter after conversion
         * @param {*} element
         * @return {boolean}
         */
        postFilter(element: R): boolean {
            return this.__postFilter == null ? true : (target => (typeof target === 'function') ? target(element) : (<any>target).test(element))(this.__postFilter);
        }

        /**
         * Elements are converted from T to R.
         * T and R may be identical, related (e.g. sub-classed) or completely unrelated.
         * @param {*} element
         * @return {*}
         */
        abstract apply(element: T): R;

        constructor(preTest: (p1: T) => boolean, iterable: java.lang.Iterable<T>, postTest: (p1: R) => boolean) {
            if (this.wrappedIterator === undefined) { this.wrappedIterator = null; }
            if (this.__preFilter === undefined) { this.__preFilter = null; }
            if (this.__postFilter === undefined) { this.__postFilter = null; }
            this.nextElement = null;
            this.__hasNext = null;
            this.wrappedIterator = iterable.iterator();
            this.__preFilter = <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(preTest));
            this.__postFilter = <any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0) =>  (funcInst['test'] ? funcInst['test'] : funcInst) .call(funcInst, arg0)})(postTest));
        }

        /**
         * 
         * @return {*}
         */
        public iterator(): java.util.Iterator<R> {
            return this;
        }

        /**
         * 
         * @return {boolean}
         */
        public hasNext(): boolean {
            if (this.__hasNext == null){
                this.nextMatch();
            }
            return this.__hasNext;
        }

        /**
         * 
         * @return {*}
         */
        public next(): R {
            if (this.__hasNext == null){
                this.nextMatch();
            }
            if (!this.__hasNext){
                throw new java.util.NoSuchElementException();
            }
            return this.nextMatch();
        }

        nextMatch(): R {
            const lastMatch: R = this.nextElement;
            while((this.wrappedIterator.hasNext())) {{
                const next: T = this.wrappedIterator.next();
                if (this.preFilter(next)){
                    const converted: R = this.apply(next);
                    if (this.postFilter(converted)){
                        this.nextElement = converted;
                        this.__hasNext = true;
                        return lastMatch;
                    }
                }
            }};
            this.__hasNext = false;
            return lastMatch;
        }

        /**
         * 
         */
        public remove() {
            this.wrappedIterator.remove();
        }
    }
    FilteredIterator["__class"] = "com.vzome.core.generic.FilteredIterator";
    FilteredIterator["__interfaces"] = ["java.util.Iterator","java.lang.Iterable"];



    export namespace FilteredIterator {

        export class Filters {
            constructor() {
            }

            /**
             * A static convenience function that may be passed as a preFilter parameter
             * @param <T>
             * @param {*} element
             * @return {boolean} {@code true} if element is not null
             */
            public static elementNotNull<T>(element: T): boolean {
                return element != null;
            }

            /**
             * A static convenience function that may be passed as a postFilter parameter
             * @param <R>
             * @param {*} result
             * @return {boolean} {@code true} if result is not null
             */
            public static resultNotNull<R>(result: R): boolean {
                return result != null;
            }

            /**
             * A static convenience function that may be used with another predicate
             * to be passed as a preFilter or postFilter parameter
             * @param <B>
             * @param {*} predicate The predicate to be negated.
             * @param {*} arg The parameter to be passed to the predicate.
             * @return {boolean} The opposite of what the predicate returns.
             */
            public static not<B>(predicate: (p1: B) => boolean, arg: B): boolean {
                return (target => (typeof target === 'function') ? target(arg) : (<any>target).test(arg))((target => (typeof target === 'function') ? target() : (<any>target).negate())(predicate));
            }

            /**
             * A static convenience function that may be used to combine two other predicates
             * to be passed as a preFilter or postFilter parameter
             * @param <B>
             * @param {*} check1 The 1st predicate to be evaluated.
             * @param {*} check2 The 2nd predicate to be evaluated.
             * @param {*} arg The parameter to be passed to the predicates.
             * @return {boolean} {@code true} only if both predicates are true.
             */
            public static and<B>(check1: (p1: B) => boolean, check2: (p1: B) => boolean, arg: B): boolean {
                return (target => (typeof target === 'function') ? target(arg) : (<any>target).test(arg))((target => (typeof target === 'function') ? target(check2) : (<any>target).and(check2))(check1));
            }

            /**
             * A static convenience function that may be used to combine two other predicates
             * to be passed as a preFilter or postFilter parameter
             * @param <B>
             * @param {*} check1 The 1st predicate to be evaluated.
             * @param {*} check2 The 2nd predicate to be evaluated.
             * @param {*} arg The parameter to be passed to the predicates.
             * @return {boolean} {@code true} if either predicate is true.
             */
            public static or<B>(check1: (p1: B) => boolean, check2: (p1: B) => boolean, arg: B): boolean {
                return (target => (typeof target === 'function') ? target(arg) : (<any>target).test(arg))((target => (typeof target === 'function') ? target(check2) : (<any>target).or(check2))(check1));
            }
        }
        Filters["__class"] = "com.vzome.core.generic.FilteredIterator.Filters";

    }

}

