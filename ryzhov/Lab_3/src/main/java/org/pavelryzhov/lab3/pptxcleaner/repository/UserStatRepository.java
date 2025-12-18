package org.pavelryzhov.lab3.pptxcleaner.repository;

import org.pavelryzhov.lab3.pptxcleaner.entity.UserStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatRepository extends JpaRepository<UserStat, Long> {
    Optional<UserStat> findByUsername(String username);
}
