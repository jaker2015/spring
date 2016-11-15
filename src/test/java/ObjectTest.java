import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.yesway.mongdb.dao.ILogInfoDao;
import cn.yesway.mongdb.vo.LogInfoVo;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml"})
public class ObjectTest {

	@Autowired
	ILogInfoDao iLogInfoDao;
	
	@Test
	public void insert(){
		LogInfoVo vo =  new LogInfoVo();
		List<LogInfoVo> oldlist = iLogInfoDao.findByCondition(vo);
		vo.setName("jaker test");
		vo.setValue(UUID.randomUUID()+"asd");
		vo.setAddTime(new Date().toLocaleString());
		iLogInfoDao.save(vo);
		vo = new LogInfoVo();
		List<LogInfoVo> newlist = iLogInfoDao.findByCondition(vo);
		Assert.assertTrue(newlist.size()>oldlist.size());
	}
	@Test
	public void update(){
		LogInfoVo vo =  new LogInfoVo();
		List<LogInfoVo> oldlist = iLogInfoDao.findByCondition(vo);
		String id =oldlist.get(0).getId();
		vo.setId(id);
		vo.setName(UUID.randomUUID().toString());
		vo.setValue(UUID.randomUUID().toString());
		iLogInfoDao.updateById(id, vo);
		LogInfoVo newVo = iLogInfoDao.get(id);
		//比较更新的字段值不一致
		Assert.assertTrue(!oldlist.get(0).getName().equals(newVo.getName()));
		Assert.assertTrue(!oldlist.get(0).getValue().equals(newVo.getValue()));
	}
	@Test
	public void find(){
		LogInfoVo vo =  new LogInfoVo();
		List<LogInfoVo> list = iLogInfoDao.findByCondition(vo);
		vo.setId("1");
		List<LogInfoVo> newlist = iLogInfoDao.findByCondition(vo);
		Assert.assertTrue(list.size()>0);
		Assert.assertTrue(newlist.size()==0);
	}
	
	@Test
	public void delete(){
		LogInfoVo vo =  new LogInfoVo();
		List<LogInfoVo> list = iLogInfoDao.findByCondition(vo);
		iLogInfoDao.deleteById(list.get(0));
		List<LogInfoVo> newlist = iLogInfoDao.findByCondition(vo);
		Assert.assertTrue(list.size()==newlist.size()+1);
	}
}
