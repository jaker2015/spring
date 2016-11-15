package cn.yesway.mongdb.dao.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import cn.yesway.mongdb.dao.IMogodbBaseDao;

public abstract class MongodbBaseDao<T> implements IMogodbBaseDao<T> {

	@Autowired
	private MongoTemplate mongoTemplate;

	// 保存一个对象到mongodb
	public T save(T bean) {
		mongoTemplate.save(bean);
		return bean;
	}

	// 根据id删除对象
	public void deleteById(T t) {
		mongoTemplate.remove(t);
	}

	// 根据对象的属性删除
	public void deleteByCondition(T t) {
		Query query = buildBaseQuery(t);
		mongoTemplate.remove(query, getEntityClass());
	}

	// 通过条件查询更新数据
	public void update(Query query, Update update) {
		mongoTemplate.updateMulti(query, update, this.getEntityClass());
	}

	// 根据id进行更新
	public void updateById(String id, T t) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = buildBaseUpdate(t);
		update(query, update);
	}

	// 通过条件查询实体(集合)
	public List<T> find(Query query) {
		return mongoTemplate.find(query, this.getEntityClass());
	}

	public List<T> findByCondition(T t) {
		Query query = buildBaseQuery(t);
		return mongoTemplate.find(query, getEntityClass());
	}

	// 通过一定的条件查询一个实体
	public T findOne(Query query) {
		return mongoTemplate.findOne(query, this.getEntityClass());
	}

	// 通过ID获取记录
	public T get(String id) {
		return mongoTemplate.findById(id, this.getEntityClass());
	}

	// 通过ID获取记录,并且指定了集合名(表的意思)
	public T get(String id, String collectionName) {
		return mongoTemplate
				.findById(id, this.getEntityClass(), collectionName);
	}

	// 根据vo构建查询条件Query
	private Query buildBaseQuery(T t) {
		Query query = new Query();

		Field[] fields = t.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				Object value = field.get(t);
				if (value != null) {
					QueryField queryField = field
							.getAnnotation(QueryField.class);
					if (queryField != null) {
						query.addCriteria(queryField.type().buildCriteria(
								queryField, field, value));
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return query;
	}

	private Update buildBaseUpdate(T t) {
		Update update = new Update();
		Field[] fields = t.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				Object value = field.get(t);
				if (value != null) {
					update.set(field.getName(), value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return update;
	}

	// 利用反射获取需要操作的实体类class
	@SuppressWarnings("unchecked")
	protected Class<T> getEntityClass() {
		return ((Class<T>) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface QueryField {
		QueryType type() default QueryType.EQUALS;

		String attribute() default "";
	}

	public enum QueryType {
		EQUALS {
			@Override
			public Criteria buildCriteria(QueryField queryFieldAnnotation,
					Field field, Object value) {
				if (check(queryFieldAnnotation, field, value)) {
					String queryField = getQueryFieldName(queryFieldAnnotation,
							field);
					return Criteria.where(queryField).is(value.toString());
				}
				return new Criteria();
			}
		},
		LIKE {
			@Override
			public Criteria buildCriteria(QueryField queryFieldAnnotation,
					Field field, Object value) {
				if (check(queryFieldAnnotation, field, value)) {
					String queryField = getQueryFieldName(queryFieldAnnotation,
							field);
					return Criteria.where(queryField).regex(value.toString());
				}
				return new Criteria();
			}
		},
		IN {
			@Override
			public Criteria buildCriteria(QueryField queryFieldAnnotation,
					Field field, Object value) {
				if (check(queryFieldAnnotation, field, value)) {
					if (value instanceof List) {
						String queryField = getQueryFieldName(
								queryFieldAnnotation, field);
						// 此处必须转型为List，否则会在in外面多一层[]
						return Criteria.where(queryField).in((List<?>) value);
					}
				}
				return new Criteria();
			}
		};
		//检查是否注册或者文件内容为null
		private static boolean check(QueryField queryField, Field field,
				Object value) {
			return !(queryField == null || field == null || value == null);
		}

		public abstract Criteria buildCriteria(QueryField queryFieldAnnotation,
				Field field, Object value);

		/**
		 * 如果实体bean的字段上QueryField注解没有设置attribute属性时，默认为该字段的名称
		 *
		 * @param queryFieldAnnotation
		 * @param field
		 * @return
		 */
		private static String getQueryFieldName(QueryField queryField,
				Field field) {
			String queryFieldValue = queryField.attribute();
			if (!StringUtils.hasText(queryFieldValue)) {
				queryFieldValue = field.getName();
			}
			return queryFieldValue;
		}
	}
}
