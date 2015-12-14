package voters;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!h2Server")
public class DbConfig {

    @Bean
    public DataSource dataSource(){
        return DataSourceBuilder
                .create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:tcp://localhost:9092/mem:db1")
                .username("sa")
                .build();
    }
}