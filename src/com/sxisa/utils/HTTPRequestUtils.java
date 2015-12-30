package com.sxisa.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HTTPRequestUtils
{
	/**
	 * 获取网页源代码
	 * 
	 * @param urlString
	 *            网页地址
	 * @return 返回一个字符串，包含着网页源代码
	 */
	public static String get(String urlString)
	{
		if (urlString == null)
		{
			return null;
		}
		URL url = null;
		StringBuilder webSource = new StringBuilder();
		try
		{
			url = new URL(urlString);
			URLConnection connection = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lineString = null;
			while ((lineString = reader.readLine()) != null)
			{
				webSource.append(lineString);
			}
			lineString = webSource.toString().replaceAll(" +", " ");
			return lineString;
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
