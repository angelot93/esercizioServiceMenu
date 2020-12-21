package it.omicron.entity;

import java.util.List;



// questa classe modella la entity di MenuNode


public class MenuNode {
	private int nodeId;
	private String nodeName;
	private String nodeType;
	private String flowType;
	private String groupType;
    private Resource resource;
    private List<MenuNode> nodes;
    private int nodeDepth;

    
	@Override
	public String toString() {
		return "MenuNode [nodeId=" + nodeId + ", nodeName=" + nodeName + ", nodeType=" + nodeType + ", flowType="
				+ flowType + ", groupType=" + groupType + ", resource=" + resource + ", nodes=" + nodes + ", nodeDepth="
				+ nodeDepth + "]";
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public List<MenuNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<MenuNode> nodes) {
		this.nodes = nodes;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public int getNodeDepth() {
		return nodeDepth;
	}

	public void setNodeDepth(int nodeDepth) {
		this.nodeDepth = nodeDepth;
	}
    
}