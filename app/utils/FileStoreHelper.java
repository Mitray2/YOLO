package utils;

import play.Logger;
import play.Play;

import java.io.File;
import java.io.IOException;

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

    public static File getRssFile() {
        try {
            File rssFolder = new File(getFileStoreFile(), "other/");
            rssFolder.mkdir();
            File rssXml = new File(rssFolder, "rss.xml");
            rssXml.createNewFile();
            return rssXml;
        } catch (IOException e) {
            Logger.error("Cannot find rss.xml", e);
        }
        return null;
    }

}
