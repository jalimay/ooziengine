package jalimay.cn.ooziengine.workflow;

import jalimay.cn.ooziengine.action.Action;
import jalimay.cn.ooziengine.engine.AbstEngine;
import jalimay.cn.ooziengine.engine.AbstEngine.ExecuteResult;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

/**
 * 定义一组业务操作的配置，可以使用xml,json或数据库来进行业务描述。定义了这组业务操作的引擎类型。
 * 
 * @author Jalimay
 * 
 */
public final class Workflow {
	/**
	 * 执行两个Action的间隔时间，默认30秒
	 */
	private long halfTime = 30 * DateUtils.MILLIS_PER_SECOND;
	/**
	 * 业务操作集合
	 */
	private final Map<String, Action> actionMap = new HashMap<String, Action>();
	/**
	 * 业务操作的引擎
	 */
	private AbstEngine engine;

	/**
	 * 让工作流内置的引擎开始工作
	 * 
	 * @return
	 */
	public ExecuteResult start() {
		return engine.start();
	}

	public void setEngine(AbstEngine engine) {
		this.engine = engine;
	}

	public long getHalfTime() {
		return halfTime;
	}

	public void setHalfTime(long halfTime) {
		this.halfTime = halfTime;
	}

	public Map<String, Action> getActionMap() {
		return actionMap;
	}

	public void addAction(String name, Action action) {
		this.actionMap.put(name, action);
	}

}
