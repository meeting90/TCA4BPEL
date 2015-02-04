package cn.edu.nju.cs.tcao4bpel.alang;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * InterpreterException.java
 */
public class InterpreterException extends RuntimeException{

	
	

	/**
	 * 
	 */
	public InterpreterException() {
		super();
	}
	/**
	 * @param message
	 * @param cause
	 */
	public InterpreterException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param message
	 */
	public InterpreterException(String message) {
		super(message);
	}
	/**
	 * @param cause
	 */
	public InterpreterException(Throwable cause) {
		super(cause);
	}
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -882545319422455842L;

}
