package cn.yesway.mongdb.dao;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;

public interface IMogodbBaseDao<T> {

	public abstract T save (T obj);
	public abstract List<T> find (Query query);
	public abstract List<T> findByCondition(T t);
	public abstract void updateById (String id,T t);
	public abstract void deleteById(T t);
	public abstract void deleteByCondition(T t);
	public abstract T get (String id);
}
