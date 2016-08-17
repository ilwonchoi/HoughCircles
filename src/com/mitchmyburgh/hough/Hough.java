package com.mitchmyburgh.hough;

import java.lang.ArrayIndexOutOfBoundsException;

public class Hough {

    /**
     * Get the accumulator for the specified circle radius
     * @param imageMatrix the image matrix to be processed
     * @param radius the radius of teh circles to find
     * @return
     */
    public static Integer[][] getAccumulatorAtRadius(Integer[][] imageMatrix, int radius){
        Integer [][] output = new Integer[imageMatrix.length+200][imageMatrix[0].length+200]; //increase the size of the image for occluded circles
        //zero out the output
        for (int y = 0; y < output.length; y++){
            for (int x = 0; x < output[y].length; x++){
                output[y][x] = 0;
            }
        }
        //draw circles of radius, radius, around each edge point
        for (int y = 0; y < imageMatrix.length; y++){
            for (int x = 0; x < imageMatrix[y].length; x++){
                if (imageMatrix[y][x] == -1){
                    output = drawCircle(x+100,y+100, radius, output);
                }
            }
        }
        return output;
    }

    /**
     * Transform edges into hough space and detect circles
     * @param imageMatrix the edge image matrix
     * @param skip_points skip skip_points-1 points for speed
     * @return Integer [][] the detected circles
     */
    public static Integer [][] transform(Integer[][] imageMatrix, Integer[][] greyscale, int skip_points){
        Integer [][] output = new Integer[imageMatrix.length+200][imageMatrix[0].length+200]; //increase the size of the image for occluded circles
        Integer [][] circles = new Integer[imageMatrix.length+200][imageMatrix[0].length+200]; //increase the size of the image for occluded circles

        //zero output
        for (int y = 0; y < output.length; y++){
            for (int x = 0; x < output[y].length; x++){
                output[y][x] = 0;
            }
        }
        //zero circles
        for (int y = 0; y < circles.length; y++){
            for (int x = 0; x < circles[y].length; x++){
                circles[y][x] = 0;
            }
        }


        int circlesDetected; //number of circles detected per radius
        int max; //max in the current frame
        int overallMax = -100; //max over all seen frames
        //loop over radius
        for (int i = 12; i < 100; i++){//Math.max(imageMatrix.length, imageMatrix[0].length)
            circlesDetected = 0;
            max = -100;
            //loop overt pixels in image
            for (int y = 0; y < imageMatrix.length; y++){
                for (int x = 0; x < imageMatrix[y].length; x++){
                    if (x%skip_points == 0 && y%skip_points ==0 && imageMatrix[y][x] == -1){
                        output = drawCircle(x+100,y+100, i, output);
                    }
                }
            }
            //find max
            for (int y = 0; y < output.length; y++){
                for (int x = 0; x < output[y].length; x++){
                    max = Math.max(output[y][x], max);

                }
            }
            //set overall max
            overallMax = Math.max(max, overallMax);
            //draw the detected circles
            for (int y = 21; y < output.length-21; y++){
                for (int x = 21; x < output[y].length-21; x++){
                    //check if point represents circle
                    if (output[y][x] >= Math.max(max, overallMax)){
                        Boolean draw = true;
                        for (int j = 1; j < 20; j++){ //check for nearby maxes
                            try {
                                if (output[y + j][x] >= output[y][x]-4 ||
                                        output[y][x + j] >= output[y][x]-4 ||
                                        output[y - j][x] >= output[y][x]-4 ||
                                        output[y][x - j] >= output[y][x]-4 ||
                                        output[y - j][x - j] >= output[y][x]-4 ||
                                        output[y + j][x - j] >= output[y][x]-4 ||
                                        output[y - j][x + j] >= output[y][x]-4 ||
                                        output[y + j][x + j] >= output[y][x]-4) {
                                    draw = false;
                                    break;
                                }
                            } catch (ArrayIndexOutOfBoundsException e){

                            }
                        }
                        if (draw) { //draw the circle into the accumulator
                            circles = drawCircle(x, y, i, circles);
                            circlesDetected++;
                        }
                    }


                }
            }
            /*
             * detect occluded circles
             */
            if (circlesDetected != 0) { //check that a circle has already been found with this radius - bit of a hack but prevents most false positives
                for (int y = 21; y < output.length-21; y++){
                    for (int x = 21; x < output[y].length-21; x++){
                        //if point is out of normal bounds then it could be an occluded circle
                        if ((x < 100+i || y < 100+i || y > output.length-100-i || x > output[y].length-100-i) ) {
                            Boolean draw = true;
                            //find local maxes
                            int localMax = -100;
                            for (int j = 0; j < i; j++){
                                for (int k = 0; k < i; k++) {
                                    try {
                                        localMax = Math.max(localMax, output[y + (j - 10)][x + (k - 10)]);

                                    } catch (ArrayIndexOutOfBoundsException e) {
                                    }
                                }
                            }
                            //check if the center of the circle is black, then dont draw it
                            try {
                                if (!(x < 100 || y < 100 || y > output.length-100 || x > output[y].length-100) && (greyscale[y-100][x-100] <= 1 && greyscale[y-100][x-100] >= -1)){
                                    draw = false;
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                            }
                            if (output[y][x] >= localMax){
                                for (int j = 1; j < 20; j++){ //check for nearby maxes
                                    try {
                                        if (output[y + j][x] >= output[y][x]-4 ||
                                                output[y][x + j] >= output[y][x]-4 ||
                                                output[y - j][x] >= output[y][x]-4 ||
                                                output[y][x - j] >= output[y][x]-4 ||
                                                output[y - j][x - j] >= output[y][x]-4 ||
                                                output[y + j][x - j] >= output[y][x]-4 ||
                                                output[y - j][x + j] >= output[y][x]-4 ||
                                                output[y + j][x + j] >= output[y][x]-4) {
                                            draw = false;
                                            break;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e){

                                    }
                                }
                                if (draw) { // draw circle into accumulator
                                    circles = drawCircle(x, y, i, circles);
                                    circlesDetected++;
                                }
                            }
                        }
                    }
                }
            }
            //zero output
            for (int y = 0; y < output.length; y++){
                for (int x = 0; x < output[y].length; x++){
                    output[y][x] = 0;
                }
            }
            System.out.println("Detected "+circlesDetected+" circles for radius "+i+" with max "+max);
        }

        //ouput array of circles
        for (int y = 0; y < circles.length; y++){
            for (int x = 0; x < circles[y].length; x++){
                if (circles[y][x] != 0){
                    circles[y][x] = -1;
                }
            }
        }
        return circles;
    }


    /**
     * Draw circle from https://en.wikipedia.org/wiki/Midpoint_circle_algorithm
     * @param x0 x center
     * @param y0 y center
     * @param radius radius
     * @param output matrix to write circle to
     * @return
     */
    private static Integer[][] drawCircle(int x0, int y0, int radius, Integer[][] output){
        int x = radius;
        int y = 0;
        int err = 0;

        while (x >= y) {
            try {
                output[y0 + y][x0 + x] += 1;
                output[y0 + x][x0 + y] += 1;
                output[y0 + x][x0 - y] += 1;
                output[y0 + y][x0 - x] += 1;
                output[y0 - y][x0 - x] += 1;
                output[y0 - x][x0 - y] += 1;
                output[y0 - x][x0 + y] += 1;
                output[y0 - y][x0 + x] += 1;
            } catch (ArrayIndexOutOfBoundsException e){

            }

            y += 1;
            err += 1 + 2*y;
            if (2*(err-x) + 1 > 0)
            {
                x -= 1;
                err += 1 - 2*x;
            }
        }
        return output;
    }
}