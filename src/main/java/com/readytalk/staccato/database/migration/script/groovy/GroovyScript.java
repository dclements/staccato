package com.readytalk.staccato.database.migration.script.groovy;

import java.net.URL;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import com.readytalk.staccato.utils.Version;

/**
 * Models a groovy script (groovy, etc).
 *
 * GroovyScript equality and comparability is determined by comparing the script date.
 */
public class GroovyScript implements DynamicLanguageScript<GroovyScript> {

	private String filename;

	private Class<?> scriptClass;

	private Object scriptInstance;

	private URL url;

	private DateTime scriptDate;

	private Version scriptVersion;

	private Version databaseVersion;

	private String sha1Hash;

	private DatabaseType databaseType;

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public DateTime getScriptDate() {
		return scriptDate;
	}

	@Override
	public Class<?> getScriptClass() {
		return scriptClass;
	}

	@Override
	public Object getScriptInstance() {
		return scriptInstance;
	}

	@Override
	public Version getScriptVersion() {
		return scriptVersion;
	}

	@Override
	public Version getDatabaseVersion() {
		return databaseVersion;
	}

	@Override
	public DatabaseType getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseVersion(final Version _databaseVersion) {
		this.databaseVersion = _databaseVersion;
	}

	@Override
	public String getSHA1Hash() {
		return sha1Hash;
	}

	public void setFilename(final String _filename) {
		this.filename = _filename;
	}

	public void setScriptClass(final Class<?> _scriptClass) {
		this.scriptClass = _scriptClass;
	}

	public void setScriptDate(final DateTime _scriptDate) {
		this.scriptDate = _scriptDate;
	}

	public void setScriptInstance(final Object _scriptInstance) {
		this.scriptInstance = _scriptInstance;
	}

	public void setUrl(final URL _url) {
		this.url = _url;
	}

	public void setScriptVersion(final Version _scriptVersion) {
		this.scriptVersion = _scriptVersion;
	}

	public void setSha1Hash(final String _sha1Hash) {
		this.sha1Hash = _sha1Hash;
	}

	public void setDatabaseType(final DatabaseType _databaseType) {
		this.databaseType = _databaseType;
	}

	@Override
	public int compareTo(final GroovyScript groovyScript) {
		if (groovyScript.databaseType != null && this.databaseType != null &&
				!groovyScript.databaseType.equals(this.databaseType)) {
			return 1;
		} else {
			return groovyScript.getScriptDate().compareTo(this.getScriptDate());
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof GroovyScript)) {
			return false;
		}

		GroovyScript that = (GroovyScript) o;
		
		return new EqualsBuilder()
			.append(databaseType, that.databaseType).append(scriptDate, that.scriptDate).isEquals();
	}

	@Override
	public int hashCode() {
		
		return new HashCodeBuilder(31, 37)
				.append(scriptDate)
				.append(databaseType).hashCode();
	}

	@Override
	public String toString() {
		return filename;
	}
}
