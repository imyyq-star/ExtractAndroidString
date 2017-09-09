package com.sxisa.yyq;

import javax.swing.JLabel;

public class CustomJLabel extends JLabel
{
	private static final long serialVersionUID = 2160670535319478644L;

	private Object tag;

	public CustomJLabel(String paramString, int paramInt)
	{
		super(paramString, paramInt);
	}

	public Object getTag()
	{
		return tag;
	}

	public void setTag(Object tag)
	{
		this.tag = tag;
	}
}
