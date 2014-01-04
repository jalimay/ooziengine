package jalimay.cn.ooziengine.operator;

import jalimay.cn.ooziengine.action.ActionOperator;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

/**
 * 执行map-reduce的operator
 * 
 * @author xieweiinfo
 */
public final class MapReduceOperator implements ActionOperator {
	private final Logger log = Logger.getLogger(getClass());
	private static final String CONF_KEY_MAPRED_JOB_NAME = "mapred.job.name";
	private String actionName;
	private Map<String, String> configuration;

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public Map<String, String> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	public OperateCode operate() {
		try {
			Configuration conf = new Configuration();
			for (Entry<String, String> attr : getConfiguration().entrySet()) {
				conf.set(attr.getKey(), attr.getValue());
			}
			conf.set(CONF_KEY_MAPRED_JOB_NAME, getActionName());
			int exitCode = ToolRunner.run(conf, new MapredTool(), new String[] {});
			switch (exitCode) {
			case MapredTool.SUC_CODE:
				return OperateCode.SUCCESS;
			default:
				return OperateCode.FAILED;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return OperateCode.FAILED;
		}
	}
}

class MapredTool extends Configured implements Tool {
	public static final int SUC_CODE = 0;
	public static final int ERR_CODE = 1;

	@Override
	public int run(String[] arg0) throws Exception {
		Job job = new Job(getConf());
		boolean success = job.waitForCompletion(true);
		return success ? SUC_CODE : ERR_CODE;
	}
}