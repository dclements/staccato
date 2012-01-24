package com.readytalk.staccato.utils;

import java.net.URL;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a system resource.
 */
public class Resource {

	private String filename;

	private URL url;

	private ResourceType type;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public ResourceType getType() {
		return type;
	}

	public void setType(ResourceType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Resource)) return false;

		Resource resource = (Resource) o;
		
		return new EqualsBuilder()
			.append(filename, resource.getFilename())
			.append(url, resource.getUrl()).build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(29, 31).append(filename).append(url).build();
	}
}
