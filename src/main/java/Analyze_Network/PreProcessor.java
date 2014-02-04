/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Analyze_Network;

/**
 *
 * @author winfrees
 */
import ij.ImagePlus;
import ij.IJ;
import ij.*;

/**
 * This is a template for a plugin that requires one image to
 * be opened, and takes it as parameter.
 */
public class PreProcessor {
	private ImagePlus imageOriginal;  
        private ImagePlus imageResult;
        private ImagePlus imageNetwork;
        
	
public PreProcessor(ImagePlus imp) {
                        
                        this.imageOriginal = imp.duplicate();
                        this.imageOriginal.setTitle("Original");
                        this.imageResult = new ImagePlus("PreProcessing Result", imp.getStack());
                        
			IJ.run(imageResult, "Subtract Background...", "rolling=10 stack");
			IJ.run(imageResult, "Enhance Contrast...", "saturated=0.4 normalize equalize process_all");
			IJ.run(imageResult, "Maximum...", "radius=2 stack");
			IJ.run(imageResult, "Gaussian Blur...", "sigma=3 stack");		
			IJ.run(imageResult, "Convert to Mask", "method=Mean background=Dark stack");
                        this.imageNetwork = imageResult.duplicate();
			IJ.run(imageResult, "Skeletonize", "stack");
                           
                        imp.close();
                        imageOriginal.show();
			imageResult.show();
                        imageNetwork.setTitle("Mask Result");
                        imageNetwork.show();
			//getResults(imp);
	}
public ImagePlus getNetwork(){return this.imageNetwork;}
public ImagePlus getResult(){return this.imageResult;}
public ImagePlus getSlice(int i){
    ImageStack is = imageResult.getStack();
    
    return new ImagePlus("Slice: " + i,is.getProcessor(i));
}
    }
