package com.arzen.ifox.bean;

import java.io.Serializable;

public class Share implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// 返回码
	private int code;
	// 返回信息
	private String msg;

	private Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
	
	

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}



	public static class Data implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String msg;

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

	}
}
