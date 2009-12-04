package org.vpac.grisu.model.files;

import org.apache.commons.lang.StringUtils;

public class FileSystemItem {
	
	public enum Type implements Comparable<Type> {
		LOCAL, BOOKMARK, REMOTE
	}
	
	private String alias;
	private Type type;
	private GlazedFile rootFile;
	private boolean isDummy = false;
	
	// create dummies (as seperators)
	public FileSystemItem(Type type, String alias) {
		this.alias = alias;
		this.type = type;
		this.isDummy = true;
	}
	
	public boolean isDummy() {
		return isDummy;
	}
	
	public FileSystemItem(String alias, Type type, GlazedFile file) {
		setAlias(alias);
		this.type = type;
		this.rootFile = file;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		if ( StringUtils.isBlank(alias) ) {
			this.alias = "/";
		} else {
			this.alias = alias;
		}
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public GlazedFile getRootFile() {
		return rootFile;
	}

	public void setRootFile(GlazedFile rootFile) {
		this.rootFile = rootFile;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o instanceof FileSystemItem ) {
			FileSystemItem other = (FileSystemItem)o;
			
			if ( getType().equals(other.getType()) && getAlias().equals(other.getAlias()) ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return 53 * getType().hashCode() + getAlias().hashCode() * 21;
	}
	
	public String toString() {
		return getAlias();
	}


}
