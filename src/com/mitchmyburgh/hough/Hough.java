package com.mitchmyburgh.hough;

import java.lang.ArrayIndexOutOfBoundsException;

public class Hough {

    public Hough (){

    }

    public Integer [][] transform(Integer[][] imageMatrix, int skip_points){
        Integer [][] output = new Integer[imageMatrix.length][imageMatrix[0].length];
        Integer [][] circles = new Integer[imageMatrix.length][imageMatrix[0].length];
        Integer [][] gradientOutput = null;
        for (int y = 0; y < output.length; y++){
            for (int x = 0; x < output[y].length; x++){
                output[y][x] = 0;
            }
        }

        for (int y = 0; y < circles.length; y++){
            for (int x = 0; x < circles[y].length; x++){
                circles[y][x] = 0;
            }
        }


        int circlesDetected = 0;
        int max = -100;
        int prevMax = -100;

        for (int i = 10; i < Math.max(imageMatrix.length, imageMatrix[0].length); i++){
            circlesDetected = 0;
            max = -100;
            for (int y = 0; y < imageMatrix.length; y++){
                for (int x = 0; x < imageMatrix[y].length; x++){
                    if (x%skip_points == 0 && y%skip_points ==0 && imageMatrix[y][x] == -1){
                        output = drawCircle(x,y, i, output);
                    }
                }
            }
            for (int y = 0; y < output.length; y++){
                for (int x = 0; x < output[y].length; x++){
                    max = Math.max(output[y][x], max);
                }
            }
            gradientOutput = sobel(output);
            for (int y = 0; y < gradientOutput.length; y++){
                for (int x = 0; x < gradientOutput[y].length; x++){
                    if (output[y][x] >= Math.max(max, 75)){
                        circles = drawCircle(x, y, i, circles);
                        circlesDetected++;
                    }
                }
            }
            for (int y = 0; y < output.length; y++){
                for (int x = 0; x < output[y].length; x++){
                    output[y][x] = 0;
                }
            }
            System.out.println("Detected "+circlesDetected+" circles for radius "+i+" with max "+max);
        }


		/*Integer [][] gradientOutput = sobel(output);

		Integer [][] circles = new Integer[gradientOutput.length][gradientOutput[0].length];
		for (int y = 0; y < circles.length; y++){
		for (int x = 0; x < circles[y].length; x++){
		circles[y][x] = 0;
	}
}*/

/*for (int y = 0; y < gradientOutput.length; y++){
for (int x = 0; x < gradientOutput[y].length; x++){
if (gradientOutput[y][x] < 0.01 && gradientOutput[y][x] > -0.01 && output[y][x] >= 3000){
circles = drawCircle(x, y, 40, circles);
}
}
}*/

        for (int y = 0; y < circles.length; y++){
            for (int x = 0; x < circles[y].length; x++){
                if (circles[y][x] != 0){
                    circles[y][x] = -1;
                }
            }
        }
        return circles;
    }

    public Integer [][] transform2(Integer[][] imageMatrix, int skip_points){
        Integer [][] output = new Integer[imageMatrix.length][imageMatrix[0].length];
        Integer [][] circles = new Integer[imageMatrix.length][imageMatrix[0].length];
        Integer [][] gradientOutput = null;
        for (int y = 0; y < output.length; y++){
            for (int x = 0; x < output[y].length; x++){
                output[y][x] = 0;
            }
        }

        for (int y = 0; y < circles.length; y++){
            for (int x = 0; x < circles[y].length; x++){
                circles[y][x] = 0;
            }
        }


        int max = -100;

        for (int i = 10; i < Math.max(imageMatrix.length, imageMatrix[0].length); i++){
            for (int y = 0; y < imageMatrix.length; y++){
                for (int x = 0; x < imageMatrix[y].length; x++){
                    if (x%skip_points == 0 && y%skip_points ==0 && imageMatrix[y][x] == -1){
                        output = drawCircle(x,y, i, output);
                    }
                }
            }
        }

        for (int y = 0; y < output.length; y++){
            for (int x = 0; x < output[y].length; x++){
                max = Math.max(output[y][x], max);
            }
        }
        gradientOutput = sobel(output);

        for (int y = 0; y < gradientOutput.length; y++){
            for (int x = 0; x < gradientOutput[y].length; x++){
                if (gradientOutput[y][x] < 0.01 && gradientOutput[y][x] > -0.01 && output[y][x] >= max-100){
                    circles = drawCircle(x, y, 40, circles);
                }
            }
        }

        for (int y = 0; y < circles.length; y++){
            for (int x = 0; x < circles[y].length; x++){
                if (circles[y][x] != 0){
                    circles[y][x] = -1;
                }
            }
        }
        return circles;
    }

    //Sobel filter for differentiation
    public Integer[][] sobel(Integer[][] imageMatrix) {
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

    //https://en.wikipedia.org/wiki/Midpoint_circle_algorithm
    private Integer[][] drawCircle(int x0, int y0, int radius, Integer[][] output){
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