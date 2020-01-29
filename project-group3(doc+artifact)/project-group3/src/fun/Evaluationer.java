package fun;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.*;

public class Evaluationer {
	public static ArrayList<Fact> edb;
	public static ArrayList<Fact> idb;
	public static ArrayList<Fact> newidb = new ArrayList<Fact>();
	public static ArrayList<Fact> improvedIdb;
	public static ArrayList<Fact> evaluEdb;

	public static ArrayList<Rule> evaluRules;
	public static ArrayList<Fact> filterFacts;
	public static ArrayList<Fact> multiSet = new ArrayList<Fact>();
	public static Logger logger;
	public static FileHandler fileHandler;

	public static boolean DEBUG_MODE = true;

	public static void evaluation() throws SecurityException, IOException {
		logger = Logger.getLogger("Logger");
		fileHandler = new FileHandler("evaluation.log");
		fileHandler.setFormatter(new util.MyLogFormatter());
		logger.addHandler(fileHandler);
		StringBuilder sb1 = new StringBuilder();
		sb1.append("Start to evaluation\n");
		sb1.append("Evaluation file: " + Entry.FILE_NAME + "\n");
		if (Entry.SEMI_NAIVE) {
			sb1.append("Evaluation method: semi-naive");
		} else {
			sb1.append("Evaluation method: naive");

		}
		if (DEBUG_MODE) {
			logger.info(sb1.toString());
		}

		long startTime = System.currentTimeMillis();

		int loopNum = 1;

		edb = new ArrayList<Fact>();
		idb = new ArrayList<Fact>();
		filterFacts = new ArrayList<>();
		evaluRules = new ArrayList<Rule>();
		for (int i = 0; i < Entry.FACTS.size(); i++) {
			edb.add(Entry.FACTS.get(i));
		}
		for (int i = 0; i < Entry.RULES.size(); i++) {

			evaluRules.add(Entry.RULES.get(i));
		}

		int iteration = 0;
		boolean continu = true;
		int lastReach = 0;
		int thisReach = 0;

		while (continu) {
			thisReach = 0;

			newidb = new ArrayList<Fact>();
			//Test
			
			/**
			 * Test semi-naive
			 */
			if (iteration == 1 && Entry.SEMI_NAIVE)// 绗簩娆¤凯锟�?
			{

				for (int r = 0; r < evaluRules.size(); r++)// 鍙栦竴鏉ule
				{
					Rule currentRule = evaluRules.get(r);
					if (!currentRule.isConstant
							&& Entry.PREDICATES.get(currentRule.body.get(0).predicate).ie.equals("e")
							&& evaluRules.get(r).body.size() == 1) {
						evaluRules.remove(r);
						r--;
					}
				}

			}
			for (int r = 0; r < evaluRules.size(); r++)// 鍙栦竴鏉ule
			{
				Rule currentRule = evaluRules.get(r);
				if (iteration == 0)// 绗竴娆¤凯锟�?
				{
					//
					if (!currentRule.isConstant
							&& Entry.PREDICATES.get(currentRule.body.get(0).predicate).ie.equals("i")) {

						continue;
					} else {
						matching(0, currentRule);

					}
				} else// 闈炵锟�?娆¤凯锟�?
				{
					matching(1, currentRule);
				}
			}
			List<Fact> temp = new ArrayList<>();
			for (int i = 0; i < newidb.size(); i++) {
				Fact fact = new Fact(newidb.get(i));
				fact.value = newidb.get(i).value;
				fact.path = newidb.get(i).path;
				temp.add(fact);
			}
			if(Entry.SEMI_NAIVE){
				for (int i = 0; i < newidb.size(); i++) {
					
					newidb.get(i).ifNew = true;
					for (int j = 0; j < multiSet.size(); j++) {
						if (newidb.get(i).equals(multiSet.get(j))) {
							if(!newidb.get(i).path.equals(multiSet.get(j).path)){

								newidb.get(i).value = UpdateValue(newidb.get(i), multiSet.get(j), Entry.UPDATE_MODE);
								
							}

						}
					}
					
				}
			}
			for (int i = 0; i < temp.size(); i++) {
				Fact fact = new Fact(temp.get(i));
				fact.value = temp.get(i).value;
				fact.path = temp.get(i).path;
				AddtoMultiSet(fact);
			}
			

			for (int i = 0; i < newidb.size(); i++) {
				

				for (int j = i + 1; j < newidb.size(); j++) {
					if (newidb.get(i).equals(newidb.get(j))) {
						newidb.get(i).value = UpdateValue(newidb.get(i), newidb.get(j), Entry.UPDATE_MODE);
						newidb.remove(j);
					}
				}
			}
			int compareCount = 0;


			
			for (int i = 0; i < newidb.size(); i++) {
				if(!Entry.SEMI_NAIVE)
					newidb.get(i).ifNew = true;
				for (int j = 0; j < idb.size(); j++) {
					if (newidb.get(i).equals(idb.get(j)) && (newidb.get(i).value > idb.get(j).value)) {
						idb.remove(j);
						break;

					}
					if (newidb.get(i).equals(idb.get(j)) && (newidb.get(i).value == idb.get(j).value)) {
						newidb.get(i).ifNew = false;

						compareCount++;

						break;

					}
				}
			}
			if (!Entry.SEMI_NAIVE&&(compareCount == newidb.size()) && (compareCount == idb.size())) {
				continu = false;
			}
			if (Entry.SEMI_NAIVE&&(compareCount == newidb.size())) {
				continu = false;
			}
		
			if (!Entry.SEMI_NAIVE)
			{
				idb.clear();
				for (int i = 0; i < newidb.size(); i++) {
					idb.add(newidb.get(i));
				}
			}
			else {
				for (int i = 0; i < newidb.size(); i++) {
					for(int j =0;j<idb.size();j++) {
						if(newidb.get(i).equals(idb.get(j))) {
							idb.remove(j);
							break;
						}
					}
					idb.add(newidb.get(i));
				
				}
			}
				
			
			

			StringBuilder sb = new StringBuilder();
			sb.append("newidb size:" + newidb.size() + "\n");
			sb.append("Evaluation round " + loopNum + "\n");
			sb.append("The facts in the idb are:\n");
			sb.append("############\n");
			int flag = 0;
			for (Fact fact : idb) {
				if (fact.ifNew) {
					sb.append("*");
					fact.ifNew = false;
				} // trace
				if (fact.value >= Entry.MANUALLY_FIX_POINT) {
					flag++;

				}
				sb.append(fact.predicate + "(");

				for (int i = 0; i < fact.constant.length - 1; i++) {
					sb.append(fact.constant[i] + ",");
				}

				sb.append(fact.constant[fact.constant.length - 1]);
				sb.append(").");

				sb.append(fact.value);

				sb.append("\n");

			}
			sb.append("############");
			if (flag == idb.size()) {
				thisReach = flag;
			}

			if (flag == idb.size()) {
				lastReach = flag;
			}

			if (DEBUG_MODE) {
				logger.info(sb.toString());
			}

			loopNum++;

			if (Entry.SEMI_NAIVE) {
				improvedIdb = new ArrayList<Fact>();
				for (int i = 0; i < newidb.size(); i++) {
					improvedIdb.add(newidb.get(i));
				}

			}
			iteration++;

		}
		long endTime = System.currentTimeMillis();
		long cost = endTime - startTime;
		logger.log(Level.INFO, "Time cost: " + cost + "ms.");

	}

	/**
	 * 
	 * @param f1
	 *            New Fact
	 * @param f2
	 *            Exited Fact
	 * @param mode
	 *            0: a+b-a*b 1: max(a,b)
	 * @return
	 */
	public static double UpdateValue(Fact f1, Fact f2, int mode) {

		if (mode == 0) {
			return subtract(add(f1.value,f2.value), multiply(f1.value,f2.value));
		} else {
			return Double.max(f1.value, f2.value);
		}

	}

	public static void matching(int matchingMode, Rule matchRule) {

		HashMap<String, String> substitution = new HashMap<String, String>();
		ArrayList<Fact> calFact = new ArrayList<Fact>();
		for (Fact e : edb) {
			filterFacts.add(e);
		}
		if (matchingMode == 0) {
			if (matchRule.isConstant) {
				edbmatchWithConstant(matchRule);
			} else {
				edbmatch(0, substitution, matchRule, calFact);
			}
		}
		if (matchingMode == 1) {
			if (Entry.SEMI_NAIVE) {
				evaluEdb = new ArrayList<Fact>();
				for (int i = 0; i < edb.size(); i++)
					evaluEdb.add(edb.get(i));

				if (matchRule.isConstant) {
					for (int i = 0; i < idb.size(); i++)
						evaluEdb.add(idb.get(i));
					semimatchWithConstant(0, matchRule, calFact);
				} else {
					for (int i = 0; i < improvedIdb.size(); i++)
						evaluEdb.add(improvedIdb.get(i));
					semimatch(0, substitution, matchRule, calFact);// To be
																	// change
				}

			}

			else {
				evaluEdb = new ArrayList<Fact>();
				for (int i = 0; i < edb.size(); i++)
					evaluEdb.add(edb.get(i));
				for (int i = 0; i < idb.size(); i++)
					evaluEdb.add(idb.get(i));
				if (matchRule.isConstant) {
					naivematchWithConstant(0, matchRule, calFact);
				} else {
					naivematch(0, substitution, matchRule, calFact);
				}
			}
		}
	}

	public static void semimatchWithConstant(int depth, Rule matchRule, ArrayList<Fact> parentCalFact) {

		ArrayList<Fact> calfact = new ArrayList<Fact>();
		for (int i = 0; i < parentCalFact.size(); i++) {
			calfact.add(parentCalFact.get(i));
		}
		Literal currentSubgoal = matchRule.body.get(depth);

		for (int e = 0; e < evaluEdb.size(); e++) {
			if (evaluEdb.get(e).predicate.equals(currentSubgoal.predicate)) {
				// System.out.println("鍖归厤鍒帮細"+e+evaluEdb.get(e).predicate);
				boolean canMatch = true;

				for (int j = 0; j < currentSubgoal.varia.length; j++) {
					if (!currentSubgoal.varia[j].equals(evaluEdb.get(e).constant[j])) {
						canMatch = false;
						break;
					}
				}
				if (canMatch == true) {
					calfact.add(evaluEdb.get(e));
					if (depth < matchRule.body.size() - 1)
						naivematchWithConstant(depth + 1, matchRule, calfact);
					else {

						Fact newfact = new Fact(matchRule.head.predicate, matchRule.head.varia, 0.0, calfact);
						double newfact_value = conjunction(calfact, matchRule.value, Entry.CONJUNCTION_MODE,Entry.PROPAGATION_MODE);
						newfact.value = newfact_value;

						newidb.add(newfact);

					}
					// calfact.clear();
					calfact.remove(evaluEdb.get(e));

				}
			}
		}

	}

	public static void naivematchWithConstant(int depth, Rule matchRule, ArrayList<Fact> parentCalFact) {

		ArrayList<Fact> calfact = new ArrayList<Fact>();
		for (int i = 0; i < parentCalFact.size(); i++) {
			calfact.add(parentCalFact.get(i));
		}
		Literal currentSubgoal = matchRule.body.get(depth);

		for (int e = 0; e < evaluEdb.size(); e++) {
			if (evaluEdb.get(e).predicate.equals(currentSubgoal.predicate)) {
				// System.out.println("鍖归厤鍒帮細"+e+evaluEdb.get(e).predicate);
				boolean canMatch = true;

				for (int j = 0; j < currentSubgoal.varia.length; j++) {
					if (!currentSubgoal.varia[j].equals(evaluEdb.get(e).constant[j])) {
						canMatch = false;
						break;
					}
				}
				if (canMatch == true) {
					calfact.add(evaluEdb.get(e));
					if (depth < matchRule.body.size() - 1)
						naivematchWithConstant(depth + 1, matchRule, calfact);
					else {

						Fact newfact = new Fact(matchRule.head.predicate, matchRule.head.varia, 0.0, calfact);
						double newfact_value = conjunction(calfact, matchRule.value, Entry.CONJUNCTION_MODE,Entry.PROPAGATION_MODE);
						newfact.value = newfact_value;

						newidb.add(newfact);

					}

					calfact.remove(evaluEdb.get(e));

				}
			}
		}

	}

	public static double conjunction(List<Fact> calfact, double ruleValue, int modeCon, int modePro) {

		if (modeCon == 0&&modePro ==1) {// min then multiply
			double min = Double.MAX_VALUE;
			for (Fact fact : calfact) {
				if (fact.value < min) {
					min = fact.value;
				}
			}
			return min * ruleValue;

		} else if (modeCon == 1&&modePro==0) {// multiply then min
			double multiply = 1.0;
			for (Fact fact : calfact) {
				multiply = multiply * fact.value;
			}
			return multiply < ruleValue ? multiply : ruleValue;

		}else if (modeCon == 0&&modePro==0) {// min then min
			double min = 3.0;
			for (Fact fact : calfact) {
				if(fact.value<min)
					min = fact.value;
//				multiply = multiply * fact.value;
			}
			return min < ruleValue ? min : ruleValue;

		}  
		else if (modeCon == 1&&modePro==1) {// multiply then multiply
			double multiply = 1.0;
			for (Fact fact : calfact) {
				multiply = multiply * fact.value;
			}
			return multiply * ruleValue;

		}  
		else {
			return -1;
		}

	}
	
	
	
	
	public static double add(Double... doubles){
        BigDecimal result = new BigDecimal(0);
        for(Double a : doubles){
            result = result.add(new BigDecimal(String.valueOf(a)));
        }
        return result.doubleValue();
    }

    /**
     * @title 解决double乘法精度问题
     */
    public static double multiply(Double... doubles){
        BigDecimal result = new BigDecimal(1);
        for(Double a : doubles){
            result = result.multiply(new BigDecimal(String.valueOf(a)));
        }
        return result.doubleValue();
    }
    /**
     * @title 解决double除法精度问题
     * @param dividend 被除数
     * @param divisor 除数
     * @param scale 保留小数位数
     * @param roundingMode 小数保留模式
     */
    public static double divide(Double dividend,Double divisor,int scale,RoundingMode roundingMode){
        BigDecimal result = new BigDecimal(0);
        result = new BigDecimal(String.valueOf(dividend)).divide(new BigDecimal(String.valueOf(divisor)), scale, roundingMode);
        return result.doubleValue();
    }

    /**
     * @title 解决double减法精度问题
     * @param minuend 被减数
     * @param subtractor 减数
     */
    public static double subtract(Double minuend,Double subtractor){

        return new BigDecimal(String.valueOf(minuend))
                .subtract(new BigDecimal(String.valueOf(subtractor)))
                .doubleValue();
    }


	public static void naivematch(int depth, HashMap<String, String> parentSubstitution, Rule matchRule,
			ArrayList<Fact> parentCalFact) {
		String lastKey = "";
		HashMap<String, String> substitution = new HashMap<String, String>();
		ArrayList<Fact> calfact = new ArrayList<Fact>();
		for (int i = 0; i < parentCalFact.size(); i++) {
			calfact.add(parentCalFact.get(i));
		}
		for (String str : parentSubstitution.keySet()) {
			substitution.put(str, parentSubstitution.get(str));
		}
		Literal currentSubgoal = matchRule.body.get(depth);
		String[] instance = new String[currentSubgoal.varia.length];
		boolean[] matched = new boolean[currentSubgoal.varia.length];
		for (int v = 0; v < currentSubgoal.varia.length; v++) {
			String substi = null;
			substi = substitution.get(currentSubgoal.varia[v]);
			if (substi != null) {
				matched[v] = true;

			}
			instance[v] = substi;
		}

		for (int e = 0; e < evaluEdb.size(); e++) {
			if (evaluEdb.get(e).predicate.equals(currentSubgoal.predicate)) {
				// System.out.println("鍖归厤鍒帮細"+e+evaluEdb.get(e).predicate);
				boolean canMatch = true;
				for (int v = 0; v < currentSubgoal.varia.length; v++) {
					if (matched[v] == true && !evaluEdb.get(e).constant[v].equals(instance[v])) {
						// System.out.println("at "+v+" 鐭涚浘鍖归厤");
						canMatch = false;
						break;
					}
				}
				if (canMatch == true) {
					int flag = 0;
					for (int v = 0; v < currentSubgoal.varia.length; v++) {
						if (matched[v] == false) {
							if (flag == 0) {
								calfact.add(evaluEdb.get(e));
								flag++;
							}
							// System.out.println("鍔犲叆鏇夸唬闆嗭細"+currentSubgoal.varia[v]+"--"+evaluEdb.get(e).constant[v]);
							substitution.put(currentSubgoal.varia[v], evaluEdb.get(e).constant[v]);
							lastKey = currentSubgoal.varia[v];
						}
					}
					if (depth < matchRule.body.size() - 1)
						naivematch(depth + 1, substitution, matchRule, calfact);
					else {
						String[] consta = new String[matchRule.head.varia.length];
						for (int i = 0; i < matchRule.head.varia.length; i++) {
							consta[i] = substitution.get(matchRule.head.varia[i]);
						}
//						Fact newfact = new Fact(matchRule.head.predicate, consta, 0.0, 0);
						Fact newfact = new Fact(matchRule.head.predicate, consta, 0.0, calfact);
						double newfact_value = conjunction(calfact, matchRule.value, Entry.CONJUNCTION_MODE,Entry.PROPAGATION_MODE);
						newfact.value = newfact_value;
						newidb.add(newfact);
					}
					// calfact.clear();
					calfact.remove(evaluEdb.get(e));
					substitution.remove(lastKey);
				}
			}

		}
		calfact.clear();

	}

	public static void semimatch(int depth, HashMap<String, String> parentSubstitution, Rule matchRule,
			ArrayList<Fact> parentCalFact) {
		String lastKey = "";

		HashMap<String, String> substitution = new HashMap<String, String>();
		ArrayList<Fact> calfact = new ArrayList<Fact>();
		for (int i = 0; i < parentCalFact.size(); i++) {
			calfact.add(parentCalFact.get(i));
		}
		for (String str : parentSubstitution.keySet()) {
			substitution.put(str, parentSubstitution.get(str));
		}
		Literal currentSubgoal = matchRule.body.get(depth);
		String[] instance = new String[currentSubgoal.varia.length];
		boolean[] matched = new boolean[currentSubgoal.varia.length];
		for (int v = 0; v < currentSubgoal.varia.length; v++) {
			String substi = null;
			substi = substitution.get(currentSubgoal.varia[v]);
			if (substi != null) {
				matched[v] = true;

			}
			instance[v] = substi;
		}

		for (int e = 0; e < evaluEdb.size(); e++) {
			if (evaluEdb.get(e).predicate.equals(currentSubgoal.predicate)) {

				boolean canMatch = true;
				for (int v = 0; v < currentSubgoal.varia.length; v++) {
					if (matched[v] == true && !evaluEdb.get(e).constant[v].equals(instance[v])) {

						canMatch = false;
						break;
					}
				}
				if (canMatch == true) {
					int flag = 0;
					for (int v = 0; v < currentSubgoal.varia.length; v++) {
						if (matched[v] == false) {
							if (flag == 0) {
								calfact.add(evaluEdb.get(e));
								flag++;
							}

							substitution.put(currentSubgoal.varia[v], evaluEdb.get(e).constant[v]);
							lastKey = currentSubgoal.varia[v];
						}
					}
					if (depth < matchRule.body.size() - 1)
						semimatch(depth + 1, substitution, matchRule, calfact);
					else {
						String[] consta = new String[matchRule.head.varia.length];
						for (int i = 0; i < matchRule.head.varia.length; i++) {
							consta[i] = substitution.get(matchRule.head.varia[i]);
						}
						
						
						Fact newfact = new Fact(matchRule.head.predicate, consta, 0.0, calfact);
						double newfact_value = conjunction(calfact, matchRule.value, Entry.CONJUNCTION_MODE,Entry.PROPAGATION_MODE);				
						newfact.value = newfact_value;
//						AddtoMultiSet(newfact);
						newidb.add(newfact);

					}
					// calfact.clear();
					calfact.remove(evaluEdb.get(e));
					substitution.remove(lastKey);
				}
			}

		}
		calfact.clear();

	}
	public static void AddtoMultiSet(Fact fact){
		for(Fact f:multiSet){
			if(f.equals(fact)&&f.path.equals(fact.path)){
				f.value = fact.value;
				return;
			}
		}
		multiSet.add(fact);
		
	}

	public static void edbmatchWithConstant(Rule matchRule) {
		Map<Integer, Boolean> resVert = new HashMap<>();
		ArrayList<Fact> calFact = new ArrayList<Fact>();
		for (int i = 0; i < matchRule.body.size(); i++) {
			resVert.put(i, false);
			Literal body = matchRule.body.get(i);
			for (Fact fact : edb) {
				boolean equal = true;
				if (body.predicate.equals(fact.predicate)) {
					for (int j = 0; j < body.varia.length; j++) {
						if (!body.varia[j].equals(fact.constant[j])) {
							equal = false;
							break;
						}
					}
				} else {
					equal = false;
				}
				if (equal) {
					calFact.add(fact);
					resVert.put(i, true);
					break;
				}
			}
		}
		int temp = 0;
		for (int i = 0; i < matchRule.body.size(); i++) {

			if (resVert.get(i)) {
				temp++;
			}

		}
		if (temp == matchRule.body.size()) {
			Fact newfact = new Fact(matchRule.head.predicate, matchRule.head.varia, 0.0, calFact);

			double newfact_value = conjunction(calFact, matchRule.value, Entry.CONJUNCTION_MODE,Entry.PROPAGATION_MODE);
			newfact.value = newfact_value;

			newidb.add(newfact);

			
		}

	}

	public static void edbmatch(int depth, HashMap<String, String> parentSubstitution, Rule matchRule,
			ArrayList<Fact> parentcalFact) {

		// System.out.print("Subgoal"+depth+":");
		HashMap<String, String> substitution = new HashMap<String, String>();
		for (String str : parentSubstitution.keySet()) {
			substitution.put(str, parentSubstitution.get(str));
		}

		ArrayList<Fact> calFact = new ArrayList<Fact>();
		for (int i = 0; i < parentcalFact.size(); i++) {

			calFact.add(parentcalFact.get(i));
		}

		Literal currentSubgoal = matchRule.body.get(depth);
		String[] instance = new String[currentSubgoal.varia.length];
		boolean[] matched = new boolean[currentSubgoal.varia.length];

		for (int v = 0; v < currentSubgoal.varia.length; v++) {
			String substi = null;
			substi = substitution.get(currentSubgoal.varia[v]);
			if (substi != null) {
				matched[v] = true;

			}
			instance[v] = substi;
		}

		for (int e = 0; e < edb.size(); e++) {
			if (edb.get(e).predicate.equals(currentSubgoal.predicate)) {

				boolean canMatch = true;
				for (int v = 0; v < currentSubgoal.varia.length; v++) {
					if (matched[v] == true && !edb.get(e).constant[v].equals(instance[v])) {

						canMatch = false;
						break;
					}
				}
				if (canMatch == true) {
					int flag = 0;
					for (int v = 0; v < currentSubgoal.varia.length; v++) {
						if (matched[v] == false) {
							if (flag == 0) {
								calFact.add(edb.get(e));
								flag++;
							}

							substitution.put(currentSubgoal.varia[v], edb.get(e).constant[v]);
						}
					}
					if (depth < matchRule.body.size() - 1)
						edbmatch(depth + 1, substitution, matchRule, calFact);
					else {
						String[] consta = new String[matchRule.head.varia.length];
						for (int i = 0; i < matchRule.head.varia.length; i++) {
							consta[i] = substitution.get(matchRule.head.varia[i]);
						}
						/**
						 * Begin
						 */
						Fact newfact = new Fact(matchRule.head.predicate, consta, 0.0, calFact);

						boolean canInsert = true;

						double newfact_value = conjunction(calFact, matchRule.value, Entry.CONJUNCTION_MODE,Entry.PROPAGATION_MODE);
						newfact.value = newfact_value;
						newidb.add(newfact);

						
						/**
						 * End
						 */

					}
					calFact.clear();
					for (int v = 0; v < currentSubgoal.varia.length; v++) {
						if (matched[v] == false) {
							substitution.remove(currentSubgoal.varia[v]);
						}
					}
				}
			}
		}

	}

	public static void addToFilter(Fact fact) {
		Fact temp = new Fact();
		temp.constant = fact.constant;
		temp.path = fact.path;
		temp.value = fact.value;
		temp.predicate = fact.predicate;
		filterFacts.add(temp);
	}

}