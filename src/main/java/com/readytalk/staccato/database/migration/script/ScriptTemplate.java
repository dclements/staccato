package com.readytalk.staccato.database.migration.script;

import com.readytalk.staccato.utils.Version;

/**
 * Models a script template.
 */
public class ScriptTemplate {

	private String classname;
	private String contents;
	private Version version;

	public String getClassname() {
		return classname;
	}

	public void setClassname(final String _classname) {
		this.classname = _classname;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(final String _contents) {
		this.contents = _contents;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(final Version _version) {
		this.version = _version;
	}

	@Override
	public String toString() {
		return contents;
	}
}
