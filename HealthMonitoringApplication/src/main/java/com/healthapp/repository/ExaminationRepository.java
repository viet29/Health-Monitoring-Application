package com.healthapp.repository;

import com.healthapp.model.entity.Examination;
import com.healthapp.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {
    List<Examination> findAllByPatient(Patient patient);
}