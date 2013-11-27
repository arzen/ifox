package com.arzen.ifox.bean;

import java.io.Serializable;

/**
 * 初始化bean
 * 
 * @author Encore.liang
 * 
 */
public class Init implements Serializable {

	private static final long serialVersionUID = 1L;
	// 返回码
	private int code;
	// 返回信息
	private String msg;
	// 初始化成功返回的内容
	private InitData data;

	/**
	 * 返回码
	 * @return 200为成功 其他都代表出错
	 */
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * 出错后的出错信息
	 * @return
	 */
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * 成功后的返回内容
	 * @return
	 */
	public InitData getData() {
		return data;
	}

	public void setData(InitData data) {
		this.data = data;
	}

	public static class InitData implements Serializable {
		private String gid;

		/**
		 * 游戏id
		 * 
		 * @return
		 */
		public String getGid() {
			return gid;
		}

		public void setGid(String gid) {
			this.gid = gid;
		}

	}

}
