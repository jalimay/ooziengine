package jalimay.cn.ooziengine.operator;

import jalimay.cn.ooziengine.action.ActionOperator;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 指定Java类和方法，并把参数传递给方法
 * 
 * @author xieweiinfo
 */
public final class JavaOperator implements ActionOperator {
	private final Logger log = Logger.getLogger(getClass());
	private Class<?> cls;
	private Method mtd;
	private Map<String, String> params;

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public void setMtd(Method mtd) {
		this.mtd = mtd;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public OperateCode operate() {
		try {
			mtd.invoke(cls.newInstance(), params);
			return OperateCode.SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return OperateCode.FAILED;
		}
	}
}
