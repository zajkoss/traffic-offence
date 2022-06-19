package pl.kurs.trafficoffence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
@Async
public class TrafficOffenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrafficOffenceApplication.class, args);
    }

}
