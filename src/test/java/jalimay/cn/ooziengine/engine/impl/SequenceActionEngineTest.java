package jalimay.cn.ooziengine.engine.impl;

import jalimay.cn.ooziengine.action.Action;
import jalimay.cn.ooziengine.action.ActionOperator.OperateCode;
import jalimay.cn.ooziengine.engine.AbstEngine;
import jalimay.cn.ooziengine.engine.AbstEngine.ExecuteResult;
import jalimay.cn.ooziengine.engine.impl.SequenceActionEngine.EOF;
import jalimay.cn.ooziengine.engine.impl.SequenceActionEngine.Jump;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SequenceActionEngineTest {
	private Map<String, Jump> jumpMap;
	private Map<String, Action> actionMap;

	@Before
	public void init() {
		actionMap = new HashMap<String, Action>();
		jumpMap = new HashMap<String, Jump>();
	}

	@Test
	public void end() {
		String startActionName = EOF.end.name();
		AbstEngine engine = SequenceActionEngine.newInstance(actionMap, jumpMap, startActionName);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.SUCCESS, er);
	}

	@Test
	public void end2() {
		String startActionName = "B";
		String endActionName = startActionName;
		AbstEngine engine = SequenceActionEngine.newInstance(actionMap, jumpMap, startActionName, endActionName);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.SUCCESS, er);
	}

	@Test
	public void fail() {
		String startActionName = EOF.fail.name();
		AbstEngine engine = SequenceActionEngine.newInstance(actionMap, jumpMap, startActionName);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.FAILED, er);
	}

	@Test
	public void notFoundAction() {
		String startActionName = "A";
		AbstEngine engine = SequenceActionEngine.newInstance(actionMap, jumpMap, startActionName);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.FAILED, er);
	}

	@Test
	public void actionSuccess() {
		String startActionName = "A";
		Action action = Mockito.mock(Action.class);
		Mockito.when(action.execute()).thenReturn(OperateCode.SUCCESS);
		actionMap.put(startActionName, action);

		Jump jump = Jump.newInstance(EOF.end.name(), EOF.fail.name());
		jumpMap.put(startActionName, jump);
		AbstEngine engine = SequenceActionEngine.newInstance(actionMap, jumpMap, startActionName);
		engine.setHalfTime(1);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.SUCCESS, er);
	}

	@Test
	public void actionList() {
		String startActionName = "A";
		String secondName = "B";
		Action actionA = Mockito.mock(Action.class);
		Action actionB = Mockito.mock(Action.class);
		Mockito.when(actionA.execute()).thenReturn(OperateCode.SUCCESS);
		Mockito.when(actionB.execute()).thenReturn(OperateCode.SUCCESS);
		actionMap.put(startActionName, actionA);
		actionMap.put(secondName, actionB);

		Jump jumpA = Jump.newInstance(secondName, EOF.fail.name());
		Jump jumpB = Jump.newInstance(EOF.end.name(), EOF.fail.name());
		jumpMap.put(startActionName, jumpA);
		jumpMap.put(secondName, jumpB);
		AbstEngine engine = SequenceActionEngine.newInstance(actionMap, jumpMap, startActionName);
		engine.setHalfTime(1);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.SUCCESS, er);
	}

	@Test
	public void actionFail() {
		String startActionName = "A";
		Action action = Mockito.mock(Action.class);
		Mockito.when(action.execute()).thenReturn(OperateCode.FAILED);
		actionMap.put(startActionName, action);

		Jump jump = Jump.newInstance(EOF.end.name(), EOF.fail.name());
		jumpMap.put(startActionName, jump);
		AbstEngine engine = SequenceActionEngine.newInstance(actionMap, jumpMap, startActionName);
		engine.setHalfTime(1);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.FAILED, er);
	}

	@Test
	public void actionKill() {
		String startActionName = "A";
		Action action = Mockito.mock(Action.class);
		Mockito.when(action.execute()).thenReturn(OperateCode.KILL);
		actionMap.put(startActionName, action);

		Jump jump = Jump.newInstance(EOF.end.name(), EOF.fail.name());
		jumpMap.put(startActionName, jump);
		AbstEngine engine = SequenceActionEngine.newInstance(actionMap, jumpMap, startActionName);
		engine.setHalfTime(1);
		ExecuteResult er = engine.start();
		Assert.assertEquals(ExecuteResult.KILL, er);
	}
}
