package org.pavelryzhov.lab3.pptxcleaner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_stats")
@Data
@NoArgsConstructor
public class UserStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    private int totalSlidesCleaned;

    public UserStat(String username, int totalSlidesCleaned) {
        this.username = username;
        this.totalSlidesCleaned = totalSlidesCleaned;
    }
}
