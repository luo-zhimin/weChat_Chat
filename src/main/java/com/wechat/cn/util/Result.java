package com.wechat.cn.util;

public class Result<T> {

	/**
	 * 成功
	 */
	public static final Integer CODE_SUCCESS = 200;

	/**
	 * 未登录
	 */
	public static final Integer CODE_NO_LOGIN = 400;

	/**
	 * 没权限访问
	 */
	public static final Integer CODE_NO_AUTHORIZATION = 403;

	/**
	 * 系统异常
	 */
	public static final Integer CODE_SYS_ERROR = 500;
	

	private Integer code;

	private String message;

	private T data;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static Result<?> sysError() {
		Result<?> re = new Result<>();
		re.setCode(CODE_SYS_ERROR);
		re.setMessage("系统错误");
		return re;
	}

	public static Result<?> error(Integer errorCode, String msg) {
		Result<?> re = new Result<>();
		re.setCode(errorCode);
		re.setMessage(msg);
		return re;
	}

	public static Result<?> error(Integer errorCode) {
		return error(errorCode, "");
	}


	public static Result<?> success() {
		return success(null);
	}

	public static <T> Result<T> success(T data) {
		Result<T> re = new Result<T>();
		re.setData(data);
		re.setMessage("操作成功");
		re.setCode(CODE_SUCCESS);
		return re;
	}
	
	public static <T> Result<T> sysErrors() {
		Result<T> re = new Result<T>();
		re.setCode(CODE_SYS_ERROR);
		re.setMessage("系统错误");
		return re;
	}

	public static <T> Result<T> setData(int state , T data,String message){
		Result<T> re = new Result<T>();
		re.setData(data);
		re.setMessage(message);
		re.setCode(state);
		return re;
	}
}
