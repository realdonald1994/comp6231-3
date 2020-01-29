package util;

import java.util.ArrayList;

import fun.Entry;

public class Rule
{

	public Literal head=null;
	public ArrayList<Literal> body=new ArrayList<Literal>();
	public boolean isConstant = false;
	public double value = Entry.RULE_VALUE;
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isConstant() {
		return isConstant;
	}

	public void setConstant(boolean isConstant) {
		this.isConstant = isConstant;
	}

	public Rule()
	{
		
	}
	
	public Rule(Literal hea,ArrayList<Literal> bod)
	{
	
		this.head=hea;
		this.body=bod;
	}
}
