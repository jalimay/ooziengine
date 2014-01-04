package jalimay.cn.ooziengine.operator;

import jalimay.cn.ooziengine.action.ActionOperator;

import org.apache.log4j.Logger;
import org.apache.sqoop.Sqoop;

/**
 * 提交一个sqoop任务的operator
 * 
 * @author xieweiinfo
 */
public final class SqoopOperator implements ActionOperator {
	private final Logger log = Logger.getLogger(getClass());
	private String[] args;

	public void setArgs(String[] args) {
		this.args = args;
	}

	@Override
	public OperateCode operate() {
		try {
			int code = Sqoop.runTool(args);
			log.info("Sqoop return code is " + code);
			if (code == 0)
				return OperateCode.SUCCESS;
			else
				return OperateCode.FAILED;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return OperateCode.FAILED;
		}
	}

}
