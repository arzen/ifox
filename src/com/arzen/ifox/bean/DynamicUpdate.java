package com.arzen.ifox.bean;

import java.io.Serializable;

public class DynamicUpdate implements Serializable {
	private static final long serialVersionUID = 1L;
	// 返回码
	private int code;
	// 返回信息
	private String msg;

	private DynamicData data;

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

	public DynamicData getData() {
		return data;
	}

	public void setData(DynamicData data) {
		this.data = data;
	}

	public static class DynamicData implements Serializable {
		// 是否有新bane不能
		private String latest;
		// 新版本特征提示
		private String msg;
		// 下载地址
		private String url;

		public String getLatest() {
			return latest;
		}

		public void setLatest(String latest) {
			this.latest = latest;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}
}
