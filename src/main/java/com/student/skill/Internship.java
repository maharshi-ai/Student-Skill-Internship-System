package com.student.skill;
import jakarta.persistence.*;
@Entity
public class Internship {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String title, company, location, skills, type;
 @Column(length=1500) private String description;
 public Internship() {}
 public Internship(String title,String company,String location,String skills,String type,String description){
  this.title=title;this.company=company;this.location=location;this.skills=skills;this.type=type;this.description=description;
 }
 public Long getId(){return id;} public String getTitle(){return title;} public String getCompany(){return company;}
 public String getLocation(){return location;} public String getSkills(){return skills;} public String getType(){return type;}
 public String getDescription(){return description;}
}
