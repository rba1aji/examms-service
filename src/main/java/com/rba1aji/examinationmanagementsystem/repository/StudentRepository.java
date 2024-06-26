package com.rba1aji.examinationmanagementsystem.repository;

import com.rba1aji.examinationmanagementsystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
  Optional<Student> findByRegisterNumber(String regNo);

  List<Student> findAllByRegisterNumberBetween(String startRegNo, String endRegNo);
}
