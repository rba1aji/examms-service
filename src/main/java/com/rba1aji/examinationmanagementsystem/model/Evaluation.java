package com.rba1aji.examinationmanagementsystem.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Course wise evaluation.
 *
 * @author rba1aji
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Evaluation {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  private String description;

  @ManyToOne
  private Exam exam;

  @ManyToOne
  private Course course;

  @ManyToOne
  private Faculty faculty;

  private long startPaperNumber;

  private long endPaperNumber;

  @ManyToOne
  private Configuration questionPaperConfig;

  private boolean active = true;

}
