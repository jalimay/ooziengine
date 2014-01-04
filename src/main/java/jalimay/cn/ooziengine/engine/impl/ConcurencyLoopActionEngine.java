package jalimay.cn.ooziengine.engine.impl;

import jalimay.cn.ooziengine.action.Action;
import jalimay.cn.ooziengine.action.ActionOperator.OperateCode;
import jalimay.cn.ooziengine.engine.AbstEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 并发的环形引擎。一共启动retryTime次批量线程。每次启动批量线程时候，为Workflow中每个Action都开启一个独立的线程运行。
 * 当所有Action都执行成功后才结束该次批量线程。批量线程中已经执行成功的Action将会从待执行列表中移除。
 * 若超过了retryTimes后，待执行列表中还有Action，认为引擎执行失败。
 * 
 * ConcurencyLoopActionEngine.java
 * 
 * Created on 2013-10-17
 * 
 * Copyright(C) 2013, by 360buy.com.
 * 
 * Original Author: zhaolinhu Contributor(s):
 * 
 * Changes ------- $Log$
 * 
 */
public class ConcurencyLoopActionEngine extends AbstEngine {
	/**
	 * 重试次数
	 */
	private int retryTimes = 1;
	/**
	 * 每次重试的间隔时间
	 */
	private long retryHalfTime;

	public static ConcurencyLoopActionEngine newInstance(Map<String, Action> actionMap, int retryTimes, long retryHalfTime) {
		ConcurencyLoopActionEngine engine = new ConcurencyLoopActionEngine();
		engine.setActionMap(actionMap);
		engine.retryTimes = retryTimes;
		engine.retryHalfTime = retryHalfTime;
		return engine;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public long getRetryHalfTime() {
		return retryHalfTime;
	}

	@Override
	public ExecuteResult start() {
		// 并行提交工作流，任务完成后无需等待
		Map<String, Action> actionMap = getActionMap();

		for (int i = 0; i < getRetryTimes(); i++) {
			if (actionMap == null || actionMap.size() <= 0) {
				log.info("The ConcurrencyLoopActionEngine finished with no error!");
				return ExecuteResult.SUCCESS;
			}
			CountDownLatch startLatch = new CountDownLatch(1);
			CountDownLatch endLatch = new CountDownLatch(actionMap.size());
			ExecutorService exe = Executors.newFixedThreadPool(actionMap.size());
			Map<String, Future<OperateCode>> futureMap = new HashMap<String, Future<OperateCode>>();
			for (Entry<String, Action> entry : actionMap.entrySet()) {
				log.info("Action [" + entry.getKey() + "] retry " + (i + 1) + " times");
				Future<OperateCode> future = exe.submit(new ActionCallable(entry.getValue(), startLatch, endLatch));
				futureMap.put(entry.getKey(), future);
			}
			startLatch.countDown();
			try {
				endLatch.await();
				for (Entry<String, Future<OperateCode>> entry : futureMap.entrySet()) {
					try {
						switch (entry.getValue().get()) {
						case SUCCESS:
							log.info("Action [" + entry.getKey() + "] success ");
							actionMap.remove(entry.getKey());
							break;
						case FAILED:
							log.info("Action [" + entry.getKey() + "] failed " + (i + 1) + " times");
							break;
						case KILL:
						default:
							return ExecuteResult.KILL;
						}
					} catch (ExecutionException e) {
						log.info("Action [" + entry.getKey() + "] failed " + (i + 1) + " times");
					}
				}
				TimeUnit.MILLISECONDS.sleep(getRetryHalfTime());
			} catch (InterruptedException e) {
				log.error("Stop engine", e);
				return ExecuteResult.KILL;
			} finally {
				exe.shutdown();
			}
		}
		if (actionMap == null || actionMap.size() <= 0) {
			log.info("The ConcurrencyLoopActionEngine finished with no error!");
			return ExecuteResult.SUCCESS;
		} else {
			log.error("Unfinish Actions:" + actionMap.keySet());
			return ExecuteResult.FAILED;
		}
	}

	class ActionCallable implements Callable<OperateCode> {
		private Action action;

		// 开始执行工作任务信号量
		private CountDownLatch startLatch;

		// 工作任务完成信号量
		private CountDownLatch endLatch;

		public ActionCallable(Action action, CountDownLatch startLatch, CountDownLatch endLatch) {

			super();
			this.action = action;
			this.startLatch = startLatch;
			this.endLatch = endLatch;
		}

		@Override
		public OperateCode call() throws InterruptedException {
			try {
				// 等待开始信号
				startLatch.await();
				return action.execute();
			} finally {
				// 通知完成信号量，该子任务完成
				endLatch.countDown();
			}
		}

	}
}
