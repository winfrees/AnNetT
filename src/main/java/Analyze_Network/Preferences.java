/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Analyze_Network;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.*;
import java.awt.AWTEvent;



/**
 *
 * @author winfrees
 */
public class Preferences extends java.lang.Object implements DialogListener {
   
Object[] Preferences = new Object
    
public Preferences(){}


public void showDialog(){

		int[] windowList = WindowManager.getIDList();
                String[] YesNo = {"Yes", "No"};

//		if (windowList==null || windowList.length<1) {
//			//error();
//			IJ.showMessage("Error", "No open images");
//			return;
//		}
//		String[] titles = new String[windowList.length];
//		for (int i=0; i<windowList.length; i++) {
//			ImagePlus imp_temp = WindowManager.getImage(windowList[i]);
//			titles[i] = imp_temp!=null?imp_temp.getTitle():"";
//		}
                
                
                        IJ.run(imageResult, "Subtract Background...", "rolling=10 stack");
			IJ.run(imageResult, "Enhance Contrast...", "saturated=0.4 normalize equalize process_all");
			IJ.run(imageResult, "Maximum...", "radius=2 stack");
			IJ.run(imageResult, "Gaussian Blur...", "sigma=3 stack");		
			IJ.run(imageResult, "Convert to Mask", "method=Mean background=Dark stack");
                        this.imageNetwork = imageResult.duplicate();
			IJ.run(imageResult, "Skeletonize", "stack");
                        
                String Background = new String("Yes");
                int BackgroundRadius = 10;
                String Contrast = "Yes";
                String Maximum = "Yes";
                int MaximumRadius = 2;
                String Blur = "Yes";
                String ThresholdMethod ="Mean";
                
                String[] ThresholdMethodChoice = {"Default","Huang","Intermodes","IsoData","IJ_IsoData","Li","MaxEntropy","Mean","MinError","Minimum","Moments","Otsu","Percentile","RenyiEntropy","Shanbhag","Triangle","Yen"};
               

		
		GenericDialog gd = new GenericDialog("Network Analysis v0.1");

                gd.addMessage("Preprocessing Options:");
		gd.addRadioButtonGroup("Background Subtraction:", YesNo, 1, 1, YesNo[0]);
                if(Background == "Yes"){ gd.addNumericField("    Radius:", BackgroundRadius, BackgroundRadius);}
                gd.addRadioButtonGroup("Enhance Contrast:", YesNo, 1, 1, YesNo[0]);       
                gd.addRadioButtonGroup("Maximum Filter:", YesNo, 1, 1, YesNo[0]);
                if(Maximum == "Yes"){ gd.addNumericField("    Radius:", MaximumRadius, MaximumRadius);}
                gd.addRadioButtonGroup("Blur:", YesNo, 1, 1, YesNo[0]);
                gd.addChoice("Thresholding Method:", ThresholdMethodChoice, ThresholdMethodChoice[7]);
gd.addMessage("___________________________________________");

		gd.addMessage("Interface for preprocessing and network analysis");
                gd.addMessage("Author: Seth Winfree Indiana University   02/17/2014");
		gd.showDialog();
		if (gd.wasCanceled()) return;




		Background = gd.getNextString();
                if(Background == "Yes"){ BackgroundRadius = (int)gd.getNextNumber();}
                Contrast = gd.getNextString();
                Maximum = gd.getNextString();
                int MaximumRadius = 2;
                String Blur = "Yes";
                String ThresholdMethod ="Mean";
                

	return;	
	}

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        if (gd.wasCanceled()) return false;
        showDialog();
        return true;
    }

    
    
    
}
