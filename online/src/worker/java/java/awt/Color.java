package java.awt;

public class Color
{
  public static final Color WHITE = new Color( 255, 255, 255 );
  public static final Color BLACK = new Color( 0, 0, 0 );

  private final int value;

  public Color( int rgb )
  {
    this.value = 0xff000000 | rgb;
  }

  public Color( int r, int g, int b )
  {
    this( r, g, b, 255 );
  }

  public Color( int r, int g, int b, int a )
  {
    this.value = ((a & 0xFF) << 24) |
            ((r & 0xFF) << 16) |
            ((g & 0xFF) << 8)  |
            ((b & 0xFF) << 0);
  }

  public int getRed() {
      return (this.value >> 16) & 0xFF;
  }

  public int getGreen() {
      return (this.value >> 8) & 0xFF;
  }

  public int getBlue() {
      return (this.value >> 0) & 0xFF;
  }

  public int getRGB() {
      return value;
  }

  public float[] getRGBColorComponents(float[] compArray)
  {
    float[] f = new float[3];
    f[0] = ((float)getRed())/255f;
    f[1] = ((float)getGreen())/255f;
    f[2] = ((float)getBlue())/255f;
    return f;
  }
}