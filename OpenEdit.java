package sa.finder;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class OpenEdit {

	// method that gets an image using file chooser and returns a buffered image
	protected BufferedImage GetImage(BufferedImage bfImg) {
		
		String path = "";
		// if there is already an image in the location
		
		
		// make new file chooser and set the current director to the user home
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));

		// filter the files
		FileNameExtensionFilter jpg = new FileNameExtensionFilter("*.jpg/jpeg", "jpg","jpeg");
		FileNameExtensionFilter gif = new FileNameExtensionFilter("*.gif", "gif");
		FileNameExtensionFilter png = new FileNameExtensionFilter("*.png", "png");
		fc.addChoosableFileFilter(jpg);
		fc.addChoosableFileFilter(gif);
		fc.addChoosableFileFilter(png);
		
		int result = fc.showOpenDialog(null);

		// if an image was selected, set the path to that image location otherwise, return
		// a null buffered image
		if (result == JFileChooser.APPROVE_OPTION) {

			// get the selected file
			File selectedFile = fc.getSelectedFile();

			// get the path to the file
			path = selectedFile.getAbsolutePath();
		
		} else if(result == JFileChooser.CANCEL_OPTION){ 
			System.out.println("No image selected");
			return bfImg;
		}

		// make a new buffered img instances
		File img = new File(path);
		
		// try to get an image
		try {
			bfImg = ImageIO.read(img);
		} catch (IOException e) {
			System.out.println("Could not open read image");
		}
		return bfImg;
	}

	// method to copy a buffered image for the undo button
	protected BufferedImage clone (BufferedImage image) {
	    BufferedImage clone = new BufferedImage(image.getWidth(),
	            image.getHeight(), image.getType());
	    Graphics2D g2d = clone.createGraphics();
	    g2d.drawImage(image, 0, 0, null);
	    g2d.dispose();
	    return clone;
	}
	
	
	
	// method to resize an image and returns the dimensions of the new image
	protected BufferedImage ResizeImage(BufferedImage img, JLabel label) {
	
		// get the scaling factor for an image		
		double wFactor = getScaleFactor(img.getWidth(),label.getWidth());
		double hFactor = getScaleFactor(img.getHeight(),label.getHeight());
				
		
		// check to see whether the width or height of the image is smaller
		// and scale the image based on that
		double sFactor = 1; 
		if(wFactor < hFactor){
			sFactor = wFactor;
		} else {
			sFactor = hFactor;
		}
		
		// multiple the dimensions of the image to get new dimensions
		int scaledHeight = (int)(img.getHeight() * sFactor);
		int scaledWidth = (int)(img.getWidth() * sFactor);
	
		// plug in the new dimensions into the resize method to resize the image
		BufferedImage scaledImage = resize(img,scaledWidth,scaledHeight);
	
		// set the icon to the image
		img = clone(scaledImage);
		ImageIcon image = new ImageIcon(img);
		label.setIcon(image);

		
		// return the scaling factor to be used to figure out the old dimension
		return(img);
	}

	// method to resize a buffered image
	protected static BufferedImage resize(BufferedImage image, int width, int height) {
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, width, height, null);
	    g2d.dispose();
	    return bi;
	}
	
	// method to determine the scale factor of the image takes as input
	// the original image and the target size of the image
	protected double getScaleFactor(int orginSize, int targetSize) {

	    double dScale = 1;
	    if (orginSize > targetSize) {

	        dScale = (double) targetSize / (double) orginSize;

	    } else {

	        dScale = (double) targetSize / (double) orginSize;

	    }

	    return dScale;

	}
	
	
}
