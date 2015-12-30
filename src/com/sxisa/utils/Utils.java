package com.sxisa.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;

public class Utils
{
	public static String baiduTranslation(String apiKey, String source)
	{
		// 翻译后的结果字符串经JSON解析出翻译结果
		String temps = String.format(C.BAIDU_API, apiKey, source);
		TranslationResults translationResults = Utils.getJsonParseResult(HTTPRequestUtils.get(temps),
				TranslationResults.class);
		if (translationResults != null && translationResults.getTrans_result() != null
				&& !translationResults.getTrans_result().isEmpty()
				&& translationResults.getTrans_result().get(0) != null
				&& translationResults.getTrans_result().get(0).getDst() != null)
		{
			// 取出翻译结果，转换为小写并且去掉两边的空白，替换内容中的空白和非字母字符为下划线
			return translationResults.getTrans_result().get(0).getDst().toLowerCase().trim().replace(' ', '_')
					.replaceAll("[^A-Za-z0-9]+", "_");
		}
		return null;
	}

	/**
	 * 取得rootDirectory文件夹下所有的文件对象
	 * 
	 * @return
	 */
	public static List<File> getAllFile(Path path)
	{
		final List<File> files = new ArrayList<>();
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
			java.nio.file.Files.walkFileTree(path, finder);
		} catch (IOException e)
		{
			// ignore
		}
		return files;
	}

	/**
	 * 取得Json解析的结果
	 *
	 * @param jsonString
	 *            Json格式的字符串
	 * @param classT
	 *            相应的JavaBean类
	 * @return 返回相应的JavaBean类实例，解析失败，返回null，是控制台上输出出错结果，不是Log
	 */
	public static <T> T getJsonParseResult(String jsonString, Class<T> classT)
	{
		T t = null;
		try
		{
			t = JSON.parseObject(jsonString, classT);
		} catch (Exception e)
		{
			System.err.println("JSON解析出错，类名：" + classT.getName() + "，字符串是：" + jsonString);
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 是否是有效文件，有效文件包括各种基类和Fragment和Activity
	 * 
	 * @param file
	 * @return true代表是有效文件
	 * @throws IOException
	 */
	public static boolean isValidJavaFile(File file) throws IOException
	{
		String allString = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		if (allString.contains("extends BaseFragment") || allString.contains("extends BaseActivity")
				|| allString.contains("extends ParallaxSwipeBackActivity") || allString.contains("extends Activity")
				|| allString.contains("extends FragmentActivity") || allString.contains("extends AppCompatActivity")
				|| allString.contains("extends ActionBarActivity") || allString.contains("extends Fragment"))
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * 检查指定的strings.xml文件中的字符串资源是否有重复
	 * 
	 * @param xmlFileName
	 * @return false代表没有重复的字符串资源
	 */
	public static String getStringsXMLRepeat(String xmlFileName)
	{
		int sum = 0;
		HashSet<String> hashSet = new HashSet<>();

		String regex = "<string name=\"(.*?)\">(.*?)</string>";
		try
		{
			StringBuilder builder = new StringBuilder();
			LineNumberReader reader;
			reader = new LineNumberReader(new InputStreamReader(new FileInputStream(xmlFileName)));
			String line = null;

			Pattern pattern = Pattern.compile(regex);
			while ((line = reader.readLine()) != null)
			{
				// 取出一行中的指定字符串数据
				Matcher matcher = pattern.matcher(line);
				if (matcher.find())
				{
					sum++;
					if (!hashSet.add(matcher.group(2)))
					{
						builder.append("\n重复的字符串：" + matcher.group(2) + "，\t行号：" + reader.getLineNumber());
					}
				}
			}
			builder.append("\n重复的数目：" + (sum - hashSet.size()));
			reader.close();
			// 没有重复的数目
			if (sum - hashSet.size() == 0)
			{
				return null;
			} else
			{
				return builder.toString();
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
