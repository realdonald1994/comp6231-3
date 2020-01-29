package fun;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fun.Evaluationer;
import util.*;

public class Parcer
{
	//By Luguang Liu
	private static boolean isConstant(String variable){
		int count =0;

		for(int i =0;i<variable.length();i++){
			if(!Character.isUpperCase(variable.charAt(i))){
				count++;
		
			}
		}
		if(count == variable.length()) return true;
		return false;
	}
	//End
	public static void handleSentence(String sentence)
	{
		
		int i=-1;
		i=sentence.indexOf(":-");
		if(i==-1){
			generateFact(sentence);
			
		}
		else{
			generateRule(sentence);
		}
	}
	
	public static void generateFact(String factSentence)
	{
		int left=-1;
		String userValue = null;
		boolean inputValue = false;
		left=factSentence.indexOf('(');
		if(factSentence.contains("<")&&factSentence.contains(">")){
			inputValue = true;
			int lS= factSentence.indexOf("<");
			int rS= factSentence.indexOf(">");
			userValue = factSentence.substring(lS+1,rS);		
		}
		String predicate=factSentence.substring(0, left);
		String[] consta;
		Fact newfact;
		if(inputValue){
			consta=factSentence.substring(left+1, factSentence.indexOf("<")-1).split(",");	
			newfact=new Fact(predicate, consta,Double.parseDouble(userValue),1);
		}
		else{
			consta=factSentence.substring(left+1, factSentence.length()-2).split(",");	
			newfact=new Fact(predicate, consta,Entry.FACT_VALUE,1);
		}
		Entry.FACTS.add(newfact);
	}
	
	public static void generateRule(String ruleSentence)
	{
		int colon=ruleSentence.indexOf(":-");
		String userValue = null;
		boolean inputValue = false;
		Literal head=null;
		head=generateLiteral(ruleSentence.substring(0, colon));
		if(ruleSentence.contains("<")&&ruleSentence.contains(">")){
			inputValue = true;
			int lS= ruleSentence.indexOf("<");
			int rS= ruleSentence.indexOf(">");
			userValue = ruleSentence.substring(lS+1,rS);		
		}
		ArrayList<Literal> body=null;
		Rule newrule;
		if(inputValue){
			body=generateBody(ruleSentence.substring(colon+2, ruleSentence.indexOf("<")));
			newrule=new Rule(head, body);
			newrule.setValue(Double.parseDouble(userValue));
		}
		else{
			body=generateBody(ruleSentence.substring(colon+2, ruleSentence.length()-1));
			newrule=new Rule(head, body);
			newrule.setValue(Entry.RULE_VALUE);
		}
		

		if(head.isConstant){
			newrule.setConstant(true);
		}
		Entry.RULES.add(newrule);
	}
	
	public static Literal generateLiteral(String lit)
	{
		int left=-1;
		left=lit.indexOf("(");

		String predicate=lit.substring(0, left);

		String[] varia=lit.substring(left+1, lit.length()-1).split(",");
		//By Luguang Liu
		boolean isConstant = false;
		for(int i =0;i<varia.length;i++){
			if(isConstant(varia[i])){
				
				isConstant = true;
				break;
			}
		}
		
		Literal literal=new Literal(predicate, varia);
		literal.setConstant(isConstant);//By Luguang Liu
		return literal;
	}
	
	public static ArrayList<Literal> generateBody(String body)
	{
		ArrayList<Literal> lits=new ArrayList<Literal>();
		while (true)
		{
			int right=-1;
			right=body.indexOf(')');
			if(right==-1)
				break;
			Literal subgoal=generateLiteral(body.substring(0, right+1));
			if(subgoal==null)
			{
				return null;
			}

			lits.add(subgoal);

			if(right+2>=body.length())
				break;
			body=body.substring(right+2,body.length());
		}
		return lits;
	}

	public static void Parse()
	{
		List<String> inputSentences=new ArrayList<>();
		try
		{			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Entry.FILE_PATH + Entry.FILE_NAME), "UTF-8"));
			String line = "";
			while ((line = br.readLine()) != null)
			{
				if(line.trim().length()!=0)
					inputSentences.add(line.trim());
			}
		} catch (Exception e){e.printStackTrace();}

		for(int i=0;i<inputSentences.size();i++)
		{
			if(inputSentences.get(i).charAt(0)!='%')
			{
				String str=inputSentences.get(i).trim();
				int zhushi=-1;
				zhushi=str.indexOf('%');
				if(zhushi!=-1) //是注释
					str=str.substring(0, zhushi);
				//charat 返回指定索引出的字符 从0到length-1 但这个if语句为了ship.cdl文件
				if(str.charAt(str.length()-2)==':'&&str.charAt(str.length()-1)=='-')
				{
					
					while(str.charAt(str.length()-1)!='.')
					{
						str=str+inputSentences.get(i+1);
						
						inputSentences.remove(i+1);
						//System.out.println(DatalogProgram.inputSentences.size());
					}
				}
				
				int index=-1;
				index=str.indexOf('\'');
				if(index!=-1)
				{
					boolean f=false;
					for(int j=0;j<str.length();j++)
					{//查找单引号’
						if(str.charAt(j)=='\'')
						{
							f=(f==false)?true:false;
						}
						//edge(1, 2)而不是customer_city(3, 'San Francisco').
						if(str.charAt(j)==' '&&f!=true)
						{
							str=str.substring(0, j)+str.substring(j+1, str.length());
							j--;
						}
					}					
					//Parcer.sentenceCheck(str,i);
				}
				else
				{
					str=str.replaceAll(" ", "");
					//Parcer.sentenceCheck(str.replaceAll(" ", ""),i);
				}
				
			
				handleSentence(str);
			}
		}
		
		
		//predicate check 只需要替换e
		//facts
		for(int i=0;i<Entry.FACTS.size();i++)//??
		{//edge 若 null e, 2, false
			PredicateLog pl=null;
			pl=Entry.PREDICATES.get(Entry.FACTS.get(i).predicate);
			if(pl==null)
			{
				PredicateLog newpl=new PredicateLog("e", Entry.FACTS.get(i).constant.length);
				Entry.PREDICATES.put(Entry.FACTS.get(i).predicate, newpl);
			}
			
		}
		//rules 只需要替换i
		for(int i=0;i<Entry.RULES.size();i++)
		{
			Rule tmp=Entry.RULES.get(i);
			//head
			PredicateLog pl=null;
			pl=Entry.PREDICATES.get(tmp.head.predicate);
			if(pl==null)
			{
				
				PredicateLog newpl=new PredicateLog("i", tmp.head.varia.length);
				Entry.PREDICATES.put(tmp.head.predicate, newpl);
			}
			//body
			for(int j=0;j<tmp.body.size();j++)
			{
				Literal tmpl=tmp.body.get(j);
				PredicateLog pll=null;
				pll=Entry.PREDICATES.get(tmpl.predicate);
				if(pll==null)
				{
					PredicateLog newpll=new PredicateLog("i", tmpl.varia.length);
					Entry.PREDICATES.put(tmpl.predicate, newpll);
				}

			}
			
		}

	}
}