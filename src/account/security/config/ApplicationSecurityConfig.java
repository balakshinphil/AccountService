package account.security.config;

import account.model.UserRole;
import account.security.CustomAccessDeniedHandler;
import account.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public ApplicationSecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                                     UserDetailsService userDetailsService,
                                     PasswordEncoder passwordEncoder,
                                     CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }



    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint)

                .and()
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)

                .and()
                .csrf().disable().headers().frameOptions().disable()

                .and()
                .authorizeRequests() // manage access
                .mvcMatchers("/api/admin/**").hasRole(UserRole.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.GET, "/api/security/events").hasRole(UserRole.AUDITOR.name())
                .mvcMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole(UserRole.ACCOUNTANT.name())
                .mvcMatchers(HttpMethod.POST, "/api/acct/payments").hasRole(UserRole.ACCOUNTANT.name())
                .mvcMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole(UserRole.USER.name(), UserRole.ACCOUNTANT.name())
                .mvcMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                .mvcMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
