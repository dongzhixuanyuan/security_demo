package com.spring.security.demo.user

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.spring.security.demo.user.mapper.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import java.io.Serializable


/**
 * @description 文件描述
 *
 * @author liudong (liudong@rd.netease.com)
 * @date 2021/1/19 11:00 上午
 */

class User : Serializable {
    var id: Long? = null

    var login: String? = null

    var password: String? = null

    var role: String? = null // 省略get set 等
}


//账户:user1  密码:123456
//账户:admin 密码：123456

@Component
class CustomUserDetailsService : UserDetailsService {

    @Autowired
    var userMapper: UserMapper? = null
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userMapper!!.findOneByUserName(username)
            ?: throw UsernameNotFoundException("User $username was not found in db")
        val grantedAuthorities: MutableList<GrantedAuthority> = mutableListOf()
        user.role?.split(",")?.forEach {
            grantedAuthorities.add(SimpleGrantedAuthority(it))
        }
        return User(username, user.password, grantedAuthorities)
    }

}
