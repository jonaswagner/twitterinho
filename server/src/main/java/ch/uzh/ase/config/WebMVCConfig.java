package ch.uzh.ase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Properties;

/**
 * Created by flaviokeller on 24.04.17.
 */

@Configuration
@EnableWebMvc
public class WebMVCConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        Properties prop = ch.uzh.ase.config.Configuration.getInstance().getProp();
        registry.addMapping("/**")
                .allowedOrigins(prop.getProperty("clienturl"), prop.getProperty("localclienturl"))
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}