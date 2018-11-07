package eu.vdrm.utils.cmdline;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import eu.vdrm.utils.FileUtils;

public class FileActions {
	private static final Scanner scanner = new Scanner(System.in);

	public void doNewest() {
		Common.out("What is de root?");
		 String root = scanner.next();
		 try {
			 Path newestFile = FileUtils.getNewestFile(FileSystems.getDefault().getPath(root));
			 Common.out("Newest file is:");
			 Common.out(newestFile.toString() + " - " + Files.getLastModifiedTime(newestFile));
		} catch (IOException e) {
			 Common.out("Error " + e);
			 e.printStackTrace(System.out);
		}
	}
}
