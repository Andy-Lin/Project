##############################################################################
Author: Andy Lin
Date Created: 12-5-2015
Desktop Application Name: SAFinder.jar

##############################################################################

How to use desktop application SAFinder. 

1. Open file using the "Open File" button
2. Push the "Threshold Img" button, select the color channel to be used
   for thresholding (pick color of ulcer) and use the slider bar to make
   the image into black and white so that the ulcer is completely separated
   from the other non-ulcer portion. (ok if the ulcer is not the only thing
   left in the image after thresholding)
3. [Optional] Dilate (make white parts larger) or erode (make white parts smaller) 
	the image so that the size of the ulcer looks like the actual ulcer size
4. [Optional] Calibrate the pixels by inputing the number of pixels per unit
	length in the image. For example, one inch is 100 pixels in the image.
	This will require some math. 
5. Calculate the surface area by using the "Calculate Surface Area" button and 
	clicking on the region desired. 

Extra Buttons:
	"Revert" button reverts the image back to either the original image, or
	whatever image is commited using the "Commit" button.

Known Bugs: 
Most, if not all bugs can be resolved by open the image again. 

When the commit button is used, if one tries to open a new image but clicks 
cancel instead of opening the image, the original image becomes that of the
commited image. 

Once the calculate surface area button is pressed, it becomes impossible to 
threshold, erode or dilate the image anymore even when the image is reverted.
The calculate surface area button should only be press when one is sure the
image is threshold, eroded or dilated to one's liking.

One cannot input the disired threshold level for thresholding. The slider bar
must be used. 
