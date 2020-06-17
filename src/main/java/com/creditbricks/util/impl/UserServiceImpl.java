//package com.mohshri.service.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import com.mohshri.dao.UserRepository;
//import com.mohshri.model.User;
//import com.mohshri.service.UserService;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//
//@Service(value = "userService")
//public class UserServiceImpl implements UserDetailsService, UserService {
//	
//	@Autowired
//	private UserRepository userDao;
//
//	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
//		User user = userDao.findByUserName(userId);
//		if(user == null){
//			throw new UsernameNotFoundException("Invalid username or password.");
//		}
//		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPasswordHash(), getAuthority());
//	}
//
//	private List<SimpleGrantedAuthority> getAuthority() {
//		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
//	}
//
//	public List<User> findAll() {
//		List<User> list = new ArrayList<>();
//		userDao.findAll().iterator().forEachRemaining(list::add);
//		return list;
//	}
//
//	@Override
//	public void delete(long id) {
//		userDao.delete(id);
//	}
//
//	@Override
//    public User save(User user) {
//        return userDao.save(user);
//    }
//}
