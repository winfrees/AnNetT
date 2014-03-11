/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Analyze_Network;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import ij.IJ;
import static ij.measure.Measurements.AREA;
import static ij.measure.Measurements.LIMIT;
import ij.plugin.filter.ParticleAnalyzer;
import static ij.plugin.filter.ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES;




import skeleton_analysis.*;
import java.util.ArrayList;

/**
 *
 * @author winfrees
 */
public class SliceAnalysis {
    
    private ArrayList Results;
    AnalyzeSkeleton_ skel = new AnalyzeSkeleton_();
    //private ImagePlus image;
    //private ImagePlus imageNetwork;
    //private ImageStack stackNetwork;
    //private ImagePlus imageSkeleton;
    //private ImageProcessor imp;
    
    final int NETWORKAREA = 0;
    final int NETWORKCOUNT = 1;
    
    public SliceAnalysis(){}
    
    public SliceAnalysis(ImagePlus impSlice, int slice){
    

                ArrayList alResult = new ArrayList();
                		
		int isWidth = impSlice.getWidth();
		int isHeight = impSlice.getHeight();
                

                skel.setup("",impSlice);
                SkeletonResult Output = skel.run(AnalyzeSkeleton_.NONE,false,false,null,true,true);
                

//		Analyze_Skeleton_ generates a SkeletonResult class that contains the
//                values of interest by skeleton.  Whereby array position is each skeleton.  
//                Thus, to get totals like:
//                
//                # of nodes
//                # of Tubes
//                # of Branches
//                      
//                they must be summed across all skeletons
                
                   // IJ.log("                ...Extracting values");
		
                int[] nodes = Output.getJunctions();
		int[] triples = Output.getTriples();
                int[] quadruples = Output.getQuadruples();
		int[] branches = Output.getBranches();
                double[] branchLengths = Output.getAverageBranchLength();
 
		int countSkeletons = Output.getJunctions().length; //skeletons
                int countNodes = 0; //junctions
                int countTriples = 0; //3 way junction
                int countQuadruples = 0; //4way junction    
                int countBranches = 0; //slabs 
		double totalTubeLength = 0; //branches

                int countNetwork = 0;
                int averageSizeNetwork = 0;

                //Output.;
                
		//NEW AND COOL IDEAS// distribution characteristics?  statistical test of branch lengths
                //NEW AND COOL IDEAS// average network size with time per network, object tracking/growing etc.
                //NEW AND COOL IDEAS// multiparametric analysis a la VTC, machine learning etc.
		
                for(int i = 0; i <= countSkeletons-1; i++){
                
                    countNodes = countNodes + nodes[i]; //junctions
                    countTriples = countTriples + triples[i];
                    countQuadruples = countQuadruples + quadruples[i];
                    countBranches = countBranches + branches[i];
                    totalTubeLength = totalTubeLength + branchLengths[i]*branches[i];
                            }


                alResult.add(countSkeletons); //contiguous network (not nesc. closed)
                alResult.add(countNodes); //junctions
		alResult.add(countTriples); //3 way junctions	
                alResult.add(countQuadruples); //4 way junctions
		alResult.add(countBranches); //branches
                alResult.add(totalTubeLength); //length summation
                alResult.add((double)totalTubeLength/countSkeletons); //average tube length
		alResult.add((double)countBranches/countNodes); //ratio
		alResult.add((int)calculateClosedNetworkCount(impSlice)); //sum of closed networks
		alResult.add((double)calculateClosedNetworkAverageArea(impSlice)); //average size of closed networks

                this.Results = alResult;
   }
    
    private int calculateClosedNetworkCount(ImagePlus imp_pass){
    
       ResultsTable rt = new ResultsTable();
        int countRt = 0;
        
        double networkArea= 0;
        
        //ArrayList al_return = new ArrayList();

        IJ.run(imp_pass, "Invert", "");
        IJ.setThreshold(imp_pass, 255, 255);
 
        ParticleAnalyzer pa = new ParticleAnalyzer(LIMIT&EXCLUDE_EDGE_PARTICLES , AREA, rt, 0, Double.POSITIVE_INFINITY, 0, 1);
        pa.analyze(imp_pass);

        countRt = rt.getCounter();
        
        for(int c = 0; c <= countRt-1; c++){
        
            networkArea = networkArea+rt.getValueAsDouble(0, c);
        }
     
     return countRt;
    };
    private double calculateClosedNetworkAverageArea(ImagePlus imp_pass){
    
       ResultsTable rt = new ResultsTable();
        int countRt = 0;
        
        double networkArea= 0;
        
        
        //ImagePlus imp = new ImagePlus("", ip_pass);
    
        IJ.run(imp_pass, "Invert", "");
        IJ.setThreshold(imp_pass, 255, 255);
       
        ParticleAnalyzer pa = new ParticleAnalyzer(EXCLUDE_EDGE_PARTICLES&LIMIT, AREA, rt, 0, Double.POSITIVE_INFINITY, 0, 1);
        pa.analyze(imp_pass);
        
        countRt = rt.getCounter();
        
        for(int c = 0; c <= countRt-1; c++){
        
            networkArea = networkArea+rt.getValueAsDouble(0, c);
        }
        
        
     return (double)networkArea/countRt;

    };
    
    
    public ArrayList getResult() {return this.Results;} 

}
