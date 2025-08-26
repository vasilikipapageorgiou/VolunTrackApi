package gr.voluntrack.config;

import gr.voluntrack.model.Role;
import gr.voluntrack.model.User;
import gr.voluntrack.repository.RoleRepository;
import gr.voluntrack.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Φορτώνουμε τα roles αν δεν υπάρχουν
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
            }
            if (roleRepository.findByName("ROLE_VOLUNTEER").isEmpty()) {
                roleRepository.save(Role.builder().name("ROLE_VOLUNTEER").build());
            }
            if (roleRepository.findByName("ROLE_ORGANIZATION").isEmpty()) {
                roleRepository.save(Role.builder().name("ROLE_ORGANIZATION").build());
            }

            // 2. Φορτώνουμε admin user αν δεν υπάρχει
            if (userRepository.findByEmail("admin@voluntrack.com").isEmpty()) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();

                User admin = User.builder()
                        .email("admin@voluntrack.com")
                        .password(passwordEncoder.encode("admin123"))  // βάζεις εδώ το αρχικό password
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(adminRole)))
                        .build();

                userRepository.save(admin);
            }
        };
    }
}
