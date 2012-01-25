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

	public void setFilename(final String _filename) {
		this.filename = _filename;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(final URL _url) {
		this.url = _url;
	}

	public ResourceType getType() {
		return type;
	}

	public void setType(final ResourceType _type) {
		this.type = _type;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof Resource)) {
			return false;
		}

		Resource resource = (Resource) o;
		
		return new EqualsBuilder()
			.append(filename, resource.getFilename())
			.append(url, resource.getUrl()).build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(filename).append(url).build();
	}
}
