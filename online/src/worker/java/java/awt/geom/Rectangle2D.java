
package java.awt.geom;

public abstract class Rectangle2D
{
  public abstract double getWidth();

  public abstract double getHeight();

  public static class Float extends Rectangle2D
  {
    public float width;

    public float height;

    public Float( float x, float y, float w, float h )
    {
      this.width = w;
      this.height = h;
    }

    public double getWidth() {
        return (double) width;
    }
    public double getHeight() {
        return (double) height;
    }
  }
}