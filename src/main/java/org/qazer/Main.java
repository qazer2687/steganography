package org.qazer;

public class Main {
    public static void main(String[] args) {

        String carrierImage = "";
        String hiddenImage = "";
        String stegoImage = "stego.png";
        //String stegoImage = String.format("stego_%s.png", FileUtilities.getTimestamp());
        String extractedImage = String.format("extracted_%s.png", FileUtilities.getTimestamp());

        int hiddenWidth = 720;
        int hiddenHeight = 720;

        ImageSteganographer steganographer = new ImageSteganographer();

        switch(args[0]) {
            case "embed":
                carrierImage = args[1];
                hiddenImage = args[2];
                steganographer.hideImage(carrierImage, hiddenImage, stegoImage);
                System.out.println("Embedded " + hiddenImage + " into " + carrierImage + " successfully!");
                break;
            case "extract":
                stegoImage = args[1];
                steganographer.extractImage(stegoImage, extractedImage, hiddenWidth, hiddenHeight);
                System.out.println("Extracted " + extractedImage + " from " + stegoImage + " successfully!");
                break;
            default:
                System.out.println("""
                    steganography - embed or extract an image using steganography
                    
                    Usage:
                      steganography <command> [options]
                    
                    Commands:
                      embed <CARRIER> <HIDDEN>    Embed HIDDEN image into CARRIER image and write a stego image.
                      extract <STEGO>             Extract hidden image from STEGO image and write the result.
                    """);
        }
    }
}