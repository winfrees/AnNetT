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
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import static ij.plugin.filter.PlugInFilter.DOES_16;
import static ij.plugin.filter.PlugInFilter.DOES_32;
import static ij.plugin.filter.PlugInFilter.DOES_8G;
import java.util.ArrayList;


/**
 * This is a template for a plugin that requires one image to
 * be opened, and takes it as parameter.
 */
public class Analyze_Network implements PlugInFilter {
	private ImagePlus image;
        private ImagePlus imageProcessed;
        private ImageStack isOriginal;
	//private ImageStack isResults;
	
	

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
                this.isOriginal = this.image.getStack();
		
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
            
            //Pre-process the image
            //IJ.log("Pre-processing image...");
            IJ.showStatus("PreProcessing image...");
            this.imageProcessed = new ImagePlus("", this.image.getStack());
            PreProcessor source = new PreProcessor(this.imageProcessed);           
            SliceAnalysis[] result = new SliceAnalysis[this.isOriginal.getSize()];
            
            //Analyze slices
            //IJ.log("Processing Slices...");
           
            for(int i = 1; i <= this.isOriginal.getSize(); i++){
                IJ.showStatus("Processing slices...");
                IJ.showProgress(i, this.isOriginal.getSize()); 
                result[i-1] = new SliceAnalysis(source.getNetwork(), isOriginal.getProcessor(i), i);
            }
            
            //collect variables
            //IJ.log("Gathering variables...");
            ResultsTable rt = new ResultsTable();
            rt = calculateResults(result);
            rt.show("Network Analysis");
		
            image.close();
        }
        
        private ResultsTable calculateResults(SliceAnalysis[] saResult){
        
        ResultsTable rtResult = new ResultsTable(); 
        ArrayList alResult = new ArrayList(10);
        //if(alResult.size() < 1){IJ.log("No results to gather");}
        //if(alResult.size() > 0){
        for(int i=0; i <= this.isOriginal.getSize()-1; i++){
            IJ.showStatus("Gathering data...");
            IJ.showProgress(i, this.isOriginal.getSize()); 
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
		rtResult.addValue("Network size",alResult.get(9).toString()); 	
       }
        
        return rtResult;
        }
        
        public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		Class<?> clazz = Analyze_Network.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}