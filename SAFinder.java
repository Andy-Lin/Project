package sa.finder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SAFinder {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SAFinder window = new SAFinder();
					window.frmSafinder.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SAFinder() {
		initialize();
	}

	private JFrame frmSafinder;

	// make variables to store the before images
	private BufferedImage img;
	private BufferedImage previousImg;
	private BufferedImage undoImg;
	
	private JSlider slider;
	private JLabel label;
	private JLabel orginLabel;
	private JTextField textField;
	private JCheckBox cbRed;
	private JCheckBox cbBlue;
	private JCheckBox cbGreen;

	private int[] colors = { 0, 0, 0 };
	private int erodeCount = 0;
	private int dilateCount = 0;
	private int xCoor = 0;
	private int yCoor = 0;
	private boolean calBtnPress = false;
	private boolean mouseOn = false;
	private double sFactor = 1;
	private double ppu = 1;

	private void initialize() {

		// Get the dimensions of the screen that this application is running
		// and set the width to half of that screen and height to a 3/4th of the
		// screen
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		final int width = (screenSize.width) / 2;
		final int height = (screenSize.height) / 2 + (screenSize.height) / 4;

		/////////////// Make the frame and set its dimension
		/////////////// /////////////////////
		frmSafinder = new JFrame();
		frmSafinder.setTitle("SAFinder");
		frmSafinder.setBounds(100, 100, width, height);
		frmSafinder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSafinder.setResizable(false);

		/////////////// create new components //////////////////////////////////
		JToolBar toolBar = new JToolBar();
		Component horizontalStrut = Box.createHorizontalStrut(80);
		JButton btnOpenFile = new JButton("Open File");
		JButton btnThreshold = new JButton("Threshold Img");
		JButton btnDilate = new JButton("Dilate");
		JButton btnErode = new JButton("Erode");
		JButton btnCalculate = new JButton("Calculate Surface Area");
		JButton btnCalibrate = new JButton("Calibrate");
		JButton btnCommit = new JButton("Commit");
		JButton btnRevert = new JButton("Revert");
		
		
		label = new JLabel("");
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		orginLabel = new JLabel("");
		orginLabel.setVerticalAlignment(SwingConstants.TOP);
		orginLabel.setHorizontalAlignment(SwingConstants.LEFT);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setColumns(10);

		JToolBar sliderToolBar = new JToolBar();
		cbRed = new JCheckBox("Red");
		cbRed.setEnabled(false);
		cbBlue = new JCheckBox("Blue");
		cbBlue.setEnabled(false);
		cbGreen = new JCheckBox("Green");
		cbGreen.setEnabled(false);
		slider = new JSlider();
		slider.setEnabled(false);
		slider.setSnapToTicks(true);
		slider.setMaximum(255);
		slider.setVisible(true);

		///////////// add the components to the tool bars ////////////////
		toolBar.add(horizontalStrut);
		toolBar.add(btnOpenFile);
		toolBar.add(btnThreshold);
		toolBar.add(btnDilate);
		toolBar.add(btnErode);
		toolBar.add(btnCalibrate);
		toolBar.add(btnCalculate);
		toolBar.add(btnCommit);
		toolBar.add(btnRevert);
		
		sliderToolBar.add(cbRed);
		sliderToolBar.add(cbGreen);
		sliderToolBar.add(cbBlue);
		sliderToolBar.add(slider);

		//////////////// Add action to the buttons and slider //////////////
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});

		btnThreshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				threshold();
			}
		});

		btnDilate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dilating();
			}
		});

		btnErode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eroding();
			}
		});

		btnCalibrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ppu = 1;
				textField.setText("Enter pixels per unit length in the original image");
				textField.setEditable(true);
			}
		});
		
		
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calculate();
			}
		});
		
		btnCommit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(img != null){
					OpenEdit oe = new OpenEdit();
					undoImg = oe.clone(img);
					textField.setText("Committed");
					reset();
				} else {
					textField.setText("Nothing to commit");
				}
				
			}
		});
		
		btnRevert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(undoImg != null){
					OpenEdit oe = new OpenEdit();
					img = oe.clone(undoImg);
					ImageIcon icon = new ImageIcon(img);
					label.setIcon(icon);
					textField.setText("Reverted");
					reset();
				} else {
					textField.setText("Nothing to revert");
				}
			}
		});
		
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sliderChange();
			}
		});

		cbRed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbRed();
			}
		});

		cbGreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbGreen();
			}
		});

		cbBlue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbBlue();
			}
		});

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calibrate(e);
			}
		});

		label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				clicked(e);
			}
		});

		/////////////////// set the layout of the frame
		/////////////////// /////////////////////////

		GroupLayout groupLayout = new GroupLayout(frmSafinder.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(toolBar,
										GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup().addGap(196)
								.addComponent(textField, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE).addGap(192))
						.addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(sliderToolBar,
								GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup().addContainerGap()
								.addComponent(orginLabel, GroupLayout.PREFERRED_SIZE, 343, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
								.addComponent(label, GroupLayout.PREFERRED_SIZE, 343, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(sliderToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(label, GroupLayout.PREFERRED_SIZE, 546, GroupLayout.PREFERRED_SIZE)
						.addComponent(orginLabel, GroupLayout.PREFERRED_SIZE, 546, GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		
		
		
		

		frmSafinder.getContentPane().setLayout(groupLayout);
	}

	/////////////////// methods that are used in the action section
	/////////////////// ///////////////////////////
	private void openFile() {
		OpenEdit oe = new OpenEdit();
		img = oe.GetImage(undoImg);
		if (img != null) {
			reset();
			
			// clear the label of text and set the slider to not work
			// until threshold button is pressed
			orginLabel.setText(null);
			
			// get the scaleing factor
			sFactor = getSF(img,label);
			System.out.println(sFactor);
			

			// resize the image and set both the labels to the image
			img = oe.ResizeImage(img, orginLabel);
			ImageIcon icon = new ImageIcon(img);
			label.setIcon(icon);

			int h = img.getHeight();
			int w = img.getWidth();
			
			textField.setText(Integer.toString(h) + "x" + Integer.toString(w));

			// keep track of the previous image for editing and undoing
			previousImg = oe.clone(img);
			undoImg = oe.clone(img);
		} else {
			textField.setText("No image selected");
		}
	}

	// method to set the slider and check box to work if the threshold
	// button is pressed. Checks to make sure img is not null
	private void threshold() {
		if (img != null) {
			cbRed.setEnabled(true);
			cbBlue.setEnabled(true);
			cbGreen.setEnabled(true);
			mouseOn = false;
			textField.setText("Please check a color channel for thresholding");
			erodeCount = 0;
			dilateCount = 0;
		} else {
			textField.setText("Need to open a file before thresholding");
		}
	}

	// method to set the colors arr 0 (red) to 1 if check 0 if not.
	private void cbRed() {
		if (cbRed.isSelected()) {
			slider.setEnabled(true);
			textField.setText("");
			colors[0] = 1;
		} else {
			colors[0] = 0;
		}
	}

	// method to set the colors arr 1 (green) to 1 if check 0 if not.
	private void cbGreen() {
		if (cbGreen.isSelected()) {
			slider.setEnabled(true);
			textField.setText("");
			colors[1] = 1;
		} else {
			colors[1] = 0;
		}
	}

	// method to set the colors arr 2 (blue) to 1 if check 0 if not.
	private void cbBlue() {
		if (cbBlue.isSelected()) {
			slider.setEnabled(true);
			textField.setText("");
			colors[2] = 1;
		} else {
			colors[2] = 0;
		}
	}

	// method to threshold the value based on the slider value
	private void sliderChange() {
		Manipulate manip = new Manipulate();
		OpenEdit oe = new OpenEdit();

		// Reset the image to the original image so that thresholding can be
		// done using the
		// original and not altered
		img = oe.clone(previousImg);

		// get the value of the slider
		int thresLevel = slider.getValue();

		if (colors[0] == 0 && colors[1] == 0 && colors[2] == 0) {
			slider.setEnabled(false);
			textField.setText("Please select a channel for thresholding");
		} else {
			label.setText(null);
			// threshold the image and pit it into the label
			manip.thresholdImage(img, thresLevel, colors);
			ImageIcon icon = new ImageIcon(img);
			label.setIcon(icon);

			// output threshold level
			String outValue = Integer.toString(thresLevel);
			textField.setText(outValue);
		}
	}

	// method to for dilating of the image
	private void dilating() {

		Manipulate manip = new Manipulate();
		// checks to make sure the image is not null
		if (img != null) {
			// uncheck all the buttons
			uncheck();
			slider.setEnabled(false);
			mouseOn = false;
			// dilate the image and set it to the icon
			img = manip.dilate(img);
			ImageIcon icon = new ImageIcon(img);
			label.setIcon(icon);

			// a count to show how many times erosion is needed after dilating
			if (erodeCount != 0) {
				erodeCount--;
			}
			// increment the dilation count
			dilateCount++;
			StringBuilder sb = new StringBuilder();

			// output to the text field
			sb.append("erode count: ");
			sb.append(erodeCount);
			sb.append("; dilate count: ");
			sb.append(dilateCount);
			textField.setText(sb.toString());
		} else {
			textField.setText("Need a image before dilating");
		}
	}

	// method for eroding of the image, comments same as dilating
	private void eroding() {
		Manipulate manip = new Manipulate();

		if (img != null) {
			uncheck();
			slider.setEnabled(false);
			mouseOn = false;
			img = manip.erode(img);
			ImageIcon icon = new ImageIcon(img);
			label.setIcon(icon);

			if (dilateCount != 0) {
				dilateCount--;
			}
			erodeCount++;
			StringBuilder sb = new StringBuilder();
			sb.append("erode count: ");
			sb.append(erodeCount);
			sb.append("; dilate count: ");
			sb.append(dilateCount);
			textField.setText(sb.toString());

		} else {
			textField.setText("Need a image before eroding");
		}
	}

	// method to turn on the mouse for activating the click event
	private void calculate(){
		if(img != null){
			mouseOn = true;
		} else {
			textField.setText("Need image for calculating surface area");
		}
	}
	
	// method to uncheck all the checkboxes
	private void uncheck() {
		cbRed.setSelected(false);
		cbBlue.setSelected(false);
		cbGreen.setSelected(false);
		colors[0] = 0;
		colors[1] = 0;
		colors[2] = 0;
		cbRed.setEnabled(false);
		cbBlue.setEnabled(false);
		cbGreen.setEnabled(false);
	}

	// method to find the surface area using findSA based on a mouse click
	private void clicked(MouseEvent e) {
		if (img != null && mouseOn) {
			xCoor = e.getX();
			yCoor = e.getY();
			if (xCoor > img.getWidth() || yCoor > img.getHeight()) {
				textField.setText("coor out of range");
			} else {
				textField.setText("xCoor: " + String.valueOf(xCoor) + "; yCoor: " + String.valueOf(yCoor));
			}
			findSA(sFactor);
		}
	}


	private void calibrate(ActionEvent e){
		String input = textField.getText();
		try {
		      ppu = Double.parseDouble(input);
		      textField.setText(String.valueOf(ppu));
		} catch (NumberFormatException err) {
		      textField.setText("Not a valid number for calibrating");
		}
	}
	
	// method to calculate get the number of pixel
	private void findSA(double sFactor) {
		System.out.println(sFactor);
		if (img != null) {
			OpenEdit oe = new OpenEdit();
			Manipulate manip = new Manipulate();
			
			// check to see if it is the first time clicking calculate
			// if so, set the previous image to the image right before
			// clicking calculate
			if (calBtnPress == false) {
				previousImg = oe.clone(img);
				calBtnPress = true;
			} 
			
			// reset the image to the current image for flooding
			img = oe.clone(previousImg);

			// get the number of pixels from flooding. When a image is
			// passed to a method, it is similar to passing by reference

			int pixCount = manip.flood(img, xCoor, yCoor);

			double result = (pixCount) / ppu;
			
			System.out.println(pixCount);
			// set the icon to the image
			ImageIcon icon = new ImageIcon(img);
			label.setIcon(icon);

			// output the number of pixels
			textField.setText("Surface Area: " + String.valueOf(result));
		} else {
			textField.setText("Need image to calculate surface area");
		}
	}
	
	// method to get the scaleing factor
	private double getSF(BufferedImage img, JLabel label) {
		OpenEdit oe = new OpenEdit();
		// get the scaling factor for an image
		double wFactor = oe.getScaleFactor(img.getWidth(), label.getWidth());
		double hFactor = oe.getScaleFactor(img.getHeight(), label.getHeight());
		
		
		double sFactor = 1.0;
		
		if (wFactor < hFactor) {
			sFactor = wFactor;
		} else {
			sFactor = hFactor;
		}	
		return(sFactor);
	}
	

	// reset the values when a new image is opened
	private void reset() {
		cbRed.setSelected(false);
		cbGreen.setSelected(false);
		cbBlue.setSelected(false);
		cbRed.setEnabled(false);
		cbGreen.setEnabled(false);
		cbBlue.setEnabled(false);
		slider.setEnabled(false);
		colors[0] = 0;
		colors[1] = 0;
		colors[2] = 0;
		erodeCount = 0;
		dilateCount = 0;
		xCoor = 0;
		yCoor = 0;
		calBtnPress = false;
		mouseOn = false;
	}
	
}
