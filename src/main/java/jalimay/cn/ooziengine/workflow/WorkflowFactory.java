package jalimay.cn.ooziengine.workflow;

import java.util.Map;

/**
 * 一个Workflow对象的生产工厂
 * 
 * @author Jalimay
 * 
 */
public interface WorkflowFactory {
	/**
	 * 传入workflow的变量。生成workflow的参数时候可能会使用到这些变量。
	 * 
	 * @param args
	 * @return
	 */
	public Workflow newInstance(Map<String, String> args);
}
