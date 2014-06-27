import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;



public class test {

	public static void main(String [] args)
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Image c1= new Image();
		Image c2= new Image();
		ObjectCounter counter = new ObjectCounter();
		//counter.
		
		//String path_img1 ="C:\\Users\\satoskar\\workspace\\distrib\\set1\\unchanged\\pair_0000_inbound.jpg";
		//String path_img2 ="C:\\Users\\satoskar\\workspace\\distrib\\set1\\unchanged\\pair_0000_outbound.jpg";
		
		String path_img1 = args[0];
		String path_img2 = args[1];
		
		Mat img1 = utils.readImage(path_img1);
		Mat img2 = utils.readImage(path_img2);
		
		Mat dest1 = new Mat(img1.height(), img1.width(), img1.type());
		Mat dest2 = new Mat(img2.height(), img2.width(), img2.type());
		Imgproc.cvtColor(img1, dest1, Imgproc.COLOR_RGB2HLS);
		Imgproc.cvtColor(img2, dest2, Imgproc.COLOR_RGB2HLS);
		
		//utils.showImage(dest1, "test.jpg");
		//utils.showImage(dest2, "test2.jpg");
		
		List<Mat> img_list1 = new ArrayList<Mat>();
		img_list1.add(dest1);
		
		List<Mat> img_list2 = new ArrayList<Mat>();
		img_list2.add(dest2);
		
		MatOfInt histSize=new MatOfInt(50,60);
		MatOfInt channels = new MatOfInt(0, 1);
		MatOfFloat ranges = new MatOfFloat( 0,180,0,256 );

        Mat hist1 = new Mat();
        Mat hist2 = new Mat();
		Imgproc.calcHist(img_list1, channels, new Mat(), hist1, histSize, ranges);
		Imgproc.calcHist(img_list2, channels, new Mat(), hist2, histSize, ranges);
        
		
		double val = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_BHATTACHARYYA);
		
		System.out.println("Comparison score: " +(1-val)*100);
		//System.out.println(val);
		
		
	}
}
