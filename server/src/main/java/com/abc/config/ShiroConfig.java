package com.abc.config;

import com.abc.shiro.BiuRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {

    @Bean
    public Realm realm() {
        return new BiuRealm();
    }

    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        /**
         * setUsePrefix(false)用于解决一个奇怪的bug。在引入spring aop的情况下。
         * 在@Controller注解的类的方法中加入@RequiresRole注解，会导致该方法无法映射请求，导致返回404。
         * 加入这项配置能解决这个bug
         */
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        //登录页
        chainDefinition.addPathDefinition("/api/v1/admin/login", "anon");
        //可公访问的页面
        chainDefinition.addPathDefinition("/api/v1/page/**", "anon");
        chainDefinition.addPathDefinition("/api/v1/test/**", "anon");
        /*
        //静态资源
        chainDefinition.addPathDefinition("/", "anon");
        chainDefinition.addPathDefinition("/index.html", "anon");
        chainDefinition.addPathDefinition("/favicon.ico", "anon");
        */
        //其它接口登录才能访问
        chainDefinition.addPathDefinition("/api/v1/**", "authc");
        return chainDefinition;
    }


}
