package com.spring.security.demo

import com.google.gson.Gson
import com.spring.security.demo.user.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.annotation.Resource


/**
 * @description 文件描述
 *
 * @author liudong (liudong@rd.netease.com)
 * @date 2021/1/19 10:21 上午
 */
@Controller
@RequestMapping("/login")
class AppController {

    @Autowired
    var authenticationManager: AuthenticationManager? = null

    @Autowired
    var userDetailService: CustomUserDetailsService? = null

    @Autowired
    var tokenProvider:TokenProvider? = null

    @RequestMapping("/hello")
    @ResponseBody
    fun home(): String {
        return "Hello ,spring security!";
    }

//    等同于登录接口
    @ResponseBody
    @RequestMapping(value = arrayOf("/authenticate"), method = arrayOf(RequestMethod.POST))
    fun authorize(@RequestParam userName: String, @RequestParam password: String): String {
        try {
//        1.创建authenticationToken
            val token = UsernamePasswordAuthenticationToken(userName, password)
//        2.认证
            val authentication = authenticationManager!!.authenticate(token)
//        3.保存认证信息(这一步保存认证信息？貌似不一定需要)
            SecurityContextHolder.getContext().authentication = authentication
//        4.加载Userdetails
            val details = userDetailService!!.loadUserByUsername(userName)
////        5.生成自定义token
            val tokenObj = tokenProvider!!.createToken(details)
            val tokenJson = Gson().toJson(tokenObj)
            return tokenJson
        } catch (e: BadCredentialsException) {
//            验证用户名和密码失败会抛出该异常。
            return "fail:密码错误"
        }
        return "fail:登录失败"

    }

}

@Controller
@RequestMapping("/product")
class ProductTestController {
    @RequestMapping("/info")
    @ResponseBody
    fun productInfo(): String {
        var currentUser = ""
        val principal = SecurityContextHolder.getContext().authentication.principal
        if (principal is UserDetails) {
            currentUser = principal.username
        } else {
            currentUser = principal.toString()
        }
        return " some product info ,currentUser is : ${currentUser}"
    }
}

@Controller
@RequestMapping("/admin")
class AdminTestController {
    @RequestMapping("/home")
    @ResponseBody
    fun productInfo(): String {
        var currentUser = ""

        val principal = SecurityContextHolder.getContext().authentication.principal
        if (principal is UserDetails) {
            currentUser = principal.username
            principal.authorities.forEach {
                currentUser += it.authority
            }

        } else {
            currentUser = principal.toString()
        }
        return " admin home page,current user:$currentUser "
    }
}