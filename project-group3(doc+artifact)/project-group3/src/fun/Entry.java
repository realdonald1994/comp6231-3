package fun;

import java.io.*;
import java.util.*;
import util.*;

public class Entry
{
	public static ArrayList<Fact> FACTS=new ArrayList<>();
	public static ArrayList<Rule> RULES=new ArrayList<>();
	public static HashMap<String,PredicateLog> PREDICATES=new HashMap<String,PredicateLog>();
	public static String FILE_PATH = "./testcase/";
	public static String FILE_NAME="graph200.pl";
	public static double MANUALLY_FIX_POINT = 1.0;
	public static double RULE_VALUE = 0.5;
	public static double FACT_VALUE = 0.5;
	public static int UPDATE_MODE = 0;// 0: a+b - a*b   1: max(a,b)
	public static int CONJUNCTION_MODE = 0; //0: min 1:multiply
	public static int PROPAGATION_MODE = 1;//0: min  1:multiply
	public static boolean SEMI_NAIVE=false;

	
	public static void main(String[] args) throws SecurityException, IOException
	{
			String fileName = "";
			String conFileName = "";
			System.out.println("Please choose the input file: ");
			System.out.println("There will be a configuration file following each file.");
			System.out.println("For example:  graph10.pl, which the following configuration file is graph10.cf");
			System.out.println("If you want to set the default certainty or calculation mode, please modifed this file before running.");
			System.out.println("1. graph3.pl");
			System.out.println("2. clique100.pl");
			System.out.println("3. graph100.pl");
			System.out.println("4. graph200.pl (may be taking a long time..)");
			System.out.println("5. testcase.pl");
			System.out.println("6. testcase2.pl");
			System.out.println("7. induction10.pl");
			System.out.println("8. assgn3(a).pl");
			System.out.println("9. assgn3(b).pl");
			System.out.println("10. graph10.pl");
			System.out.println("11. graph50.pl");
	        Scanner sc = new Scanner(System.in); 
	        String num = sc.nextLine(); 
	        
	        switch(num){
	        case "1":
	        	fileName ="graph3.pl";
	        	conFileName ="graph3.cf";
	        	break;
	        case "2":
	        	fileName ="clique100.pl";
	        	conFileName ="clique100.cf";
	        	break;
	        case "3":
	        	fileName ="graph100.pl";
	        	conFileName ="graph100.cf";
	        	break;
	        case "4":
	        	fileName ="graph200.pl";
	        	conFileName ="graph200.cf";
	        	break;
	        case "5":
	        	fileName ="testcase.pl";
	        	conFileName ="testcase.cf";;
	        case "6":
	        	fileName ="testcase2.pl";
	        	conFileName ="testcase2.cf";
	        	break;
	        case "7":
	        	fileName ="induction10.pl";
	        	conFileName ="induction10.cf";
	        	break;
	        case "8":
	        	fileName ="assgn3(a).pl";
	        	conFileName ="assgn3(a).cf";
	        	break;
	        case "9":
	        	fileName ="assgn3(b).pl";
	        	conFileName ="assgn3(b).cf";
	        	break;
	        case "10":
	        	fileName ="graph10.pl";
	        	conFileName ="graph10.cf";
	        	break;
	        case "11":
	        	fileName ="graph50.pl";
	        	conFileName ="graph50.cf";
	        	break;
	        default:
	        	System.out.println("Please input a valid number. Please run the program again.");
	        	return;
	        }
	        System.out.println("Please input the evaluation method: 1 for naive, 2 for semi-naive");
	        String mode = sc.nextLine(); 
	        if(mode.equals("1")){
	        	SEMI_NAIVE = false;
	        }
	        else{
	        	SEMI_NAIVE = true;
	        }
	        
	        FILE_NAME = fileName;
	        ReadConfFile(FILE_PATH+conFileName);
	        
			Parcer.Parse();
			Evaluationer.evaluation();
		
	}
	public static void ReadConfFile(String path) throws IOException{
		FileInputStream inputStream = new FileInputStream(path);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
		String str = null;
		while((str = bufferedReader.readLine()) != null)
		{
			String left = str.substring(0, str.indexOf("="));
			String right = str.substring(str.indexOf("=")+1,str.length());
			switch(left){
			case "FACT_VALUE":
				FACT_VALUE = Double.parseDouble(right);
				break;
			case "RULE_VALUE":
				RULE_VALUE = Double.parseDouble(right);
				break;
			case "DISJUNCTION":
				if(right.equals("ind")){
					UPDATE_MODE = 0;
				}
				else{
					UPDATE_MODE = 1;
				}
				break;
			case "CONJUNCTION":
				if(right.equals("min")){
					CONJUNCTION_MODE = 0;
				}
				else{
					CONJUNCTION_MODE = 1;
				}
				break;
			case "PROPAGATION":
				if(right.equals("min")){
					PROPAGATION_MODE = 0;
				}
				else{
					PROPAGATION_MODE = 1;
				}
				break;
			}
		}
			
		//close
		inputStream.close();
		bufferedReader.close();


		
	}

}
