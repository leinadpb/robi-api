package robi.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import robi.api.auth.jwt.JwtAuthenticationEntryPoint;
import robi.api.auth.jwt.JwtRequestFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private UserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Arrays.stream(HttpMethod.values()).map(HttpMethod::name).collect(Collectors.toList()));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity
                .addFilterBefore(corsFilter(), SessionManagementFilter.class)
                .csrf().disable()
                .authorizeRequests().
                antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/csrf",
                        "/service-status/v1/task/status",
                        "/swagger-ui.html",
                        "/*.html",
                        "/*.js",
                        "/favicon.ico",
                        "/**/*.html",
                        "/*.png",
                        "/*.html",
                        "/*.css",
                        "/**/*.css",
                        "/**/*.png",
                        "/webjars/**",
                        "/configuration/**",
                        "/v2/**",
                        "/**/swagger-resources/**",
                        "/**/*.js",
                        "/**/swagger-ui.html",
                        "/**/services/**"
                ).permitAll().

                antMatchers(
                        HttpMethod.OPTIONS,
                        "/**/auth/**")
                .permitAll().

                // Login and create methods. Public.
                        antMatchers(HttpMethod.POST, "/**/auth/login", "/**/auth/create")
                .permitAll().
                // Any user should be able to get its information if the token allows it.
                        antMatchers(HttpMethod.GET, "/**/auth/info")
                .permitAll().


                // all other requests need to be authenticated
                anyRequest().authenticated().and().
                // make sure we use stateless session; session won't be used to
                // store user's state.
                exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
