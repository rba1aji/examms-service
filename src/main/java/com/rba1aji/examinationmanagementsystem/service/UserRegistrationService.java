package com.rba1aji.examinationmanagementsystem.service;

import com.rba1aji.examinationmanagementsystem.dto.RepoSaveResponseDto;
import com.rba1aji.examinationmanagementsystem.dto.request.SaveFacultyReqDto;
import com.rba1aji.examinationmanagementsystem.model.Department;
import com.rba1aji.examinationmanagementsystem.model.Faculty;
import com.rba1aji.examinationmanagementsystem.model.Student;
import com.rba1aji.examinationmanagementsystem.repository.FacultyRepository;
import com.rba1aji.examinationmanagementsystem.repository.StudentRepository;
import com.rba1aji.examinationmanagementsystem.utilities.BaseResponse;
import com.rba1aji.examinationmanagementsystem.utilities.EncryptionUtils;
import com.rba1aji.examinationmanagementsystem.utilities.ExcelCellUtils;
import com.rba1aji.examinationmanagementsystem.utilities.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserRegistrationService {

  private final BaseResponse baseResponse;
  private final FacultyRepository facultyRepository;
  private final StudentRepository studentRepository;

  @Transactional
  public ResponseEntity<?> registerSingleFaculty(SaveFacultyReqDto dto) {
    try {
      Faculty faculty = Faculty.builder()
          .username(dto.getUserName())
          .password(EncryptionUtils.encrypt(dto.getPassword()))
          .fullName(dto.getFullName())
          .designation(dto.getDesignation())
          .phone(dto.getPhone())
          .email(dto.getEmail())
          .build();
      if (ValidationUtils.isNotNullAndNotEmpty(dto.getDepartmentCode())) {
        faculty.setDepartment(Department.builder().code(dto.getDepartmentCode()).build());
      }
      if (facultyRepository.findByUsername(faculty.getUsername()).isPresent())
        return baseResponse.errorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Faculty username already exists!");
      faculty.setPassword(EncryptionUtils.encrypt(faculty.getPassword()));
      facultyRepository.save(faculty);
      return baseResponse.successResponse("Faculty registered successfully!");
    } catch (Exception e) {
      e.printStackTrace();
      return baseResponse.errorResponse(e);
    }
  }

  public ResponseEntity<?> excelRegisterStudents(MultipartFile file) {
    long startTime = System.currentTimeMillis();
    List<RepoSaveResponseDto> errorList = new ArrayList<>();
    List<Student> studentList = new ArrayList<>();
    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);
      sheet.removeRow(sheet.getRow(0));                                               // remove header row
      sheet.forEach(row -> {
        try {
          Student student = Student.builder()
              .registerNumber(ExcelCellUtils.getString(row.getCell(0)))
              .dateOfBirth(ExcelCellUtils.getDate(row.getCell(1)))
              .fullName(ExcelCellUtils.getString(row.getCell(2)))
              .department(Department.builder().code(ExcelCellUtils.getString(row.getCell(3))).build())
              .section(ExcelCellUtils.getString(row.getCell(4)))
              .batch(ExcelCellUtils.getString(row.getCell(5)))
              .phone(ExcelCellUtils.getString(row.getCell(6)))
              .password(EncryptionUtils.encrypt(ExcelCellUtils.getDateString(row.getCell(1))))
              .build();
          studentList.add(student);
        } catch (Exception e) {
          var error = new RepoSaveResponseDto();
          error.setMessage(e.getMessage());
          errorList.add(error);
        }
      });
      log.info("studentExcelProcess -- size:" + sheet.getPhysicalNumberOfRows() + " -- time:" + (System.currentTimeMillis() - startTime) + "ms");
      studentRepository.saveAll(studentList);
      log.info("studentRegistration -- size:" + studentList.size() + " -- time:" + (System.currentTimeMillis() - startTime) + "ms");
      return baseResponse.successResponse(
          errorList,
          "Successfully registered students with " + errorList.size() + " failure!");
    } catch (Exception e) {
      log.info("Error in excelRegisterStudents(): ", e);
      return baseResponse.errorResponse(e);
    }
  }

  public ResponseEntity<?> excelRegisterFaculties(MultipartFile file) {
    long startTime = System.currentTimeMillis();
    List<Object> errorList = new ArrayList<>();
    List<Faculty> facultyList = new ArrayList<>();
    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);
      sheet.removeRow(sheet.getRow(0));                                               // remove header row
      sheet.forEach(row -> {
        var error = new RepoSaveResponseDto();
        try {
          Faculty faculty = Faculty.builder()
              .username(ExcelCellUtils.getString(row.getCell(0)))
              .password(EncryptionUtils.encrypt(ExcelCellUtils.getString(row.getCell(1))))
              .fullName(ExcelCellUtils.getString(row.getCell(2)))
              .designation(ExcelCellUtils.getString(row.getCell(3)))
              .department(Department.builder().code(ExcelCellUtils.getString(row.getCell(4))).build())
              .phone(ExcelCellUtils.getString(row.getCell(5)))
              .email(ExcelCellUtils.getString(row.getCell(6)))
              .build();
          facultyList.add(faculty);
        } catch (Exception e) {
          error.setMessage(e.getMessage());
          errorList.add(error);
        }
      });
      log.info("facultyExcelProcess -- size:" + sheet.getPhysicalNumberOfRows() + " -- time:" + (System.currentTimeMillis() - startTime) + "ms");
      facultyRepository.saveAll(facultyList);
      log.info("facultyRegistration -- size:" + facultyList.size() + " -- time:" + (System.currentTimeMillis() - startTime) + "ms");
      return baseResponse.successResponse(errorList,
          "Successfully registered faculties with " + errorList.size() + " failure!"
      );
    } catch (Exception e) {
      log.info("Error in excelRegisterStudents(): ", e);
      return baseResponse.errorResponse(e);
    }
  }

  @Transactional
  public Student saveStudent(Student student) {
    return studentRepository.saveAndFlush(student);
  }

  @Transactional
  public Faculty saveFaculty(Faculty faculty) {
    return facultyRepository.saveAndFlush(faculty);
  }
}
