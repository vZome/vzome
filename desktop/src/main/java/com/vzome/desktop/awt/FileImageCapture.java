package com.vzome.desktop.awt;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

final class FileImageCapture
{
    public static void captureImage( final BufferedImage image, final Object dest, final String extension ) throws IOException
    {
        String format = extension.toUpperCase();
        if ( format.equals( "JPG" ) )
            format = "JPEG";
        ImageWriter writer = ImageIO.getImageWritersByFormatName( format ) .next();
        ImageWriteParam iwParam = writer .getDefaultWriteParam();

        if (iwParam.canWriteCompressed()) {
            // Ensure that the compressionType we want to use is supported
            String[] preferredTypes = null; // Listed in preferred order
            switch (format) {
                case "BMP":
                    preferredTypes = new String[]{
                        "BI_RGB",       // BI_RGB seems to result in a smaller file than BI_BITFIELDS in Windows 10
                        "BI_BITFIELDS", // OK
                        // "BI_PNG",    // File is created OK, but can't be opened by default viewer "Photos" in Windows 10
                        // "BI_JPEG",   // File is created OK, but can't be opened by default viewer "Photos" in Windows 10
                        // "BI_RLE8",   // IOException: Image can not be encoded with compression type BI_RLE8
                        // "BI_RLE4",   // IOException: Image can not be encoded with compression type BI_RLE4
                    };
                    break;

                case "GIF":
                    preferredTypes = new String[]{
                        "lzw",
                        "LZW",
                    };
                    break;

                case "JPEG":
                    preferredTypes = new String[]{
                        "JPEG",
                    };
                    break;
            }
            if (preferredTypes != null) {
                String[] compressionTypes = iwParam.getCompressionTypes();
                String chosenType = null;
                for (String preferredType : preferredTypes) {
                    for (String compressionType : compressionTypes) {
                        if (compressionType.equals(preferredType)) {
                            chosenType = preferredType;
                            break;
                        }
                    }
                    if (chosenType != null) {
                        break;
                    }
                }
                if (chosenType != null) {
                    System.out.println(format + " compression set to " + chosenType);
                    iwParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    iwParam.setCompressionType(chosenType); // this default is better for BMP, to avoid non-compression
                    iwParam.setCompressionQuality(.95f);
                }
            }
        }
    
        // A try-with-resources block closes the resource even if an exception occurs
        try (ImageOutputStream ios = ImageIO.createImageOutputStream( dest )) {
            writer .setOutput( ios );
            writer .write( null, new IIOImage( image, null, null), iwParam );
            writer .dispose(); // disposing of the writer doesn't close ios
            // ios is closed automatically by exiting the try-with-resources block
            // either normally or due to an exception
            // If this code is ever changed to not use the try-with-resources block
            // then uncomment the following line so that ios will be explicitly closed
            // ios.close();
        }
    }
}
