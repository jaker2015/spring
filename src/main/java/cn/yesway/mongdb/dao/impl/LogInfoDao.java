package cn.yesway.mongdb.dao.impl;

import org.springframework.stereotype.Component;

import cn.yesway.mongdb.dao.ILogInfoDao;
import cn.yesway.mongdb.vo.LogInfoVo;

@Component("LogInfoDaoImp")
public class LogInfoDao extends MongodbBaseDao<LogInfoVo> implements ILogInfoDao {



}
