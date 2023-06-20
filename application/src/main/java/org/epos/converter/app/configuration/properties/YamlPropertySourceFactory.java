package org.epos.converter.app.configuration.properties;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

public class YamlPropertySourceFactory implements PropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource endodedResource) throws IOException 
	{
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(endodedResource.getResource());

        Properties properties = factory.getObject();

        return new PropertiesPropertySource(endodedResource.getResource().getFilename(), properties);
	}

}
