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

        // This is the core of LSB steganography.
        // We modify the LSB of the carrier's color values.
        // (carrier & 0xFE) sets the LSB to 0.
        // (hidden >> 7) & 1 gets the MSB of the hidden color, which we'll embed.
        int newRed = (carrierRed & 0xFE) | ((hiddenRed >> 7) & 1);
        int newGreen = (carrierGreen & 0xFE) | ((hiddenGreen >> 7) & 1);
        int newBlue = (carrierBlue & 0xFE) | ((hiddenBlue >> 7) & 1);

        // Recombine the modified R, G, B values into a new pixel integer.
        // The << operator shifts the bytes back into their correct positions.
        int newPixel = (0xFF << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
        return newPixel;
    }

    /**
     * Extracts a hidden image from a steganographic image.
     * @param steganographicPath The path to the image containing the hidden data.
     * @param outputPath The path where the extracted image will be saved.
     * @param hiddenWidth The width of the hidden image.
     * @param hiddenHeight The height of the hidden image.
     */
    public void extractImage(String steganographicPath, String outputPath, int hiddenWidth, int hiddenHeight) {
        try {
            // Read the steganographic image.
            BufferedImage steganographicImage = ImageIO.read(new File(steganographicPath));

            // Create a new BufferedImage to hold the extracted hidden image.
            BufferedImage extractedImage = new BufferedImage(hiddenWidth, hiddenHeight, BufferedImage.TYPE_INT_RGB);

            // Iterate through the pixels of the steganographic image.
            for (int y = 0; y < hiddenHeight; y++) {
                for (int x = 0; x < hiddenWidth; x++) {
                    // Get the pixel from the steganographic image.
                    int stegoPixel = steganographicImage.getRGB(x, y);

                    // Extract the LSB of each color channel.
                    // (stegoPixel & 1) isolates the LSB.
                    int redLSB = (stegoPixel >> 16) & 1;
                    int greenLSB = (stegoPixel >> 8) & 1;
                    int blueLSB = stegoPixel & 1;

                    // Reconstruct the hidden pixel's color values.
                    // We shift the LSB back to the most significant bit to make it visible.
                    int newRed = redLSB << 7;
                    int newGreen = greenLSB << 7;
                    int newBlue = blueLSB << 7;

                    // Combine the new color values into a pixel integer.
                    int newPixel = (0xFF << 24) | (newRed << 16) | (newGreen << 8) | newBlue;

                    // Set the pixel in the new extracted image.
                    extractedImage.setRGB(x, y, newPixel);
                }
            }

            // Save the extracted image.
            ImageIO.write(extractedImage, "png", new File(outputPath));
            System.out.println("Image successfully extracted!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}