package com.pb.neo4j.training.model;

@SuppressWarnings("serial")
public class NeoSampleRuntimeException extends RuntimeException{

	public NeoSampleRuntimeException(String messageKey) {
		super(messageKey);
	}

	public NeoSampleRuntimeException(String messageKey, Throwable cause) {
		super(messageKey, cause);
	}

	public NeoSampleRuntimeException(Throwable cause) {
		super(cause);
	}

}
