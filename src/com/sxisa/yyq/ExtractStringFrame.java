package com.sxisa.yyq;

import java.awt.BorderLayout;
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

	// 抽取结果，文件名对应着文件中的行号及该行内容
	private Map<String, Map<Integer, String>> resultMap = new HashMap<>();
	// 存入strings.xml中的，使用Set可以保证不重复
	private Set<String> resultStringsSet = new HashSet<>();

	private boolean isLayoutSearchOver = false;
	private boolean isJavaSearchOver = false;

	// ================================================

	private JLabel javaPathLabel = new JLabel("Java文件所在的根目录");
	private JTextField javaPathField = new JTextField(40);

	private JLabel layoutPathLabel = new JLabel("Layout文件所在的根目录");
	private JTextField layoutPathField = new JTextField(40);

	private JLabel stringsPathLabel = new JLabel("strings.xml文件绝对路径");
	private JTextField stringsPathField = new JTextField(40);

	private JLabel apiKeyLabel = new JLabel("百度翻译的APIKey");
	private JTextField apiKeyPathField = new JTextField(40);

	private JButton startSearchButton = new JButton("开始检索");

	private JCheckBox allExtractCheckbox = new JCheckBox("全部抽取");

	private JButton startExtractButton = new JButton("开始抽取");

	// ================================================

	private JPanel centerPanel = new JPanel();

	public ExtractStringFrame() throws HeadlessException
	{
		super("ExtractAndroidStrings - v1.0");
		init();
	}

	public ExtractStringFrame(String title) throws HeadlessException
	{
		super(title);
		init();
	}

	public ExtractStringFrame(String javaPath, String layoutPath, String stringsPath, String apiKey)
			throws HeadlessException
	{
		super("ExtractAndroidStrings - v1.0");
		javaPathField.setText(javaPath);
		layoutPathField.setText(layoutPath);
		stringsPathField.setText(stringsPath);
		apiKeyPathField.setText(apiKey);
		init();
	}

	private void init()
	{
		// 整个窗体默认的布局是区域布局
		setLayout(new BorderLayout());

		// 设置监听
		startSearchButton.addActionListener(new StartSearchBtnListener());
		startExtractButton.addActionListener(new StartExtractBtnListener());
		allExtractCheckbox.addItemListener(new AllCheckListener());

		// =============================================================================

		/*
		 * 顶部的面板
		 */
		Dimension dimension = new Dimension(150, 40);

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

		JPanel javaPathPanel = new JPanel();
		javaPathPanel.setLayout(new BoxLayout(javaPathPanel, BoxLayout.X_AXIS));
		javaPathLabel.setPreferredSize(dimension);
		javaPathPanel.add(javaPathLabel);
		javaPathPanel.add(javaPathField);
		northPanel.add(javaPathPanel);

		JPanel layoutPathPanel = new JPanel();
		layoutPathPanel.setLayout(new BoxLayout(layoutPathPanel, BoxLayout.X_AXIS));
		layoutPathLabel.setPreferredSize(dimension);
		layoutPathPanel.add(layoutPathLabel);
		layoutPathPanel.add(layoutPathField);
		northPanel.add(layoutPathPanel);

		JPanel stringsPathPanel = new JPanel();
		stringsPathPanel.setLayout(new BoxLayout(stringsPathPanel, BoxLayout.X_AXIS));
		stringsPathLabel.setPreferredSize(dimension);
		stringsPathPanel.add(stringsPathLabel);
		stringsPathPanel.add(stringsPathField);
		northPanel.add(stringsPathPanel);

		JPanel apiKeyPanel = new JPanel();
		apiKeyPanel.setLayout(new BoxLayout(apiKeyPanel, BoxLayout.X_AXIS));
		apiKeyLabel.setPreferredSize(dimension);
		apiKeyPanel.add(apiKeyLabel);
		apiKeyPanel.add(apiKeyPathField);
		northPanel.add(apiKeyPanel);

		// 开始检索按钮
		JPanel startSearchPanel = new JPanel();
		startSearchPanel.add(startSearchButton);
		northPanel.add(startSearchPanel);

		// =========================================

		JPanel tipsPanel = new JPanel();
		tipsPanel.setLayout(new GridLayout(1, 3));

		allExtractCheckbox.setPreferredSize(new Dimension(140, 50));
		allExtractCheckbox.setSelected(true);

		tipsPanel.add(allExtractCheckbox);
		tipsPanel.add(new JLabel("源字符串"));
		tipsPanel.add(new JLabel("目标字符串", JLabel.CENTER));

		northPanel.add(tipsPanel);

		this.add(northPanel, BorderLayout.NORTH);

		// =============================

		/*
		 * 抽取结果
		 */
		JScrollPane centerScrollPane = new JScrollPane();
		centerScrollPane.setLayout(new ScrollPaneLayout());
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		// for (int i = 0; i < 14; i++)
		// {
		// addItem("jhad", 2, "fsjdlf", "fjsado", "fjsdlf");
		// }

		centerScrollPane.setViewportView(centerPanel);
		this.add(centerScrollPane, BorderLayout.CENTER);

		// ===============================================

		/*
		 * 底部的开始抽取按钮
		 */
		JPanel southPanel = new JPanel();
		southPanel.add(startExtractButton);
		this.add(southPanel, BorderLayout.SOUTH);

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
	 * @param fileNameString
	 * @param lineNumber
	 * @param line
	 * @param target
	 * @param replacement
	 */
	private void addItem(String fileNameString, int lineNumber, String line, String target, String replacement)
	{
		CustomJPanel tempPanel = new CustomJPanel();
		tempPanel.setLayout(new BorderLayout(10, 40));

		JCheckBox tempCheckBox = new JCheckBox("是否抽取");
		tempCheckBox.setSelected(true);
		tempPanel.add(tempCheckBox, BorderLayout.WEST);

		tempPanel.add(new JLabel(target, JLabel.CENTER), BorderLayout.CENTER);
		tempPanel.add(new JTextField(replacement, 21), BorderLayout.EAST);
		tempPanel.setPreferredSize(new Dimension(0, 40));
		tempPanel.setTag(new ItemInfo(fileNameString, lineNumber, line));
		centerPanel.add(tempPanel);
	}

	/**
	 * 开始检索Layout文件
	 */
	private void startSearchLayout()
	{
		startSearchButton.setText("正在检索，请稍候...");
		new Thread(new StartSearchLayoutRunnable()).start();
	}

	/**
	 * 开始检索Java文件
	 */
	private void startSearchJava()
	{
		startSearchButton.setText("正在检索，请稍候...");
		new Thread(new StartSearchJavaRunnable()).start();
	}

	/**
	 * 开始检索
	 * 
	 * @author Administrator
	 *
	 */
	class StartSearchBtnListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (startSearchButton.getText().contains("正在检索"))
			{
				return;
			}
			if (javaPathField.getText().isEmpty() && layoutPathField.getText().isEmpty())
			{
				JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(),
						"请至少输入Java或Layout文件任意一个的根目录", "错误提示", JOptionPane.NO_OPTION);
			} else if (stringsPathField.getText().isEmpty())
			{
				JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(),
						"请输入" + javaPathLabel.getText(), "错误提示", JOptionPane.NO_OPTION);
			} else if (apiKeyPathField.getText().isEmpty())
			{
				JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(),
						"请输入" + apiKeyLabel.getText(), "错误提示", JOptionPane.NO_OPTION);
			} else
			{
				File javaFile = new File(javaPathField.getText());
				File layoutFile = new File(layoutPathField.getText());
				if (!javaPathField.getText().isEmpty() && !javaFile.isDirectory())
				{
					JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(), "请输入正确的Java文件根目录",
							"错误提示", JOptionPane.NO_OPTION);
					return;
				}
				if (!layoutPathField.getText().isEmpty() && !layoutFile.isDirectory())
				{
					JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(), "请输入正确的Layout文件根目录",
							"错误提示", JOptionPane.NO_OPTION);
					return;
				}
				File stringsXMLFile = new File(stringsPathField.getText());
				if (!stringsPathField.getText().isEmpty() && !stringsXMLFile.isFile())
				{
					JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(), "请输入正确的strings文件路径",
							"错误提示", JOptionPane.NO_OPTION);
					return;
				}
				// TODO 开始抽取，先检测strings.xml是否有重复的字符串
				centerPanel.removeAll();
				resultMap.clear();
				String s = Utils.getStringsXMLRepeat(stringsPathField.getText());
				if (s != null)
				{
					JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(),
							"strings.xml中发现有重复的字符串，以下为详细内容\n" + s, "发现重复的字符串", JOptionPane.NO_OPTION);
					ExtractStringFrame.this.pack();
				} else
				{
					if (!javaPathField.getText().isEmpty() && !layoutPathField.getText().isEmpty())
					{
						startSearchJava();
						startSearchLayout();
						isJavaSearchOver = false;
						isLayoutSearchOver = false;
					} else
					{
						if (!javaPathField.getText().isEmpty())
						{
							startSearchJava();
							isJavaSearchOver = false;
							isLayoutSearchOver = true;
						} else if (!layoutPathField.getText().isEmpty())
						{
							startSearchLayout();
							isLayoutSearchOver = false;
							isJavaSearchOver = true;
						}
					}
				}
			}
		}
	}

	/**
	 * 开始抽取
	 */
	class StartExtractBtnListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			File stringsXMLFile = new File(stringsPathField.getText());
			if (!stringsPathField.getText().isEmpty() && !stringsXMLFile.isFile())
			{
				JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(), "请输入正确的strings文件路径",
						"错误提示", JOptionPane.NO_OPTION);
				return;
			}
			// 如果滑动面板中没有项，就提示
			if (centerPanel.getComponentCount() == 0)
			{
				JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(), "没有可以抽取的字符串", "温馨提示",
						JOptionPane.NO_OPTION);
			} else
			{
				resultStringsSet.clear();
				isJavaSearchOver = false;
				isLayoutSearchOver = false;

				for (int i = 0; i < centerPanel.getComponentCount(); i++)
				{
					CustomJPanel jPanel = (CustomJPanel) centerPanel.getComponent(i);
					ItemInfo fileInfo = (ItemInfo) jPanel.getTag();
					JCheckBox jCheckBox = (JCheckBox) jPanel.getComponent(0);
					JLabel jLabel = (JLabel) jPanel.getComponent(1);
					JTextField jTextField = (JTextField) jPanel.getComponent(2);

					// 如果选择了抽取，并且文本框内不为空，则抽取
					if (jCheckBox.isSelected() && !jTextField.getText().isEmpty())
					{
						// 文件已存在，取出对应的行号集合。行号不存在才添加
						if (resultMap.containsKey(fileInfo.getFilePath()))
						{
							// 取出文件对应的行号集合
							Map<Integer, String> tempMap = resultMap.get(fileInfo.getFilePath());
							if (!tempMap.containsKey(fileInfo.getLineNumber()))
							{
								if (fileInfo.getFilePath().endsWith(".java"))
								{
									tempMap.put(fileInfo.getLineNumber(), fileInfo.getLine().replace(jLabel.getText(),
											"getString(R.string." + jTextField.getText() + ")"));
								} else if (fileInfo.getFilePath().endsWith(".xml"))
								{
									tempMap.put(fileInfo.getLineNumber(), fileInfo.getLine().replace(jLabel.getText(),
											"@string/" + jTextField.getText()));
								}
								resultStringsSet.add("<string name=\"" + jTextField.getText() + "\">" + jLabel.getText()
										+ "</string>");
							}
						}
						// 文件不存在，新建行号集合
						else
						{
							Map<Integer, String> tempMap = new HashMap<>();
							if (fileInfo.getFilePath().endsWith(".java"))
							{
								tempMap.put(fileInfo.getLineNumber(), fileInfo.getLine().replace(jLabel.getText(),
										"getString(R.string." + jTextField.getText() + ")"));
							} else if (fileInfo.getFilePath().endsWith(".xml"))
							{
								tempMap.put(fileInfo.getLineNumber(), fileInfo.getLine().replace(jLabel.getText(),
										"@string/" + jTextField.getText()));
							}
							resultMap.put(fileInfo.getFilePath(), tempMap);
							resultStringsSet.add(
									"<string name=\"" + jTextField.getText() + "\">" + jLabel.getText() + "</string>");
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
							new InputStreamReader(new FileInputStream(stringsPathField.getText())));
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
							new OutputStreamWriter(new FileOutputStream(stringsPathField.getText())));
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
				centerPanel.removeAll();
				ExtractStringFrame.this.pack();
				JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(), "抽取完成", "温馨提示",
						JOptionPane.NO_OPTION);
			}
		}
	}

	/**
	 * 全部抽取
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
			for (int i = 0; i < centerPanel.getComponentCount(); i++)
			{
				// 强制转换为JPanel
				JPanel j = (JPanel) centerPanel.getComponent(i);
				JCheckBox checkBox = (JCheckBox) j.getComponent(0);
				checkBox.setSelected(jcb.isSelected());
			}
		}
	}

	private class StartSearchLayoutRunnable implements Runnable
	{
		@Override
		public void run()
		{
			// 取得所有文件的对象
			final List<File> files = Utils.getAllFile(Paths.get(layoutPathField.getText()));

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
						Matcher textMatcher = null;
						Matcher hintMatcher = null;
						while ((readLine = reader.readLine()) != null)
						{
							textMatcher = textPattern.matcher(readLine);
							hintMatcher = hintPattern.matcher(readLine);
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
										addItem(file.getAbsolutePath(), reader.getLineNumber(), readLine,
												matchingString, translationString);
									}
								}
							}
							if (hintMatcher.find())
							{
								String matchingString = hintMatcher.group(1);
								if (matchingString != null && !matchingString.equals("")
										&& !matchingString.startsWith("@string/"))
								{
									String translationString = Utils.baiduTranslation(apiKeyPathField.getText(),
											matchingString);
									if (translationString != null)
									{
										addItem(file.getAbsolutePath(), reader.getLineNumber(), readLine,
												matchingString, translationString);
									}
								}
							}
						}
						reader.close();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			} // for End

			isLayoutSearchOver = true;

			ExtractStringFrame.this.pack();

			if (isJavaSearchOver)
			{
				startSearchButton.setText("开始检索");
				// 如果检索后发现面板数量依然为0，说明没有找到可以抽取的
				if (centerPanel.getComponentCount() == 0)
				{
					JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(),
							"Layout文件中未发现可以抽取的字符串", "检索结果", JOptionPane.NO_OPTION);
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
		}
	}

	private class StartSearchJavaRunnable implements Runnable
	{
		@Override
		public void run()
		{
			// 取得所有文件的对象
			final List<File> files = Utils.getAllFile(Paths.get(javaPathField.getText()));

			// 通过正则，找出所有被双引号""包含的字符串
			Pattern pattern = Pattern.compile("\"(.*?)\"");

			for (File file : files)
			{
				// 是文件并且是java文件
				if (file.isFile() && file.getName().endsWith(".java"))
				{
					try
					{
						// 如果不是有效文件就略过
						if (!Utils.isValidJavaFile(file))
						{
							continue;
						}
						// 一行一行的读取
						LineNumberReader reader = new LineNumberReader(
								new InputStreamReader(new FileInputStream(file)));
						String readLine = null;
						while ((readLine = reader.readLine()) != null)
						{
							// 找出被双引号包含起来的字符串
							Matcher matcher = pattern.matcher(readLine);
							if (matcher.find())
							{
								// 判断字符串是否有可以抽取的地方
								if (!isLineExtract(readLine))
								{
									continue;
								}
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
									addItem(file.getAbsolutePath(), reader.getLineNumber(), readLine, matchingString,
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

			isJavaSearchOver = true;

			ExtractStringFrame.this.pack();

			if (isLayoutSearchOver)
			{
				startSearchButton.setText("开始检索");
				// 如果检索后发现面板数量依然为0，说明没有找到可以抽取的
				if (centerPanel.getComponentCount() == 0)
				{
					JOptionPane.showInternalMessageDialog(ExtractStringFrame.this.getContentPane(),
							"Java文件中未发现可以抽取的字符串", "检索结果", JOptionPane.NO_OPTION);
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
		}
	}

	/**
	 * 是否抽取此行中的字符串到strings.xml中
	 * 
	 * @param line
	 * @return false代表不抽取
	 */
	static boolean isLineExtract(String line)
	{
		if (line.trim().startsWith("Log.") || line.trim().startsWith("System.out.") || line.trim().startsWith("//")
				|| line.trim().startsWith("* ") || line.trim().startsWith("System.err."))
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
		// 如果不包含中文，或者是###开头，或者是空字符串，或者是（）？，那么不抽取
		if (!CharUtil.isChinese(source) || source.trim().startsWith("###") || source.equals("") || source.equals("（")
				|| source.equals("）") || source.equals("？"))
		{
			return false;
		} else
		{
			return true;
		}
	}
}
