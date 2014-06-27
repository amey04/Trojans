package SIFT;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;


public class Test
{
public void run(Mat pathObject, Mat pathScene, String pathResult) {

	
    System.out.println("\nRunning FindObject");

    Mat img_object = pathObject; //0 = CV_LOAD_IMAGE_GRAYSCALE
    Mat img_scene = pathScene;

 //   dispImage(pathObject);
  //  dispImage(img_scene);
    
    Highgui.imwrite("logo.jpg", img_object);
   Highgui.imwrite("dede.jpg", img_scene);
    
    
    FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF); //4 = SURF
   
    MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
    MatOfKeyPoint keypoints_scene  = new MatOfKeyPoint();

    detector.detect(img_object, keypoints_object);
    detector.detect(img_scene, keypoints_scene);

    DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SURF); //2 = SURF,3 = SIFT;

    Mat descriptor_object = new Mat();
    Mat descriptor_scene = new Mat() ;

    extractor.compute(img_object, keypoints_object, descriptor_object);
    extractor.compute(img_scene, keypoints_scene, descriptor_scene);

    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED); // 1 = FLANNBASED
    MatOfDMatch matches = new MatOfDMatch();
       
    matcher.match(descriptor_object, descriptor_scene, matches);
    List<DMatch> matchesList = matches.toList();
    

    Double max_dist = 0.0;
    Double min_dist = 100.0;

    for(int i = 0; i < descriptor_object.rows(); i++){
        Double dist = (double) matchesList.get(i).distance;
        if(dist < min_dist) min_dist = dist;
        if(dist > max_dist) max_dist = dist;
    }

    System.out.println("-- Max dist : " + max_dist);
    System.out.println("-- Min dist : " + min_dist);    

    LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
    MatOfDMatch gm = new MatOfDMatch();

    for(int i = 0; i < descriptor_object.rows(); i++){
        if(matchesList.get(i).distance < 3*min_dist ){
            good_matches.addLast(matchesList.get(i));
        }
    }

    gm.fromList(good_matches);

    Mat img_matches = new Mat();
    Features2d.drawMatches(img_object, keypoints_object, img_scene, keypoints_scene, gm, img_matches, new Scalar(0,255,0), new Scalar(0,0,255), new MatOfByte(), 2);
   /* Mat img_matches;
    drawMatches( img_object, keypoints_object, img_scene, keypoints_scene,
                 good_matches, img_matches, Scalar::all(-1), Scalar::all(-1),
                 vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS );*/
    LinkedList<Point> objList = new LinkedList<Point>();
    LinkedList<Point> sceneList = new LinkedList<Point>();

    List<KeyPoint> keypoints_objectList = keypoints_object.toList();
    List<KeyPoint> keypoints_sceneList = keypoints_scene.toList();

    for(int i = 0; i<good_matches.size(); i++){
        objList.addLast(keypoints_objectList.get(good_matches.get(i).queryIdx).pt);
        sceneList.addLast(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt);
    }

    MatOfPoint2f obj = new MatOfPoint2f();
    obj.fromList(objList);

    MatOfPoint2f scene = new MatOfPoint2f();
    scene.fromList(sceneList);
    
    //////////////////////////////////////
    
    Point[] ptarray = scene.toArray();
    System.out.println("Length"+ptarray.length+"       "+ptarray[0].x+"      "+ptarray[0].y);
    for(int i=0; i<ptarray.length; i++)
    {
    	System.out.println("x_coord"+ptarray[i].x+"y_coord"+ptarray[i].y);
    }
    int cnt=0;
    int max_count=0;
    double x_coor, y_coor;
  
    
    for (int i=0; i<288-50; i=i+4)
        {
    	for (int j=0; j<352-50; j=j+4)
    	    {
    		cnt=0;
    		for (int p=0; p<50; p++)
    		    {
    			for (int q=0; q<50; q++)
    			    {
    				//System.out.println("In");
    				for (int r=0; r< ptarray.length; r++)
    				    {
    					  if ((((int)ptarray[r].x) == (p+i)) && (((int)ptarray[r].y)==(q+j)))	
    					      {//System.out.println("Match");
    						//    System.out.println(ptarray[r].x + "     " +ptarray[r].y);
    						    cnt++;
    					      }
    				    }
    				
    			   }
      		   }
    	//	System.out.println("BLOCK DONE");
    		if(cnt > max_count)
			{ 
				max_count = cnt;
				System.out.println(i + "    " + j);
				System.out.println("cnt" + cnt + "    ");
			}
    	}
    }
    
    
    
    
    
    
 ////////////////////////////////////
    
    

  //  Mat hg = Calib3d.findHomography(obj, scene);

    Mat obj_corners = new Mat(4,1,CvType.CV_32FC2);
    Mat scene_corners = new Mat(4,1,CvType.CV_32FC2);

    obj_corners.put(0, 0, new double[] {0,0});
    obj_corners.put(1, 0, new double[] {img_object.cols(),0});
    obj_corners.put(2, 0, new double[] {img_object.cols(),img_object.rows()});
    obj_corners.put(3, 0, new double[] {0,img_object.rows()});

 //   Core.perspectiveTransform(obj_corners,scene_corners, hg);

   // Core.line(img_matches, new Point(scene_corners.get(0,0)), new Point(scene_corners.get(1,0)), new Scalar(0, 255, 0),4);
   // Core.line(img_matches, new Point(scene_corners.get(1,0)), new Point(scene_corners.get(2,0)), new Scalar(0, 255, 0),4);
   // Core.line(img_matches, new Point(scene_corners.get(2,0)), new Point(scene_corners.get(3,0)), new Scalar(0, 255, 0),4);
   // Core.line(img_matches, new Point(scene_corners.get(3,0)), new Point(scene_corners.get(0,0)), new Scalar(0, 255, 0),4);

    System.out.println(String.format("Writing %s", pathResult));
    Highgui.imwrite(pathResult, img_matches);
}

/*
void dispImage (Mat m)
{
	 int type = BufferedImage.TYPE_BYTE_GRAY;
     if ( m.channels() > 1 ) {
         type = BufferedImage.TYPE_3BYTE_BGR;
     }
    // System.out.println(m.channels());
     int bufferSize = m.channels()*m.cols()*m.rows();
     //System.out.println(bufferSize);
     byte [] b = new byte[bufferSize];
     m.get(0,0,b); // get all the pixels
     
     BufferedImage image = new BufferedImage(m.cols(),m.rows(),type);
   //  System.out.println("Rows=" + m.cols() + "Cols =" + m.rows());
     final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
     System.arraycopy(b, 0, targetPixels, 0, b.length);  
     System.out.println(targetPixels[0]);
     // System.out.println(targetPixels.length);
    
     for (int p=0; p<288; p++){
    	 for (int q=0; q<352; q++)
    	 {
    		 int temp1 = (int) 0xff000000 | ((targetPixels[p*352+q] & 0xff) << 16) | ((targetPixels[p*352+q] & 0xff) << 8)| ((targetPixels[p*352+q] & 0xff));
       		 image.setRGB(q,p, temp1); 
    	 }
     }
    
     JFrame frame = new JFrame();
	 JLabel label = new JLabel(new ImageIcon(image));
	 frame.getContentPane().add(label, BorderLayout.CENTER);
	 frame.pack();
	 frame.setVisible(true);

}*/

public static void main(String[] args) {
	
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	
	SIFT.ImgR logo = new SIFT.ImgR();
	SIFT.ImgR searchImg = new SIFT.ImgR();
	
	Mat H1 = new Mat(288,352,CvType.CV_8U);
	Mat H2 = new Mat(288,352,CvType.CV_8U);
	
	logo.readImage(args[0]);
	searchImg.readImage(args[1]); 
	int cnt=0;
	for(int i=0; i<288; i++)
	   {
		for (int j=0; j<352; j++)
		{
			 H1.put(i,j, logo.input[cnt]);
			 H2.put(i,j, searchImg.input[cnt]);
			 cnt++;
		} 
	   }	
		
   new Test().run(H1, H2, "resultadoFlann.jpg");

}

}
