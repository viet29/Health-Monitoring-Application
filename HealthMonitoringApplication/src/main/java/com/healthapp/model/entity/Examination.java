package com.healthapp.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "examination")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Examination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "spo2")
    private Float spO2;

    @Column(name = "heart_rate")
    private Float heartRate;

    @Column(name = "exam_time")
    @CreationTimestamp
    private LocalDateTime examTime;
}
