package org.qazer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSteganographer {


    public void hideImage(String carrierPath, String hiddenPath, String outputPath) {
        try {
            // Read both the carrier and hidden images into BufferedImage objects.
            // A BufferedImage is a powerful class for working with image data.
            BufferedImage carrierImage = ImageIO.read(new File(carrierPath));
            BufferedImage hiddenImage = ImageIO.read(new File(hiddenPath));

            // Check if the carrier image is large enough to hold the hidden image.
            // We're embedding 3 bits per pixel (1 for R, 1 for G, 1 for B),
            // which means we need 3 times the number of pixels.
            if (carrierImage.getWidth() * carrierImage.getHeight() * 3 < hiddenImage.getWidth() * hiddenImage.getHeight() * 3) {
                System.out.println("Error: Carrier image is too small to hide the data.");
                return;
            }

            // Iterate over each pixel of the carrier image.
            for (int y = 0; y < hiddenImage.getHeight(); y++) {
                for (int x = 0; x < hiddenImage.getWidth(); x++) {
                    // Get the RGB values of the pixels from both images.
                    // The getRGB() method returns a single integer with packed RGB values.
                    int carrierPixel = carrierImage.getRGB(x, y);
                    int hiddenPixel = hiddenImage.getRGB(x, y);

                    // Extract the R, G, B values from the packed integers.
                    // The bitwise & operator is used to get the specific color byte.
                    // The >> operator shifts bits to the right to move the color byte to the front.
                    int newPixel = getNewPixel(carrierPixel, hiddenPixel);

                    // Set the new pixel value in the carrier image.
                    carrierImage.setRGB(x, y, newPixel);
                }
            }

            // Save the modified carrier image to the specified output path.
            // We use the PNG format because it is lossless and won't compress away the hidden data.
            ImageIO.write(carrierImage, "png", new File(outputPath));
            System.out.println("Image successfully hidden!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getNewPixel(int carrierPixel, int hiddenPixel) {
        int carrierRed = (carrierPixel >> 16) & 0xFF;
        int carrierGreen = (carrierPixel >> 8) & 0xFF;
        int carrierBlue = carrierPixel & 0xFF;

        int hiddenRed = (hiddenPixel >> 16) & 0xFF;
        int hiddenGreen = (hiddenPixel >> 8) & 0xFF;
        int hiddenBlue = hiddenPixel & 0xFF;

        // Embed the 2 most significant bits of hidden image into 2 LSBs of carrier
        int newRed = (carrierRed & 0xFC) | ((hiddenRed >> 6) & 0x03);
        int newGreen = (carrierGreen & 0xFC) | ((hiddenGreen >> 6) & 0x03);
        int newBlue = (carrierBlue & 0xFC) | ((hiddenBlue >> 6) & 0x03);

        return (0xFF << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
    }

    public void extractImage(String steganographicPath, String outputPath, int hiddenWidth, int hiddenHeight) {
        try {
            BufferedImage steganographicImage = ImageIO.read(new File(steganographicPath));
            BufferedImage extractedImage = new BufferedImage(hiddenWidth, hiddenHeight, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < hiddenHeight; y++) {
                for (int x = 0; x < hiddenWidth; x++) {
                    int stegoPixel = steganographicImage.getRGB(x, y);

                    // Extract 2 LSBs from each channel
                    int redLSB = (stegoPixel >> 16) & 0x03;
                    int greenLSB = (stegoPixel >> 8) & 0x03;
                    int blueLSB = stegoPixel & 0x03;

                    // Scale 0–3 to 0–255 for visible colors
                    int newRed = redLSB * 85;   // 0, 85, 170, 255
                    int newGreen = greenLSB * 85;
                    int newBlue = blueLSB * 85;

                    int newPixel = (0xFF << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                    extractedImage.setRGB(x, y, newPixel);
                }
            }

            ImageIO.write(extractedImage, "png", new File(outputPath));
            System.out.println("Image successfully extracted!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}