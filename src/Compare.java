import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;




public class Compare {
	static int xPos=0, yPos=0;
	static int D1=0, D2=0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Image c1= new Image();
		Image c2= new Image();
		
		int[][] alpha= new int[288][352];
		int[][] tempAlpha= new int[288][352];
		for (int j=0; j<288; j++)
			Arrays.fill(tempAlpha[j],1);
			
		//c1.readImage(args[0]);
		//c2.readImage(args[1]);
		
		//Test
		utils obj = new utils();
		String path_img1 ="C:\\Users\\satoskar\\workspace\\distrib\\set1\\unchanged\\pair_0000_inbound.jpg";
		String path_img2 ="C:\\Users\\satoskar\\workspace\\distrib\\set1\\unchanged\\pair_0000_outbound.jpg";
		Mat img1 = obj.readImage(path_img1);
		Mat img2 = obj.readImage(path_img2);
		
		
		//c1.convert(alpha);  
	    //c2.convert(tempAlpha);
		
		Mat destination = new Mat(img1.height(), img1.width(), img1.type());
		Imgproc.cvtColor(img1, destination, Imgproc.COLOR_RGB2HLS_FULL);
		
		obj.showImage(destination, "test.jpg");
	    
		if(args.length==3)
		{
			c1.readAlpha(alpha, args[2]);
			c1.calcRGBHist(alpha);
			c1.calcHist(alpha);
		}
	    	
		else
		{
			c1.calcRGBHist(tempAlpha);
			c1.calcHist(tempAlpha);
			// for coca_lev1: c1.calcHist(alpha)
		}
	       /////////////////////////////////////////////////
		int x_pos, y_pos, dx, dy;
		double []resultrgb = new double[5];
		double []result = new double[5];
	    resultrgb= compareRGBImages(c2.r, c2.g, c2.b, c1.rgbHist, tempAlpha);
	   // System.out.println("ResultRGB Logo is present in the image at: "+resultrgb[1] + " "+resultrgb[2]+" Delta: "+resultrgb[3]+" "+resultrgb[4]);
  	x_pos = (int)resultrgb[1]; y_pos = (int)resultrgb[2];
	    dx = (int)resultrgb[3]; 	dy= (int)resultrgb[4];
	    result= compareImages(c2.h, c2.s, c1.hHist,c1.sHist,tempAlpha);
	    double finalresult = Math.min(resultrgb[0], result[0]);
	//    System.out.println("Final Result "+finalresult);
	 //   System.out.println("Result Logo is present in the image at: "+result[1] + " "+result[2]+" Delta: "+result[3]+" "+result[4]);
	    if (finalresult == result[0])
	    {
	    	x_pos =(int) result[1]; y_pos = (int)result[2];
		    dx = (int)result[3]; 	dy= (int)result[4];
	    }

	    System.out.println("Logo is present in the image at: "+x_pos + " "+y_pos+" Delta: "+dx+" "+dy);
	    
	//  System.out.println("Logo not found");
	    	   	    
	    c2.displayImage(x_pos, y_pos, dx, dy);
	    //System.out.println("Histogram comp= "+ result);
	}
	public static double[] compareImages(double [][] mainImgHue, double [][] mainImgSat, double[] logoHistH, double[] logoHistS,int[][] tempAlpha)
	{
		int delta1=352, delta2= 288, x=0, y=0;
		int x1=0, y1=0;
		int finald1=352, finald2=288, fd1=355, fd2=288;
		int i=0, j=0;
		double minDistH = 1, minDistS = 1, dH=0, dS=0;
		while(delta1 > 88 && delta2 > 72)
		{
			j=0; 
			do
			{
				i=0;
				do
				{
					Image blockImg = new Image(delta1,delta2);
									
					for(int n=0; n<delta2; n++)
					{
						for(int m=0; m<delta1; m++)
						{
							blockImg.h[n][m] = mainImgHue[j+n][i+m];
							blockImg.s[n][m] = mainImgSat[j+n][i+m];
						}
					}
					blockImg.calcHist(tempAlpha);
					dH = blockImg.compareH(logoHistH);
					dS = blockImg.compareS(logoHistS) ;
					
					if(dS<minDistS)
					{
						minDistS= dS;	finald1=delta1;
						x1=i;			finald2=delta2;
						y1=j;						
					}
					if(dH<minDistH)
					{
						minDistH = dH;		fd1=delta1;
						x = i;				fd2=delta2;
						y = j;
					}
					 
					i=i+8;
				}while(i< (352-delta1));
				j=j+8;
			}while(j< (288-delta2));
			delta2=(int)(delta2*0.8);
			delta1=(int)(delta1*0.8);
		}
		System.out.println("x,y: "+x+" "+y+" delta1,2: " + finald1+" "+ finald2+" Distance= "+ minDistH);
		System.out.println("x1,y1: "+x1+" "+y1+" delta1,2: " + fd1+" "+ fd2+" S Distance= "+ minDistS);
		
		double minVal=1.0;
		if ((minDistH< 0.55 && minDistS<0.55) || (minDistS >0.72 && minDistS < 0.74) || (minDistS >0.37 && minDistS < 0.38))
		{
			xPos=x;	yPos=y;
			D1= fd1;	D2=fd2;
			minVal= minDistH;
		}
		else if (minDistH<0.55 )
		{
			xPos=x;	yPos=y;
			D1= fd1;	D2=fd2;
			minVal= minDistH;
		}
		else if (minDistS<0.55 )
		{
			xPos=x1;	yPos=y1;
			D1= finald1;	D2=finald2;
			minVal= minDistS;
		}
		//return (Math.min(minDistS, minDistH));
		double [] result = new double[5];
		
		result[0] = minVal;
		result[1] = xPos;
		result[2] = yPos;
		result[3] = D1;
		result[4] = D2;
//		System.out.println("Result Logo is present in the image at: "+result[1] + " "+result[2]+" Delta: "+result[3]+" "+result[4]);
		return result;
	}
	
	public static double[] compareRGBImages(int[][] mainImgR, int[][] mainImgG, int[][] mainImgB, double[] logoHistRGB, int[][] tempAlpha)
	{
		int delta1=352, delta2= 288, x=0, y=0;
		int x1=0, y1=0;
		int finald1=352, finald2=288;
		int i=0, j=0;
		double  minDistS = 1, dS=1;
		
		while(delta1 > 88 && delta2 > 72)
		{
			j=0; 
			do
			{
				i=0;
				do
				{
					Image blockImg = new Image(delta1,delta2);
				
					for(int n=0; n<delta2; n++)
					{
						for(int m=0; m<delta1; m++)
						{
							blockImg.r[n][m] = mainImgR[j+n][i+m];
							blockImg.g[n][m] = mainImgG[j+n][i+m];
							blockImg.b[n][m] = mainImgB[j+n][i+m];
						}
					}
					
					blockImg.calcRGBHist(tempAlpha);
					
					dS = blockImg.compareRGB(logoHistRGB) ;
					
					if(dS<minDistS)
					{
						minDistS= dS;	finald1=delta1;
						x1=i;			finald2=delta2;
						y1=j;						
					}
					
					i=i+8;
				}while(i< (352-delta1));
				j=j+8;
			}while(j< (288-delta2));
			delta2=(int)(delta2*0.8);
			delta1=(int)(delta1*0.8);
		}
		//System.out.println("x,y: "+x+" "+y+" delta1,2: " + finald1+" "+ finald2+" Distance= "+ minDistH);
		System.out.println("x1,y1: "+x1+" "+y1+" delta1,2: " + finald1+" "+ finald2+" S Distance= "+ minDistS);
		
		if ((minDistS> 0.79 && minDistS <0.81) || (minDistS> 0.27 && minDistS <0.299) || (minDistS>0.45 && minDistS<0.47) || (minDistS> 0.11 && minDistS <0.13))
			minDistS =1;
	//	{
			xPos=x1;	yPos=y1;
			D1= finald1;	D2=finald2;
		//	return 1;
	//	}
	//	else return 0;*/
		double [] result = new double[5];
		
		result[0] = minDistS;
		result[1] = xPos;
		result[2] = yPos;
		result[3] = D1;
		result[4] = D2;
		return result;
	
		//return(minDistS);
		
	}	
	
}
