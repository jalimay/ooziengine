package jalimay.cn.ooziengine.action;

/**
 * 每个action中可以包含至少一个operator， 可以理解为该业务操作中的一个小细节
 * ，并且按operator声明的顺序执行。一个operator可以是一个MapReduce，可以是一个Sqoop任务，可以是一个HDFS操作，
 * 甚至可以是一段Java代码
 * 
 * @author Jalimay
 */
public interface ActionOperator {
	/**
	 * 执行当前Operator
	 * 
	 * @return OperateCode
	 */
	public OperateCode operate();

	/**
	 * 每个ActionOperator的执行结果
	 * 
	 * @author xieweiinfo
	 */
	public static enum OperateCode {
		/**
		 * 运行成功
		 */
		SUCCESS,
		/**
		 * 运行失败
		 */
		FAILED,
		/**
		 * 被终止
		 */
		KILL;
	}
}
