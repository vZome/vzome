
package java.awt.geom;

public interface PathIterator
{
  public static final int SEG_MOVETO          = 0;
  public static final int SEG_LINETO          = 1;
  public static final int SEG_CLOSE           = 4;

  public boolean isDone();

  public int currentSegment( float[] coords );

  public void next();
}