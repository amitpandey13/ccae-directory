package com.pdgc.general.util.files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.pdgc.general.util.CheckedConsumer;

public abstract class FileUtil {
	
	public static void deleteFile(Path pathToDelete) throws IOException {
		// in case of directory we need to delete all the files and sub-directories recursively
		if (Files.isDirectory(pathToDelete)) {
			Files.walkFileTree(pathToDelete, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if (exc != null) {
						throw exc;
					}
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			Files.delete(pathToDelete);
		}
	}

	/**
	 * Delete all files in the specified directory.
	 * @param directory
	 */
	public static void deleteFilesInDirectory(File directory) {
		if (!directory.isDirectory())
			return;
		
		for(File file : directory.listFiles()) {
			if(!file.isDirectory()) {
				file.delete();
			}
		}
	}
	
	/**
	 * Creates a directory with the specified path if it doesn't already exist.
	 * Returns the File object
	 * @param path
	 */
	public static File createDirectory(String path) {
		File directory = new File(path);
    	if (!directory.exists()) {
    		directory.mkdirs();
    	}
    	return directory;
	}
	
	public static void writeBytesToFile(byte[] bytes, File file) throws IOException {
		file.createNewFile();
		try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
    		fileOutputStream.write(bytes);
    		fileOutputStream.close();
    	}
	}
	
	public static byte[] readBytesFromFile(File file) throws IOException {
		if(!file.exists() || !file.isFile())
			return null;
		
		try(FileInputStream fileInputStream = new FileInputStream(file)){
			byte[] bytes = new byte[(int)file.length()];
			fileInputStream.read(bytes);
			fileInputStream.close();
			return bytes;
		}
	}

	public static byte[] readAllBytes(InputStream is) throws IOException {
		int nRead;
		byte[] data = new byte[16384];
	
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		return buffer.toByteArray();
	}

	public static byte[] readAllBytesFromResource(Class<?> clazz, String resourceName) throws IOException {
		try (final InputStream is = clazz.getResourceAsStream(resourceName)) {
			if (is != null) {
				return readAllBytes(is);
			} else {
				throw new IllegalArgumentException("Cannot find resource: " + resourceName);
			}
		}
	}

	/**
	 * Performs the specified action on all the files/sub-directories contained in the directory passed. It does not traverse the file tree
	 * further.
	 * 
	 * Files.list generates a DirectoryStream which locks the Directory. Using try with resources ensures that the lock is released after
	 * method call
	 */
	public static void performActionOnAllFilesInDirectory(Path directory, CheckedConsumer<Path> consumer) throws IOException {
		if (Files.isDirectory(directory)) {
			try (Stream<Path> files = Files.list(directory)) {
				for (Path file : files.collect(Collectors.toList())) {
					consumer.action(file);
				}
			}
		}
	}
	
	/**
	 * Applies the specified function to all the files/sub-directories contained in the directory passed. It does not traverse the file tree further.
	 * 
	 * Files.list generates a DirectoryStream which locks the Directory. Using try with resources ensures that the lock is released after
	 * method call
	 * 
	 * Differs from performActionOnAllFilesInDirectory in that this method returns 
	 * 
	 * @param directory
	 * @param function
	 * @return
	 * @throws IOException
	 */
	public static <R> R applyFunctionToStreamOfAllFilesInDirectory(Path directory, Function<Stream<Path>, R> function) throws IOException {
		try (Stream<Path> files = Files.list(directory)) {
			return function.apply(files);
		}
	}
	
	public static void deleteDirectory(File directory) throws IOException {
		if (directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				deleteDirectory(file);
			}
		}
		
		Files.deleteIfExists(directory.toPath());
	}

	public static int countMatchingFiles(Stream<Path> files, Predicate<Path> filter) {
		return (int) files.filter(filter).count();
	}

	public static String getFileExtension(String path) {
		Path file = FileSystems.getDefault().getPath(path);
		String fileName = file.getFileName().toString();
		int index = fileName.lastIndexOf(".");
		if (index > 0 && index < fileName.length()) {
			return fileName.substring(index + 1);
		}
		return null;
	}
}