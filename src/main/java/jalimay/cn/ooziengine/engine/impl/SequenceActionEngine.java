package jalimay.cn.ooziengine.engine.impl;

import jalimay.cn.ooziengine.action.Action;
import jalimay.cn.ooziengine.action.ActionOperator.OperateCode;
import jalimay.cn.ooziengine.engine.AbstEngine;

import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;

/**
 * 按顺序执行Workflow的Engine。用这个Engine执行的ActionMap，每个Action的配置中需指定成功与失败的跳转Action。
 * 
 * @author xieweiinfo
 */
public class SequenceActionEngine extends AbstEngine {
	/**
	 * 代表结束运行的action name
	 * 
	 * @author Jalimay
	 * 
	 */
	public static enum EOF {
		/**
		 * 表示所有需要执行的都已经成功完成了
		 */
		end,
		/**
		 * 表示运行过程中失败了，结束流程，并且向外抛出异常
		 */
		fail;
	}

	private SequenceActionEngine() {
	}

	/**
	 * 指定了起始Action的顺序执行引擎，该引擎start后将会一直运行，直到某个Action定义了exit的jump动作(sucJump or
	 * errJump)，或被kill掉。
	 * 
	 */
	public static SequenceActionEngine newInstance(Map<String, Action> actionMap, Map<String, Jump> jumpMap, String startActionName) {
		return newInstance(actionMap, jumpMap, startActionName, EOF.end.name());
	}

	/**
	 * 指定了起始Action与结束Action的顺序执行引擎，该引擎start后将会一直运行，直到某个Action定义了exit的jump动作(
	 * sucJump or errJump)，或被kill掉，或者运行到来和endActionName相同的一个Action。
	 * 
	 */
	public static SequenceActionEngine newInstance(Map<String, Action> actionMap, Map<String, Jump> jumpMap, String startActionName, String endActionName) {
		SequenceActionEngine engine = new SequenceActionEngine();
		engine.setActionMap(actionMap);
		engine.startActionName = startActionName;
		engine.endActionName = endActionName;
		engine.jumpMap = jumpMap;
		return engine;
	}

	private String startActionName;
	private String endActionName;
	private Map<String, Jump> jumpMap;

	/**
	 * 按顺序执行Workflow中的所有Action
	 * 
	 * @throws InterruptedException
	 * @throws NoSuchElementException
	 */
	@Override
	public ExecuteResult start() {
		String currentActionName = startActionName;
		Map<String, Action> actions = getActionMap();
		while (true) {
			// 如果没有提前预置标志结束的ActionName，则使用end。如果当前actionName等于标志结束的ActionName，则退出并认为引擎成功结束
			if (StringUtils.equals(currentActionName, endActionName)) {
				log.info("Meet action [" + currentActionName + "], end the SequenceActionEngine.");
				return ExecuteResult.SUCCESS;
			}
			// 如果当前actionName等于标志运行失败的ActionName，退出引擎并抛出异常
			if (StringUtils.equals(currentActionName, EOF.fail.name())) {
				log.error("Meet action [fail], stop the SequenceActionEngine");
				return ExecuteResult.FAILED;
			}
			// 如果要运行的actionName在工作计划中不存在，则抛出异常
			if (!actions.containsKey(currentActionName)) {
				log.error("Doesn't found any action named [" + currentActionName + "] in this Workflow.");
				return ExecuteResult.FAILED;
			}
			log.info("Current action is [" + currentActionName + "]");
			Action action = actions.get(currentActionName);
			OperateCode oc = action.execute();
			log.info("Action [" + currentActionName + "] is " + oc.name());
			String nextAction = null;
			Jump j = jumpMap.get(currentActionName);
			switch (oc) {
			case SUCCESS:
				nextAction = j.getSucJumpTo();
				break;
			case FAILED:
				nextAction = j.getErrJumpTo();
				break;
			case KILL:
			default:
				log.info("Kill the engine.");
				return ExecuteResult.KILL;
			}
			currentActionName = nextAction;

			try {
				Thread.sleep(getHalfTime());
			} catch (InterruptedException e) {
				return ExecuteResult.KILL;
			}
		}
	}

	/**
	 * 某个Action执行完成后的跳转动作
	 * 
	 * @author Jalimay
	 * 
	 */
	public static class Jump {
		private String sucJumpTo;
		private String errJumpTo;

		private Jump() {
		}

		public static Jump newInstance(String suc, String err) {
			Jump j = new Jump();
			j.sucJumpTo = suc;
			j.errJumpTo = err;
			return j;
		}

		public String getSucJumpTo() {
			return sucJumpTo;
		}

		public String getErrJumpTo() {
			return errJumpTo;
		}
	}

}
