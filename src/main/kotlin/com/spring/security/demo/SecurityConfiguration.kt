package com.spring.security.demo

import com.fasterxml.jackson.core.filter.TokenFilter
import com.spring.security.demo.user.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component


/**
 * @description 文件描述
 *
 * @author liudong (liudong@rd.netease.com)
 * @date 2021/1/19 10:17 上午
 */
@Configuration
@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Autowired
    var userDetailService: CustomUserDetailsService? = null

    @Autowired
    var authTokenConfigurer:MyAuthTokenConfigurer? = null

    @Autowired
    var myTokenFilter:MyTokenFilter? = null

    override fun configure(http: HttpSecurity) {
        http.csrf().disable(). //这个很关键，否则行为将是凡是permitAll()的url，get请求都是ok的，但是post请求一直需要权限。
        authorizeRequests()
            .antMatchers("/product/**").hasAuthority("USER")
            .antMatchers("/admin/**").hasAuthority("ADMIN")
            .antMatchers("/login/**").permitAll()
            .anyRequest()
            .permitAll()
            .and()
            .apply(authTokenConfigurer!!)
            .and()
            .formLogin()
            .and()
            .httpBasic()
            .and()
            .sessionManagement()
//                设置了Stateless策略后，意味着所有的权限验证都将不通过
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(myTokenFilter!!,UsernamePasswordAuthenticationFilter::class.java)
            .headers().cacheControl()

    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder())
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    //    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

@Component
class MyAuthTokenConfigurer : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    @Autowired
    var tokenFilter:MyTokenFilter? = null

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
