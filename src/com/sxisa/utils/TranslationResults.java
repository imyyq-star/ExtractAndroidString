package com.sxisa.utils;
import java.util.List;

public class TranslationResults
{

	/**
	 * from : zh to : en trans_result : [{"src":"六","dst":"Six"}]
	 */

	private String from;
	private String to;
	/**
	 * src : 六 dst : Six
	 */

	private List<TransResultEntity> trans_result;

	public void setFrom(String from)
	{
		this.from = from;
	}

	public void setTo(String to)
	{
		this.to = to;
	}

	public void setTrans_result(List<TransResultEntity> trans_result)
	{
		this.trans_result = trans_result;
	}

	public String getFrom()
	{
		return from;
	}

	public String getTo()
	{
		return to;
	}

	public List<TransResultEntity> getTrans_result()
	{
		return trans_result;
	}

	public static class TransResultEntity
	{
		private String src;
		private String dst;

		public void setSrc(String src)
		{
			this.src = src;
		}

		public void setDst(String dst)
		{
			this.dst = dst;
		}

		public String getSrc()
		{
			return src;
		}

		public String getDst()
		{
			return dst;
		}
	}
}
