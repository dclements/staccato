package com.readytalk.staccato.database.migration.script.sql;

import java.net.URL;

import org.apache.commons.lang3.builder.CompareToBuilder;
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

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	@Override
	public int compareTo(SQLScript sqlScript) {
		return new CompareToBuilder().append(filename, sqlScript.getFilename()).build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SQLScript)) return false;

		SQLScript sqlScript = (SQLScript) o;

		if (!filename.equals(sqlScript.filename)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(filename).build();
	}
}
