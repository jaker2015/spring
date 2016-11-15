package cn.yesway.mongdb.vo;

import java.io.Serializable;

import cn.yesway.mongdb.dao.impl.MongodbBaseDao.QueryField;
import cn.yesway.mongdb.dao.impl.MongodbBaseDao.QueryType;

public class LogInfoVo implements Serializable{
	@QueryField
	private String id;
	
	@QueryField
	private String name;
	
	@QueryField(type = QueryType.LIKE,attribute ="value")
	private String value;
	
	@QueryField
	private String addTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
}
