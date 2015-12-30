package com.sxisa.utils;

public class ItemInfo
{
	private String filePath;// 文件路径
	private int lineNumber; // 行号
	private String line;// 该行要替换的

	public ItemInfo(String filePath, int lineNumber, String line)
	{
		super();
		this.filePath = filePath;
		this.lineNumber = lineNumber;
		this.line = line;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public String getLine()
	{
		return line;
	}

}
