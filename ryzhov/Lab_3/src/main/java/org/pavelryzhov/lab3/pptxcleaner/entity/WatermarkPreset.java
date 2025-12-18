package org.pavelryzhov.lab3.pptxcleaner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "watermark_presets")
@Data
@NoArgsConstructor
public class WatermarkPreset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String replacementText;
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserStat user;

    public WatermarkPreset(String title, String replacementText, String color, UserStat user) {
        this.title = title;
        this.replacementText = replacementText;
        this.user = user;
        this.color = color;
    }
}
