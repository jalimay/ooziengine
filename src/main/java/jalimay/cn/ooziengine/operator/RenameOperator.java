package jalimay.cn.ooziengine.operator;

import jalimay.cn.ooziengine.action.ActionOperator;
import jalimay.cn.ooziengine.utils.FileSystemType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileAlreadyExistsException;
import org.apache.log4j.Logger;

/**
 * 将HDFS或本地的一个路径(from)改名为另一个路径(to)
 * 
 * @author xieweiinfo
 */
public final class RenameOperator implements ActionOperator {
	private final Logger log = Logger.getLogger(getClass().getName());
	private FileSystemType fst = FileSystemType.HDFS;
	private String from;
	private String to;

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setFst(FileSystemType fst) {
		this.fst = fst;
	}

	private void check(boolean existsFrom, boolean existsTo) throws FileNotFoundException, FileAlreadyExistsException {
		if (!existsFrom)
			throw new FileNotFoundException(from);
		if (existsTo)
			throw new FileAlreadyExistsException(to);
	}

	@Override
	public OperateCode operate() {
		try {
			boolean existsFrom = false;
			boolean existsTo = false;
			boolean rename = false;
			switch (fst) {
			case HDFS:
				FileSystem fs = FileSystem.get(new Configuration());
				Path fromPath = new Path(from);
				Path toPath = new Path(to);
				existsFrom = fs.exists(fromPath);
				existsTo = fs.exists(toPath);
				check(existsFrom, existsTo);
				rename = fs.rename(fromPath, toPath);
				break;
			case LOCAL:
				File fromFile = new File(from);
				File toFile = new File(to);
				existsFrom = fromFile.exists();
				existsTo = toFile.exists();
				check(existsFrom, existsTo);
				rename = fromFile.renameTo(toFile);
				break;
			default:
				throw new IOException("不可识别的文件系统类型");
			}
			if (rename) {
				log.info("Rename HDFS path [" + from + "] to [" + to + "].");
				return OperateCode.SUCCESS;
			} else {
				throw new IOException("Rename [" + from + "] to [" + to + "] failed.");
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return OperateCode.FAILED;
		}
	}
}
