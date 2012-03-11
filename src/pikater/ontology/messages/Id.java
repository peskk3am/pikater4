package pikater.ontology.messages;

import jade.content.Concept;

public class Id  implements Concept{
	static final long serialVersionUID = 1779859178465258533L;
	private String identificator;
	private Id subid;
	
	public Id getSubid() {
		return subid;
	}
	public void setSubid(Id subid) {
		this.subid = subid;
	}
	public String getIdentificator() {
		return identificator;
	}
	public void setIdentificator(String identificator) {
		this.identificator = identificator;
	}
	
	public Id(String identificator){
		this.identificator = identificator;
	}
	
	public Id(){
	}
	
}
