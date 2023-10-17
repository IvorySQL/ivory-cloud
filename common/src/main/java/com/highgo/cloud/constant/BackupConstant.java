package com.highgo.cloud.constant;

/**
 * 备份常量
 * @author chushaolin
 *
 */
public class BackupConstant {
	//创建过程中
	public static final String CREATE_INPROGESS = "in progress";

	//创建成功
	public static final String CREATE_SUCCESS = "success";

	//创建失败
	public static final String CREATE_FAILED = "failed";

	// 自动
	public static final String AUTO = "AUTO";

	//恢复默认状态
	public static final String NOT_START = "not start";

	//恢复成功
	public static final String RECOVERY_DEFAULT_MESSAGE = "数据未使用";
	//恢复成功
	public static final String RECOVERY_SUCCESS_MESSAGE = "数据恢复成功";

	//恢复失败
	public static final String RECOVERY_FAILED_MESSAGE = "数据恢复失败";

	//备份失败
	public static final String BACKUP_FAILED = "备份失败";
	//备份成功
	public static final String BACKUP_SUCCEED = "数据备份成功";
	//正在进行数据恢复
	public static final String RECOVERY_PROGRESSING_MESSAGE = "正在进行数据恢复!";

	//正在进行数据备份
	public static final String BACKUP_PROGRESSING_MESSAGE = "正在进行数据备份!";



	/**
	 * 逻辑备份
	 */
	public static final int LOGIC_BACKUP = 0;

	/**
	 * 物理备份
	 */
	public static final int PHYSIC_BACKUP = 1;

	/**
	 * 全量备份
	 */
	public static final int SYNCTYPE_FULL = 0;

	/**
	 * 增量备份
	 */
	public static final int SYNCTYPE_INCREMENT = 1;

	/**
	 * 自动备份
	 */
	public static final int AUTO_BACKUP=1;
	/**
	 * 手动备份
	 */
	public static final int NOTAUTO_BACKUP=0;

	/**
	 * 全量备份后，增量备份的频率，单位是分钟
	 */
	public static final int INCREMENT_RATE = 30;

	/**
	 * 备份恢复挂载路径
	 */
	public static final String DB_BACK_PATH = "backup";


	/**
	 *  备份目录
	 *   todo:改为数据库存储
	 */
	public static final String TEMP_BACKUP_PATH = "/opt/backup";

	/**
	 *  备份恢复的文件名称
	 */
	public static final String PG_RMAN = "pg_rman";
	public static final String DB_BACKUP = "db_backup";

	/**
	 *  前端提示
	 */
	public static final String CREATE_BACKUP_POLICY_FAILED = "添加保留策略失败";
	public static final String CREATE_BACKUP_POLICY_SUCCEEDED = "添加保留策略成功";
	public static final String ALREADY_EXIST_BACKUP_POLICY = "已存在该数据库对应的保留策略";
	public static final String UPDATE_BACKUP_POLICY_SUCCEEDED = "修改保留策略成功";
	public static final String UPDATE_BACKUP_POLICY_FAILED = "修改保留策略失败";
	public static final String DELETE_BACKUP_POLICY_SUCCEEDED = "删除保留策略成功";
	public static final String DELETE_BACKUP_POLICY_FAILED = "删除保留策略失败";

	public static final String CREATE_RETENTION_POLICY_FAILED = "添加备份策略失败";
	public static final String CREATE_RETENTION_POLICY_SUCCEEDED = "添加备份策略成功";
	public static final String ALREADY_EXIST_RETENTION_POLICY = "已存在该数据库对应的备份策略";
	public static final String UPDATE_RETENTION_POLICY_SUCCEEDED = "修改备份策略成功";
	public static final String UPDATE_RETENTION_POLICY_FAILED = "修改备份策略失败";
	public static final String DELETE_RETENTION_POLICY_SUCCEED = "删除备份策略成功";
	public static final String DELETE_RETENTION_POLICY_FAILED = "删除备份策略失败";

	/**
	 *  备份方式
	 *  FULL全量 INC增量
	 */
	public static final String DB_BACKUP_FULL = "FULL";
	public static final String DB_BACKUP_FULL_CHN = "全量";
	public static final String DB_BACKUP_INC = "INC";
	public static final String DB_BACKUP_0INC = "0INC";
	public static final String DB_BACKUP_INC_CHN = "增量";

	/**
	 *  数据备份失败
	 */
	public static final String DB_BACKUP_SHOW_ERROR = "ERROR";
	/**
	 *  数据备份成功
	 */
	public static final String DB_BACKUP_SHOW_OK = "OK";
	/**
	 *  数据备份失败
	 *  使用start time找不到对应的备份文件
	 *  db_backup 返回的报错信息
	 */
	public static final String DB_BACKUP_INIT_RESTORE_FAILED_INFO = "ERROR: cannot do restore DETAIL: " +
			"There is no valid full backup which can be used for given recovery condition";

}
