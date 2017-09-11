package com.sxisa.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
	/**
	 * @param dirPath 目录的路径
	 * @param srcEncoding 文件的编码
	 */
	public static void dirToUTF8(String dirPath, String srcEncoding) throws Exception{
		List<String> contentList = new ArrayList<>();
		List<String> fileList = new ArrayList<>();

		for (File file : getAllFile(dirPath)) {
			fileList.add(file.getAbsolutePath());
			contentList.add(readFileContent(file.getAbsolutePath(), srcEncoding));

			file.delete();
		}

		for (int i = 0; i < fileList.size(); i++) {
			writeContent(fileList.get(i), "UTF-8", contentList.get(i));
		}
	}

	/**
	 * 取得 path 路徑下所有的文件对象
	 * 
	 * @return 返回 path 路徑下所有的文件對象
	 */
	public static List<File> getAllFile(Path path)
	{
		final List<File> files = new ArrayList<File>();
		SimpleFileVisitor<Path> finder = new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				files.add(file.toFile());
				return super.visitFile(file, attrs);
			}
		};
		try
		{
			Files.walkFileTree(path, finder);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return files;
	}

	/**
	 * 取得path文件夹下所有的文件对象
	 * 
	 * 如果路徑不存在，返回null
	 * 
	 * @return
	 */
	public static List<File> getAllFile(String path)
	{
		if (!new File(path).exists())
		{
			return null;
		}
		return getAllFile(Paths.get(path));
	}

	// 取得读取流
	public static BufferedReader getReader(String filePath) throws FileNotFoundException, UnsupportedEncodingException
	{
		return getReader(filePath, Charset.defaultCharset().name());
	}

	public static BufferedReader getReader(String filePath, String charsetName)
			throws FileNotFoundException, UnsupportedEncodingException
	{
		return new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charsetName));
	}

	// 取得写入流
	public static BufferedWriter getWriter(String filePath) throws FileNotFoundException, UnsupportedEncodingException
	{
		return getWriter(filePath, Charset.defaultCharset().name());
	}

	public static BufferedWriter getWriter(String filePath, String charsetName)
			throws FileNotFoundException, UnsupportedEncodingException
	{
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), charsetName));
	}

	// 读取所有行
	public static List<String> readAllLines(String filePath) throws IOException
	{
		List<String> list = new ArrayList<>();
		try (BufferedReader reader = FileUtils.getReader(filePath, Charset.defaultCharset().name());)
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				list.add(line);
			}
		}
		return list;
	}

	public static List<String> readAllLines(String filePath, String charsetName) throws IOException
	{
		List<String> list = new ArrayList<>();
		try (BufferedReader reader = FileUtils.getReader(filePath, charsetName);)
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				list.add(line);
			}
		}
		return list;
	}

	// 写入所有行
	public static void writeContent(String filePath, String charsetName, String content) throws IOException
	{
		try (BufferedWriter writer = FileUtils.getWriter(filePath, charsetName);)
		{
			writer.write(content);
			writer.newLine();
		}
	}

	public static void writeAllLines(String filePath, String charsetName, List<String> contentList) throws IOException
	{
		try (BufferedWriter writer = FileUtils.getWriter(filePath, charsetName);)
		{
			for (String string : contentList)
			{
				writer.write(string);
				writer.newLine();
			}
		}
	}

	public static void writeAllLines(String filePath, List<String> contentList) throws IOException
	{
		writeAllLines(filePath, Charset.defaultCharset().name(), contentList);
	}

	// 读取文件内容
	public static String readFileContent(String filePath, String charsetName)
	{
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charsetName));)
		{

			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
				builder.append("\r\n");
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return builder.toString();
	}

	public static String readFileContent(String filePath)
	{
		return readFileContent(filePath, "UTF-8");
	}
}
