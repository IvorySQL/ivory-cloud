package com.highgo.cloud.enums;


import com.highgo.cloud.constant.DBConstant;

/**
 * 数据库操作的枚举类
 * @author chushaolin
 *
 */
public enum DBOperation {
	
	//停止，重启
	STOP(DBConstant.STOPDB_VALUE, DBConstant.STOPDB_NAME), RESTART(DBConstant.RESTARTDB_VALUE, DBConstant.RESTARTDB_NAME),
	START(DBConstant.STARTDB_VALUE, DBConstant.STARTDB_NAME);
	
    // 操作的可读化名字
    private String name;  
    
    //操作的代码
    private int index;
    
	private DBOperation(int index, String name) {
		this.name = name;
		this.index = index;
	}  
    
    // 获取代码对应的操作方法名 
    public static String getName(int index) {  
        for (DBOperation c : DBOperation.values()) {
            if (c.getIndex() == index) {  
                return c.name;
			}
        }  
        return null;  
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}  
    
    
}
