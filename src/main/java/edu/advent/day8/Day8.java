package edu.advent.day8;

import edu.advent.utils.InputLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/*      ---- Part One ----

        The Elves' spirits are lifted when they realize you have an opportunity to reboot one of their Mars rovers,
        and so they are curious if you would spend a brief sojourn on Mars. You land your ship near the rover.

        When you reach the rover, you discover that it's already in the process of rebooting!
        It's just waiting for someone to enter a BIOS password.
        The Elf responsible for the rover takes a picture of the password (your puzzle input) and sends it to you via the Digital Sending Network.

        Unfortunately, images sent via the Digital Sending Network aren't encoded with any normal encoding;
        instead, they're encoded in a special Space Image Format. None of the Elves seem to remember why this is the case.
        They send you the instructions to decode it.

        Images are sent as a series of digits that each represent the color of a single pixel.
        The digits fill each row of the image left-to-right, then move downward to the next row,
        filling rows top-to-bottom until every pixel of the image is filled.

        Each image actually consists of a series of identically-sized layers that are filled in this way.
        So, the first digit corresponds to the top-left pixel of the first layer,
        the second digit corresponds to the pixel to the right of that on the same layer,
        and so on until the last digit, which corresponds to the bottom-right pixel of the last layer.

        For example, given an image 3 pixels wide and 2 pixels tall, the image data 123456789012 corresponds to the following image layers:

        Layer 1: 123
                 456

        Layer 2: 789
                 012
        The image you received is 25 pixels wide and 6 pixels tall.

        To make sure the image wasn't corrupted during transmission,
        the Elves would like you to find the layer that contains the fewest 0 digits.
        On that layer, what is the number of 1 digits multiplied by the number of 2 digits?

        #ANSWER = 1965

        ---- Part Two ----

        Now you're ready to decode the image.
        The image is rendered by stacking the layers and aligning the pixels with the same positions in each layer.
        The digits indicate the color of the corresponding pixel: 0 is black, 1 is white, and 2 is transparent.

        The layers are rendered with the first layer in front and the last layer in back.
        So, if a given position has a transparent pixel in the first and second layers, a black pixel in the third layer,
        and a white pixel in the fourth layer, the final image would have a black pixel at that position.

        For example, given an image 2 pixels wide and 2 pixels tall, the image data 0222112222120000 corresponds to the following image layers:

        Layer 1: 02
                 22

        Layer 2: 11
                 22

        Layer 3: 22
                 12

        Layer 4: 00
                 00
        Then, the full image can be found by determining the top visible pixel in each position:

        The top-left pixel is black because the top layer is 0.
        The top-right pixel is white because the top layer is 2 (transparent), but the second layer is 1.
        The bottom-left pixel is white because the top two layers are 2, but the third layer is 1.
        The bottom-right pixel is black because the only visible pixel in that position is 0 (from layer 4).
        So, the final image looks like this:

        01
        10
        What message is produced after decoding your image?

        #ANSWER = GZKJY
*/
public class Day8 {

    private static final int LAYER_WIDTH = 25;
    private static final int LAYER_HEIGHT = 6;

    public static void main(String[] args) {
        String input = InputLoader.getInputAsStrings("day8").get(0);

        assertThat(calculatePart1(input)).isEqualTo(1965);
        printImage(calculatePart2(input)); //GZKJY
    }

    private static void printImage(long[][] image) {
        for (long[] row : image) {
            for (long pixel : row) {
                if (pixel != 0) {
                    System.out.print(pixel);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    private static long calculatePart1(String input) {
        List<long[][]> layers = fillLayers(input);
        long minZeros = Long.MAX_VALUE;
        long minZerosLayerIdx = 0;
        for (int i = 0; i < layers.size(); i++) {
            long[][] layer = layers.get(i);
            long zeros = calculateNumsOnLayer(layer, 0);
            if (zeros < minZeros) {
                minZeros = zeros;
                minZerosLayerIdx = i;
            }
        }
        return calculateNumsOnLayer(layers.get((int) minZerosLayerIdx), 1) * calculateNumsOnLayer(layers.get((int) minZerosLayerIdx), 2);
    }

    private static long[][] calculatePart2(String input) {
        List<long[][]> layers = fillLayers(input);
        long[][] outputLayer = new long[LAYER_HEIGHT][LAYER_WIDTH];
        for (int i = 0; i < LAYER_HEIGHT; i++) {
            for (int j = 0; j < LAYER_WIDTH; j++) {
                for (long[][] currentLayer : layers) {
                    long layerPixel = currentLayer[i][j];
                    if (layerPixel != 2) {
                        outputLayer[i][j] = layerPixel;
                        break;
                    }
                }
            }
        }
        return outputLayer;
    }

    private static long calculateNumsOnLayer(long[][] layer, long numberToCalc) {
        long numberCounter = 0;
        for (long[] rows : layer) {
            for (long num : rows) {
                if (num == numberToCalc) numberCounter++;
            }
        }
        return numberCounter;
    }

    private static List<long[][]> fillLayers(String input) {
        long[] numbers = Arrays.stream(input.split("")).mapToLong(Long::parseLong).toArray();
        int numbersPointer = 0;
        List<long[][]> layers = new ArrayList<>();
        while (numbersPointer < numbers.length) {
            long[][] layer = new long[LAYER_HEIGHT][LAYER_WIDTH];
            for (int i = 0; i < LAYER_HEIGHT; i++) {
                for (int j = 0; j < LAYER_WIDTH; j++) {
                    layer[i][j] = numbers[numbersPointer];
                    numbersPointer++;
                }
            }
            layers.add(layer);
        }
        return layers;
    }
}
