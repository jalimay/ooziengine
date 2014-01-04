package jalimay.cn.ooziengine.workflow;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WorkflowFactoryImplXML implements WorkflowFactory {

	@Override
	public Workflow newInstance(Map<String, String> args) {
		Workflow wf = new Workflow();
		return wf;
	}

}

final class Parameters {
	private final static Logger log = Logger.getLogger(Parameters.class.getName());
	/**
	 * 将直接声明的变量与导入的变量合并后的变量集合。注入Action时使用该集合。
	 */
	private final Map<String, String> parameterMap = new HashMap<String, String>();

	/**
	 * 从指定配置文件读取一批参数设置
	 * 
	 * @author Jalimay
	 * 
	 */
	public static class ImportParam {
		private String key;
		private String importPath;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getImportPath() {
			return importPath;
		}

		public void setImportPath(String importPath) {
			this.importPath = importPath;
		}

	}

	/**
	 * 直接声明的参数设置
	 * 
	 * @author Jalimay
	 * 
	 */
	public static class DeclareParam {
		private String key;
		private String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public static Parameters newInstance(List<DeclareParam> declareParams, List<ImportParam> importParams) throws IOException {
		Parameters instance = new Parameters();
		if (declareParams != null) {
			for (DeclareParam param : declareParams) {
				log.debug("Declare:" + param.getKey() + "=" + param.getValue());
				instance.parameterMap.put(param.getKey(), param.getValue());
			}
		}
		if (importParams != null) {
			for (ImportParam param : importParams) {
				log.debug("Import:" + param.getKey() + " from [" + param.getImportPath() + "]");
				Properties properties = new Properties();
				properties.load(new FileInputStream(param.getImportPath()));
				for (String key : properties.stringPropertyNames()) {
					log.debug("Import:" + key + "=" + properties.getProperty(key));
					instance.parameterMap.put(key, properties.getProperty(key));
				}
			}
		}
		return instance;
	}

	/**
	 * 用Parameters中已定义的参数替换Action中使用参数的内容时被调用
	 * 
	 * @param value
	 * @return
	 * @author xieweiinfo
	 * @date 2012-11-30
	 */
	public String inject(final String value) {
		String tmp = new String(value);
		if (StringUtils.isNotEmpty(tmp))
			for (String key : this.parameterMap.keySet()) {
				try {
					tmp = tmp.replaceAll(wrapParam(key), this.parameterMap.get(key));
				} catch (IllegalArgumentException e) {
					log.error("Error occured when replaceAll [" + wrapParam(key) + "] with [" + this.parameterMap.get(key) + "]");
					throw e;
				}
			}
		return tmp;
	}

	/**
	 * 按oozie的参数格式进行参数包装
	 * 
	 * @param key
	 * @return
	 */
	private String wrapParam(final String key) {
		return "\\$\\{" + key + "\\}";
	}
}
