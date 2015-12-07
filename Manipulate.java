package sa.finder;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;


public class Manipulate {

	// method to threshold a buffered image image
	protected BufferedImage thresholdImage(BufferedImage image, int thresLevel,int[] arr){
		
		
		// make the colors to convert image to black and white
		Color myWhite = new Color(255, 255, 255); // Color white
		Color myBlack = new Color(0,0,0); // color black
		int whiteColor = myWhite.getRGB();
		int blackColor = myBlack.getRGB();
		
		// go through each pixel
		for (int i = 0; i < image.getWidth(); i++) {
		    for (int j = 0; j < image.getHeight(); j++) {
		    	// get the value of each color
		    	int color = image.getRGB(i, j);
		    	
		    	// get the color value at a pixel based on what is checked
				int colVal = getPixelColor(color, arr);
								
				// threshold to black and white image
		    	if(colVal < thresLevel){
		    		image.setRGB(i, j, blackColor);
		    	} else {
		    		image.setRGB(i, j, whiteColor);
		    	}
		    }
		}
		
		return image;
	}
	

	// dilate the image making it larger
 	protected BufferedImage dilate(BufferedImage img){
		// make the colors used for comparison
		Color myWhite = new Color(255, 255, 255); // Color white
		Color myBlack = new Color(0,0,0); // color black
		int whiteColor = myWhite.getRGB();
		int blackColor = myBlack.getRGB();
		
		// make another image that is the same size as the passed image
		BufferedImage img2 = new BufferedImage(img.getWidth(),
	            img.getHeight(), img.getType());
		
		// get the surrounding pixel colors in a cross
		for(int row = 1; row < img.getWidth() -1 ;row++){
			for(int col = 1; col < img.getHeight() - 1;col++){		
				int neighbor =  getRed(img.getRGB(row + 1, col)) + 
				getRed(img.getRGB(row, col + 1)) +
				getRed(img.getRGB(row - 1, col)) + 
				getRed(img.getRGB(row , col - 1));
				
				// check to see if the pixel and surrounding pixels are black
				// if so, set it to black otherwise set it to white
				if(neighbor == 0 && getRed(img.getRGB(row, col)) == 0){
					img2.setRGB(row, col, blackColor);
				} else {
					img2.setRGB(row, col, whiteColor);
				}
			}	
		}
	
		return img2;
	}

	
 	
 	// erode the image, making it smaller
	protected BufferedImage erode(BufferedImage img){
		
		Color myWhite = new Color(255, 255, 255); // Color white
		Color myBlack = new Color(0,0,0); // color black
		int whiteColor = myWhite.getRGB();
		int blackColor = myBlack.getRGB();
		
		BufferedImage img2 = new BufferedImage(img.getWidth(),
	            img.getHeight(), img.getType());
		
		for(int row = 1; row < img.getWidth() -1 ;row++){
			for(int col = 1; col < img.getHeight() - 1;col++){		
				int neighbor =  getRed(img.getRGB(row + 1, col)) + 
				getRed(img.getRGB(row, col + 1)) +
				getRed(img.getRGB(row - 1, col)) + 
				getRed(img.getRGB(row , col - 1));
				
				// check to see if the pixel and surrounding pixels are black
				// if so, set the pixel of img2 to black otherwise, set it to white
				if(neighbor == 1020 && getRed(img.getRGB(row, col)) == 255){
					img2.setRGB(row, col, whiteColor);
				} else {
					img2.setRGB(row, col, blackColor);
				}
				
			}	
		}
		return img2;
	}
		
	protected int flood(BufferedImage img ,int coorX, int coorY){
		
		Color myRed = new Color(255, 0, 0);
		int redColor = myRed.getRGB();

		int myColor = img.getRGB(coorX, coorY);
		int srcColor = getRed(myColor);
		
		// 2d array to mark whether a pixel has been visited
		boolean[][] painted = new boolean[img.getHeight()][img.getWidth()];
		
		// make a queue for the points
		Queue<Point> queue = new LinkedList<Point>();
		queue.add(new Point(coorX, coorY));
		int pixelCount = 0;
		
		
		
		while (!queue.isEmpty()) {
			// get the first point of the queue and remove it from the queue
			Point p = queue.remove();
			
			// if the pixel is within the bounds
			
			if ((p.x >= 0) && (p.x < img.getWidth() && (p.y >= 0) && (p.y < img.getHeight()))) {
				// if the pixel has not been visited and is the same color as the initial pixel
			
				if (!painted[p.y][p.x] && getRed(img.getRGB(p.x, p.y)) == srcColor) {
					// set that pixel location to red and visited
					painted[p.y][p.x] = true;
					img.setRGB(p.x, p.y, redColor);
					
					// count the pixels
					pixelCount++;

					// add the points surrounding the pixel to the queue
					queue.add(new Point(p.x + 1, p.y));
					queue.add(new Point(p.x - 1, p.y));
					queue.add(new Point(p.x, p.y + 1));
					queue.add(new Point(p.x, p.y - 1));
				}
			}
		}
		
		Color myBlue = new Color(0, 0, 255);
		int blueColor = myBlue.getRGB();
		img.setRGB(coorX, coorY, blueColor);
				
		return pixelCount;
	}

	
			
	// method to get the average color of a pixel based on the color channels selected
	// color channel in the form of an integer array with 0 representing not selected
	// and 1 representing selected. arr[0]: red, arr[1]:green, arr[2]:blue
	protected static int getPixelColor(int color, int[] arr){
		// default of no colors selected 
		int red = 0;
		int green = 0;
		int blue = 0;
		int numCol = 0;
		
		// check to see which color channel to extract from
		// 0 : red, 1: green, 2: blue
		if(arr[0] == 1){
			red = getRed(color);
			numCol++;
		}
		if(arr[1] == 1){
			green = getGreen(color);
			numCol++;
		}
		if(arr[2] == 1){
			blue = getBlue(color);
			numCol++;
		}

		// get the channel value 
		int colVal = (red+blue+green)/numCol;
		return colVal;
	}
	
	// method to get the red channel value based on a color obtain from getRGB
	protected static int getRed(int color) {
		return (color & 0x00ff0000)  >> 16;
	}
	
	// method to get the green channel value based on a color obtain from getRGB
	protected static int getGreen(int color) {
		return	(color & 0x0000ff00)  >> 8;
	}
	
	// method to get the blue channel value based on a color obtain from getRGB
	protected static int getBlue(int color) {
		return (color & 0x000000ff)  >> 0;	
	}
	
}
