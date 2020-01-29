package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fun.Entry;
import fun.Evaluationer;


public class Fact
{

	public String predicate="";
	public String[] constant=null;
	public double value = Entry.FACT_VALUE;
	public int length = 1;
	public boolean ifNew= false;
	public List<Fact> path;
	public Fact()
	
	{
		path = new ArrayList<>();
	}
	
	public Fact(String pre,String[] consta,double value,int length)
	{
		path = new ArrayList<>();

		this.predicate=pre;
		this.constant=consta;
		this.value = value;
		this.length = length;
	}
	//to generate path calfact
	public Fact(String pre,String[] consta,double value,List<Fact> calfact)
	{
		path = new ArrayList<>();

		this.predicate=pre;
		this.constant=consta;
		this.value = value;
		this.length = 1;
		for(Fact fact:calfact){
			Fact f1 = new Fact(fact);//avoid recursively path
			this.path.add(f1);
		}
	
	}
	//to generate path calfact
	public Fact(Fact fact)
	{
		path = new ArrayList<>();

		this.predicate=fact.predicate;
		this.constant=fact.constant;
		this.value = 0.0;
		this.length = 1;
	
	}
	

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (!(obj instanceof Fact)) {
			return false;

		}
		Fact fact = (Fact) obj;
		if (fact.predicate.equals(this.predicate) && fact.constant.length == this.constant.length) {
			
			for (int i = 0; i < this.constant.length; i++) {
				if (!fact.constant[i].equals(this.constant[i])) {
					return false;
				}
			}

		} else {
			return false;
		}
		return true;
	}
	public boolean completeEquals(Object obj) {
		// TODO Auto-generated method stub
		if (!(obj instanceof Fact)) {
			return false;

		}
		Fact fact = (Fact) obj;
		if (fact.predicate.equals(this.predicate) && fact.constant.length == this.constant.length) {
			boolean res = false;
			for (int i = 0; i < this.constant.length; i++) {
				if (!fact.constant[i].equals(this.constant[i])) {
					return false;
				}
			}

		} else {
			return false;
		}
		int equalNum = 0;;
		
		if(fact.path.size()!= this.path.size()){

			return false;
		}
		else{
			for(Fact f1:this.path){
				for(Fact f2:fact.path){
					if(f1.completeEquals(f2)) equalNum++;
				}
				
			}
			
			
		}
		if(equalNum == this.path.size()) return true;
		return false;
	}
	
	public boolean valueEquals(Object obj) {
		// TODO Auto-generated method stub
		if (!(obj instanceof Fact)) {
			return false;

		}
		Fact fact = (Fact) obj;
		if (fact.predicate.equals(this.predicate) && fact.constant.length == this.constant.length) {
			boolean res = false;
			for (int i = 0; i < this.constant.length; i++) {
				if (!fact.constant[i].equals(this.constant[i])) {
					return false;
				}
			}

		} else {
			return false;
		}
		
		if(fact.value != this.value){
			return false;
		}
		else{
			return true;
		}
		
		
	}
	
	
	
}