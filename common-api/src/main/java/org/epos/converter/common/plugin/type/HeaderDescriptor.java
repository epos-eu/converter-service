package org.epos.converter.common.plugin.type;

/**
 * @deprecated old DO - closest replacement probably {@link org.epos.converter.app.plugin.managment.model.PluginDescriptor}
 */
public final class HeaderDescriptor {

	String name;
	String id;
	String version;
	String authorName;
	String authorContact;
	
	public HeaderDescriptor(String name, String id, String version, String authorName, String authorContact) {
		super();
		this.name = name;
		this.id = id;
		this.version = version;
		this.authorName = authorName;
		this.authorContact = authorContact;
	}
	
	public String getName() {
		return name;
	}
	public String getId() {
		return id;
	}
	public String getVersion() {
		return version;
	}
	public String getAuthorName() {
		return authorName;
	}
	public String getAuthorContact() {
		return authorContact;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authorContact == null) ? 0 : authorContact.hashCode());
		result = prime * result + ((authorName == null) ? 0 : authorName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HeaderDescriptor other = (HeaderDescriptor) obj;
		if (authorContact == null) {
			if (other.authorContact != null)
				return false;
		} else if (!authorContact.equals(other.authorContact))
			return false;
		if (authorName == null) {
			if (other.authorName != null)
				return false;
		} else if (!authorName.equals(other.authorName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}
