package jalimay.cn.ooziengine.operator;

import jalimay.cn.ooziengine.action.ActionOperator;
import jalimay.cn.ooziengine.utils.FileSystemType;
import jalimay.cn.ooziengine.workflow.Workflow;
import jalimay.cn.ooziengine.workflow.WorkflowFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

/**
 * 加载一个新的workflow.xml，并开始执行他，当新的wrokflow.xml执行完成后，认为这个action执行成功。
 * 
 * @author xieweiinfo
 */
public final class SubWorkflowOperator implements ActionOperator {
	private final Logger log = Logger.getLogger(getClass());
	private String refPath;
	private Map<String, String> args;
	private FileSystemType fst = FileSystemType.HDFS;
	private WorkflowFactory factory;

	public void setRefPath(String refPath) {
		this.refPath = refPath;
	}

	public void setArgs(Map<String, String> args) {
		this.args = args;
	}

	public void setFst(FileSystemType fst) {
		this.fst = fst;
	}

	public void setFactory(WorkflowFactory factory) {
		this.factory = factory;
	}

	@Override
	public OperateCode operate() {
		final String path = refPath;
		log.info("Ready to start subWorkflow:" + path);
		InputStream is = null;
		try {
			switch (fst) {
			case HDFS:
				FileSystem fs = FileSystem.get(new Configuration());
				is = fs.open(new Path(path));
				break;
			case LOCAL:
				File file = new File(path);
				is = new FileInputStream(file);
				break;
			default:
				throw new IOException("不可识别的文件系统类型");
			}
			log.info("SubWorkflow:[" + path + "] finished.");
			// TODO: new Executor instance
			Workflow wf = factory.newInstance(args);
			switch (wf.start()) {
			case SUCCESS:
				return OperateCode.SUCCESS;
			case FAILED:
				return OperateCode.FAILED;
			case KILL:
			default:
				return OperateCode.KILL;
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return OperateCode.FAILED;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			}
		}
	}

}
