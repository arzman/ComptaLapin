package org.arthur.compta.lapin.application.exception;

public class ComptaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2812849133067757807L;

	public ComptaException(String string, Exception e) {
		super(string,e);
	}

	public ComptaException(String string) {
		super();
	}

}
