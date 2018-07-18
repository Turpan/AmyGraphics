package lucyAnimation;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import amyGraphics.Animation;
import amyGraphics.Animation.MalformedAnimationException;

public class LucyIO {
	
	private static final String HEADER = "LUCYOG";
	private static final byte[] HEADERDATA = HEADER.getBytes();
	
	private static final String META = "META";
	private static final byte[] METADATA = META.getBytes();
	
	private static final String METAEND = "METAEND";
	private static final byte[] METAENDDATA = METAEND.getBytes();
	
	private static final String FOOTER = "LUCYEND";
	private static final byte[] FOOTERDATA = FOOTER.getBytes();
	
	private static final int BYTELENGTH = 4;
	
	/*
	 * will throw an exception if directory does not exist
	 * lucy file creation program will handle this
	 */
	public static void writeLucyFile(Animation animation, String fileLocation) throws IOException {
		BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(fileLocation));
		BufferedImage image = animation.getSprite();
		byte[] width = intToByte(image.getWidth());
		byte[] height = intToByte(image.getHeight());
		byte[] framewidth = intToByte(animation.getFrameWidth());
		byte[] frameheight = intToByte(animation.getFrameHeight());
		byte[] frameorderlength = intToByte(animation.getFrameOrder().length);
		byte[][] frameorder = new byte[animation.getFrameOrder().length][];
		for (int i=0; i<frameorder.length; i++) {
			frameorder[i] = intToByte(animation.getFrameOrder()[i]);
		}

		//header
		fileWriter.write(HEADERDATA);
		
		//meta data
		fileWriter.write(METADATA);
		fileWriter.write(width);
		fileWriter.write(height);
		fileWriter.write(framewidth);
		fileWriter.write(frameheight);
		fileWriter.write(frameorderlength);
		for (byte[] data : frameorder) {
			fileWriter.write(data);
		}
		fileWriter.write(METAENDDATA);
		
		//image file data
		for (int y=0; y<image.getHeight(); y++) {
			for (int x=0; x<image.getWidth(); x++) {
				byte[] rgba = intToByte(image.getRGB(x, y));
				fileWriter.write(rgba);
			}
		}
		
		//footer
		fileWriter.write(FOOTERDATA);
		
		//finish up
		fileWriter.close();
	}
	
	/*
	 * Any exception is thrown, or if the file is formatted incorrectly, 
	 * this method will return null. The editor will handle the rest
	 */
	public static Animation readLucyFile(String fileLocation) {
		int offset = 0;
		// load file data into array
		byte[] data = new byte[] {0};
		try {
			BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(fileLocation));
			data = fileReader.readAllBytes();
			fileReader.close();
		} catch (IOException e) {
			return null;
		}
		//check if file is not long enough
		if (data.length < HEADERDATA.length) {
			return null;
		}
		
		//check header
		if (!checkSignature(data, offset, HEADERDATA)) {
			return null;
		}
		offset += HEADERDATA.length;
		
		//check metadata
		if (!checkSignature(data, offset, METADATA)) {
			return null;
		}
		offset += METADATA.length;
		
		int width = getByteData(data, offset);
		if (width <= 0) {
			return null;
		}
		offset += BYTELENGTH;
		
		int height = getByteData(data, offset);
		if (height <= 0) {
			return null;
		}
		offset += BYTELENGTH;
		
		int framewidth = getByteData(data, offset);
		if (framewidth <= 0) {
			return null;
		}
		offset += BYTELENGTH;
		
		int frameheight = getByteData(data, offset);
		if (frameheight <= 0) {
			return null;
		}
		offset += BYTELENGTH;
		
		int frameorderlength = getByteData(data, offset);
		if (frameorderlength <= 0) {
			return null;
		}
		offset += BYTELENGTH;
		
		int[] frameorder = new int[frameorderlength];
		for (int i=0; i<frameorderlength; i++) {
			int framedata = getByteData(data, offset);
			if (framedata < 0) {
				return null;
			}
			frameorder[i] = framedata;
			offset += BYTELENGTH;
		}
		
		if (!checkSignature(data, offset, METAENDDATA)) {
			return null;
		}
		offset += METAENDDATA.length;
		
		//Time to load the image data
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				int rgba = getByteData(data, offset);
				image.setRGB(x, y, rgba);
				offset += BYTELENGTH;
			}
		}
		
		//check the footer
		if (!checkSignature(data, offset, FOOTERDATA)) {
			return null;
		}
		
		//put it together
		try {
			Animation animation = new Animation(image, framewidth, frameheight, frameorder);
			//all done
			return animation;
		} catch (MalformedAnimationException e) {
			return null;
		}
	}
	
	private static byte[] intToByte(int num) {
		return new byte[] { 
			(byte)(num >> 24),
			(byte)(num >> 16),
			(byte)(num >> 8),
			(byte)num };
	}
	
	private static int byteToInt(byte[] data) {
		return data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF);
	}
	
	/*
	 * return false if array is not long enough or if data does not match
	 */
	private static boolean checkSignature(byte[] data, int offset, byte[] sig) {
		try {
			for (int i=offset; i< offset + sig.length; i++) {
				if (data[i] != sig[i-offset]) {
					return false;
				}
			}
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	/*
	 * will catch for nullpointer which denotes that the file is cut short too early
	 */
	private static int getByteData(byte[] data, int offset) {
		try {
			byte[] num = new byte[BYTELENGTH];
			for (int i=offset; i<offset+4; i++) {
				num[i-offset] = data[i];
			}
			return byteToInt(num);
		} catch (NullPointerException e) {
			return -1;
		}
	}
	
}
