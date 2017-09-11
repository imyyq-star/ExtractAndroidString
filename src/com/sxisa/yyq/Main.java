package com.sxisa.yyq;

import com.sxisa.utils.C;

public class Main
{
	public static void main(String[] args)
	{
		new ExtractStringFrame(
				
				C.JAVA_PATH,
				C.LAYOUT_PATH,
				C.STRINGS_PATH, 
				C.EN_STRINGS_PATH, 
				"SystemCache.context.getString",
				C.BAIDU_API_KEY);
//		 System.out.println(Utils.baiduTranslation("20160907000028349",
//				 "你好"));
	}
}
