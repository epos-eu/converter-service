package org.epos.converter.common.test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.epos.converter.common.type.ContentFormat;

public class TestResourcesAccessor {
	
	private static final PathMatcher JSON_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.json");
	private static final PathMatcher XML_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.xml");
	
	private static final Path PARENT_ROOT_TEST_RESOURCES_PATH = Paths.get("..", "src", "test", "resources");
	private static final Path ROOT_TEST_RESOURCES_PATH = Paths.get(".", "src", "test", "resources");
	
	private final Path rootLocation;
	
	public TestResourcesAccessor(Path resourceLocation) throws IOException {
		this(resourceLocation, false);
	}
	
	public TestResourcesAccessor(Path relativeResourceLocation, boolean isModuleSource) throws IOException {
		super();
		rootLocation = getAbsoluteResourceLocation(relativeResourceLocation, isModuleSource);
	}
	
	public Stream<Path> getPayloadIncoming(Path inPayloadsPath, ContentFormat inPayloadsContentFormat) throws IOException {
		
		Path[] inPayloadPaths = getAbsResourcePaths(rootLocation.resolve(inPayloadsPath), inPayloadsContentFormat);
		
		return IntStream.range(0, inPayloadPaths.length)
				.peek(i -> {
					String inPayloadPath = inPayloadPaths[i].getFileName().toString();
					
					// check incoming payload filename extension is as expected
					String inFileExt = FilenameUtils.getExtension(inPayloadPath);
					
					if (!inPayloadsContentFormat.getFileExtensions().contains(inFileExt)) {
						String fmtStr = "Unexpected incoming payload filename extension (%s). Available extensions... %s";
						throw new IllegalArgumentException(
									String.format(fmtStr, inFileExt, 
										String.join(", ", inPayloadsContentFormat.getFileExtensions()))
									);
					}
				})
				.mapToObj(i -> inPayloadPaths[i]);
	}
		
	public Stream<Pair<Path, Path>> getPayloadPairs(Path inPayloadsPath, ContentFormat inPayloadsContentFormat,
											Path outPayloadsPath, ContentFormat outPayloadsContentFormat) throws IOException {
		
		Path[] inPayloadPaths = getAbsResourcePaths(rootLocation.resolve(inPayloadsPath), inPayloadsContentFormat);
		Path[] outPayloadPaths = getAbsResourcePaths(rootLocation.resolve(outPayloadsPath), outPayloadsContentFormat);
		
		if (inPayloadPaths.length != outPayloadPaths.length) {
			throw new IllegalStateException(String.format(
					"The number of incoming paylaod samples (%s) does not match the number of outgoing payload samples (%s)", 
					inPayloadPaths.length, outPayloadPaths.length));
		}

		return IntStream.range(0, inPayloadPaths.length)
				.peek(i -> {
					String inPayloadPath = inPayloadPaths[i].getFileName().toString();
					String outPayloadPath = outPayloadPaths[i].getFileName().toString();
					
					// 1) check incoming/outgoing payload base filenames match
					String inFileName = FilenameUtils.getBaseName(inPayloadPath);
					String outFileName = FilenameUtils.getBaseName(outPayloadPath);
					
					if (!inFileName.equals(outFileName)) {
						String failStr = String.format(
								"Incoming and Outgoing payload filenames must match. However, '%s' and '%s' do not!", 
								inFileName, outFileName);
						throw new IllegalArgumentException(failStr);
					}
					
					// 2) check incoming payload filename extension is as expected
					String inFileExt = FilenameUtils.getExtension(inPayloadPath);
					String outFileExt = FilenameUtils.getExtension(outPayloadPath);
					
					if (!inPayloadsContentFormat.getFileExtensions().contains(inFileExt)) {
						String fmtStr = "Unexpected incoming payload filename extension (%s). Available extensions... %s";
						throw new IllegalArgumentException(
									String.format(fmtStr, inFileExt, 
										String.join(", ", inPayloadsContentFormat.getFileExtensions()))
									);
					}
					
					// 3) check outgoing payload filename extension is as expected
					if (!outPayloadsContentFormat.getFileExtensions().contains(outFileExt)) {
						String fmtStr = "Unexpected outgoing payload filename extension (%s). Available extensions... %s";
						throw new IllegalArgumentException(
									String.format(fmtStr, outFileExt, 
										String.join(", ", outPayloadsContentFormat.getFileExtensions()))
									);
					}
				})
				.mapToObj(i -> new ImmutablePair<>(inPayloadPaths[i], outPayloadPaths[i]));
	}
	
	public Stream<Path> getResourcePaths(ContentFormat resourceContentFormat) throws IOException {		
		return getResourcePaths(Paths.get(""), resourceContentFormat);
	}
	
	public Stream<Path> getResourcePaths(Path relativeResourcePath, ContentFormat resourceContentFormat) throws IOException {		
		Path[] payloadPaths = getAbsResourcePaths(rootLocation.resolve(relativeResourcePath), resourceContentFormat);
		return Arrays.stream(payloadPaths);
	}
	
	public Path getResourcePath(Path relativeResourcePath) throws IOException {		
		return getAbsResourcePath(rootLocation.resolve(relativeResourcePath));
	}

	private Path getAbsResourcePath(Path resourceLocation) {
		if (Files.isRegularFile(resourceLocation)) {
			return resourceLocation;
		} else {
			throw new IllegalStateException(String.format("Specified path, '%s', is not a valid file path.", resourceLocation));
		}
	}

	private static Path[] getAbsResourcePaths(Path resourceLocation, ContentFormat contentFormat) throws IOException {
		
		PathMatcher pathMatcher;
		
		switch (contentFormat) {
			case XML  : {
				pathMatcher = XML_FILE_MATCHER;
				break;
			}
			case JSON : {
				pathMatcher = JSON_FILE_MATCHER;
				break;			
			}
			default : {
				throw new IllegalArgumentException();
			}
		}
		
		return Files.list(resourceLocation)
				.filter(Files::isRegularFile)
				.filter(p -> pathMatcher.matches(p))
				.sorted().toArray(Path[]::new);
	}
	
	private static Path getAbsoluteResourceLocation(Path relativeTestResourcesPath, boolean isModuleSource) throws IOException {
		
		Path rootLocation = isModuleSource ? ROOT_TEST_RESOURCES_PATH : PARENT_ROOT_TEST_RESOURCES_PATH;
		
		if (Files.notExists(rootLocation)) {				
			throw new IOException(String.format("Cannot find root directory for test resources '%s'", rootLocation.toString()));
		}
		
		Path absoluteTestResourcePath = rootLocation.resolve(relativeTestResourcesPath);
		
		if (Files.notExists(absoluteTestResourcePath)) {
			throw new IOException(String.format("Cannot find relative directory for test resources '%s'", absoluteTestResourcePath.toString()));
		}
		
		return absoluteTestResourcePath;		
	}


}
