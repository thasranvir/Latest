package uk.co.anmedia.main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.FileLoadDescriptor;

import com.sun.media.jai.codec.SeekableStream;

public class ImageProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "C:\\Users\\admin\\TVL\\new\\";
		String filename = "FatheroftheBride2.jpg";
		File f = new File(path+filename);
//		compressFile(path, f, filename);
	}
	private void compressFile(String realPath, File in, String fileName) {
		
		BufferedImage input = null;
		
		try {
		
		if (fileName.endsWith(".jpg") || fileName.endsWith(".JPG")) {
		RenderedImage img1 = (RenderedImage) JAI.create("fileload",	in.getAbsolutePath());
		
			input = getBufferedImage(fromRenderedToBuffered(img1));
			} else if (fileName.endsWith(".gif") || fileName.endsWith(".GIF")) {
		
				RenderedOp img1 = FileLoadDescriptor.create(in.getAbsolutePath(), null, null, null);
		
			      input = getBufferedImage(img1.getAsBufferedImage());
		
		} else if (fileName.endsWith(".bmp") || fileName.endsWith(".BMP")) {
		
				//  Wrap the InputStream in a SeekableStream.
				InputStream is;
				try {
				
					is = new FileInputStream(in);
				SeekableStream s = SeekableStream.wrapInputStream(is, false);
		
				// Create the ParameterBlock and add the SeekableStream to it.
				ParameterBlock pb = new ParameterBlock();
				pb.add(s);
		
				// Perform the BMP operation
				RenderedOp img1 = JAI.create("BMP", pb);
		
				input = getBufferedImage(img1.getAsBufferedImage());
		
				is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	    	} else if (fileName.endsWith(".png") || fileName.endsWith(".PNG")) {
	
	     		// Wrap the InputStream in a SeekableStream.
	     		InputStream is;
			try {
				is = new FileInputStream(in);
				SeekableStream s = SeekableStream.wrapInputStream(is, false);
		
				// Create the ParameterBlock and add the SeekableStream to it.
				ParameterBlock pb = new ParameterBlock();	
				pb.add(s);
				
				// Perform the PNG operation
				RenderedOp img1 = JAI.create("PNG", pb);
		
				input = getBufferedImage(img1.getAsBufferedImage());
		
				is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	    }
		
		if (input == null)
			return;
		
		// Get Writer and set compression
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
		
			if (iter.hasNext()) {
				ImageWriter writer = (ImageWriter) iter.next();
				ImageWriteParam iwp = writer.getDefaultWriteParam();
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				float values[] = iwp.getCompressionQualityValues();
				iwp.setCompressionQuality(values[2]);
				String newName = realPath + "/" + "Compress"+2+ getFileName(fileName);
				File outFile = new File(newName);
				FileImageOutputStream output;
		
				output = new FileImageOutputStream(outFile);
				writer.setOutput(output);
				IIOImage image =	new IIOImage(input, null, null);
				System.out.println("Writing "+ values[2] + "%" + " index : "+2);
				writer.write(null, image, iwp);
				output.flush();
				output.close();
				outFile = null;
				image = null;
				output = null;
				input.flush();
				input = null;
				writer.dispose();
				writer = null;
			}
		} catch (FileNotFoundException finfExcp) {
				System.out.println(finfExcp);
			} catch (IOException ioExcp) {
				System.out.println(ioExcp);
			}
		}
		
		private BufferedImage getBufferedImage(Image img) {
	
			// create a new BufferedImage and draw the original image on it
			int w = img.getWidth(null);
			int h = img.getHeight(null);
			int thumbWidth = 480;
			int thumbHeight = 270;
	
		    // if width is less than 480 keep the width as it is.
			if (w < thumbWidth)
				thumbWidth = w;
	
			// if height is less than 270 keep the height as it is.
			if (h < thumbHeight)
				thumbHeight = h;
	
			//if less than 330*250 then do not compress
			if (w > 480 || h > 270) {
	
				double imageRatio = (double) w / (double) h;
				double thumbRatio = (double) thumbWidth / (double) thumbWidth;
	
				if (thumbRatio < imageRatio) {
					thumbHeight = (int) (thumbWidth / imageRatio);
				} else {
					thumbWidth = (int) (thumbHeight * imageRatio);
				}
			}
			// draw original image to thumbnail image object and
			// scale it to the new size on-the-fly
			BufferedImage bi = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bi.createGraphics();
			g2d.drawImage(img, 0, 0, thumbWidth, thumbHeight, null);
			g2d.dispose();
			return bi;
		}
	
		public BufferedImage fromRenderedToBuffered(RenderedImage img) {
			if (img instanceof BufferedImage) {
				return (BufferedImage) img;
			}
			ColorModel cm = img.getColorModel();
			int w  = img.getWidth();
			int h  = img.getHeight();
			WritableRaster raster = cm.createCompatibleWritableRaster(w,h);
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			Hashtable<String, Object> props = new Hashtable<String, Object>();
			String[] keys = img.getPropertyNames();
	
			if (keys != null) {
				for (int i = 0 ; i < keys.length ; i++) {
					props.put(keys[i], img.getProperty(keys[i]));
				}
			}
			BufferedImage ret = new BufferedImage(cm, raster, isAlphaPremultiplied, props);
			img.copyData(raster);
			cm = null;
			return ret;
		}
	
		/**
		 * @param fileName
		 * @return
		 */
		private String getFileName(String fileName) {
			String filName = fileName;
			if(!filName.endsWith(".jpg")) {
				if (filName.endsWith(".bmp")) {
					filName = filName.replaceAll(".bmp", ".jpg");
				}
				if (filName.endsWith(".jpeg")) {
					filName = filName.replaceAll(".jpeg", ".jpg");
				}
				if (filName.endsWith(".png")) {
					filName = filName.replaceAll(".png", ".jpg");
				}
				if (filName.endsWith(".gif")) {
					filName = filName.replaceAll(".gif", ".jpg");
				}
			}
			return filName;
		}
}
