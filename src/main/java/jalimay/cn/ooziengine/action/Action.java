package jalimay.cn.ooziengine.action;

import jalimay.cn.ooziengine.action.ActionOperator.OperateCode;

import java.util.List;

/**
 * 一个具体的业务操作内容，一个workflow中常常会含有多个action。每个action中可以包含至少一个operator，
 * 可以理解为该业务操作中的一个小细节
 * ，并且按operator声明的顺序执行。一个operator可以是一个MapReduce，可以是一个Sqoop任务，可以是一个HDFS操作，
 * 甚至可以是一段Java代码
 * 
 * @author Jalimay
 * 
 */
public class Action {
	/**
	 * 该Action具体执行的操作内容
	 */
	private List<ActionOperator> operators;

	public List<ActionOperator> getOperators() {
		return operators;
	}

	public void setOperators(List<ActionOperator> operators) {
		this.operators = operators;
	}

	/**
	 * 按顺序执行一个Action下所有的Operator
	 * 
	 * @return 若所有Operator都成功了，返回SUCCESS。若有一个FAIL或者KILL，则返回FAIL或者KILL
	 * @throws Exception
	 */
	public OperateCode execute() {
		if (operators != null) {
			for (ActionOperator op : operators) {
				OperateCode opCode = op.operate();
				switch (opCode) {
				case SUCCESS:
					break;
				case FAILED:
					return OperateCode.FAILED;
				case KILL:
				default:
					return OperateCode.KILL;
				}
			}
		}
		return OperateCode.SUCCESS;
	}

}
