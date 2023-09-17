
package java.awt.geom;

import java.util.ArrayList;

public class GeneralPath
{
  private final ArrayList xs = new ArrayList();
  private final ArrayList ys = new ArrayList();
  private final ArrayList actions = new ArrayList();

  public void moveTo( double x, double y)
  {
    xs .add( x );
    ys .add( y );
    actions .add( PathIterator.SEG_MOVETO );
  }

  public void lineTo( double x, double y )
  {
    xs .add( x );
    ys .add( y );
    actions .add( PathIterator.SEG_LINETO );
  }

  public void closePath()
  {
    xs .add( 0 );
    ys .add( 0 );
    actions .add( PathIterator.SEG_CLOSE );
  }

  public final PathIterator getPathIterator( Object at )
  {
    return new PathIterator() {

      private int index = 0;

      public boolean isDone()
      {
        return index == actions .size();
      }

      public int currentSegment( float[] coords )
      {
        coords[ 0 ] = (float) xs .get( index );
        coords[ 1 ] = (float) ys .get( index );
        return (int) actions .get( index );
      }

      public void next()
      {
        ++index;
      }
    };
  }
}