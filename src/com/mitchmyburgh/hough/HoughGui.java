package com.mitchmyburgh.hough;

import components.ImageFileView;
import components.ImageFilter;
import components.ImagePreview;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * Hough Gui Class
 */
public class HoughGui extends JPanel {
    /**
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
    private JRadioButton cannyEdgeDetection;
    private JButton accumulatorButton;
    private JSlider accumulatorRadiusSlider;
    private JTextField accumulatorRadiusTextField;
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

    //currently disabled button
    private JButton disabledButton;

    //Edge Detection Algorithm
    int edge_detection = EdgeDetector.SOBEL;

    // Images
    BufferedImage baseImage;
    BufferedImage greyscaleImage;
    BufferedImage edgeImage;

    //Matrices - the pixel values of the images are extracted into a matrix for easy processing
    Integer[][] greyscaleImageMatrix;
    Integer[][] edgeImageMatrix;
    /**
     * Default Constructor
     */
    public HoughGui() {
        //set buttons disabledButton
        setButtonStatus();
        /**
         * Load an image
         */
        loadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        /**
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
        /**
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
        /**
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
        /**
         * Display Accumulator Image
         */
        accumulatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        circlesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonDisabled(circlesButton);
            }
        });
        circlesOverlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonDisabled(circlesOverlayButton);
            }
        });
        /**
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
        cannyEdgeDetection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                edge_detection = EdgeDetector.CANNY;
                processImage();
                if (disabledButton.getText().equals("Edges")) {
                    displayImage.setIcon(new ImageIcon(edgeImage));
                    frame.pack();
                }
            }
        });
        /**
         * Accumulator Radius slider
         */
        accumulatorRadiusSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                accumulatorRadiusTextField.setText(((JSlider)e.getSource()).getValue()+"");
            }
        });
        accumulatorRadiusTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (((JTextField)e.getSource()).getText().matches("^-?\\d+$")) {
                    accumulatorRadiusSlider.setValue(Integer.parseInt(((JTextField)e.getSource()).getText()));
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

    public static void create(){

    }
}
