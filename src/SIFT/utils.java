package SIFT;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public  class utils {
	
	public static ArrayList<Mat> readManyImages(String filename) throws IOException{
		
		ArrayList<Mat> names = new ArrayList<Mat>();
		String inputImagePath = new java.io.File( "." ).getCanonicalPath() + "\\" + "source\\";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			Mat readImage = readImage(inputImagePath + line);
			names.add(readImage);
		}
		br.close();
		return names;
		
	}

	public static Mat readImage(String imageName){
		Mat image = Highgui.imread(imageName);
		return image;
	}
	
	public static Mat convertToGray(Mat image){
		Mat destination = new Mat(image.height(), image.width(), image.type());
		Imgproc.cvtColor(image, destination, Imgproc.COLOR_RGBA2GRAY);
		return destination;
	}
	
	public static void showImage(Mat m,String imgStr){
		Highgui.imwrite(imgStr,m);
		JFrame frame = new JFrame("My GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		 
		// Inserts the image icon
		ImageIcon image = new ImageIcon(imgStr);
		frame.setSize(image.getIconWidth()+10,image.getIconHeight()+35);
		// Draw the Image data into the BufferedImage
		JLabel label1 = new JLabel(" ", image, JLabel.CENTER);
		frame.getContentPane().add(label1);
		 
		frame.validate();
		frame.setVisible(true);
		}
	
		public Mat convertToHSV(Mat input){
			Mat baseHSV = new Mat(input.rows(), input.cols(), input.type());
			Imgproc.cvtColor(input, baseHSV, Imgproc.COLOR_BGR2HSV);
			return baseHSV;
		}
		
}
	

