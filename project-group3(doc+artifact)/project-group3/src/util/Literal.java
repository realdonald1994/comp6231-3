package util;

public class Literal
{
	public String predicate="";
	public String[] varia=null;
	//By Luguang Liu
	public boolean isConstant = false;
	public boolean isConstant() {
		return isConstant;
	}

	public void setConstant(boolean isConstant) {
		this.isConstant = isConstant;
	}
	//End By Luguang Liu
	public Literal()
	{
		
	}
	
	public Literal(String pre,String[] vari)
	{
		this.predicate=pre;
		this.varia=vari;				
	}


}
