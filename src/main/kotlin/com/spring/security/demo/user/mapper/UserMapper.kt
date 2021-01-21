package com.spring.security.demo.user.mapper

import com.spring.security.demo.user.User
import org.apache.ibatis.annotations.Mapper

/**
 * @description 文件描述
 *
 * @author liudong (liudong@rd.netease.com)
 * @date 2021/1/19 11:19 上午
 */
@Mapper
interface UserMapper {
    fun findOneByUserName(userName:String):User?
}