package org.epos.converter.app.plugin.managment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.epos.converter.app.configuration.properties.PluginsConfigProperties;
import org.epos.converter.app.plugin.managment.exception.PluginInstallationException;
import org.epos.converter.app.plugin.managment.model.PluginHeaderDescriptor;
import org.epos.converter.app.plugin.managment.model.PluginKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * To obtain a plugin's artifact the assumption is made that it is of type ICS-V1 (see {@link PluginsMode}).
 * The repository is assumed to be a Git repo, and when secured it is assumed to be hosted on GitLab.
 * 
 */
@Repository
@EnableConfigurationProperties(PluginsConfigProperties.class)
public class PluginsFileManagerJavaICSV1 implements PluginsFileManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(PluginsFileManagerJavaICSV1.class);
	
	private final PluginsConfigProperties pluginsConfigProps;

	@Autowired
	public PluginsFileManagerJavaICSV1(PluginsConfigProperties pluginsConfigProps) {
		this.pluginsConfigProps = pluginsConfigProps;
	}

	static URL generateSecuredGitLabRepoUrl(URL repoUrl, String repoToken) throws MalformedURLException 
	{	
		String httpUrlStr = repoUrl.toExternalForm();
		
		return UriComponentsBuilder.fromHttpUrl(httpUrlStr)
			.userInfo("gitlab-ci-token:" + repoToken)
			.build().toUri().toURL();
	}
	
	@Override
	public boolean isPluginInstalled(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor) 
	{
		Path pluginInstallLoc = getPluginInstallLocation(pluginKey, pluginDescriptor);
		
		LOG.debug("PLUGLOC '{}' EXISTS? {}", pluginInstallLoc.toAbsolutePath().toString(), Files.exists(pluginInstallLoc));
		
		return pluginDescriptor.getRepoArtifactLocations().stream()
				.map(p -> pluginInstallLoc.resolve(p))
				.allMatch(Files::exists);
	}
	
	@Override
	public Path getPluginInstallLocation(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor) 
	{
		Path pluginsInstallLoc = pluginsConfigProps.getPluginsInstallationLocation();
		return pluginsInstallLoc.resolve(pluginDescriptor.getInstallLocation());
	}
	
	@Override
	public void install(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor) throws PluginInstallationException 
	{
		URL repoUrl = pluginDescriptor.getRepoLocation();
		install(pluginKey, pluginDescriptor, repoUrl);
	}
	
	@Override
	public void install(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor, String accessToken) throws PluginInstallationException 
	{
		URL repoLocation = pluginDescriptor.getRepoLocation();
		try {
			URL repoUrl = (accessToken!=null)?generateSecuredGitLabRepoUrl(repoLocation, accessToken): repoLocation;			
			install(pluginKey, pluginDescriptor, repoUrl);
		} catch (MalformedURLException e) {
			String errMsg = String.format(
					"Failed to generate GitLab URL with CI access token so as to clone project located at %s",
					repoLocation.toExternalForm());
			throw new PluginInstallationException(errMsg, e);
		}
	}
	
	private void install(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor, URL repoUrl) throws PluginInstallationException 
	{
		Path relativePluginLoc = Path.of(pluginDescriptor.getInstallLocation());
		
		Path gitCloneLocation = createCloneLocation(relativePluginLoc);		
		clonePluginRepo(repoUrl, gitCloneLocation, pluginKey.getVersion());
		
		Path pluginInstallLoc = createPluginInstallLocation(relativePluginLoc);

		List<String> repoArtifactRelLocations = pluginDescriptor.getRepoArtifactLocations();
		copyArtifactsFromClonedRepo(gitCloneLocation, pluginInstallLoc, repoArtifactRelLocations);
		
		removeCloneLocation();
	}

	private void removeCloneLocation() 
	{
		Path cloneLocation = getRootPluginCloneLocation();
		
		try {
			Files.walkFileTree(cloneLocation, new SimpleFileVisitor<Path>() {
				   @Override
				   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					   Files.setAttribute(file, "dos:readonly", false);	// required when running on Windows env
				       Files.delete(file);
				       return FileVisitResult.CONTINUE;
				   }

				   @Override
				   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				       Files.delete(dir);
				       LOG.debug("Clone directory removed ({})", dir.toAbsolutePath().toString());
				       return FileVisitResult.CONTINUE;
				   }
				});
		} catch (IOException e) {
			String warnMsg = String.format("Failed to completely remove clone location, '%s'", cloneLocation);
			LOG.warn(warnMsg, e);
		}
	}

	/**
	 * Copies artifacts from Git clone location to plugin install location
	 * 
	 * @param cloneLocation
	 * @param pluginInstallLoc
	 * @param repoArtifactRelLocs
	 * @throws PluginInstallationException 
	 */
	private void copyArtifactsFromClonedRepo(Path cloneLocation, Path pluginInstallLoc, List<String> repoArtifactRelLocs) 
			throws PluginInstallationException 
	{
		for (String loc: repoArtifactRelLocs) {
			Path src = cloneLocation.resolve(loc);
			Path dest = pluginInstallLoc.resolve(loc);

			try {
				Files.createDirectories(dest.getParent());
				Files.copy(src, dest);
				if (LOG.isDebugEnabled()) {
					String msg = String.format("Copied plugin artifact '%s' into '%s'",
							src.getFileName(), dest.getParent());
					LOG.debug(msg);
				}
			} catch (IOException e) {
				String errStr = String.format(
						"Failed to copy artifact from clone location to plugin installation location%n"
						+ "  (Clone location: %s)%n"
						+ "  (Plugin Installation location: %s)",
						src.toString(), dest.toString());
				throw new PluginInstallationException(errStr, e);
			}
		}		
	}

	private void clonePluginRepo(URL repoUrl, Path gitCloneLocation, String tag) throws PluginInstallationException 
	{
		final String tagCoordinates = String.format("refs/tags/%s", tag);
		
		try {
			Git result = Git.cloneRepository().setURI(repoUrl.toExternalForm())					
					.setBranchesToClone(Collections.singleton(tagCoordinates))
					.setBranch(tagCoordinates)
					.setDirectory(gitCloneLocation.toFile())
					.call();
			result.close();
		}  catch (GitAPIException e) {
			String errStr = String.format(
					"Failed to clone plugin '%s'",
					repoUrl.toExternalForm());
			throw new PluginInstallationException(errStr);
		}
	}

	private Path createCloneLocation(Path relativePluginLoc) throws PluginInstallationException 
	{
		Path rootPluginCloneLocation = getRootPluginCloneLocation();

		/* Check the proposed root plugin directory does not already exist.
		 *   This root directory will be subsequently deleted: And should be wary of deleting non-cloned data.
		 */
		if (Files.isDirectory(rootPluginCloneLocation)) {
			String errStr = String.format(
					"Failed to create clone location for plugin (%s).%n"
					+ "  Directory already exists. An pre-existing directory cannot be used as a clone location.",					
					rootPluginCloneLocation.toString());
			throw new PluginInstallationException(errStr);
		}
		
		Path pluginCloneLocation = rootPluginCloneLocation.resolve(relativePluginLoc);
		
		try {
			return Files.createDirectories(pluginCloneLocation);
		} catch (IOException e) {
			String errStr = String.format(
					"Failed to create clone location for plugin (%s)",					
					pluginCloneLocation.toString());
			throw new PluginInstallationException(errStr);
		}
	}
	
	private Path createPluginInstallLocation(Path relativePluginLoc) throws PluginInstallationException  
	{
		Path pluginsInstallLoc = pluginsConfigProps.getPluginsInstallationLocation();
		Path pluginInstallLoc = pluginsInstallLoc.resolve(relativePluginLoc);
		
		try {
			return Files.createDirectories(pluginInstallLoc);
		} catch (IOException e) {
			String errStr = String.format(
					"Failed to create install location for plugin (%s)",					
					pluginInstallLoc.toString());
			throw new PluginInstallationException(errStr);
		}
	}

	private Path getRootPluginCloneLocation() 
	{
		Path rootCloneLocation = pluginsConfigProps.getPluginsRootCloneLocation();		
		return rootCloneLocation != null ? 
				rootCloneLocation : pluginsConfigProps.getPluginsInstallationLocation().resolve("GIT_CLONES");
	}

	
	@Override
	public void uninstall(PluginKey pluginKey, PluginHeaderDescriptor pluginDescriptor) {
		// TODO Auto-generated method stub
	}


}
