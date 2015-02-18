package de.caegroup.process;

import java.io.*;
//import java.util.*;
//import org.apache.solr.common.util.NamedList;
import java.util.ArrayList;

import org.apache.commons.lang3.SerializationUtils;

public class Variable
implements Serializable
{
	/*----------------------------
	  structure
	----------------------------*/

	static final long serialVersionUID = 1;
	private String key = "default";
	private String value = null;
	private String description = "";
	private String glob = null;
	private ArrayList<String> choice = new ArrayList<String>();
	private ArrayList<Test> test = new ArrayList<Test>();
	private int minoccur = 0;
	private int maxoccur = 999999;
	private String type = "string"; // string|float|integer|flag
	private boolean free = true;
	
	private String status = "";	// waiting/finished/error

	private Step parent = null;
	
	private ArrayList<Log> log = new ArrayList<Log>();


	/*----------------------------
	  constructors
	----------------------------*/
	public Variable()
	{
		this.parent = new Step();
		log("info", "variable created with an unknown parent");
	}

//	public Variable(String key)
//	{
//		this.key = key;
//	}
//
//	public Variable(String key, String value)
//	{
//		this.key = key;
//		this.value = value;
//	}

	/*----------------------------
	  methods
	----------------------------*/
	/**
	 * clone
	 * returns a clone of this
	 * @return Variable
	 */
	@Override
	public Variable clone()
	{
		return SerializationUtils.clone(this);
	}
	
	/**
	 * reset this
	 */
	public void reset()
	{
		this.getLog().clear();
		this.setStatus("");
		for(Test actTest : this.getTest())
		{
			actTest.reset();
		}
	}
	
	public boolean match(Match match)
	{
		String fieldname = match.getField();
		String pattern = match.getPattern();

		String string_to_test = new String();
		if 		(fieldname.equals("key")) 		{string_to_test = this.getKey();}
		else if (fieldname.equals("value")) 	{string_to_test = this.getValue();}

		if (string_to_test.matches(pattern)) { return true;	}
		else { return false; }
	}
	
	public void performAllTests()
	{
		for(Test t : this.test)
		{
			log("debug", "perform test: "+t.getName());
			t.performTest(this);
		}
	}
	
	public boolean doAllTestsPass()
	{
//		System.out.println("Variable "+this.getKey());
		for(Test t : this.test)
		{
//			System.out.println("testName "+t.getName()+" parameter: "+t.getParam().get(0).getContent());
			if (!(t.getTestResult()))
			{
//				System.out.println("test "+t.getName()+" does not pass with this call: value="+this.getValue()+" pattern="+t.getParam().get(0).getContent());
				return false;
			}
		}
//		System.out.println("all tests pass.");
		return true;
	}
	
	public String getAllTestsFeedback()
	{
		String testsFeedback = "";
		for(Test t : this.test)
		{
			if (!(testsFeedback.equals("")))
			{
				testsFeedback = testsFeedback + "\n\n";
			}
			testsFeedback = testsFeedback + "testName: " + t.getName() + "\n";
			testsFeedback = testsFeedback + "testDescription: " + t.getDescription() + "\n";
			testsFeedback = testsFeedback + "testResult: " + t.getTestResult() + "\n";
			testsFeedback = testsFeedback + "testFeedback: " + t.getTestFeedback();
		}
		return testsFeedback;
	}

	public String getFailedTestsFeedback()
	{
		String testsFeedback = "";
		for(Test t : this.test)
		{
			if (!(t.getTestResult()))
			{
				if (!(testsFeedback.equals("")))
				{
					testsFeedback = testsFeedback + "\n\n";
				}
				testsFeedback = testsFeedback + "testName: " + t.getName() + "\n";
				testsFeedback = testsFeedback + "testDescription: " + t.getDescription() + "\n";
				testsFeedback = testsFeedback + "testResult: " + t.getTestResult() + "\n";
				testsFeedback = testsFeedback + "testFeedback: " + t.getTestFeedback();
			}
		}
		return testsFeedback;
	}

	public String getFirstFailedTestsFeedback()
	{
		String testsFeedback = "";
		for(Test t : this.test)
		{
			if (!(t.getTestResult()))
			{
				if (!(testsFeedback.equals("")))
				{
					testsFeedback = testsFeedback + "\n\n";
				}
				testsFeedback = testsFeedback + "testName: " + t.getName() + "\n";
				testsFeedback = testsFeedback + "testDescription: " + t.getDescription() + "\n";
				testsFeedback = testsFeedback + "testResult: " + t.getTestResult() + "\n";
				testsFeedback = testsFeedback + "testFeedback: " + t.getTestFeedback();
				
				return testsFeedback;
			}
		}
		return testsFeedback;
	}

	/**
	 * stores a message in the object log
	 * @param String loglevel, String logmessage
	 */
	public void log(String loglevel, String logmessage)
	{
		this.log.add(new Log("variable-"+this.getKey(), loglevel, logmessage));
	}

	/*----------------------------
	  methods get
	----------------------------*/
	public String getKey()
	{
		return this.key;
	}

	public String getGlob()
	{
		return this.glob;
	}

	public String getValue()
	{
		return this.getParent().resolveString(this.value);
	}

	public String getDescription()
	{
		return this.description;
	}

	public int getMinoccur()
	{
		return this.minoccur;
	}

	public int getMaxoccur()
	{
		return this.maxoccur;
	}

	public boolean getFree()
	{
		return this.free;
	}

	public boolean isFree()
	{
		return this.free;
	}

	public ArrayList<Test> getTest()
	{
		return this.test;
	}

	public ArrayList<String> getChoice()
	{
		return this.choice;
	}

	public String getField(String fieldname)
	{
		String returnvalue = new String();
		if 		(fieldname.equals("key")) 		{returnvalue = this.getKey();}
		else if (fieldname.equals("value")) 	{returnvalue = this.getValue();}
		else 	{returnvalue = "no field '"+fieldname+"' in Object 'Variable'";}
		return returnvalue;
	}
	
	public ArrayList<Log> getLog()
	{
		return this.log;
	}

	/*----------------------------
	methods set
	----------------------------*/
	public void setKey(String key)
	{
		this.key = key;
	}

	public void setGlob(String glob)
	{
		this.glob = glob;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setMinoccur(int minoccur)
	{
		this.minoccur = minoccur;
	}

	public void setMaxoccur(int maxoccur)
	{
		this.maxoccur = maxoccur;
	}

	public void setFree(boolean free)
	{
		this.free = free;
	}

	public void setTest(ArrayList<Test> test)
	{
		this.test = test;
	}

	public void setChoice(ArrayList<String> choice)
	{
		this.choice = choice;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		log("info", "setting status to '"+status+"'");
		this.status = status;
	}

	/**
	 * @return the parent
	 */
	public Step getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Step parent) {
		this.parent = parent;
	}


}
