package com.student.skill;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name="student_applications")
public class StudentApplication {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String studentName,email,course;
 @ManyToOne(optional=false) private Internship internship;
 private String status="PENDING";
 private LocalDateTime appliedAt=LocalDateTime.now();
 public StudentApplication() {}
 public StudentApplication(String n,String e,String c,Internship i){studentName=n;email=e;course=c;internship=i;}
 public Long getId(){return id;} public String getStudentName(){return studentName;} public String getEmail(){return email;}
 public String getCourse(){return course;} public Internship getInternship(){return internship;} public String getStatus(){return status;}
 public LocalDateTime getAppliedAt(){return appliedAt;}
}
