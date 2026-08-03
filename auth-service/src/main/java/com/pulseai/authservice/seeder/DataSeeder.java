package com.pulseai.authservice.seeder;

import com.pulseai.authservice.constants.SecurityConstants;
import com.pulseai.authservice.entity.UserCredential;
import com.pulseai.authservice.repository.UserCredentialRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            log.info("Seeding initial data into auth_db");

            UserCredential vp = new UserCredential();
            vp.setEmployeeId(1L);
            vp.setEmail("vp@company.com");
            vp.setPassword(passwordEncoder.encode("password"));
            vp.setRole(SecurityConstants.ROLE_VP);
            vp.setRegion("GLOBAL");
            repository.save(vp);

            UserCredential globalHr = new UserCredential();
            globalHr.setEmployeeId(2L);
            globalHr.setEmail("globalhr@company.com");
            globalHr.setPassword(passwordEncoder.encode("password"));
            globalHr.setRole(SecurityConstants.ROLE_GLOBAL_HR);
            globalHr.setRegion("GLOBAL");
            repository.save(globalHr);

            UserCredential regionalHrPune = new UserCredential();
            regionalHrPune.setEmployeeId(3L);
            regionalHrPune.setEmail("shivanivanga01@gmail.com");
            regionalHrPune.setPassword(passwordEncoder.encode("password"));
            regionalHrPune.setRole(SecurityConstants.ROLE_REGIONAL_HR);
            regionalHrPune.setRegion("PUNE");
            repository.save(regionalHrPune);

            UserCredential regionalHrBangalore = new UserCredential();
            regionalHrBangalore.setEmployeeId(5L);
            regionalHrBangalore.setEmail("hr.bangalore@pulseai.com");
            regionalHrBangalore.setPassword(passwordEncoder.encode("password"));
            regionalHrBangalore.setRole(SecurityConstants.ROLE_REGIONAL_HR);
            regionalHrBangalore.setRegion("BANGALORE");
            repository.save(regionalHrBangalore);

            UserCredential regionalHrMumbai = new UserCredential();
            regionalHrMumbai.setEmployeeId(6L);
            regionalHrMumbai.setEmail("hr.mumbai@pulseai.com");
            regionalHrMumbai.setPassword(passwordEncoder.encode("password"));
            regionalHrMumbai.setRole(SecurityConstants.ROLE_REGIONAL_HR);
            regionalHrMumbai.setRegion("MUMBAI");
            repository.save(regionalHrMumbai);

            UserCredential regionalHrHyderabad = new UserCredential();
            regionalHrHyderabad.setEmployeeId(7L);
            regionalHrHyderabad.setEmail("hr.hyderabad@pulseai.com");
            regionalHrHyderabad.setPassword(passwordEncoder.encode("password"));
            regionalHrHyderabad.setRole(SecurityConstants.ROLE_REGIONAL_HR);
            regionalHrHyderabad.setRegion("HYDERABAD");
            repository.save(regionalHrHyderabad);

            UserCredential regionalHrChennai = new UserCredential();
            regionalHrChennai.setEmployeeId(8L);
            regionalHrChennai.setEmail("hr.chennai@pulseai.com");
            regionalHrChennai.setPassword(passwordEncoder.encode("password"));
            regionalHrChennai.setRole(SecurityConstants.ROLE_REGIONAL_HR);
            regionalHrChennai.setRegion("CHENNAI");
            repository.save(regionalHrChennai);

            UserCredential regionalHrThane = new UserCredential();
            regionalHrThane.setEmployeeId(9L);
            regionalHrThane.setEmail("hr.thane@pulseai.com");
            regionalHrThane.setPassword(passwordEncoder.encode("password"));
            regionalHrThane.setRole(SecurityConstants.ROLE_REGIONAL_HR);
            regionalHrThane.setRegion("THANE");
            repository.save(regionalHrThane);

            UserCredential employee = new UserCredential();
            employee.setEmployeeId(4L);
            employee.setEmail("employee@company.com");
            employee.setPassword(passwordEncoder.encode("password"));
            employee.setRole("ROLE_EMPLOYEE");
            employee.setRegion("APAC");
            repository.save(employee);

            log.info("Seeded VP, Global HR, Regional HR, and Employee accounts");
        }
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataSeeder.class);
    public DataSeeder(UserCredentialRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }
}
