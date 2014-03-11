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
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.StackCombiner;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import static ij.plugin.filter.PlugInFilter.DOES_16;
import static ij.plugin.filter.PlugInFilter.DOES_32;
import static ij.plugin.filter.PlugInFilter.DOES_8G; 
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * This is a template for a plugin that requires one image to
 * be opened, and takes it as parameter.
 */
public class Analyze_Network implements PlugInFilter {
	private ImagePlus image;
        private ImagePlus imageProcessed;
        private ImageStack isOriginal;

        private Object[] Preferences = new Object[7];
        private Calibration cal = new Calibration();
	//private ImageStack isResults;
        
        private String[] UnCalibratedHeadings = new String[11];
	private String[] CalibratedHeadings = new String[11];;

	/**
	 * This method gets called by ImageJ / Fiji to determine
	 * whether the current image is of an appropriate type.
	 *
	 * @param arg can be specified in plugins.config
	 * @param image is the currently opened image
	 * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
	 */
	@Override
	public int setup(String arg, ImagePlus imp) {
		
		this.image = imp;
		
		/*
		 * The current return value accepts all gray-scale
		 * images (if you access the pixels with ip.getf(x, y)
		 * anyway, that works quite well.
		 *
		 * It could also be DOES_ALL; you can add "| NO_CHANGES"
		 * to indicate that the current image will not be
		 * changed by this plugin.
		 *
		 * Beware of DOES_STACKS: this will call the run()
		 * method with all slices of the current image
		 * (channels, z-slices and frames, all). Most likely
		 * not what you want.
		 */
		//return DOES_8G | DOES_16 | DOES_32;
                return DOES_8G;
	}

	/**
	 * This method is run when the current image was accepted.
	 *
	 * @param ip is the current slice (typically, plugins use
	 * the ImagePlus set above instead).
	 * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
	 */
	@Override
	public void run(ImageProcessor ip) {
            
            
            
            this.isOriginal = this.image.getStack();
           //this.isProcessed = this.image.duplicate().getStack();
            this.cal = this.image.getCalibration();
            
            
            
            String[] uch = {"Slice","Total Branch Networks","Nodes","Triples","Quadruples","Tubes","Total Length","Avg Length","Tubes/Nodes","Closed Networks","Network size"};
            String[] ch = {"Slice","Total Branch Networks","Nodes","Triples","Quadruples","Tubes","Total Length("+cal.getUnit()+")","Avg Length("+cal.getUnit()+")","Tubes/Nodes","Closed Networks","Network size("+cal.getUnit()+"^2)"};

            this.UnCalibratedHeadings = uch;
            this.CalibratedHeadings = ch;
            
            //Pre-process the image
            //IJ.log("Pre-processing image...");
            IJ.showStatus("Gathering settings...");
            
            Preferences pref = new Preferences();
            Preferences = pref.getPreferences();
            
            Date startTime = new Date();
            IJ.showStatus("PreProcessing image...");
            IJ.log("____________________________________________________");
            IJ.log("Starting network analysis on "+ this.image.getTitle() + "...");
            IJ.log("Date: " + DateFormat.getDateInstance().format(new Date()));
            IJ.log("Start time: " + DateFormat.getTimeInstance().format(new Date()));
            
            
            this.imageProcessed = this.image.duplicate();
            PreProcessor source = new PreProcessor(this.imageProcessed, Preferences);           
            SliceAnalysis[] result = new SliceAnalysis[this.isOriginal.getSize()];

           
            for(int i = 1; i <= this.isOriginal.getSize(); i++){
                IJ.showStatus("Processing slices...");
                IJ.showProgress(i, this.isOriginal.getSize()); 
                result[i-1] = new SliceAnalysis(new ImagePlus("",source.getResult().getStack().getProcessor(i)), new ImagePlus("",source.getNetwork().getStack().getProcessor(i)),i);
  
            }

            ResultsTable rt = new ResultsTable();
            rt = calculateResults(result);
            String results_title = new String("Network Analysis for "+this.image.getTitle());
            
            if(Preferences[7] == "Yes"){results_title = new String("Calibrated Network Analysis for "+this.image.getTitle());};
            if(Preferences[7] == "No"){results_title = new String("Uncalibrated Network Analysis for "+this.image.getTitle());};
            rt.show(results_title);

            StackCombiner sc = new StackCombiner();
            
            ImagePlus fused = new ImagePlus("fused "+this.image.getTitle(), sc.combineHorizontally(source.getResult().getImageStack(), source.getNetwork().getImageStack()));
            Date finishTime = new Date();
            
            long totalTime = finishTime.getTime()-startTime.getTime();
            
            IJ.log("Finish time: " + DateFormat.getTimeInstance().format(finishTime));
            IJ.log("Processing time: " + (totalTime/1000) + " sec");
            fused.show();
        }
        
        private ResultsTable calculateResults(SliceAnalysis[] saResult){
        
        ResultsTable rtResult = new ResultsTable(); 
        ArrayList alResult = new ArrayList(10);

        for(int i=0; i <= this.isOriginal.getSize()-1; i++){
            IJ.showStatus("Gathering data...");
            IJ.showProgress(i, this.isOriginal.getSize()); 
            if(Preferences[7] == "Yes"){
                alResult = saResult[i].getResult();
        	rtResult.incrementCounter();
                rtResult.addValue(this.CalibratedHeadings[0], i+1); //slice
                rtResult.addValue(this.CalibratedHeadings[1], alResult.get(0).toString());
                rtResult.addValue(this.CalibratedHeadings[2], alResult.get(1).toString());
		rtResult.addValue(this.CalibratedHeadings[3], alResult.get(2).toString());
                rtResult.addValue(this.CalibratedHeadings[4], alResult.get(3).toString());
		rtResult.addValue(this.CalibratedHeadings[5], alResult.get(4).toString()); //branches
		rtResult.addValue(this.CalibratedHeadings[6], (cal.pixelWidth * Double.parseDouble(alResult.get(5).toString()))); //junctions
		rtResult.addValue(this.CalibratedHeadings[7], (cal.pixelWidth * Double.parseDouble(alResult.get(6).toString())));
		rtResult.addValue(this.CalibratedHeadings[8], alResult.get(7).toString());
		rtResult.addValue(this.CalibratedHeadings[9],alResult.get(8).toString());  
		rtResult.addValue(this.CalibratedHeadings[10],(cal.pixelWidth * Double.parseDouble(alResult.get(9).toString())));}
                
            if(Preferences[7] == "No"){
                alResult = saResult[i].getResult();
        	rtResult.incrementCounter();
                rtResult.addValue("Slice", i+1); //slice
                rtResult.addValue("Total Branch Networks", alResult.get(0).toString());
                rtResult.addValue("Nodes", alResult.get(1).toString());
		rtResult.addValue("Triples", alResult.get(2).toString());
                rtResult.addValue("Quadruples", alResult.get(3).toString());
		rtResult.addValue("Tubes", alResult.get(4).toString()); //branches
		rtResult.addValue("Total Length",  alResult.get(5).toString()); //junctions
		rtResult.addValue("Avg Length", alResult.get(6).toString());
		rtResult.addValue("Tubes/Nodes", alResult.get(7).toString());
		rtResult.addValue("Closed Networks",alResult.get(8).toString());  
		rtResult.addValue("Network size",alResult.get(9).toString());} 
       }
        
        return rtResult;
        }
        
//        public static void main(String[] args) {
////		// set the plugins.dir property to make the plugin appear in the Plugins menu
////		Class<?> clazz = Analyze_Network.class;
////		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
////		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
////		System.setProperty("plugins.dir", pluginsDir);
//
////		// start ImageJ
////		new ImageJ();
////
////		// run the plugin
////		IJ.runPlugIn(clazz.getName(), "");
//	}
}