package eu.vdrm.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FileUtils {
	private static final Logger LOG = LogManager.getLogger(FileUtils.class);
	
	public static Path getNewestFile(Path start) {
		if (!Files.exists(start) || !Files.isDirectory(start)) {
			throw new IllegalArgumentException("Path " + start + " does not exist");
		}

		VisitNewestImpl impl = new VisitNewestImpl();
		
		try {
			Files.walkFileTree(start, impl);
		} catch (IOException e) {
			LOG.error("Error looking for newest File " + e, e);
			return null;
		}
		return impl.getFile();
		
	}

	public static class VisitNewestImpl extends  SimpleFileVisitor<Path> {
		private FileTime checkTime;
		private Path foundFile;
		public VisitNewestImpl() {
			checkTime = FileTime.fromMillis(0L);
		}
		
		@Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (checkTime.compareTo(Files.getLastModifiedTime(file)) < 0) {
            	foundFile = file;
            	checkTime = Files.getLastModifiedTime(file);
            }
            return FileVisitResult.CONTINUE;
        }
		
		public Path getFile() {
			return foundFile;
		}
	}
}
