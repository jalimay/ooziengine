package jalimay.cn.ooziengine.engine.impl;

import jalimay.cn.ooziengine.action.Action;
import jalimay.cn.ooziengine.action.ActionOperator.OperateCode;
import jalimay.cn.ooziengine.engine.AbstEngine;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

/**
 * 非并发的环形引擎。一共启动retryTime次随机顺序流程。每次启动随机顺序流程时候，依次执行Workflow中的每个Action。
 * 当所有Action都执行成功后才结束该次随机顺序流程。随机顺序流程中已经执行成功的Action将会从待执行列表中移除。
 * 若超过了retryTimes后，待执行列表中还有Action，认为引擎执行失败。
 * 
 * @author xieweiinfo
 */
public class LoopActionEngine extends AbstEngine {
	/**
	 * 重试次数
	 */
	private int retryTimes = 1;
	/**
	 * 每次重试的间隔时间
	 */
	private long retryHalfTime;

	public int getRetryTimes() {
		return retryTimes;
	}

	public long getRetryHalfTime() {
		return retryHalfTime;
	}

	private LoopActionEngine() {

	}

	/**
	 * 
	 * @param retryTimes
	 *            重试次数
	 * @param retryHalfTime
	 *            每次重试休息多久(毫秒)
	 * @return
	 */
	public static LoopActionEngine newInstance(Map<String, Action> actionMap, int retryTimes, long retryHalfTime) {
		LoopActionEngine engine = new LoopActionEngine();
		engine.setActionMap(actionMap);
		engine.retryTimes = retryTimes;
		engine.retryHalfTime = retryHalfTime;
		return engine;
	}

	@Override
	public ExecuteResult start() {
		Map<String, Action> actionMap = getActionMap();
		int totalActions = actionMap.size();
		log.info("Totally [" + totalActions + "] actions prepared to execute");
		Set<String> successedActionNames = new HashSet<String>();
		int retryedTimes = 0;
		while (true) {
			retryedTimes++;
			for (Entry<String, Action> entry : actionMap.entrySet()) { // 遍历所有Action，找到未执行成功过的
				String currentActionName = entry.getKey();
				// 这个Action已经被成功执行过了，就不再执行，跳到下个Action
				if (successedActionNames.contains(currentActionName))
					continue;
				log.info("Action [" + currentActionName + "] try " + retryedTimes + " times");
				Action action = entry.getValue();
				OperateCode oc = action.execute();

				switch (oc) {
				case SUCCESS:
					successedActionNames.add(currentActionName);
					break;
				case FAILED:
					log.error("Action [" + currentActionName + "] fail " + retryedTimes + " times");
					break;
				case KILL:
				default:
					return ExecuteResult.KILL;
				}

			}
			// 如果已经成功的Action和总共的Action数量相当，则任务全部执行完成
			if (successedActionNames.size() == totalActions) {
				log.info("The LoopActionEngine finished with no error!");
				return ExecuteResult.SUCCESS;
			}
			// 如果任务没有全部执行完，但超过重试次数了
			else if (retryedTimes >= getRetryTimes()) {
				log.info("Finished actions:" + successedActionNames);
				log.info("Unfinished actions:" + CollectionUtils.subtract(actionMap.keySet(), successedActionNames));
				log.error("LoopActionEngine retryed " + retryedTimes + " times, but still failed to finish the whole actionMap...It will be terminated.");
				return ExecuteResult.FAILED;
			}
			// 任务没有全部执行完，而且没有超过重试次数
			else {
				try {
					Thread.sleep(getRetryHalfTime());
				} catch (InterruptedException e) {
					return ExecuteResult.KILL;
				}
			}
			try {
				Thread.sleep(getHalfTime());
			} catch (InterruptedException e) {
				return ExecuteResult.KILL;
			}
		}
	}

}
