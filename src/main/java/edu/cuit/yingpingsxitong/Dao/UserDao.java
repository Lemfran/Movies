package edu.cuit.yingpingsxitong.Dao;

import edu.cuit.yingpingsxitong.Entity.Movie;
import edu.cuit.yingpingsxitong.Entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    // 插入用户
    void insertUser(User user);

    // 根据用户名查找用户
    User findUserByUsername(String username);

    // 更新用户信息
    void updateUser(User user);

    // 根据用户ID删除用户
    void deleteUser(Integer userId);

    User findUserById(Integer userId);

    void updatePermission(Integer userId,Boolean permission);

    void updateManager(Integer userId,Boolean manager);

    List<User> findAllUsers();
}
