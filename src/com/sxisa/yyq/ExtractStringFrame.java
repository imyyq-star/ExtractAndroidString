package com.sxisa.yyq;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;

import com.sxisa.utils.CharUtil;
import com.sxisa.utils.ItemInfo;
import com.sxisa.utils.Utils;

public class ExtractStringFrame extends JFrame
{
	private static final long serialVersionUID = 8953218614066651595L;
	private static final String ACTION_SEARCH_JAVA = "ACTION_SEARCH_JAVA";
	private static final String ACTION_SEARCH_LAYOUT = "ACTION_SEARCH_LAYOUT";
	private static final String ACTION_CLEAR_SEARCH = "ACTION_CLEAR_SEARCH";
	private static final String ACTION_START_EXTRACT = "ACTION_START_EXTRACT";
	private static final String ACTION_CHECK_STRINGS_XML = "ACTION_CHECK_STRINGS_XML";

	// ================================================
 
	private JLabel labelJavaPath = new JLabel("Java文件所在的根目录");
	private JTextField textFieldJavaPath = new JTextField(40);
  
	private JLabel labelLayoutPath = new JLabel("Layout文件所在的根目录");
	private JTextField textFieldLayoutPath = new JTextField(40); 

	private JLabel labelStringsPath = new JLabel("strings.xml文件绝对路径");
	private JTextField textFieldStringsPath = new JTextField(40); 
	private JLabel lableAppGetString = new JLabel("App Context getString");
	private JTextField textFieldAppGetString = new JTextField(40); 

	private JLabel apiKeyLabel = new JLabel("百度翻译的APIKey");
	private JTextField apiKeyPathField = new JTextField(40);

	private JButton btnStartSearchJava = new JButton("开始检索Java");
	private JButton btnStartSearchLayout = new JButton("开始检索Layout");
	private JButton btnClearSearch = new JButton("清除结果");
	private JButton btnCheckStringsXML = new JButton("检测strings.xml是否有重复字符串");
   
	private JCheckBox cbAllExtract = new JCheckBox("全部抽取");
 
	private JButton btnStartExtract = new JButton("开始抽取");
 
	private JPanel panelCenter = new JPanel();
	
	// ================================================
	
	// 抽取结果，文件名对应着文件中的行号及该行内容
	private Map<String, Map<Integer, String>> resultMap = new HashMap<>();
	// 存入strings.xml中的，使用Set可以保证不重复
	private Set<String> resultStringsSet = new HashSet<>();

	private boolean isBeingSearch = false;

	public ExtractStringFrame() throws HeadlessException
	{
		this("", "", "", "", "");
	}

	public ExtractStringFrame(String javaPath, String layoutPath, String stringsPath, String appContextGetString, String apiKey)
			throws HeadlessException
	{
		super("ExtractAndroidStrings - v1.1");
		textFieldJavaPath.setText(javaPath);
		textFieldLayoutPath.setText(layoutPath);
		textFieldStringsPath.setText(stringsPath);
		textFieldAppGetString.setText(appContextGetString);
		apiKeyPathField.setText(apiKey);
		init();
	}

	private void init()
	{
		// 整个窗体默认的布局是区域布局
		setLayout(new BorderLayout());

		// 设置监听 
		BtnListener listener = new BtnListener();

		btnStartSearchJava.setActionCommand(ACTION_SEARCH_JAVA);
		btnStartSearchJava.addActionListener(listener);
		
	 	btnStartSearchLayout.setActionCommand(ACTION_SEARCH_LAYOUT);
		btnStartSearchLayout.addActionListener(listener);
		
		btnClearSearch.setActionCommand(ACTION_CLEAR_SEARCH);
		btnClearSearch.addActionListener(listener);
		
		btnStartExtract.setActionCommand(ACTION_START_EXTRACT);
		btnStartExtract.addActionListener(listener);
		
		btnCheckStringsXML.setActionCommand(ACTION_CHECK_STRINGS_XML);
		btnCheckStringsXML.addActionListener(listener);
		
		cbAllExtract.addItemListener(new AllCheckListener());

		// =============================================================================

		/*
		 * 顶部的面板
		 */
		Dimension dimension = new Dimension(180, 40);

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

		JPanel javaPathPanel = new JPanel();
		javaPathPanel.setLayout(new BoxLayout(javaPathPanel, BoxLayout.X_AXIS));
		labelJavaPath.setPreferredSize(dimension);
		javaPathPanel.add(labelJavaPath);
		javaPathPanel.add(textFieldJavaPath);
		northPanel.add(javaPathPanel);

		JPanel layoutPathPanel = new JPanel();
		layoutPathPanel.setLayout(new BoxLayout(layoutPathPanel, BoxLayout.X_AXIS));
		labelLayoutPath.setPreferredSize(dimension);
		layoutPathPanel.add(labelLayoutPath);
		layoutPathPanel.add(textFieldLayoutPath);
		northPanel.add(layoutPathPanel);

		JPanel stringsPathPanel = new JPanel();
		stringsPathPanel.setLayout(new BoxLayout(stringsPathPanel, BoxLayout.X_AXIS));
		labelStringsPath.setPreferredSize(dimension);
		stringsPathPanel.add(labelStringsPath);
		stringsPathPanel.add(textFieldStringsPath);
		northPanel.add(stringsPathPanel);
		 
		JPanel appGetStringPathPanel = new JPanel();
		appGetStringPathPanel.setLayout(new BoxLayout(appGetStringPathPanel, BoxLayout.X_AXIS));
		lableAppGetString.setPreferredSize(dimension);
		appGetStringPathPanel.add(lableAppGetString);
		appGetStringPathPanel.add(textFieldAppGetString);
		northPanel.add(appGetStringPathPanel);

		JPanel apiKeyPanel = new JPanel();
		apiKeyPanel.setLayout(new BoxLayout(apiKeyPanel, BoxLayout.X_AXIS));
		apiKeyLabel.setPreferredSize(dimension);
		apiKeyPanel.add(apiKeyLabel);
		apiKeyPanel.add(apiKeyPathField);
		northPanel.add(apiKeyPanel);

		// 开始检索按钮
		JPanel startSearchPanel = new JPanel();
		startSearchPanel.setLayout(new BoxLayout(startSearchPanel, BoxLayout.X_AXIS));
		startSearchPanel.add(btnStartSearchJava);
		startSearchPanel.add(btnStartSearchLayout);
		startSearchPanel.add(btnClearSearch);
		startSearchPanel.add(btnCheckStringsXML);
		northPanel.add(startSearchPanel);

		// =========================================

		JPanel tipsPanel = new JPanel();
		tipsPanel.setLayout(new BoxLayout(tipsPanel, BoxLayout.X_AXIS));

		cbAllExtract.setPreferredSize(new Dimension(540, 50));

		tipsPanel.add(cbAllExtract);
		tipsPanel.add(btnStartExtract);

		northPanel.add(tipsPanel);

		this.add(northPanel, BorderLayout.NORTH);

		// =============================

		/*
		 * 抽取结果
		 */
		JScrollPane centerScrollPane = new JScrollPane();
		centerScrollPane.setLayout(new ScrollPaneLayout());
		panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.Y_AXIS));

//		 for (int i = 0; i < 2; i++)
//		 {
//		 addItem("jhad", 2, "fsjdlf", "fjsado", "fjsdlf");
//		 }

		centerScrollPane.setViewportView(panelCenter);
		this.add(centerScrollPane, BorderLayout.CENTER);

		// ===============================================

		this.setVisible(true);
		this.setLocation(500, 100);
		this.pack();
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * 添加检索的结果项
	 * 
	 * @param absolutePath
	 * @param lineNumber
	 * @param line
	 * @param target
	 * @param replacement
	 */
	private void addItem(String absolutePath, String fileName, int lineNumber, String line, String target, String replacement)
	{
		CustomJPanel tempPanel = new CustomJPanel();
		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.X_AXIS));

		JCheckBox tempCheckBox = new JCheckBox("是否抽取");
		tempPanel.add(tempCheckBox);

		String src = "<html>" + fileName + ":" + lineNumber + "<br>" + target + "</html>";
		CustomJLabel jLabel = new CustomJLabel(src, JLabel.CENTER);
		jLabel.setTag(target);
		tempPanel.add(jLabel);
		tempPanel.add(new JTextField(replacement, 21));
		tempPanel.setPreferredSize(new Dimension(0, src.length() * 2));
		tempPanel.setTag(new ItemInfo(absolutePath, lineNumber, line));
		panelCenter.add(tempPanel);
	}

	/**
	 * 开始检索Layout文件
	 */
	private void startSearchLayout()
	{
		File layoutFile = new File(textFieldLayoutPath.getText());
		if (!textFieldLayoutPath.getText().isEmpty() && !layoutFile.isDirectory())
		{
			showError("请输入正确的Layout文件根目录");
			return;
		}
		
		btnStartSearchLayout.setText("正在检索，请稍候...");
		
		// 取得所有文件的对象
		final List<File> files = Utils.getAllFile(Paths.get(textFieldLayoutPath.getText()));

		Pattern textPattern = Pattern.compile("android:text=\"(.*?)\"");
		Pattern hintPattern = Pattern.compile("android:hint=\"(.*?)\"");

		// 循环遍历所有的文件
		for (File file : files)
		{
			// 是文件并且是xml文件
			if (file.isFile() && file.getName().endsWith(".xml"))
			{
				// 查找此文件中可以抽取的字符串
				try
				{
					// 一行一行的读取
					LineNumberReader reader = new LineNumberReader(
							new InputStreamReader(new FileInputStream(file)));
					String readLine = null;
					while ((readLine = reader.readLine()) != null)
					{
						if (readLine.trim().startsWith("<!--")) // 注释不抽取
						{
							continue;
						}
						if (!CharUtil.isChinese(readLine)) // 不是中文不抽取
						{
							continue;
						}
						extractLayoutString(file.getAbsolutePath(), file.getName(), reader.getLineNumber(), readLine, textPattern.matcher(readLine));
						extractLayoutString(file.getAbsolutePath(), file.getName(), reader.getLineNumber(), readLine, hintPattern.matcher(readLine));
					}
					reader.close();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		} // for End

		ExtractStringFrame.this.pack();

		btnStartSearchLayout.setText("开始检索Layout");
		result();
	}

	private void result() {
		// 如果检索后发现面板数量依然为0，说明没有找到可以抽取的
		if (panelCenter.getComponentCount() == 0)
		{
			showError("未发现可以抽取的字符串");
		} else
		{
			// 取得屏幕的高度，如果Frame的高度大于等于屏幕的高度，则根据屏幕高度动态设置Frame高度
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
			int height = (int) screensize.getHeight();
			if (ExtractStringFrame.this.getHeight() >= height)
			{
				ExtractStringFrame.this.setSize(ExtractStringFrame.this.getWidth(), (int) (height / 1.3));
			}
		}
	}
 
	private void extractLayoutString(String absolutePath, String fileName, int lineNum, String readLine, Matcher textMatcher) {
		// 在xml中，一行只可能有一个字符串
		if (textMatcher.find())
		{
			String matchingString = textMatcher.group(1);// 取出双引号内的
			// 是否可以抽取
			if (matchingString != null && !matchingString.equals("")
					&& !matchingString.startsWith("@string/"))
			{
				String translationString = Utils.baiduTranslation(apiKeyPathField.getText(),
						matchingString);
				if (translationString != null)
				{
					addItem(absolutePath, fileName, lineNum, readLine,
							matchingString, translationString);
				}
			}
		}
	}

	/**
	 * 开始检索Java文件
	 */
	private void startSearchJava()
	{
		// 路径错误
		File javaFile = new File(textFieldJavaPath.getText());
		if (!textFieldJavaPath.getText().isEmpty() && !javaFile.isDirectory())
		{
			showError("请输入正确的Java文件根目录");
			return;
		}
		
		btnStartSearchJava.setText("正在检索，请稍候...");
		
		// 取得所有文件的对象
		final List<File> files = Utils.getAllFile(Paths.get(textFieldJavaPath.getText()));

		// 通过正则，找出所有被双引号""包含的字符串
		Pattern pattern = Pattern.compile("\"(.*?)\"");

		for (File file : files)
		{
			// 是文件并且是java文件
			if (file.isFile() && file.getName().endsWith(".java"))
			{
				// 如果是测试文件，则不抽取
				if (file.getParent().endsWith("test"))
				{
					continue;
				}
				try
				{
					// 一行一行的读取
					LineNumberReader reader = new LineNumberReader(
							new InputStreamReader(new FileInputStream(file)));
					String readLine = null;
					while ((readLine = reader.readLine()) != null)
					{
						// 判断字符串是否有可以抽取的地方
						if (!isLineExtract(readLine))
						{
							continue;
						}
						// 找出被双引号包含起来的字符串
						Matcher matcher = pattern.matcher(readLine);
						while (matcher.find())
						{
							String matchingString = matcher.group();
							// 去掉双引号后的源字符串
							String sourceString = matchingString.substring(1, matchingString.length() - 1);
							// 这个字符串不抽取就略过
							if (!isSourceExtract(sourceString))
							{
								continue;
							}

							String translationString = Utils.baiduTranslation(apiKeyPathField.getText(),
									sourceString);
							if (translationString != null)
							{
								addItem(file.getAbsolutePath(), file.getName(), reader.getLineNumber(), readLine, sourceString,
										translationString);
							} else
							{
								System.out.println(sourceString);
							}
						}
					}
					reader.close();
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		} // for End

		ExtractStringFrame.this.pack();

		btnStartSearchJava.setText("开始检索");
		result();
	}
	
	/**
	 * 清除掉搜索结果
	 */
	private void clearSearch()
	{
		panelCenter.removeAll();
		ExtractStringFrame.this.pack();
	}
	
	/**
	 * 开始抽取
	 */
	private void startExtract()
	{
		List<Component> components = new ArrayList<>();
		
		File stringsXMLFile = new File(textFieldStringsPath.getText());
		if (!textFieldStringsPath.getText().isEmpty() && !stringsXMLFile.isFile())
		{
			JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(), "请输入正确的strings文件路径",
					"错误提示", JOptionPane.NO_OPTION);
			return;
		}
		// 如果滑动面板中没有项，就提示
		if (panelCenter.getComponentCount() == 0)
		{
			JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(), "没有可以抽取的字符串，请先开始检索Java或Layout文件", "温馨提示",
					JOptionPane.NO_OPTION);
		} else
		{
			resultStringsSet.clear();

			// 循环结果面板，一条一条
			for (int i = 0; i < panelCenter.getComponentCount(); i++)
			{
				CustomJPanel jPanel = (CustomJPanel) panelCenter.getComponent(i);
				ItemInfo fileInfo = (ItemInfo) jPanel.getTag();
				JCheckBox jCheckBox = (JCheckBox) jPanel.getComponent(0);
				String jLabel = (String) ((CustomJLabel) jPanel.getComponent(1)).getTag();
				JTextField jTextField = (JTextField) jPanel.getComponent(2);

				// 如果选择了抽取，并且文本框内不为空，则抽取
				if (jCheckBox.isSelected() && !jTextField.getText().isEmpty())
				{
					components.add(jPanel);
					
					// 文件已存在，取出对应的行号集合。行号不存在才添加
					if (resultMap.containsKey(fileInfo.getFilePath()))
					{
						// 取出文件对应的行号集合
						Map<Integer, String> tempMap = resultMap.get(fileInfo.getFilePath());
						if (!tempMap.containsKey(fileInfo.getLineNumber()))
						{
							if (fileInfo.getFilePath().endsWith(".java"))
							{
								tempMap.put(fileInfo.getLineNumber(), fileInfo.getLine().replace(jLabel,
										textFieldAppGetString.getText() + 
										"(R.string." + jTextField.getText() + ")"));
							} else if (fileInfo.getFilePath().endsWith(".xml"))
							{
								tempMap.put(fileInfo.getLineNumber(), fileInfo.getLine().replace(jLabel,
										"@string/" + jTextField.getText()));
							}
							resultStringsSet.add("<string name=\"" + jTextField.getText() + "\">" + jLabel
									+ "</string>");
						}
					}
					// 文件不存在，新建行号集合
					else
					{
						Map<Integer, String> tempMap = new HashMap<>();
						if (fileInfo.getFilePath().endsWith(".java"))
						{
							tempMap.put(fileInfo.getLineNumber(), fileInfo.getLine().replace(jLabel,
									"getString(R.string." + jTextField.getText() + ")"));
						} else if (fileInfo.getFilePath().endsWith(".xml"))
						{
							tempMap.put(fileInfo.getLineNumber(), fileInfo.getLine().replace(jLabel,
									"@string/" + jTextField.getText()));
						}
						resultMap.put(fileInfo.getFilePath(), tempMap);
						resultStringsSet.add(
								"<string name=\"" + jTextField.getText() + "\">" + jLabel + "</string>");
					}
				}
			} // for End
			try
			{
				StringBuilder builder = new StringBuilder();

				Set<String> resultSet = resultMap.keySet();
				Iterator<String> resultIterator = resultSet.iterator();
				while (resultIterator.hasNext())
				{
					builder.setLength(0);
					// 取出文件名称，操作文件
					String filePath = resultIterator.next();

					Map<Integer, String> lineNumberMap = resultMap.get(filePath);

					// 一行一行的读出来，如果当前行在行号map中，那么替换当前行为map中的行
					LineNumberReader reader = new LineNumberReader(
							new InputStreamReader(new FileInputStream(filePath)));
					String line = null;
					while ((line = reader.readLine()) != null)
					{
						if (lineNumberMap.containsKey(reader.getLineNumber()))
						{
							builder.append(lineNumberMap.get(reader.getLineNumber()));
							builder.append(System.getProperty("line.separator"));
						} else
						{
							builder.append(line);
							builder.append(System.getProperty("line.separator"));
						}
					}
					reader.close();

					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(filePath)));
					writer.write(builder.toString());
					writer.flush();
					writer.close();

				} // while End
				builder.setLength(0);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(textFieldStringsPath.getText())));
				String line = null;
				while ((line = reader.readLine()) != null)
				{
					if (!line.contains("</resources>")) // 如果没有读到结尾，就继续，当读到结尾了，就把新内容添加进去
					{
						builder.append(line);
						builder.append(System.getProperty("line.separator"));
					} else
					{
						for (String string : resultStringsSet)
						{
							builder.append(string);
							builder.append(System.getProperty("line.separator"));
						}
						builder.append("</resources>");
						break;
					}
				}
				reader.close();

				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(textFieldStringsPath.getText())));
				writer.write(builder.toString());
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			for (Component component : components)
			{
				panelCenter.remove(component);
			}
			ExtractStringFrame.this.pack();
			JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(), "抽取完成", "温馨提示",
					JOptionPane.NO_OPTION);
		}
	}
	
	/**
	 * 检测strings.xml文件
	 */
	private void checkStringsXML()
	{
		if (!textFieldStringsPath.getText().isEmpty() && !new File(textFieldStringsPath.getText()).isFile())
		{
			showError("请输入正确的strings文件路径");
			return;
		}
		String s = Utils.getStringsXMLRepeat(textFieldStringsPath.getText());
		if (s != null)
		{
			JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(),
					"strings.xml中发现有重复的字符串，以下为详细内容\n" + s, "发现重复的字符串", JOptionPane.NO_OPTION);
			ExtractStringFrame.this.pack();
		}
	}
	
	/**
	 * 按钮监听
	 * 
	 * @author Administrator
	 *
	 */
	class BtnListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (isBeingSearch)
			{
				return;
			}
			switch (e.getActionCommand()) 
			{
			case ACTION_CLEAR_SEARCH:
				clearSearch();
				break;
			case ACTION_SEARCH_JAVA:
				startSearchJava();
				break;
			case ACTION_SEARCH_LAYOUT:
				startSearchLayout();
				break;
			case ACTION_START_EXTRACT:
				startExtract();
				break;
			case ACTION_CHECK_STRINGS_XML:
				checkStringsXML();
				break;
			}
		}
	}

	/**
	 * 全部抽取或全部不抽取
	 * 
	 * @author Administrator
	 *
	 */
	class AllCheckListener implements ItemListener
	{

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			JCheckBox jcb = (JCheckBox) e.getItem();
			// 循环面板中的每一行
			for (int i = 0; i < panelCenter.getComponentCount(); i++)
			{
				// 强制转换为JPanel
				JPanel j = (JPanel) panelCenter.getComponent(i);
				JCheckBox checkBox = (JCheckBox) j.getComponent(0);
				checkBox.setSelected(jcb.isSelected());
			}
		}
	}
	
	private void showError(String msg)
	{
		JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(),
				msg, "错误提示", JOptionPane.NO_OPTION);
	}

	/**
	 * 是否抽取此行中的字符串到strings.xml中
	 * 
	 * @param line
	 * @return false代表不抽取
	 */
	static boolean isLineExtract(String line)
	{
		line = line.trim();
		if (line.startsWith("Log.") || line.startsWith("System.out.") || line.startsWith("//") || line.startsWith("* ")
				|| line.startsWith("System.err.")
				|| line.startsWith("XLog.")
				|| line.startsWith("throw new")
				)
		{
			return false;
		} else
		{
			return true;
		}
	}

	/**
	 * 是否抽取旧内容到strings.xml中
	 * 
	 * @param source
	 *            即将抽取的旧内容
	 * @return false代表不抽取
	 */
	static boolean isSourceExtract(String source)
	{
		source = source.trim();
		// 如果不包含中文，或者是###开头，或者是空字符串，或者是（）？，那么不抽取
		if (!CharUtil.isChinese(source) || source.startsWith("###") || source.equals("") || source.equals("（")
				|| source.equals("）") || source.equals("？")
				 || source.equals("，")
				 || source.equals("："))
		{
			return false;
		} else
		{
			return true;
		}
	}
}
