package com.rba1aji.examinationmanagementsystem.repository;

import com.rba1aji.examinationmanagementsystem.model.ExamBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamBatchRepository extends JpaRepository<ExamBatch, Long>, JpaSpecificationExecutor<ExamBatch> {
  List<ExamBatch> findAllByFacultyIdAndActiveOrderByStartTimeDesc(long facultyId, boolean active);
}
