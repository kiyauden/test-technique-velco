package tech.velco.testtechnique;

import org.apache.tika.Tika;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestTechniqueApplication {

    public static void main(final String[] args) {
        SpringApplication.run(TestTechniqueApplication.class, args);
    }

    /**
     * Creates a Tika bean (used for validation)
     */
    @Bean
    public Tika getTika() {
        return new Tika();
    }
}
