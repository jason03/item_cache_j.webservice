package net.zeotrope.item;

import org.springframework.boot.SpringApplication;

import java.util.Arrays;
import java.util.stream.Stream;

public class ItemApplicationTest {

    void main(String[] args) {
        var argsWithProfile =
                Stream.concat(Arrays.stream(new String[]{"--spring.profiles.active=test"}), Arrays.stream(args)).toArray(String[]::new);
        SpringApplication.from(ItemServiceApplication::main).with(TestcontainersConfiguration.class).run(argsWithProfile);
    }

}
