package com.highgo.cloud.model;
 
import java.util.List;

public class ResultTO<T>
{
	//结果
    private boolean success = false;
    
    //消息
    private String message; 
    
    //返回码
    private int resultcode;
    
    //数据
    private T data;
    
    //数据列表
    private List<T> dataList;
    
    //总行数
    private long totalRows;
    
    public void setTotalRows(long totalRows)
    {
    	this.totalRows = totalRows;
    }
    public long getTotalRows()
    {
    	return this.totalRows;
    }
    
    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public int getResultcode()
    {
        return resultcode;
    }

    public void setResultcode(int resultcode)
    {
        this.resultcode = resultcode;
    }


    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public List<T> getDataList()
    {
        return dataList;
    }

    public void setDataList(List<T> dataList)
    {
        this.dataList = dataList;
    }
    
    
}
