package com.eta.transport;

public class LogingResponse extends Response {
	Boolean success;

	public LogingResponse(Boolean success) {
		super();
		this.success = success;
	}
	
	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	@Override
	public String toString() {
		return "LogingResponse [success=" + success + "]";
	}
}
