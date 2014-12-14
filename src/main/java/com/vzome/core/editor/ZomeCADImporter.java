
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.vzome.core.algebra.PentagonField;

public class ZomeCADImporter
{
    private static final int CHECKPOINT                         = 0x0001;

    private static final int END_OF_FILE                        = 0x0002;

    private static final int TESSELATE_UNION                    = 0x0010;

    private static final int TESSELATE_INTERSECTION             = 0x0011;
    
    private static final int MIRROR_ALONG_BLUE_UNION            = 0x0020;

    private static final int MIRROR_ALONG_BLUE_INTERSECTION     = 0x0021;

    private static final int ROTATE_AROUND_BLUE_UNION           = 0x0030;

    private static final int ROTATE_AROUND_YELLOW_UNION         = 0x0031;
    
    private static final int ROTATE_AROUND_RED_UNION            = 0x0032;

    private static final int ROTATE_AROUND_BLUE_INTERSECTION    = 0x0040;

    private static final int ROTATE_AROUND_YELLOW_INTERSECTION  = 0x0041;

    private static final int ROTATE_AROUND_RED_INTERSECTION     = 0x0042;
    
    private static final int ADD_HUB                            = 0x0100;

    private static final int ADD_YELLOW_STRUT                   = 0x0101;

    private static final int ADD_BLUE_STRUT                     = 0x0102;

    private static final int ADD_RED_STRUT                      = 0x0103;
    
    private static final int ADD_GREEN_STRUT                    = 0x0104;

    private static final int REMOVE_HUB                         = 0x0200;

    private static final int REMOVE_YELLOW_STRUT                = 0x0201;

    private static final int REMOVE_BLUE_STRUT                  = 0x0202;
    
    private static final int REMOVE_RED_STRUT                   = 0x0203;

    private static final int REMOVE_GREEN_STRUT                 = 0x0204;
    
    public interface Events
    {
        int RED = 0, YELLOW = 1, BLUE = 2, GREEN = 3;
        
        void beginModel();
        
        void beginEdit();
        
        void addBall( int[] /*AlgebraicVector*/ location );
        
        void removeBall( int[] /*AlgebraicVector*/ location );
        
        void addStrut( int[] /*AlgebraicVector*/ location, int orbit, int zone, int size );
        
        void removeStrut( int[] /*AlgebraicVector*/ location, int orbit, int zone, int size );
        
        void tesselate( int[] /*AlgebraicVector*/ from, int[] /*AlgebraicVector*/ to, boolean add );
        
        void reflect( int[] /*AlgebraicVector*/ center, int zone, boolean add );
        
        void rotate( int[] /*AlgebraicVector*/ center, int orbit, int zone, boolean add );
        
        void unknown( int type, int[] params );
        
        void endEdit();
        
        void endModel();
    }
    
    
    public static void main( String[] args )
    {
        try {
            String outFile = args.length > 1 ? args[ 1 ] : null;
            InputStream bytes = new FileInputStream( args[ 0 ] );
            Events events = new DebugEvents( outFile );
            PentagonField field = new PentagonField();
            new ZomeCADImporter( bytes, events, field ) .parseStreamInternal();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
        
    private final DataInputStream data;
    
    private final Events events;
    
    private final PentagonField field;
    
    private ZomeCADImporter( InputStream bytes, Events events, PentagonField field )
    {
        data = new DataInputStream( bytes );
        this.events = events;
        this.field = field;
        SCALE = ((float) field .evaluateNumber( field .createPower( 3 ) ) / 37.21721f) * 0.5f;
    }

    private void parseStreamInternal() throws IOException
    {
        events .beginModel();
        data .skipBytes( 8 );  // skip the header

        int majorVersion = readInt();
        int minorVersion = data .readUnsignedByte();
        int build = data .readUnsignedByte();

        boolean fileStarted = false, fileDone = false;
        do {
            long packetSize = readLong() - 6;
            int packetType = readInt();
            float x = 0, y = 0, z = 0;
            int[] /*AlgebraicVector*/ start = null, end = null;
            int orbit = 0, zone = 0, size = 0;

            // --------- 1. the orbit is encoded in the packet type
            switch ( packetType )
            {
            case MIRROR_ALONG_BLUE_UNION:
            case MIRROR_ALONG_BLUE_INTERSECTION:
            case ADD_BLUE_STRUT:
            case REMOVE_BLUE_STRUT:
            case ROTATE_AROUND_BLUE_UNION:
            case ROTATE_AROUND_BLUE_INTERSECTION:
                orbit = Events.BLUE;
                break;
                
            case ADD_YELLOW_STRUT:
            case REMOVE_YELLOW_STRUT:
            case ROTATE_AROUND_YELLOW_UNION:
            case ROTATE_AROUND_YELLOW_INTERSECTION:
                orbit = Events.YELLOW;
                break;
                
            case ADD_RED_STRUT:
            case REMOVE_RED_STRUT:
            case ROTATE_AROUND_RED_UNION:
            case ROTATE_AROUND_RED_INTERSECTION:
                orbit = Events.RED;
                break;
                
            case ADD_GREEN_STRUT:
            case REMOVE_GREEN_STRUT:
                orbit = Events.GREEN;
                break;
            }

            // ------------- 2. read the start location
            switch ( packetType )
            {
            case ADD_HUB:
            case REMOVE_HUB:
            case MIRROR_ALONG_BLUE_UNION:
            case MIRROR_ALONG_BLUE_INTERSECTION:
            case ADD_BLUE_STRUT:
            case REMOVE_BLUE_STRUT:
            case ROTATE_AROUND_BLUE_UNION:
            case ROTATE_AROUND_BLUE_INTERSECTION:
            case ADD_YELLOW_STRUT:
            case REMOVE_YELLOW_STRUT:
            case ROTATE_AROUND_YELLOW_UNION:
            case ROTATE_AROUND_YELLOW_INTERSECTION:
            case ADD_RED_STRUT:
            case REMOVE_RED_STRUT:
            case ROTATE_AROUND_RED_UNION:
            case ROTATE_AROUND_RED_INTERSECTION:
            case ADD_GREEN_STRUT:
            case REMOVE_GREEN_STRUT:
                x = readFloat();
                y = readFloat();
                z = readFloat();
                start = inferVector( x, y, z );
                break;
            default:
                break;
            }
            // ------------- 3. read the end location
            switch ( packetType )
            {
            case TESSELATE_UNION:
            case TESSELATE_INTERSECTION:
                x = readFloat();
                y = readFloat();
                z = readFloat();
                end = inferVector( x, y, z );
                break;
            default:
                break;
            }
            
            // ----------- 4. read the color, and ignore it
            switch ( packetType )
            {
            case ADD_HUB:
            case ADD_BLUE_STRUT:
            case ADD_YELLOW_STRUT:
            case ADD_RED_STRUT:
            case ADD_GREEN_STRUT:
            case REMOVE_HUB:
            case REMOVE_BLUE_STRUT:
            case REMOVE_YELLOW_STRUT:
            case REMOVE_RED_STRUT:
            case REMOVE_GREEN_STRUT:
                data .skipBytes( 6 );
                break;
            default:
                break;
            }
            
            // ----------- 5. read zone and strut size
            switch ( packetType )
            {
            case ADD_BLUE_STRUT:
            case ADD_YELLOW_STRUT:
            case ADD_RED_STRUT:
            case ADD_GREEN_STRUT:
            case REMOVE_BLUE_STRUT:
            case REMOVE_YELLOW_STRUT:
            case REMOVE_RED_STRUT:
            case REMOVE_GREEN_STRUT:
                zone = readInt();
                size = readInt();
                break;
            case MIRROR_ALONG_BLUE_UNION:
            case MIRROR_ALONG_BLUE_INTERSECTION:
            case ROTATE_AROUND_BLUE_UNION:
            case ROTATE_AROUND_BLUE_INTERSECTION:
            case ROTATE_AROUND_YELLOW_UNION:
            case ROTATE_AROUND_YELLOW_INTERSECTION:
            case ROTATE_AROUND_RED_UNION:
            case ROTATE_AROUND_RED_INTERSECTION:
                zone = readInt();
                break;
            default:
                break;
            }
            
            // ----------- 6. read the remaining data, process the event
            
            switch ( packetType ) 
            {
            case CHECKPOINT:
                if ( !fileStarted )
                {
                    fileStarted = true;
                    events .endEdit();
                }
                events .beginEdit();
                break;

            case ADD_HUB:
                events .addBall( start );
                break;
                
            case REMOVE_HUB:
                events .removeBall( start );
                break;

            case ADD_BLUE_STRUT:
            case ADD_YELLOW_STRUT:
            case ADD_RED_STRUT:
            case ADD_GREEN_STRUT:
                events .addStrut( start, orbit, zone, size );
                break;
                
            case REMOVE_BLUE_STRUT:
            case REMOVE_YELLOW_STRUT:
            case REMOVE_RED_STRUT:
            case REMOVE_GREEN_STRUT:
                events .removeStrut( start, orbit, zone, size );
                break;

            case MIRROR_ALONG_BLUE_UNION:
                events .reflect( start, zone, true );
                break;

            case MIRROR_ALONG_BLUE_INTERSECTION:
                events .reflect( start, zone, false );
                break;

            case ROTATE_AROUND_BLUE_UNION:
            case ROTATE_AROUND_YELLOW_UNION:
            case ROTATE_AROUND_RED_UNION:
                events .reflect( start, zone, true );
                break;

            case ROTATE_AROUND_BLUE_INTERSECTION:
            case ROTATE_AROUND_YELLOW_INTERSECTION:
            case ROTATE_AROUND_RED_INTERSECTION:
                events .reflect( start, zone, false );
                break;

            case END_OF_FILE:
                fileDone = true;
                break;

            default:
                int[] params = new int[ (int) packetSize/2 ];
                for ( int i = 0; i < params.length; i++ )
                    params[ i ] = readInt();
                events .unknown( packetType, params );
            break;
            }

        } while ( !fileDone );

        events .endModel();
    }

    private static final float EPSILON = 0.001f;

    public int[] inferVector( float x, float y, float z )
    {
        int[] result = new int[ 12 ];
        field .setVectorComponent( result, 0, inferNumber( x ) );
        field .setVectorComponent( result, 1, inferNumber( y ) );
        field .setVectorComponent( result, 2, inferNumber( z ) );
        return result;
    }
    
    public int[] inferNumber( float value )
    {
        if ( value - 0f < EPSILON )
            return new int[]{ 0,1,0,1 };

        int[] result = null;
        boolean negate = value < 0f;
        value = Math .abs( value );
        if ( Math .abs( value - 1f ) < EPSILON )
            result = new int[]{ 1,1,0,1 };
        else if ( Math .abs( value - 0.5f ) < EPSILON )
            result = new int[]{ 1,2,0,1 };
        else {
            int sign = Float .compare( value, 1f );
            for ( int i = 1; i < 13; i++ )
            {
                int[] candidate = field .createPower( sign * i );
                double real = field .evaluateNumber( candidate );
                if ( Math .abs( value - real ) < EPSILON )
                {
                    result = candidate;
                    break;
                }
                if ( sign > 0 && real > value )
                    break;
                if ( sign < 0 && real < value )
                    break;
            }
        }
        if ( result == null )
        {
            int sign = Float .compare( value, 0.5f );
            for ( int i = 1; i < 13; i++ )
            {
                int[] candidate = field .createPower( sign * i );
                candidate = field .multiply( candidate, field .createRational( new int[]{ 1,2 } ) );
                double real = field .evaluateNumber( candidate );
                if ( Math .abs( value - real ) < EPSILON )
                {
                    result = candidate;
                    break;
                }
                if ( sign > 0 && real > value )
                    break;
                if ( sign < 0 && real < value )
                    break;
            }
        }
        if ( result == null )
        {
            int sign = Float .compare( value, 0.25f );
            for ( int i = 1; i < 13; i++ )
            {
                int[] candidate = field .createPower( sign * i );
                candidate = field .multiply( candidate, field .createRational( new int[]{ 1,4 } ) );
                double real = field .evaluateNumber( candidate );
                if ( Math .abs( value - real ) < EPSILON )
                {
                    result = candidate;
                    break;
                }
                if ( sign > 0 && real > value )
                    break;
                if ( sign < 0 && real < value )
                    break;
            }
        }
        if ( negate & (result != null) )
            return field .negate( result );

        return result;
    }
    
    private long readLong() throws IOException
    {
        long result = 0;
        for ( int i = 0; i < 4; i++ )
        {
            long aByte = data .readUnsignedByte();
            aByte <<= 8*i;
            result += aByte;
        }
        return result;
    }
    
    private int readInt() throws IOException
    {
        int lowOrder = data .readUnsignedByte();
        int highOrder = data .readUnsignedByte();
        return (highOrder << 8) + lowOrder;
    }
    
    private final float SCALE;
    
    private float readFloat() throws IOException
    {
        byte[] result = new byte[4];
        for ( int i = 0; i < 4; i++ )
            result[ 3-i ] = (byte) data .readUnsignedByte();
        DataInputStream reversed = new DataInputStream( new ByteArrayInputStream( result ) );
        float value = reversed .readFloat();
        reversed .close();
        return value * SCALE;
    }

    private static class DebugEvents implements Events
    {
        private final PrintWriter out;

        public DebugEvents( String string ) throws FileNotFoundException
        {
            if ( string == null )
                out = new PrintWriter( System .out );
            else
                out = new PrintWriter( new FileOutputStream( string ) );
        }

        public void addBall( int[] /*AlgebraicVector*/ location )
        {
            out .println( "  add ball  " + location );
            out .flush();
        }
        
        private String zone( int orbit, int zone )
        {
            String color = null;
            switch ( orbit ) {
            case RED:
                color = "red";
                break;
            case YELLOW:
                color = "yellow";
                break;
            case BLUE:
                color = "blue";
                break;
            case GREEN:
                color = "green";
                break;
            default:
                break;
            }
            return color + "  " + Integer .toString( zone );
        }

        public void addStrut( int[] /*AlgebraicVector*/ location, int orbit, int zone, int size )
        {
            out .println( "  add strut  " + location + "  " + zone( orbit, zone ) + "  " + size );
            out .flush();
        }

        public void beginEdit()
        {
            out .println( "-----" );
            out .flush();
        }

        public void beginModel()
        {
            out .println( "[==========" );
            out .flush();
        }

        public void endEdit()
        {}

        public void endModel()
        {
            out .println( "==========]" );
            out .flush();
        }

        public void reflect( int[] /*AlgebraicVector*/ center, int zone, boolean add )
        {
            out .println( "  reflect  " + zone( 99, zone ) + "  " + (add?"union":"intersection") );
            out .flush();
        }

        public void removeBall( int[] /*AlgebraicVector*/ location )
        {
            out .println( "remove ball  " + location );
            out .flush();
        }

        public void removeStrut( int[] /*AlgebraicVector*/ location, int orbit, int zone,
                int size )
        {
            out .println( "  remove strut  " + location + "  " + zone( orbit, zone ) + "  " + size );
            out .flush();
        }

        public void rotate( int[] /*AlgebraicVector*/ center, int orbit, int zone,
                boolean add )
        {
            out .println( "  rotate  " + zone( orbit, zone ) + "  " + (add?"union":"intersection") );
            out .flush();
        }

        public void tesselate( int[] /*AlgebraicVector*/ from, int[] /*AlgebraicVector*/ to, boolean add )
        {
            out .println( "  tesselate  " + from + " -> " + to + "  " + (add?"union":"intersection") );
            out .flush();
        }

        public void unknown( int type, int[] params )
        {
            out .println( "  UNKNOWN  " );
            out .flush();
        }

    }

}
