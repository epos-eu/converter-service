package org.epos.converter.common.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.epos.converter.common.type.ContentFormat;

public class TestUtils {
	
	public static final PathMatcher JSON_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.json");
	public static final PathMatcher XML_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.xml");

	public static final PathMatcher JAR_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.jar");
	
	public static final Path PARENT_ROOT_TEST_RESOURCES_PATH = Paths.get("..", "src", "test", "resources");
	public static final Path MODULE_ROOT_TEST_RESOURCES_PATH = Paths.get(".", "src", "test", "resources");
		
	public static Stream<Path> getResourcePaths(Path relativeTestResourcesPath, ContentFormat format, boolean useParentProjectAsRoot) throws IOException {
		
		PathMatcher matcher;
		
		switch (format) {
			case XML  : {
				matcher = XML_FILE_MATCHER;
				break;
			}
			case JSON : {
				matcher = JSON_FILE_MATCHER;
				break;			
			}
			default : {
				throw new IllegalArgumentException();
			}
		}
		
		return getResourcePaths(relativeTestResourcesPath, matcher, useParentProjectAsRoot);
	}
	
	public static String readContentToString(Path testResource) throws IOException 
	{		
//		try (BufferedReader reader = new BufferedReader(
//				new InputStreamReader(new FileInputStream(testResource.toFile())))) {
//			
//			return reader.lines()
//					.parallel()
//					.collect(Collectors.joining(System.lineSeparator()));
//		}
		return Files.lines(testResource)
					.collect(Collectors.joining(System.lineSeparator()));
	}

	
	public static String readContentToString(Path testResource, Charset charSet) throws IOException 
	{		
//		try (BufferedReader reader = new BufferedReader(
//				new InputStreamReader(new FileInputStream(testResource.toFile())))) {
//			
//			return reader.lines()
//					.parallel()
//					.collect(Collectors.joining(System.lineSeparator()));
//		}
		return Files.lines(testResource, charSet)	
					.parallel()
					.collect(Collectors.joining(System.lineSeparator()));
	}
	
	public static Path getAbsoluteResourcePath(Path relativeTestResourcesPath, boolean useParentProjectAsRoot) throws IOException {
		
		Path rootLocation = useParentProjectAsRoot ? PARENT_ROOT_TEST_RESOURCES_PATH : MODULE_ROOT_TEST_RESOURCES_PATH;
		
		if (Files.notExists(rootLocation)) {				
			throw new IOException(String.format("Cannot find root directory for test resources '%s'", rootLocation.toString()));
		}
		
		Path absoluteTestResourcePath = rootLocation.resolve(relativeTestResourcesPath);
		
		if (Files.notExists(absoluteTestResourcePath)) {
			throw new IOException(String.format("Cannot find relative directory for test resources '%s'", absoluteTestResourcePath.toString()));
		}
		
		return absoluteTestResourcePath;		
	}
	
	
	private static Stream<Path> getResourcePaths(Path relativeTestResourcesPath, PathMatcher pathMatcher, boolean useParentProjectAsRoot) throws IOException {
				
		try {
			Path resourcesPath = getAbsoluteResourcePath(relativeTestResourcesPath, useParentProjectAsRoot);
			return Files.list(resourcesPath)
					.filter(Files::isRegularFile)
					.filter(p -> pathMatcher.matches(p))
					.sorted();
		} catch (IOException e) {
			// Log ERROR (e.getMessage())
			throw e;
		}		
	}
	
	public static String findNameOfTargetJar(String targetPluginLocation) {
		
		Path normalisedTargetPluginLocation = Paths.get(targetPluginLocation).normalize();
		
		try {
			Set<Path> jarFiles = Files.list(normalisedTargetPluginLocation)
					.filter(Files::isRegularFile)
					.filter(JAR_FILE_MATCHER::matches)
					.collect(Collectors.toSet());
			if (jarFiles.size() != 1) {
				throw new IllegalStateException(String.format("Could not determine target plugin to invoke from location, '%s'%n "
						+ "There are %d jars at this location.", targetPluginLocation, jarFiles.size()));
			}
			return jarFiles.iterator().next().getFileName().toString();
		} catch (IOException e) {
			throw new IllegalStateException(String.format("Could not determine location of the target plugin, %s", targetPluginLocation));
		}
	}
	

}
