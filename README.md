# HoughCircles

Hough Circle Detection by Mitch Myburgh (MYBMIT001)

Running the software:
```
java -jar HoughCircles.jar
```

The code is available in:
```
./src
```

Example output is located in:
```
./example_output
```

The test images are located in:
```
./test_images
```

The Software works as follows:

An image is loaded in the gui and processed, the image can be viewed by clicking the button "Base Image". The image if first converted to greyscale and can be viewed by clicking the button "Greyscale". This greyscale image is processed by one of the included edge detection algorithms (Simple - an attempt at using a [1, -1] filter that evolved into a more complex filter, Prewitt, Sobel and Roberts). Sobel is the default edge detection scheme and the recommended one. The edges are then passed into a Hough Transform method that detects the circles. The accumulators at various radii can be seen by clicking the "Accumulator" button and sliding the "Accumulator Radius" Slider. The detected circles are dsiplayed under the "Circles" Button and are overlayed on the image under the "Circles Overlay" button. Finally the user can save all the images under all the buttons using the "Save Images" Button.

The hough transform loops over the possible radii and for each edgepoint draws a circle it into a accumulator (the accumulator is zeroed and then rewritten to for the next radius). The accumulator is made 200 pixels larger than the original image to help account for occluded circles. The accumulator is then looped over and the local maxima found. It was difficult to find an appropriate threshold to determine the local maxima, but in the end, points greater than or equal to than the maximum of the maximum value in the current frame and the highest value seen currently were considered viable points. In order to weed out non-circle shapes the are around the maximum was checked for points close in value, which if found would mean the current point was discarded.

For occluded circles the matrix was looped over again, iff the current raduis had discovered circles already, which although it prevents finding occluded circles of new radii, it removed many circles that were incorectly classified, in addition to speeding up the code. In this case a local maxima was determined and values greater than or equal to this local maxima were allowed through.

Circles were then plotted arround the selected points and the final output was produced.


Throughout the process the images are stored in a matrix containing colour values at the points.

Note that the code takes a few seconds to run.
