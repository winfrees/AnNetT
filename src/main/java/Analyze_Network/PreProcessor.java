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
        
	
public PreProcessor(ImagePlus imp, Object[] Preferences) {
                        
                        this.imageOriginal = imp.duplicate();
                        this.imageOriginal.setTitle("Original");
                        this.imageResult = imp.duplicate();
                        
                        
                        //this.imageResult = new ImagePlus("PreProcessing Result", imp.getStack());
                        
                        //IJ.log("Starting PreProcessing...");

                        if((String)Preferences[0] == "Yes"){IJ.run(imageResult, "Subtract Background...", "rolling="+Preferences[1]+" stack"); IJ.log("Subtract Background... rolling="+Preferences[1]+" stack");}
			if((String)Preferences[2] == "Yes"){IJ.run(imageResult, "Enhance Contrast...", "saturated=0.4 normalize equalize process_all");IJ.log("Enhance Contrast...  saturated=0.4 normalize equalize process_all");}
			if((String)Preferences[3] == "Yes"){IJ.run(imageResult, "Maximum...", "radius="+Preferences[4]+" stack");IJ.log("Maximum...  radius="+Preferences[4]+" stack");}
			if((String)Preferences[5] == "Yes"){IJ.run(imageResult, "Gaussian Blur...", "sigma=3 stack");IJ.log("Gaussian Blur...  sigma=3 stack");}		
			IJ.run(imageResult, "Convert to Mask", "method="+(String)Preferences[6]+" background=Dark stack");
                        this.imageNetwork = imageResult.duplicate();
			IJ.run(imageResult, "Skeletonize", "stack");
                           
                        imp.close();
                        imageOriginal.show();
			//imageResult.show();
                        //imageNetwork.setTitle("Mask Result");
                        //imageNetwork.show();
			//getResults(imp);
	}
public ImagePlus getNetwork(){return this.imageNetwork;}
public ImagePlus getResult(){return this.imageResult;}
public ImagePlus getSlice(int i){
    ImageStack is = imageResult.getStack();
    
    return new ImagePlus("Slice: " + i,is.getProcessor(i));
}

    }
