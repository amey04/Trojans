

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;

import javax.swing.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

public class Image {
	
	BufferedImage img;
    	
    double[][] h;
    double[][] s;//= new double[288][352];
    double[][] v;//= new double[288][352];
    
    int height;
    int width;
    
    double[] hHist= new double[360];
    double[] sHist= new double[256];
    
    int[][] r;
    int[][] g;
    int[][] b;
    
    double[] rHist= new double[256];
    double[] gHist= new double[256];
    double[] bHist= new double[256];
    double[] rgbHist= new double[256];
    
    
    Image(int delta1, int delta2)
    {
    	img = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
    	h= new double[delta2][delta1];
    	s= new double[delta2][delta1];
    	v= new double[delta2][delta1];
    	height=delta2;
    	width=delta1;
    	r= new int[delta2][delta1];
    	g= new int[delta2][delta1];
    	b= new int[delta2][delta1];
    }
    
    Image()
    {
    	img = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
    	h= new double[288][352];
    	s= new double[288][352];
    	v= new double[288][352];
    	height=288;
    	width=352;
    	r= new int[288][352];
    	g= new int[288][352];
    	b= new int[288][352];
    }
    
    void displayImage(int x1, int y1, int x2, int y2)
    {
    	JFrame frame = new JFrame();
    	JLabel label = new JLabel();
    	JPanel  square = new JPanel (); 
    
    	label.setIcon(new ImageIcon(img));
    	square.setOpaque(false);
        square.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        square.setBounds( x1, y1, x2, y2 );  
         
        frame.getContentPane().add(square, BorderLayout.CENTER);
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
       
	void readImage(String fileName)
	{
		File file = new File(fileName);
		InputStream is;
		try {
			is = new FileInputStream(file);
			long len = file.length();
		    byte[] bytes = new byte[(int)len];
		    int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	        
	        int ind = 0;
			for(int y = 0; y < 288; y++){
				for(int x = 0; x < 352; x++){
		 
				this.r[y][x] = bytes[ind]& 0xff;
				this.g[y][x] = bytes[ind+352*288]& 0xff ;
				this.b[y][x] = bytes[ind+352*288*2]& 0xff; 
						
				int pix = 0xff000000 | ((this.r[y][x] & 0xff) << 16) | ((this.g[y][x] & 0xff) << 8) | (this.b[y][x] & 0xff);
				//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
				img.setRGB(x,y,pix);
				ind++;
			}
		}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void readAlpha(int[][] alpha, String fileName)
	{
		File file = new File(fileName);
		InputStream is;
		try {
			is = new FileInputStream(file);
		
		long len = file.length();
	    byte[] bytes = new byte[(int)len];
	    int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        
        int ind=0;
        for (int j=0; j<288; j++){
        for (int i=0; i<352; i++)	
        	{
        	alpha[j][i]= bytes[ind]&0xff;
				ind++;
        	}
        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void convert(int alpha[][])
	{
		float[] hsb= new float[3];
		
		for(int j=0;j<288; j++)
		{
			for (int i=0; i<352; i++)
			{
					Color.RGBtoHSB(r[j][i], g[j][i], b[j][i], hsb);
					h[j][i] = hsb[0];
					s[j][i] = hsb[1];
					v[j][i] = hsb[2];
			}
		}
	}
	
	void calcHist( int alpha[][])
	{
		int j,i,p;
		int[][] tempH= new int[height][width];
		int[][] tempS= new int[height][width];
				
		for (j=0; j<height; j++)
		{
			for (i=0;i<width;i++)
			{
				tempH[j][i]= (int)Math.floor((this.h[j][i]*360.0));
				tempS[j][i]= (int)Math.floor((this.s[j][i]*255.0));
			}
		}
		
		for (j=0; j<height; j++)
		{
			for (i=0; i<width; i++)
			{
				if(alpha[j][i]==1)
				{
					if(0<=tempH[j][i] && 360>tempH[j][i])
						this.hHist[tempH[j][i]]++;	
					if(0<=tempS[j][i] && 255>=tempS[j][i])
						this.sHist[tempS[j][i]]++;
				}
			}
		}
		int val1=0, val2=0;
		
		for(p=0; p <360; p++)
		{
		//	System.out.println(hHist[p]);
			val1+= this.hHist[p];
		}
		for(p=0; p <=255; p++)
		{
			//System.out.println(sHist[p]);
			val2+= sHist[p];
		}
		
		for(p=0; p<360; p++)
		{
			this.hHist[p]= this.hHist[p]*1.0/val1;
			//System.out.println(this.hHist[p]);
		}
		for(p=0; p<=255; p++)
			this.sHist[p]= this.sHist[p]*1.0/val2;
		/*System.out.println(sHist[p]);*/
		
		//System.out.println("val1= " + val1+ "  " + val2);
	}
	
	double compareH(double logoHist[])
	{
		Mat H1 = new Mat(1,360,CvType.CV_32F);
	    Mat H2 = new Mat(1,360,CvType.CV_32F);	
	    
	   for(int i=0; i<360; i++)
	   {
		   H1.put(0, i, logoHist[i]);
		   H2.put(0, i, this.hHist[i]);
	   }
	    double result=Imgproc.compareHist(H1, H2, Imgproc.CV_COMP_BHATTACHARYYA);
	    return(result);
	}
	
	double compareS(double logoHist[])
	{
		Mat H1 = new Mat(1,255,CvType.CV_32F);
	    Mat H2 = new Mat(1,255,CvType.CV_32F);	
	    
	   for(int i=0; i<255; i++)
	   {
		   H1.put(0, i, logoHist[i]);
		   H2.put(0, i, this.sHist[i]);
	   }
	    double result=Imgproc.compareHist(H1, H2, Imgproc.CV_COMP_BHATTACHARYYA);
	    return(result);
	    
	}

	void calcRGBHist(int alpha[][])
	{
		int i,j,p;
		for (j=0; j<height; j++)
		{
			for (i=0; i<width; i++)
			{
				if(alpha[j][i]==1)
				{
					if(0<=this.r[j][i] && 255>=this.r[j][i])
						this.rHist[this.r[j][i]]++;	
					if(0<=this.g[j][i] && 255>=this.g[j][i])
						this.gHist[this.g[j][i]]++;
					if(0<=this.b[j][i] && 255>=this.b[j][i])
						this.bHist[this.b[j][i]]++;
				}
			}
		}
		
		int val1=0, val2=0, val3=0;
		
		for(p=0; p <256; p++)
		{
			val1+= this.rHist[p];
			val2+= this.gHist[p];
			val3+= this.bHist[p];			
		}
			
		for(p=0; p<256; p++)
		{
			this.rHist[p]= this.rHist[p]*1.0/val1;
			this.gHist[p]= this.gHist[p]*1.0/val2;
			this.bHist[p]= this.bHist[p]*1.0/val3;
		}
		
		for(p=0; p<256; p++)
		{
			this.rgbHist[p]= this.rHist[p]*16 + this.gHist[p]*4 + this.bHist[p];
		}
	}	

	double compareRGB(double logoHist[])
	{
		Mat H1 = new Mat(1,256,CvType.CV_32F);
	    Mat H2 = new Mat(1,256,CvType.CV_32F);	
	    
	   for(int i=0; i<=255; i++)
	   {
		   H1.put(0, i, logoHist[i]);
		   H2.put(0, i, this.rgbHist[i]);
	   }
	    double result=Imgproc.compareHist(H1, H2, Imgproc.CV_COMP_BHATTACHARYYA);
	    return(result);
	}
	
}

