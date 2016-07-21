package net.petrikainulainen.spring.testmvc.config;

import net.petrikainulainen.spring.testmvc.security.authentication.RestAuthenticationEntryPoint;
import net.petrikainulainen.spring.testmvc.security.authentication.RestAuthenticationFailureHandler;
import net.petrikainulainen.spring.testmvc.security.authentication.RestAuthenticationSuccessHandler;
import net.petrikainulainen.spring.testmvc.security.authentication.RestLogoutSuccessHandler;
import net.petrikainulainen.spring.testmvc.security.authorization.TodoPermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityContext extends WebSecurityConfigurerAdapter {

    @Override
    // Disable security on static resources
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Bean
    public DefaultMethodSecurityExpressionHandler expressionHandler(){
        DefaultMethodSecurityExpressionHandler  expressionHandler
                = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(todoPermissionEvaluator());

        return expressionHandler;
    }

    @Bean
    public TodoPermissionEvaluator todoPermissionEvaluator() {
        return new TodoPermissionEvaluator();
    }

    @Bean
    // Configures a custom authentication success handler that returns the HTTP status code 200
    // instead of the 301 HTTP status code
    public RestAuthenticationSuccessHandler restAuthenticationSuccessHandler() {
        return new RestAuthenticationSuccessHandler();
    }

    @Bean
    // Configures a custom authentication failure handler
    public RestAuthenticationFailureHandler restAuthenticationFailureHandler() {
        return new RestAuthenticationFailureHandler();
    }

    @Bean
    // Configures a custom logout success handler
    public RestLogoutSuccessHandler restLogoutSuccessHandler() {
        return new RestLogoutSuccessHandler();
    }

    @Bean
    // Configures the authentication entry point
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    // Configures a custom login filter bean
    public UsernamePasswordAuthenticationFilter loginFilter() throws Exception {
        UsernamePasswordAuthenticationFilter authFilter =
                new UsernamePasswordAuthenticationFilter();

        authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login", "POST"));
        authFilter.setAuthenticationManager(authenticationManager());
        authFilter.setAuthenticationSuccessHandler(restAuthenticationSuccessHandler());
        authFilter.setAuthenticationFailureHandler(restAuthenticationFailureHandler());
        authFilter.setUsernameParameter("username");
        authFilter.setPasswordParameter("password");
        authFilter.setPostOnly(true);

        return authFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint())
                .and()
                .formLogin()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .logoutSuccessHandler(restLogoutSuccessHandler())
                .logoutUrl("/api/logout")
                .and()
                .csrf().disable();

        http.addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }

    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }


}
