/*
 * Copyright (C) 2014 Indiana University
 * Authors email winfrees at iupui dot edu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/**
 *
 * @author Seth Winfree <Seth Winfree at Indiana University>
 */
package analyze_network;

import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import java.awt.AWTEvent;

//lots to do here
public class Preferences extends java.lang.Object implements DialogListener {

    private Object[] Preferences = new Object[9];
    private boolean valid = false;

    public Preferences() {
        valid = showDialog();
    }

    public boolean showDialog() {

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
//                        IJ.run(imageResult, "Subtract Background...", "rolling=10 stack");
//			IJ.run(imageResult, "Enhance Contrast...", "saturated=0.4 normalize equalize process_all");
//			IJ.run(imageResult, "Maximum...", "radius=2 stack");
//			IJ.run(imageResult, "Gaussian Blur...", "sigma=3 stack");		
//			IJ.run(imageResult, "Convert to Mask", "method=Mean background=Dark stack");
//                        this.imageNetwork = imageResult.duplicate();
//			IJ.run(imageResult, "Skeletonize", "stack");
//                        
        String Background = "Yes";
        int BackgroundRadius = 10;
        String Contrast = "Yes";
        String Maximum = "Yes";
        int MaximumRadius = 2;
        String Blur = "Yes";
        String Calibration = "Yes";
        String ThresholdMethod = "Mean";
        String ExcludeEdge = "No";
        int NetworkSize = 300;

        String[] ThresholdMethodChoice = {"Default", "Huang", "Intermodes", "IsoData", "IJ_IsoData", "Li", "MaxEntropy", "Mean", "MinError", "Minimum", "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag", "Triangle", "Yen"};

        GenericDialog gd = new GenericDialog("Network Analysis v0.1");

        gd.addMessage("Preprocessing Options:");
        gd.addRadioButtonGroup("Background Subtraction:", YesNo, 1, 1, YesNo[0]);
        if (Background == "Yes") {
            gd.addNumericField("    Radius:", BackgroundRadius, 0);
        }
        gd.addRadioButtonGroup("Enhance Contrast:", YesNo, 1, 1, YesNo[0]);
        gd.addRadioButtonGroup("Maximum Filter:", YesNo, 1, 1, YesNo[0]);
        if (Maximum == "Yes") {
            gd.addNumericField("    Radius:", MaximumRadius, 0);
        }
        gd.addRadioButtonGroup("Blur:", YesNo, 1, 1, YesNo[0]);
        //gd.addChoice("Thresholding Method:", ThresholdMethodChoice, ThresholdMethodChoice[7]);
        gd.addRadioButtonGroup("Use image calibration:", YesNo, 1, 1, YesNo[0]);
        gd.addNumericField("Minimum Network Size (px)", NetworkSize, 0);
        gd.addRadioButtonGroup("Exclude Edge Networks:", YesNo, 1, 1, YesNo[1]);
        gd.addMessage("___________________________________________");

        gd.addMessage("Interface for preprocessing and network analysis");
        gd.addMessage("Author: Seth Winfree Indiana University   02/17/2014");
        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }

        Background = gd.getNextRadioButton();
        BackgroundRadius = (int) gd.getNextNumber();
        Contrast = gd.getNextRadioButton();
        Maximum = gd.getNextRadioButton();;
        MaximumRadius = (int) gd.getNextNumber();
        Blur = gd.getNextRadioButton();
        //ThresholdMethod = gd.getNextChoice();
        Calibration = gd.getNextRadioButton();
        NetworkSize = (int) gd.getNextNumber();
        ExcludeEdge = gd.getNextRadioButton();

        //Object[] Preferences = new Object[7];
        this.Preferences[0] = Background;
        this.Preferences[1] = BackgroundRadius;
        this.Preferences[2] = Contrast;
        this.Preferences[3] = Maximum;
        this.Preferences[4] = MaximumRadius;
        this.Preferences[5] = Blur;
        this.Preferences[6] = NetworkSize;
        this.Preferences[7] = Calibration;
        this.Preferences[8] = ExcludeEdge;

        return true;
    }

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        if (gd.wasCanceled()) {
            return false;
        }
        showDialog();
        return true;
    }

    public boolean getStatus() {
        return this.valid;
    }

    public Object[] getPreferences() {

        return this.Preferences;

    }

}
