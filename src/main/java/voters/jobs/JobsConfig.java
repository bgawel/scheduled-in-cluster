package voters.jobs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!h2Server")
class JobsConfig {

    @Bean
    Job1 job1() {
        return new Job1();
    }

    @Bean
    Job2 job2() {
        return new Job2();
    }

    @Bean
    Job3 job3() {
        return new Job3();
    }
}
