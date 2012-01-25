package com.readytalk.staccato.database.migration.script.sql;

import java.net.URL;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.readytalk.staccato.database.migration.script.Script;

/**
 * Models a sql script.  SQL Scripts currently don't have a comparable implementation.
 */
public class SQLScript implements Script<SQLScript> {

	private String filename;
	private URL url;

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public URL getUrl() {
		return url;
	}

	public void setFilename(final String _filename) {
		this.filename = _filename;
	}

	public void setUrl(final URL _url) {
		this.url = _url;
	}

	@Override
	public int compareTo(SQLScript sqlScript) {
		return new CompareToBuilder().append(filename, sqlScript.getFilename()).build();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof SQLScript)) {
			return false;
		}

		final SQLScript sqlScript = (SQLScript) o;
		
		return new EqualsBuilder().append(filename, sqlScript.getFilename()).build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(filename).build();
	}
}
