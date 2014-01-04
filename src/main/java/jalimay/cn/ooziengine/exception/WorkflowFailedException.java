package jalimay.cn.ooziengine.exception;

/**
 * Workflow执行到fail action时候抛出
 * 
 * @author Jalimay
 * 
 */
@Deprecated
public class WorkflowFailedException extends Exception {
	private static final long serialVersionUID = 1L;

	public WorkflowFailedException(String msg) {
		super(msg);
	}
}
