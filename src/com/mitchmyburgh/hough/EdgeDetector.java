package com.mitchmyburgh.hough;

/**
 * Created by mitch on 2016/08/16.
 */
public class EdgeDetector {
    //Edge Detector Types
    public static final int SIMPLE = 0;
    public static final int PREWITT = 1;
    public static final int SOBEL = 2;
    public static final int ROBERTS = 3;
    public static final int CANNY = 4;


    /**
     * Detect edges in image using edgeDetector type
     * @param imageGreyscale the greyscale image as a matrix
     * @param edgeDetectorType type of edge detector to use
     * @return
     */
    public static Integer[][] detect(Integer[][] imageGreyscale, int edgeDetectorType) {
        switch (edgeDetectorType) {
            case SIMPLE:
                return detectSimple(imageGreyscale);
            case PREWITT:
                return detectPrewitt(imageGreyscale);
            case SOBEL:
                return detectSobel(imageGreyscale);
            case ROBERTS:
                return detectRoberts(imageGreyscale);
            case CANNY:
                return detectCanny(imageGreyscale);
            default:
                System.out.println("Edge Detector no Detected");
                break;
        }
        return null;
    }

    /**
     * Simple edge detection
     * @param imageMatrix the image matrix
     * @return
     */
    public static Integer[][] detectSimple(Integer[][] imageMatrix) {
        int threshold = 20;
        Integer[][] imageEdge = new Integer[imageMatrix.length][imageMatrix[0].length];
        for (int y = 0; y < imageMatrix.length; y++){
            for (int x = 0; x < imageMatrix[y].length; x++){
                if (x == 0){
                    imageEdge[y][x] = 0; //using extending the currect pixels value at the boundaries
                } else if (x == imageMatrix[y].length-1){
                    imageEdge[y][x] = 0; //using extending the currect pixels value at the boundaries
                }else {
                    if (y!=0 && y!= imageMatrix.length-1){
                        imageEdge[y][x] = -1*imageMatrix[y][x-1]+4*imageMatrix[y][x]-1*imageMatrix[y][x+1]-1*imageMatrix[y-1][x]-1*imageMatrix[y+1][x];
                    } else {
                        imageEdge[y][x] = -1*imageMatrix[y][x-1]+2*imageMatrix[y][x]-1*imageMatrix[y][x+1];
                    }
                    if (imageEdge[y][x] < threshold && imageEdge[y][x] > -1*threshold){ //set the threshold
                        imageEdge[y][x] = 0;
                    } else if (imageEdge[y][x] > 1) {
                        imageEdge[y][x]*= -1;
                    }
                    //make every on pixel -1
                    if (imageEdge[y][x] != 0){
                        imageEdge[y][x] = -1;
                    }
                }
            }
        }
        return imageEdge;
    }

    /**
     * Prewitt edge detection
     * @param imageMatrix the image matrix
     * @return
     */
    public static Integer[][] detectPrewitt(Integer[][] imageMatrix) {
        Integer[][] output = new Integer[imageMatrix.length][imageMatrix[0].length];
        for (int y = 0; y < imageMatrix.length; y++){
            for (int x = 0; x < imageMatrix[y].length; x++){
                int Gx = 0;
                int Gy = 0;
                //left
                if (x != 0){
                    Gx += -1*imageMatrix[y][x-1];
                }
                //right
                if (x != imageMatrix[y].length-1){
                    Gx += 1*imageMatrix[y][x+1];
                }
                //top
                if (y != 0){
                    Gy += -1*imageMatrix[y-1][x];
                }
                //bottom
                if (y != imageMatrix.length-1){
                    Gy += 1*imageMatrix[y+1][x];
                }
                //top left
                if (x != 0 && y != 0){
                    Gx += -1*imageMatrix[y-1][x-1];
                    Gy += -1*imageMatrix[y-1][x-1];
                }
                //top right
                if (x != imageMatrix[y].length-1 && y != 0){
                    Gx += 1*imageMatrix[y-1][x+1];
                    Gy += -1*imageMatrix[y-1][x+1];
                }
                //bottom left
                if (x != 0 && y != imageMatrix.length-1){
                    Gx += -1*imageMatrix[y+1][x-1];
                    Gy += 1*imageMatrix[y+1][x-1];
                }
                //bottom right
                if (x != imageMatrix[y].length-1 && y != imageMatrix.length-1){
                    Gx += 1*imageMatrix[y+1][x+1];
                    Gy += 1*imageMatrix[y+1][x+1];
                }
                //Average the G values along x and y
                output[y][x] = (int)(Math.sqrt(Gx*Gx+Gy*Gy) + 0.5d);
                if (output[y][x] < 200 && output[y][x] > -200) { //threshholding makes edges a bit more jagged but we get a nice binary image
                    output[y][x] = 0;
                } else {
                    output[y][x] = -1;
                }
            }
        }
        return output;
    }

    /**
     * Sobel edge detection
     * @param imageMatrix the image matrix
     * @return
     */
    public static Integer[][] detectSobel(Integer[][] imageMatrix) {
        Integer[][] output = new Integer[imageMatrix.length][imageMatrix[0].length];
        for (int y = 0; y < imageMatrix.length; y++){
            for (int x = 0; x < imageMatrix[y].length; x++){
                int Gx = 0;
                int Gy = 0;
                //left
                if (x != 0){
                    Gx += -2*imageMatrix[y][x-1];
                }
                //right
                if (x != imageMatrix[y].length-1){
                    Gx += 2*imageMatrix[y][x+1];
                }
                //top
                if (y != 0){
                    Gy += -2*imageMatrix[y-1][x];
                }
                //bottom
                if (y != imageMatrix.length-1){
                    Gy += 2*imageMatrix[y+1][x];
                }
                //top left
                if (x != 0 && y != 0){
                    Gx += -1*imageMatrix[y-1][x-1];
                    Gy += -1*imageMatrix[y-1][x-1];
                }
                //top right
                if (x != imageMatrix[y].length-1 && y != 0){
                    Gx += 1*imageMatrix[y-1][x+1];
                    Gy += -1*imageMatrix[y-1][x+1];
                }
                //bottom left
                if (x != 0 && y != imageMatrix.length-1){
                    Gx += -1*imageMatrix[y+1][x-1];
                    Gy += 1*imageMatrix[y+1][x-1];
                }
                //bottom right
                if (x != imageMatrix[y].length-1 && y != imageMatrix.length-1){
                    Gx += 1*imageMatrix[y+1][x+1];
                    Gy += 1*imageMatrix[y+1][x+1];
                }
                //Average the G values along x and y
                output[y][x] = (int)(Math.sqrt(Gx*Gx+Gy*Gy) + 0.5d);
                if (output[y][x] < 200 && output[y][x] > -200) { //threshholding makes edges a bit more jagged but we get a nice binary image
                    output[y][x] = 0;
                } else {
                    output[y][x] = -1;
                }
            }
        }
        return output;
    }

    /**
     * Roberts edge detection
     * @param imageMatrix the image matrix
     * @return
     */
    public static Integer[][] detectRoberts(Integer[][] imageMatrix) {
        Integer[][] output = new Integer[imageMatrix.length][imageMatrix[0].length];
        for (int y = 0; y < imageMatrix.length; y++){
            for (int x = 0; x < imageMatrix[y].length; x++){
                int Gx = 0;
                int Gy = 0;
                //center
                Gx = 1*imageMatrix[y][x];
                //right
                if (x != imageMatrix[y].length-1){
                    Gy += 1*imageMatrix[y][x+1];
                }
                //bottom
                if (y != imageMatrix.length-1){
                    Gy += -1*imageMatrix[y+1][x];
                }
                //bottom right
                if (x != imageMatrix[y].length-1 && y != imageMatrix.length-1){
                    Gx += -1*imageMatrix[y+1][x+1];
                }
                //Average the G values along x and y
                output[y][x] = (int)(Math.sqrt(Gx*Gx+Gy*Gy) + 0.5d);
                if (output[y][x] < 20 && output[y][x] > -20) { //threshholding makes edges a bit more jagged but we get a nice binary image
                    output[y][x] = 0;
                } else {
                    output[y][x] = -1;
                }
            }
        }
        return output;
    }

    /**
     * Canny edge detection
     * @param imageMatrix the image matrix
     * @return
     */
    public static Integer[][] detectCanny(Integer[][] imageMatrix) {
        Integer[][] output = new Integer[imageMatrix.length][imageMatrix[0].length];
        return output;
    }

    /**
     * Perform erosion on image
     * @param imageMatrix the image matrix
     * @return
     */
    public static Integer[][] erosion(Integer[][] imageMatrix){
        Integer[][] output = new Integer[imageMatrix.length][imageMatrix[0].length];
        for (int y = 0; y < imageMatrix.length; y++){
            for (int x = 0; x < imageMatrix[y].length; x++){
                Boolean on = true;
                //center
                if (imageMatrix[y][x] != -1){
                    on = false;
                }
                //left
                if (x != 0 && on){
                    if (imageMatrix[y][x-1] != -1){
                        on = false;
                    }
                }
                //right
                if (x != imageMatrix[y].length-1 && on){
                    if (imageMatrix[y][x+1] != -1){
                        on = false;
                    }
                }
                //top
                if (y != 0 && on){
                    if (imageMatrix[y-1][x] != -1){
                        on = false;
                    }
                }
                //bottom
                if (y != imageMatrix.length-1 && on){
                    if (imageMatrix[y+1][x] != -1){
                        on = false;
                    }
                }
                //top left
                if (x != 0 && y != 0 && on){
                    if (imageMatrix[y-1][x-1] != -1){
                        on = false;
                    }
                }
                //top right
                if (x != imageMatrix[y].length-1 && y != 0 && on){
                    if (imageMatrix[y-1][x+1] != -1){
                        on = false;
                    }
                }
                //bottom left
                if (x != 0 && y != imageMatrix.length-1 && on){
                    if (imageMatrix[y+1][x-1] != -1){
                        on = false;
                    }
                }
                //bottom right
                if (x != imageMatrix[y].length-1 && y != imageMatrix.length-1 && on){
                    if (imageMatrix[y+1][x+1] != -1){
                        on = false;
                    }
                }
                if (on){
                    output[y][x] = -1;
                } else {
                    output[y][x] = 0;
                }
            }
        }
        return output;
    }

    /**
     * Perform dilation on the image
     * @param imageMatrix the image matrix
     * @return
     */
    public static Integer[][] dilation(Integer[][] imageMatrix){
        Integer[][] output = new Integer[imageMatrix.length][imageMatrix[0].length];
        for (int y = 0; y < imageMatrix.length; y++){
            for (int x = 0; x < imageMatrix[y].length; x++){
                Boolean on = false;
                //center
                if (imageMatrix[y][x] == -1){
                    output[y][x] = -1;
                    on = true;
                } else {
                    output[y][x] = 0;
                }
                //left
                if (x != 0 && on){
                    output[y][x-1] = -1;
                }
                //right
                if (x != imageMatrix[y].length-1 && on){
                    output[y][x+1] = -1;
                }
                //top
                if (y != 0 && on){
                    output[y-1][x] = -1;
                }
                //bottom
                if (y != imageMatrix.length-1 && on){
                    output[y+1][x] = -1;
                }
                //top left
                if (x != 0 && y != 0 && on){
                    output[y-1][x-1] = -1;
                }
                //top right
                if (x != imageMatrix[y].length-1 && y != 0 && on){
                    output[y-1][x+1] = -1;
                }
                //bottom left
                if (x != 0 && y != imageMatrix.length-1 && on){
                    output[y+1][x-1] = -1;
                }
                //bottom right
                if (x != imageMatrix[y].length-1 && y != imageMatrix.length-1 && on){
                    output[y+1][x+1] = -1;
                }
            }
        }
        return output;
    }

}
