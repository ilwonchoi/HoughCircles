package com.mitchmyburgh.hough;

import components.ImageFileView;
import components.ImageFilter;
import components.ImagePreview;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

/**
 * Hough Gui Class
 */
public class HoughGui extends JPanel {
    /*
     * UI Elements
     */
    private JButton loadImageButton;
    private JPanel basePanel;
    private JButton baseImageButton;
    private JButton greyscaleButton;
    private JButton edgesButton;
    private JButton circlesButton;
    private JButton circlesOverlayButton;
    private JLabel displayImage;
    private JPanel optionPanel;
    private JPanel edgeDetectionOptions;
    private JRadioButton simpleEdgeDetection;
    private JRadioButton prewittEdgeDetection;
    private JRadioButton sobelEdgeDetection;
    private JRadioButton robertsEdgeDetection;
    private JButton accumulatorButton;
    private JSlider accumulatorRadiusSlider;
    private JTextField accumulatorRadiusTextField;
    private JButton saveImagesButton;
    private static JFrame frame;

    //File chooser
    JFileChooser fc;

    //Is the current button available?
    private Boolean baseImageButtonAvailable = false;
    private Boolean greyscaleButtonAvailable = false;
    private Boolean edgesButtonAvailable = false;
    private Boolean accumulatorButtonAvailable = false;
    private Boolean circlesButtonAvailable = false;
    private Boolean circlesOverlayButtonAvailable = false;
    private Boolean saveImagesButtonAvailable = false;

    //currently disabled button
    private JButton disabledButton;

    //Edge Detection Algorithm
    int edge_detection = EdgeDetector.SOBEL;

    // Images
    private BufferedImage baseImage;
    private BufferedImage greyscaleImage;
    private BufferedImage edgeImage;
    private BufferedImage accumulatorImage;
    private BufferedImage circlesImage;
    private BufferedImage circlesOverlayImage;

    //Matrices - the pixel values of the images are extracted into a matrix for easy processing
    private Integer[][] greyscaleImageMatrix;
    private Integer[][] edgeImageMatrix;
    private Integer[][] accumulatorImageMatrix;
    private Integer[][] circlesImageMatrix;
    private Integer[][] circlesOverlayImageMatrix;

    //The accumulator radius
    int accumlatorRadius;
    /**
     * Default Constructor
     */
    public HoughGui() {
        //set buttons disabledButton
        setButtonStatus();
        //set size of textfield
        accumulatorRadiusTextField.setPreferredSize(new Dimension(50,26));
        /*
         * Load an image
         */
        loadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //from https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
                //Set up the file chooser.
                if (fc == null) {
                    fc = new JFileChooser();

                    //Add a custom file filter and disable the default
                    //(Accept All) file filter.
                    fc.addChoosableFileFilter(new ImageFilter());
                    fc.setAcceptAllFileFilterUsed(false);

                    //Add custom icons for file types.
                    fc.setFileView(new ImageFileView());

                    //Add the preview pane.
                    fc.setAccessory(new ImagePreview(fc));
                }

                //Show it.
                int returnVal = fc.showDialog(HoughGui.this,
                        "Attach");

                //Process the results.
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        baseImage = ImageIO.read(fc.getSelectedFile());
                        displayImage.setIcon(new ImageIcon(baseImage));
                        frame.pack();
                        baseImageButtonAvailable = true;
                        setButtonDisabled(baseImageButton);
                        processImage();
                    } catch (IOException err) {
                        System.out.println("Error reading file");
                    }
                }

                //Reset the file chooser for the next time it's shown.
                fc.setSelectedFile(null);
            }
        });
        /*
         * Display the base image
         */
        baseImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonDisabled(baseImageButton);
                displayImage.setIcon(new ImageIcon(baseImage));
                frame.pack();
            }
        });
        /*
         * Display the Greyscale Image
         */
        greyscaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonDisabled(greyscaleButton);
                displayImage.setIcon(new ImageIcon(greyscaleImage));
                frame.pack();
            }
        });
        /*
         * Display Edge Image
         */
        edgesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonDisabled(edgesButton);
                displayImage.setIcon(new ImageIcon(edgeImage));
                frame.pack();
            }
        });
        /*
         * Display Accumulator Image
         */
        accumulatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonDisabled(accumulatorButton);
                displayImage.setIcon(new ImageIcon(accumulatorImage));
                frame.pack();
            }
        });
        /*
         * Display circles image
         */
        circlesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonDisabled(circlesButton);
                displayImage.setIcon(new ImageIcon(circlesImage));
                frame.pack();
            }
        });
        /*
         * display the circles as an overlay on teh greyscale image
         */
        circlesOverlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonDisabled(circlesOverlayButton);
                displayImage.setIcon(new ImageIcon(circlesOverlayImage));
                frame.pack();
            }
        });
        /*
         * save all images
         */
        saveImagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Select a directory to save into:");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //
                // disable the "All files" option.
                //
                chooser.setAcceptAllFileFilterUsed(false);
                //
                if (chooser.showOpenDialog(HoughGui.this) == JFileChooser.APPROVE_OPTION) {
                    writeImagesToFile(chooser.getSelectedFile().getAbsolutePath());
                }
                else {
                    System.out.println("No Selection ");
                }
            }
        });
        /*
         * Edge detection radio action listeners
         */
        simpleEdgeDetection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                edge_detection = EdgeDetector.SIMPLE;
                processImage();
                if (disabledButton.getText().equals("Edges")) {
                    displayImage.setIcon(new ImageIcon(edgeImage));
                    frame.pack();
                }
            }
        });
        prewittEdgeDetection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                edge_detection = EdgeDetector.PREWITT;
                processImage();
                if (disabledButton.getText().equals("Edges")) {
                    displayImage.setIcon(new ImageIcon(edgeImage));
                    frame.pack();
                }
            }
        });
        sobelEdgeDetection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                edge_detection = EdgeDetector.SOBEL;
                processImage();
                if (disabledButton.getText().equals("Edges")) {
                    displayImage.setIcon(new ImageIcon(edgeImage));
                    frame.pack();
                }
            }
        });
        robertsEdgeDetection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                edge_detection = EdgeDetector.ROBERTS;
                processImage();
                if (disabledButton.getText().equals("Edges")) {
                    displayImage.setIcon(new ImageIcon(edgeImage));
                    frame.pack();
                }
            }
        });
        /*
         * Accumulator Radius slider
         */
        accumulatorRadiusSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                accumulatorRadiusTextField.setText(((JSlider)e.getSource()).getValue()+"");
                accumlatorRadius = ((JSlider)e.getSource()).getValue();
                generateAccumulator();
                if (disabledButton.getText().equals("Accumulator")) {
                    displayImage.setIcon(new ImageIcon(accumulatorImage));
                    frame.pack();
                }
            }
        });
        /*
         * Accumulator text field
         */
        accumulatorRadiusTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (((JTextField)e.getSource()).getText().matches("^-?\\d+$")) { //check if int
                    accumulatorRadiusSlider.setValue(Integer.parseInt(((JTextField)e.getSource()).getText()));
                    accumlatorRadius = Integer.parseInt(((JTextField)e.getSource()).getText());
                    generateAccumulator();
                    if (disabledButton.getText().equals("Accumulator")) {
                        displayImage.setIcon(new ImageIcon(accumulatorImage));
                        frame.pack();
                    }
                }
            }
        });
    }

    /**
     * Main Method to create and display the GUI
     * @param args
     */
    public static void main(String[] args) {
        frame = new JFrame("Hough Circles Detector");
        frame.setContentPane(new HoughGui().basePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void setButtonStatus() {
        baseImageButton.setEnabled(baseImageButtonAvailable);
        greyscaleButton.setEnabled(greyscaleButtonAvailable);
        edgesButton.setEnabled(edgesButtonAvailable);
        accumulatorButton.setEnabled(accumulatorButtonAvailable);
        circlesButton.setEnabled(circlesButtonAvailable);
        circlesOverlayButton.setEnabled(circlesOverlayButtonAvailable);
    }

    /**
     * Set teh clicked button disabledButton and re-enable all other buttons
     * @param button The button to disable
     */
    private void setButtonDisabled (JButton button) {
        setButtonStatus();
        disabledButton = button;
        button.setEnabled(false);
    }

    /**
     * Process image
     */
    private void processImage() {
        createGreyscaleImage();
        greyscaleButtonAvailable = true;
        setButtonStatus();
        setButtonDisabled(disabledButton);
        detectEdges();
        edgesButtonAvailable = true;
        setButtonStatus();
        setButtonDisabled(disabledButton);
        generateAccumulator();
        accumulatorButtonAvailable = true;
        setButtonStatus();
        setButtonDisabled(disabledButton);
        performHoughTransform();
        circlesButtonAvailable = true;
        setButtonStatus();
        setButtonDisabled(disabledButton);
        generateCirclesOverlayImage();
        circlesOverlayButtonAvailable = true;
        saveImagesButtonAvailable = true;
        setButtonStatus();
        setButtonDisabled(disabledButton);
    }

    /**
     * Generate Greyscale Image
     */
    private void createGreyscaleImage () {
        //extract pixel values into matrix and convert to greyscale
        //from: http://stackoverflow.com/questions/9131678/convert-a-rgb-image-to-grayscale-image-reducing-the-memory-in-java
        greyscaleImageMatrix= new Integer[baseImage.getHeight()][baseImage.getWidth()];
        for (int x = 0; x < baseImage.getWidth(); x++){
            for (int y = 0; y < baseImage.getHeight(); y++){
                int rgb = baseImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                greyscaleImageMatrix[y][x] = (r + g + b) / 3;
            }
        }
        //convert the matrix back to an image
        greyscaleImage = new BufferedImage(greyscaleImageMatrix[0].length, greyscaleImageMatrix.length, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < greyscaleImageMatrix.length; y++){
            for (int x = 0; x < greyscaleImageMatrix[y].length; x++){
                greyscaleImage.setRGB(x, y, (greyscaleImageMatrix[y][x] << 16) + (greyscaleImageMatrix[y][x] << 8) + greyscaleImageMatrix[y][x]);
            }
        }
    }

    /**
     * Detect the edges in the greyscale image
     */
    private void detectEdges () {
        edgeImageMatrix = EdgeDetector.detect(greyscaleImageMatrix, edge_detection);
        //convert the matrix back to an image
        edgeImage = new BufferedImage(edgeImageMatrix[0].length, edgeImageMatrix.length, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < edgeImageMatrix.length; y++){
            for (int x = 0; x < edgeImageMatrix[y].length; x++){
                edgeImage.setRGB(x, y, edgeImageMatrix[y][x]);
            }
        }
    }

    /**
     * Generate accumulator image for the current matrix
     */
    private void generateAccumulator () {
        accumulatorImageMatrix = Hough.getAccumulatorAtRadius(edgeImageMatrix, accumlatorRadius);
        //convert the matrix back to an image
        accumulatorImage = new BufferedImage(accumulatorImageMatrix[0].length, accumulatorImageMatrix.length, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < accumulatorImageMatrix.length; y++){
            for (int x = 0; x < accumulatorImageMatrix[y].length; x++){
                accumulatorImage.setRGB(x, y, accumulatorImageMatrix[y][x]);
            }
        }
    }

    /**
     * Perform Hough Transform on the image
     */
    private void performHoughTransform () {
        circlesImageMatrix = Hough.transform(edgeImageMatrix, greyscaleImageMatrix, 1);
        //convert the matrix back into an image
        circlesImage = new BufferedImage(circlesImageMatrix[0].length, circlesImageMatrix.length, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < circlesImageMatrix.length; y++){
            for (int x = 0; x < circlesImageMatrix[y].length; x++){
                circlesImage.setRGB(x, y, circlesImageMatrix[y][x]);
            }
        }
    }

    /**
     * Generate image with the circles overlapping
     */
    private void generateCirclesOverlayImage () {
        circlesOverlayImage = new BufferedImage(circlesImageMatrix[0].length-200, circlesImageMatrix.length-200, BufferedImage.TYPE_INT_RGB);
        for (int y = 100; y < circlesImageMatrix.length-100; y++){
            for (int x = 100; x < circlesImageMatrix[y].length-100; x++){
                circlesOverlayImage.setRGB(x-100, y-100, greyscaleImageMatrix[y-100][x-100]);
                if (circlesImageMatrix[y][x] == -1) {
                    circlesOverlayImage.setRGB(x-100, y-100, Color.RED.getRGB());
                }
            }
        }
    }

    private void writeImagesToFile (String folderName) {
        try{
            ImageIO.write(baseImage, "gif", new File(folderName+"/image_base.gif"));
            System.out.println("Image Saved to "+folderName+"/image_base.gif");
            ImageIO.write(greyscaleImage, "gif", new File(folderName+"/image_greyscale.gif"));
            System.out.println("Image Saved to "+folderName+"/image_greyscale.gif");
            ImageIO.write(edgeImage, "gif", new File(folderName+"/image_edge.gif"));
            System.out.println("Image Saved to "+folderName+"/image_edge.gif");
            ImageIO.write(accumulatorImage, "gif", new File(folderName+"/image_accumulator.gif"));
            System.out.println("Image Saved to "+folderName+"/image_accumulator.gif");
            ImageIO.write(circlesImage, "gif", new File(folderName+"/image_circles.gif"));
            System.out.println("Image Saved to "+folderName+"/image_circles.gif");
            ImageIO.write(circlesOverlayImage, "gif", new File(folderName+"/image_circles_overlay.gif"));
            System.out.println("Image Saved to "+folderName+"/image_circles_overlay.gif");
        } catch (IOException e){
            System.out.println("IO Exception - Exiting");
        }
    }
}
