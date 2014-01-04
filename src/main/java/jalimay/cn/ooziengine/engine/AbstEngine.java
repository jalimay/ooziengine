package jalimay.cn.ooziengine.engine;

import jalimay.cn.ooziengine.action.Action;

import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

/**
 * 一个执行workflow的引擎，它的任务是把workflow完整执行完成。每个workflow对象中内置一种执行引擎。
 * 
 * @author xieweiinfo
 */
public abstract class AbstEngine {
	protected final Logger log = Logger.getLogger(getClass());
	/**
	 * 每执行两个Action间留给引擎休息的时间，默认30秒。
	 */
	private long halfTime = 30 * DateUtils.MILLIS_PER_SECOND;
	/**
	 * 引擎此次需要执行的任务
	 */
	private Map<String, Action> actionMap;

	public Map<String, Action> getActionMap() {
		return actionMap;
	}

	public void setActionMap(Map<String, Action> actionMap) {
		this.actionMap = actionMap;
	}

	public long getHalfTime() {
		return halfTime;
	}

	public void setHalfTime(long halfTime) {
		this.halfTime = halfTime;
	}

	/**
	 * 让引擎开始工作。工作计划已经提前预置在引擎内部。
	 * 
	 */
	public abstract ExecuteResult start();

	/**
	 * 引擎的工作结果
	 * 
	 * @author Jalimay
	 * 
	 */
	public static enum ExecuteResult {
		SUCCESS, FAILED, KILL;
	}

	public static enum EngineType {
		Sequence, Loop, ConcurencyLoop;
	}

}
