package edu.cuit.yingpingsxitong.Service;

import edu.cuit.yingpingsxitong.Entity.User;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import edu.cuit.yingpingsxitong.Dao.UserDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService{
    private final UserDao userDao;

    @Autowired
    public UserService(SqlSessionTemplate sqlSessionTemplate) {
        this.userDao = sqlSessionTemplate.getMapper(UserDao.class);
    }

    public void insertUser(User user) {
        userDao.insertUser(user);
    }

    public User findUserById(int id) {
        return userDao.findUserById(id);
    }

    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    public void deleteUser(Integer userId) {
       userDao.deleteUser(userId);
    }

    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }
    public void updatePermission(Integer userId,Boolean permission) {
        userDao.updatePermission(userId,permission);
    }
    public void updateManager(Integer userId,Boolean manager) {
        userDao.updateManager(userId,manager);
    }
}

