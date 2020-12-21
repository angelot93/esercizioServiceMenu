package it.omicron.entity;

//questa classe modella la entity di nome Resource

public class Resource {
	private int id;
	private String type;
	private String version;
	
	
	@Override
	public String toString() {
		return "Resource [id=" + id + ", type=" + type + ", version=" + version + "]";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Resource() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	
}