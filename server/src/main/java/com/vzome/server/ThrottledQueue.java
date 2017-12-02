package com.vzome.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * A simple throttled blocking-queue that returns elements at a constant rate (M
 * elements in N time)
 * 
 * @author Kamran Zafar
 * 
 */
public class ThrottledQueue {
    // M elements
    private final int mElements;

    // N Time in milliseconds
    private final long nTime;

    // delay per queue element from last fetch
    private final long delayPerElement;

    // delay offset in milliseconds
    private long delayOffset = System.currentTimeMillis();

    // Reference start time
    private final long startTime = System.nanoTime();

    private final DelayQueue<DelayedElement> queue = new DelayQueue<DelayedElement>();

    public ThrottledQueue(int mElements, long nTime) {
        this.mElements = mElements;
        this.nTime = nTime;

        // calculate delay per element
        delayPerElement = (long) Math.ceil( nTime / mElements );
    }

    /**
     * Adds an element to the queue
     * 
     * @param item
     */
    public void add(Object element) {
        queue.put( new DelayedElement( element ) );
    }

    /**
     * Blocks till delay expires and sets the next offset
     * 
     * @return Object
     */
    public synchronized Object get() {
        try {
            Object obj = queue.take().getElement();

            // set next offset
            delayOffset = System.currentTimeMillis();

            return obj;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public int getMElements() {
        return mElements;
    }

    public long getNTime() {
        return nTime;
    }

    public long getDelayPerElement() {
        return delayPerElement;
    }

    /**
     * Start time in nanoseconds; reference point
     * 
     * @return
     */
    public long getStartTime() {
        return startTime;
    }

    private class DelayedElement implements Delayed {
        private final Object element;
        private final long insertTime; // relative to startTime

        public DelayedElement(Object element) {
            this.element = element;

            // elapsed time in nano seconds
            insertTime = System.nanoTime() - startTime;
        }

        public Object getElement() {
            return element;
        }

        /**
         * Returns < 0 if delay expires
         * 
         * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert( delayOffset + delayPerElement - System.currentTimeMillis(), TimeUnit.MILLISECONDS );
        }

        /**
         * Compares elements in order to return them in the same order they were
         * inserted
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(Delayed o) {
            DelayedElement de = (DelayedElement) o;
            if (insertTime < de.getInsertTime())
                return -1;
            if (insertTime > de.getInsertTime())
                return 1;

            return 0;
        }

        public long getInsertTime() {
            return insertTime;
        }
    }

    // Test
    public static void main(String[] args) throws Exception {
        // Create a throttled queue that returns 2 elements in 1 second
        final ThrottledQueue q = new ThrottledQueue( 1, 2000 );

        // some elements, e.g. SMS records
        q.add( "element 1" );
        q.add( "element 2" );
        q.add( "element 3" );
        q.add( "element 4" );
        q.add( "element 5" );
        q.add( "element 6" );
        q.add( "element 7" );
        q.add( "element 8" );
        q.add( "element 9" );
        q.add( "element 10" );

        System.out.println( "Delay per element: " + q.getDelayPerElement() );

        // A thread that consumes elements in the queue
        Thread consumer = new Thread( new Runnable() {
            @Override
            public void run() {
                while (true)
                	  if (!q.isEmpty()) {
                    // Do something. e.g. send SMS
                    System.out.println( new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss:S" ).format( new Date( System
                            .currentTimeMillis() ) ) + " - " + q.get() );
                	  } else {
                		  Thread.yield();
                	  }
            }
        } );

        consumer.start();
        consumer.join();
    }
}