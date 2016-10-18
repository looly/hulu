package com.xiaoleilu.huludemo.dao;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.xiaoleilu.hulu.exception.DaoException;
import com.xiaoleilu.huludemo.po.User;

/**
 * 虚拟数据库，为了演示，直接存HashMap里了
 * @author loolly
 *
 */
public class VirtualDao {
	
	//------------------------ 这个是个单例，饱汉模式 --------
	private static VirtualDao instance = new VirtualDao();
	public static VirtualDao getInstance() {
		return instance;
	}
	//---------------------------------------------------------
	
	/** 好吧，这是个非常简陋的KV数据库，我在这里只是为了演示，所以没有引入数据库 */
	private ConcurrentHashMap<String, User> userDb = new ConcurrentHashMap<String, User>();
	
	/**
	 * 保存用户
	 * @param user 用户对象
	 * @throws DaoException
	 */
	public void saveUser(User user) throws DaoException {
		if(user == null) {
			throw new DaoException("User is empty!");
		}
		
		final String name = user.getName();
		
		if(userDb.contains(name)) {
			throw new DaoException("User is exist!");
		}
		
		userDb.put(name, user);
	}
	
	/**
	 * 列出所有用户
	 * @return 用户列表
	 */
	public Collection<User> listUsers() {
		return userDb.values();
	}
}
