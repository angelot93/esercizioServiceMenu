package it.omicron.entity;

import java.util.List;

// questa classe modella la entity di nome MenuContent

public class MenuContent {
    private String version;
    private List<MenuNode> nodes;

    public MenuContent() {
        super();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

	public List<MenuNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<MenuNode> nodes) {
		this.nodes = nodes;
	}
  
}
