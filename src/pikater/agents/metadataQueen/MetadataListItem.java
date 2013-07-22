package pikater.agents.metadataQueen;

import jade.util.leap.ArrayList;
import pikater.ontology.messages.Metadata;

public class MetadataListItem {
	private Metadata metadata;
	private int id;
	ArrayList to_compute = new ArrayList();
	
	public MetadataListItem(Metadata _metadata, int _id){
		setMetadata(_metadata);
		setId(_id);
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
