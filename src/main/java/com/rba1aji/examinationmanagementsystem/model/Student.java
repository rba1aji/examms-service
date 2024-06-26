package com.rba1aji.examinationmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Student extends User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @JsonIgnore
  private String password;

  @Column(unique = true)
  private String registerNumber;

  private Date dateOfBirth;

  private String fullName;

  @ManyToOne
  @JoinColumn
  private Department department;

  private String section;

  private String batch;

  private String phone;
}