package controllers;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import modelDTO.AjaxResponse;
import models.User;
import play.Logger;
import play.Play;
import play.db.jpa.Blob;
import play.mvc.Controller;
import utils.FileStoreHelper;
import utils.ImageUtil;
import utils.ImageUtil.ImageConvertException;
import utils.SessionHelper;

public class AvatarController extends BasicController {

	private static final Integer[] sizes = {27, 36, 55, 85, 123};
	
	public static void test() {
		String test = "";
		test += "<form action=\"\\AvatarController\\updateAvatar\" enctype=\"multipart/form-data\" method=\"post\">";
		test += "<input type=\"file\" name=\"newAvatar\" size=\"40\">";
		test += "<input type=\"submit\" value=\"Send\">";
		test += "</form>";
		renderHtml(test);
	}

	public static void updateAvatar(Blob newAvatar) {
		AjaxResponse response = new AjaxResponse();
		checkUserLogged();
		if (newAvatar == null) {
			response.resultCode = 1;
			response.errorDetails = "no.image";
      renderText(new Gson().toJson(response));
		}
		if (newAvatar.length() > 1024 * 1024 * 2) {
			response.resultCode = 1;
			response.errorDetails = "exceeded.image.size";
      renderText(new Gson().toJson(response));
		}
		if (!newAvatar.type().startsWith("image")) {
			response.resultCode = 1;
			response.errorDetails = "invalid.file.format";
      renderText(new Gson().toJson(response));
		}
		File avatarFolder = createAvatarFolder();
		try {
			File originalImageTemp = new File(avatarFolder, File.separator + "newAvatar_o_temp");
			FileUtils.copyInputStreamToFile(newAvatar.get(), originalImageTemp);
			
			for (Integer size : sizes) {
				createAvatar(avatarFolder, originalImageTemp, size);				
			}
			
			Thread.sleep(500);
		} catch (Exception e) {
			Logger.error(e, "can't create user avatar");
			response.resultCode = 1;
			response.errorDetails = "error.avatar.creation";
      renderText(new Gson().toJson(response));
		}
    renderText(new Gson().toJson(response));
	}

	private static void createAvatar(File avatarFolder, File originalImageTemp, Integer size)
			throws ImageConvertException {
		File originalImage = new File(avatarFolder, File.separator + "newAvatar_"+size+".jpg");
		ImageUtil.createThumb(originalImageTemp, originalImage, size);
	}

	public static void approveAvatar() {
		checkUserLogged();
		AjaxResponse response = new AjaxResponse();
		try {
			File avatarFolder = createAvatarFolder();
			
			File originalImage = new File(avatarFolder, File.separator + "newAvatar_o");
			File originalImageTemp = new File(avatarFolder, File.separator + "newAvatar_o_temp");
			FileUtils.copyFile(originalImageTemp, originalImage);
			
			for (Integer size : sizes) {
				updateAvatar(avatarFolder, size);
			}
			
			User user = SessionHelper.getCurrentUser(session);
			if (user.haveAvatar == null || !user.haveAvatar) {
				user = User.findById(user.id);
				user.haveAvatar = true;
				user.save();
			}
		} catch (Exception e) {
			Logger.error(e, "can't update user avatar");
			response.resultCode = 1;
			response.errorDetails = "error.avatar.update";
			renderJSON(response);
		}
		renderJSON(response);
	}

	private static void updateAvatar(File avatarFolder, Integer size) throws IOException {
		File originalImage = new File(avatarFolder, File.separator + "newAvatar_"+size+".jpg");
		File avatar = new File(avatarFolder, File.separator + "avatar_"+size+".jpg");
		FileUtils.copyFile(originalImage, avatar);
	}
	
	private static void checkUserLogged() {
		AjaxResponse response = new AjaxResponse();
		if (SessionHelper.getCurrentUser(session) == null) {
			response.resultCode = 1;
			response.errorDetails = "not.authenticated.user";
			renderJSON(response);
		}
	}

	private static File createAvatarFolder() {
		User user = SessionHelper.getCurrentUser(session);
		if (user != null) {
			File fileStore = FileStoreHelper.getFileStoreFile();
			File avatarStore = new File(fileStore, File.separator + "avatars");
			avatarStore.mkdir();
			Logger.info("Avatar Store: %s", avatarStore);
			File userAvatarDir = new File(avatarStore, File.separator + user.getId().toString());
			userAvatarDir.mkdir();
			return userAvatarDir;
		}
		return null;
	}
	
}
