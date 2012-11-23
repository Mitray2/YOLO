package utils;

import java.io.File;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;

public class ImageUtil {

	public static void createThumb(File from, File to, Integer size) throws ImageConvertException {
		//String size1 = "x" + (size * 2);
		//String size2 = "" + size + "x" + size + "+0+0";
		//createThumb(from, to, "-thumbnail", size1, "-resize", "50%", "-gravity", "center", "-crop", size2, "+repage", "-quality", "100", "-format", "jpg");
		//createThumb(from, to, "-thumbnail", "x200", "-resize", "50%", "-gravity", "center", "-crop", "100x100+0+0", "+repage", "-quality", "100", "-format", "jpg");
		
		createThumb(from, to, "-thumbnail", size + "x" + size + "^", "-gravity", "center", "-extent", size + "x" + size, "-quality", "100", "-format", "jpg");
	}
	
	public static void createAvatar(File from, File to, Integer size) throws ImageConvertException {
		String sizeS = size + "x" + size + "!";
		createThumb(from, to, "-thumbnail", sizeS, "-quality", "100", "-format", "jpg");
	}
	
	private static void createThumb(File from, File to, String... args) throws ImageConvertException {
		ConvertCmd cmd = new ConvertCmd();
		//cmd.setSearchPath("D:\\Programs\\ImageMagick-6.8.0-Q16\\");
		cmd.setSearchPath("/usr/bin/");
		IMOperation op = new IMOperation();
		op.addImage(from.getAbsolutePath());
		op.addRawArgs(args);
		op.addImage(to.getAbsolutePath());
		try {
			cmd.run(op);
		} catch (Exception e) {
			throw new ImageConvertException(e);
		}
	}
	
	public static class ImageConvertException extends Exception {

		public ImageConvertException() {
			super();
		}

		public ImageConvertException(String message, Throwable cause) {
			super(message, cause);
		}

		public ImageConvertException(String message) {
			super(message);
		}

		public ImageConvertException(Throwable cause) {
			super(cause);
		}
		
	}
	
	public static void main(String[] args) {
		try {
			ImageUtil.createAvatar(new File("D:\\temp\\duck.png"), new File("D:\\temp\\duck_a_55.png"), 55);
			ImageUtil.createAvatar(new File("D:\\temp\\duck.png"), new File("D:\\temp\\duck_a_36.png"), 36);
			ImageUtil.createAvatar(new File("D:\\temp\\duck.png"), new File("D:\\temp\\duck_a_27.png"), 27);
			ImageUtil.createThumb(new File("D:\\temp\\duck.png"), new File("D:\\temp\\duck_t_100.png"), 100);
			ImageUtil.createThumb(new File("D:\\temp\\duck.png"), new File("D:\\temp\\duck_t_55.png"), 55);
			ImageUtil.createThumb(new File("D:\\temp\\duck.png"), new File("D:\\temp\\duck_t_36.png"), 36);
			ImageUtil.createThumb(new File("D:\\temp\\duck.png"), new File("D:\\temp\\duck_t_27.png"), 27);
		} catch (ImageConvertException e) {
			e.printStackTrace();
		}
	}
	
}
