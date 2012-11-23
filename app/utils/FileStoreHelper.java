package utils;

import java.io.File;

import play.Logger;
import play.Play;

public class FileStoreHelper {

	private static File fileStore;

	public static File getFileStoreFile() {
		if (fileStore == null) {
			String folderName = Play.configuration.getProperty(Play.mode.name()
					+ ".application.file.store");
			fileStore = new File(folderName);
			fileStore.mkdirs();
			Logger.info("File Store: %s", fileStore);
		}
		return fileStore;
	}

}
