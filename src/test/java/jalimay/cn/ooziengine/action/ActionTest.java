package jalimay.cn.ooziengine.action;

import jalimay.cn.ooziengine.action.ActionOperator.OperateCode;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ActionTest {
	private Action action;
	private List<ActionOperator> operators;

	@Before
	public void init() {
		action = new Action();
		operators = new ArrayList<ActionOperator>();
		action.setOperators(operators);
	}

	@Test
	public void zeroOperator() {
		OperateCode oc = action.execute();
		Assert.assertEquals(OperateCode.SUCCESS, oc);
	}

	@Test
	public void twoOperator() {
		ActionOperator opa = Mockito.mock(ActionOperator.class);
		Mockito.when(opa.operate()).thenReturn(OperateCode.SUCCESS);
		operators.add(opa);
		ActionOperator opb = Mockito.mock(ActionOperator.class);
		Mockito.when(opb.operate()).thenReturn(OperateCode.SUCCESS);
		operators.add(opb);

		OperateCode oc = action.execute();
		Assert.assertEquals(OperateCode.SUCCESS, oc);
	}

	@Test
	public void twoOperatorOneFailed() {
		ActionOperator opa = Mockito.mock(ActionOperator.class);
		Mockito.when(opa.operate()).thenReturn(OperateCode.SUCCESS);
		operators.add(opa);
		ActionOperator opb = Mockito.mock(ActionOperator.class);
		Mockito.when(opb.operate()).thenReturn(OperateCode.FAILED);
		operators.add(opb);

		OperateCode oc = action.execute();
		Assert.assertEquals(OperateCode.FAILED, oc);
	}

	@Test
	public void twoOperatorOneKilled() {
		ActionOperator opa = Mockito.mock(ActionOperator.class);
		Mockito.when(opa.operate()).thenReturn(OperateCode.SUCCESS);
		operators.add(opa);
		ActionOperator opb = Mockito.mock(ActionOperator.class);
		Mockito.when(opb.operate()).thenReturn(OperateCode.KILL);
		operators.add(opb);

		OperateCode oc = action.execute();
		Assert.assertEquals(OperateCode.KILL, oc);
	}
}
