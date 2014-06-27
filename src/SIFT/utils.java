package SIFT;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public  class utils {
	
	
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
	

