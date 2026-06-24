package com.re.artworkgallery_security.config;

import com.re.artworkgallery_security.model.Account;
import com.re.artworkgallery_security.model.Artwork;
import com.re.artworkgallery_security.model.Role;
import com.re.artworkgallery_security.repository.AccountRepository;
import com.re.artworkgallery_security.repository.ArtworkRepository;
import com.re.artworkgallery_security.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role(null, "ROLE_ADMIN");
            Role artistRole = new Role(null, "ROLE_ARTIST");
            roleRepository.save(adminRole);
            roleRepository.save(artistRole);

            Account admin = new Account(null, "admin", passwordEncoder.encode("admin123"), true, Set.of(adminRole));
            Account artist1 = new Account(null, "artist1", passwordEncoder.encode("artist123"), true, Set.of(artistRole));
            Account artist2 = new Account(null, "artist2", passwordEncoder.encode("artist123"), true, Set.of(artistRole));
            
            admin = accountRepository.save(admin);
            artist1 = accountRepository.save(artist1);
            artist2 = accountRepository.save(artist2);

            Artwork a1 = new Artwork(null, "Mona Lisa", "A masterpiece", true, artist1.getId());
            Artwork a2 = new Artwork(null, "Starry Night", "Beautiful night sky", false, artist1.getId());
            Artwork a3 = new Artwork(null, "The Scream", "Anxious painting", true, artist2.getId());
            Artwork a4 = new Artwork(null, "Guernica", "Anti-war painting", false, artist2.getId());

            artworkRepository.save(a1);
            artworkRepository.save(a2);
            artworkRepository.save(a3);
            artworkRepository.save(a4);
        }
    }
}
