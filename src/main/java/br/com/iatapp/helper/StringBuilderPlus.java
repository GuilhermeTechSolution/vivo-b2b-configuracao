package br.com.iatapp.helper;

public class StringBuilderPlus {

	private StringBuilder sb;

	public StringBuilderPlus(){
		sb = new StringBuilder();
	}

	public void append(String str)
	{
		sb.append(str != null ? str : "");
	}

	public void appendLine(String str)
	{
		//sb.append(str != null ? str : "").append(System.getProperty("line.separator"));
		sb.append(str != null ? str : "").append(System.lineSeparator());
	}

	public String toString()
	{
		return sb.toString();
	}
}
