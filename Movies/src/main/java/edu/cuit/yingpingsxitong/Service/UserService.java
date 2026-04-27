package edu.cuit.yingpingsxitong.Service;

import edu.cuit.yingpingsxitong.Entity.User;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import edu.cuit.yingpingsxitong.Dao.UserDao;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService{
    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(SqlSessionTemplate sqlSessionTemplate) {
        this.userDao = sqlSessionTemplate.getMapper(UserDao.class);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        if (encodedPassword == null) return false;
        if (encodedPassword.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        }
        return rawPassword.equals(encodedPassword);
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

    public int countUsers() {
        return userDao.countUsers();
    }

    public void updatePermission(Integer userId,Boolean permission) {
        userDao.updatePermission(userId,permission);
    }

    public void updateManager(Integer userId,Boolean manager) {
        userDao.updateManager(userId,manager);
    }
}
