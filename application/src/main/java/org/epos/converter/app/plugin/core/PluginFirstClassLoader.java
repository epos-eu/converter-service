package org.epos.converter.app.plugin.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * @author patk
 *
 * Taken from https://gist.github.com/reda-alaoui/a3030964293268eca48ddc66d8a07d74
 */
public class PluginFirstClassLoader extends URLClassLoader {
	
	private ClassLoader system;

	public PluginFirstClassLoader(URL[] classpath, ClassLoader parent) {
		super(classpath, parent);
		system = getSystemClassLoader();
	}

	@Override
	protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
		
		// has it already been loaded?
		Class<?> clazz = findLoadedClass(className);
		
		// class loading
		if (clazz == null) {
			try {
				// attempt class loading using specified classpath
				clazz = findClass(className);
			} catch (ClassNotFoundException ex) {
				// attempt class loading using parent classloader
				try {
					clazz = super.loadClass(className, resolve);
				} catch (ClassNotFoundException _ex) {
					// attempt class loading using system classloader
					if (system != null) {
						clazz = system.loadClass(className);
					}
				}
			}
		}
		
		// if true then try to load all the classes referenced by the class. 
		if (resolve) {
			resolveClass(clazz);
		}
		
		return clazz;
	}

	@Override
	public InputStream getResourceAsStream(String className) {
		
		URL url = getResource(className);
		try {
			return url != null ? url.openStream() : null;
		} catch (IOException e) {
			// do nothing
		}
		return null;		

	}

	@Override
	public URL getResource(String className) {
		URL resource = findResource(className);
		
		if (resource == null) {
			resource = super.getResource(className);
		}

		if (resource == null && system != null) {
			resource = system.getResource(className);
		}
		
		return resource;
	}

	@Override
	public Enumeration<URL> getResources(String className) throws IOException {
		
		Enumeration<URL> systemUrls = null;
		
		if (system != null) {
			systemUrls = system.getResources(className);
		}
		
		Enumeration<URL> localUrls = findResources(className);
		Enumeration<URL> parentUrls = null;
		
		if (getParent() != null) {
			parentUrls = getParent().getResources(className);
		}
		
		final List<URL> urls = new ArrayList<URL>();
		if (localUrls != null) {
			while (localUrls.hasMoreElements()) {
				URL local = localUrls.nextElement();
				urls.add(local);
			}
		}
		if (systemUrls != null) {
			while (systemUrls.hasMoreElements()) {
				urls.add(systemUrls.nextElement());
			}
		}
		if (parentUrls != null) {
			while (parentUrls.hasMoreElements()) {
				urls.add(parentUrls.nextElement());
			}
		}
		return new Enumeration<URL>() {
			Iterator<URL> iter = urls.iterator();

			public boolean hasMoreElements() {
				return iter.hasNext();
			}

			public URL nextElement() {
				return iter.next();
			}
		};
		
	}
	
	

}
