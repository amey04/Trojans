package SIFT;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImgR {

	int width = 352;
	int height = 288;
	
	int red[][] = new int[288][352];
	int blue[][] = new int[288][352];
	int green[][] = new int[288][352];
	
    public BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	public int[] input = new int[352*288];

	public void readImage(String fileName){
		
		    try {
		    File file = new File(fileName);
		    InputStream is = new FileInputStream(file);

		    long len = file.length();
		    byte[] bytes = new byte[(int)len];
		    
		    int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	    
	    	
	    	int ind = 0;
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
			 
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 
					
				
					
											
					// Copying R,G & B pixels 	
					green[y][x]=(input[y*352 +x]>>8 &0xff);
					red[y][x]=(input[y*352+x]>>16 &0xff);
					blue[y][x]=(input[y*352+ x] &0xff);
					
					int temp= (int) (0.299*(r & 0xff) + 0.587*(g & 0xff) + 0.114*(b & 0xff));
					//System.out.println("temp=" + temp);
					
					int pixel = 0xff000000 | ((temp & 0xff) << 16) | ((temp & 0xff) << 8) | (temp & 0xff);
			        input[y*352+x]= (temp & 0xff);
			        img.setRGB(x, y, pixel);
			        ind++;
					
					//int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
			        //img.setRGB(x,y,pix);
				}
			}
			
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
		      e.printStackTrace();
		  }
	/*	    JFrame frame = new JFrame();
		    JLabel label = new JLabel(new ImageIcon(img));
		    frame.getContentPane().add(label, BorderLayout.CENTER);
		    frame.pack();
		    frame.setVisible(true);*/
	
		    
}
}
	
