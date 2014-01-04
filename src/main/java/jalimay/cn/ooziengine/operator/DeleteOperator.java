package jalimay.cn.ooziengine.operator;

import jalimay.cn.ooziengine.action.ActionOperator;
import jalimay.cn.ooziengine.utils.FileSystemType;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

/**
 * 删除某个HDFS或本地的某个目录或文件，删除成功或路径不存在都返回成功
 * 
 * @author Jalimay
 * 
 */

public final class DeleteOperator implements ActionOperator {
	private final Logger log = Logger.getLogger(getClass());
	private FileSystemType fst = FileSystemType.HDFS;
	/**
	 * 待删除的目录
	 */
	private String path;
	/**
	 * 是否可删除文件夹,默认可删除
	 */
	private boolean isDeleteDictionary = true;

	/**
	 * 设置是否运行删除文件夹
	 * 
	 * @param isDeleteDictionary
	 */
	public void setDeleteDictionary(boolean isDeleteDictionary) {
		this.isDeleteDictionary = isDeleteDictionary;
	}

	/**
	 * 设置待删除的目录。
	 * 
	 * @param path
	 * @author xieweiinfo
	 * @date 2013年8月16日
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 设置操作的文件系统类型
	 * 
	 * @param fst
	 */
	public void setFst(FileSystemType fst) {
		this.fst = fst;
	}

	@Override
	public OperateCode operate() {
		try {
			boolean exists = false;
			boolean delete = false;
			switch (fst) {
			case HDFS:
				FileSystem fs = FileSystem.get(new Configuration());
				Path deletePath = new Path(path);
				exists = fs.exists(deletePath);
				delete = fs.delete(deletePath, isDeleteDictionary);
				break;
			case LOCAL:
				File file = new File(path);
				exists = file.exists();
				delete = file.delete();
				break;
			default:
				throw new IOException("不可识别的文件系统类型");
			}
			// 删除成功或路径不存在都返回成功
			if (!exists || delete) {
				log.info("Delete " + fst.name() + " path:" + path);
				return OperateCode.SUCCESS;
			} else {
				return OperateCode.FAILED;
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return OperateCode.FAILED;
		}
	}
}
