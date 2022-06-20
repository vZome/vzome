
package com.vzome.desktop.awt;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WatermarkImage
{
    public static void main( String[] args )
    {
        File sourceImageFile = new File( "/Users/vorth/Downloads/welcomeDodec.png");
        File watermarkImageFile = new File( "/Users/vorth/Downloads/3D-Icon-3.png" );
        File destImageFile = new File( "/Users/vorth/Downloads/watermarked.png" );
        
        addImageWatermark(watermarkImageFile, sourceImageFile, destImageFile);
    }

    /**
     * Embeds an image watermark over a source image to produce
     * a watermarked one.
     * @param watermarkImageFile The image file used as the watermark.
     * @param sourceImageFile The source image file.
     * @param destImageFile The output image file.
     */
    static void addImageWatermark( File watermarkImageFile, File sourceImageFile, File destImageFile )
    {
        try {
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

            watermarkImage( sourceImage, watermarkImage );

            ImageIO.write( sourceImage, "png", destImageFile );

            System.out.println("The image watermark is added to the image.");

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void watermarkImage( BufferedImage sourceImage, BufferedImage watermarkImage )
    {
        // initializes necessary graphic properties
        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
        g2d.setComposite(alphaChannel);

        // calculates the coordinate where the image is painted
        int topLeftX = 0; //(sourceImage.getWidth() - watermarkImage.getWidth()) / 2;
        int topLeftY = 0; //(sourceImage.getHeight() - watermarkImage.getHeight()) / 2;

        // paints the image watermark
        g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);
        g2d.dispose();
    }
}
