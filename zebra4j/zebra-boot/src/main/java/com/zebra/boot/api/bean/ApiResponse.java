package com.zebra.boot.api.bean;

/**
 * 封装Api Restfull 返回Json
 * Created by lzy@js-dev.cn on 2016/11/17 0017.
 */
public class ApiResponse<T> {

	/**
	 * SUCCESS = 1
	 */
	public static final int SUCCESS = 1;

	/**
	 * ERROR = 0
	 */
	public static final int ERROR = 0;

	/**
	 * 错误编码
	 */
	private int code;

	/**
	 * 提示信息
	 */
	private String msg;

	/**
	 * 自带数据
	 */
	private T data;

	/**
	 * 返回成功信息
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> ApiResponse<T> sucess(T data){
		ApiResponse<T> resp = new ApiResponse<T>();
		resp.setCode(SUCCESS);
		resp.setData(data);
		return resp;
	}

	/**
	 * 返回错误信息
	 * @param errorMsg
	 * @param <T>
	 * @return
	 */
	public static <T> ApiResponse<T> error(String errorMsg){
		return ApiResponse.<T>error(errorMsg, ERROR);

	}

	/**
	 * 返回含有错误码的响应
	 * @param errorMsg
	 * @param code
	 * @param <T>
	 * @return
	 */
	private static <T> ApiResponse<T> error(String errorMsg, int code) {
		ApiResponse<T> resp = new ApiResponse<T>();
		resp.setCode(code);
		resp.setMsg(errorMsg);
		return resp;
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

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}



}
