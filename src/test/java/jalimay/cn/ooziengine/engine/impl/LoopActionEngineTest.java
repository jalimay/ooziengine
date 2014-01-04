package jalimay.cn.ooziengine.engine.impl;

import jalimay.cn.ooziengine.action.Action;
import jalimay.cn.ooziengine.action.ActionOperator.OperateCode;
import jalimay.cn.ooziengine.engine.AbstEngine;
import jalimay.cn.ooziengine.engine.AbstEngine.ExecuteResult;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class LoopActionEngineTest {
	private Map<String, Action> actionMap;

	@Before
	public void init() {
		actionMap = new HashMap<String, Action>();
	}

	@Test
	public void zeroAction() {
		AbstEngine engine = LoopActionEngine.newInstance(actionMap, 1, 10);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.SUCCESS, er);
	}

	@Test
	public void twoAction() {
		Action actionA = Mockito.mock(Action.class);
		Mockito.when(actionA.execute()).thenReturn(OperateCode.SUCCESS);
		Action actionB = Mockito.mock(Action.class);
		Mockito.when(actionB.execute()).thenReturn(OperateCode.SUCCESS);
		actionMap.put("A", actionA);
		actionMap.put("B", actionB);
		AbstEngine engine = LoopActionEngine.newInstance(actionMap, 1, 10);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.SUCCESS, er);
	}

	@Test
	public void twoActionOneFailed() {
		Action actionA = Mockito.mock(Action.class);
		Mockito.when(actionA.execute()).thenReturn(OperateCode.SUCCESS);
		Action actionB = Mockito.mock(Action.class);
		Mockito.when(actionB.execute()).thenReturn(OperateCode.FAILED);
		actionMap.put("A", actionA);
		actionMap.put("B", actionB);
		AbstEngine engine = LoopActionEngine.newInstance(actionMap, 2, 10);
		engine.setHalfTime(5);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.FAILED, er);
	}
}
