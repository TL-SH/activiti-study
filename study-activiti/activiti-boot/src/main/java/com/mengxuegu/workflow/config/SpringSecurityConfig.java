package com.mengxuegu.workflow.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(SpringSecurityConfig.class);

    /**
     * 内存 UserDetailsManager
     */
    @Bean
    public UserDetailsService myUserDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        // 初始化账号角色数据
        addGroupAndRoles(inMemoryUserDetailsManager);
        return inMemoryUserDetailsManager;
    }

    private void addGroupAndRoles(UserDetailsManager userDetailsManager) {
        // 注意：后面流程办理人，必须是当前存在的用户 username
        String[][] usersGroupsAndRoles = {
                {"meng", "123456", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
                {"xue", "123456", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
                {"gu", "123456", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam"},
                {"小梦", "123456", "ROLE_ACTIVITI_ADMIN", "GROUP_otherTeam"},
                {"小学", "123456", "ROLE_ACTIVITI_ADMIN", "GROUP_otherTeam"},
                {"小谷", "123456", "ROLE_ACTIVITI_ADMIN", "GROUP_otherTeam"}
        };

        for (String[] user : usersGroupsAndRoles) {
            List<String> authoritiesStrings = Arrays.asList(Arrays.copyOfRange(user, 2, user.length));
            logger.info("> Registering new user: " + user[0] + " with the following Authorities[" + authoritiesStrings + "]");
            userDetailsManager.createUser(new User(user[0], passwordEncoder().encode(user[1]),
                    authoritiesStrings.stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList())));
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}