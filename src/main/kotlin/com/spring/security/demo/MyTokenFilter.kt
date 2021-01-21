package com.spring.security.demo

import com.google.gson.Gson
import com.spring.security.demo.user.CustomUserDetailsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * @description 文件描述
 *
 * @author liudong (liudong@rd.netease.com)
 * @date 2021/1/21 2:53 下午
 */
@Component
open class MyTokenFilter() : GenericFilterBean() {
    private val log: Logger = LoggerFactory.getLogger(MyTokenFilter::class.java)


    @Autowired
    var tokenProvider: TokenProvider? = null

    @Autowired
    var userDetailService: CustomUserDetailsService? = null

    private val XAUTH_TOKEN_HEADER_NAME = "my-auth-token"
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        (request as HttpServletRequest).also {
            val tokenHeader = it.getHeader(XAUTH_TOKEN_HEADER_NAME)
            if (!tokenHeader.isNullOrEmpty()) {
                val tokenBean = Gson().fromJson(tokenHeader, Token::class.java)
                if (tokenBean != null) {
                    val userDetails = userDetailService!!.loadUserByUsername(tokenBean.token)
                    if (tokenProvider!!.validateToken(tokenHeader,userDetails)) {
                        log.debug("validateToken ok...")
                        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails.username,userDetails.password,userDetails.authorities)
                        SecurityContextHolder.getContext().authentication = authenticationToken
                    }
                }
                tokenProvider!!.getUserNameFromToken(tokenHeader)
            }
            chain.doFilter(request, response)
        }

    }
}