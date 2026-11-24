package com.student.skill;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class DataLoader {
 @Bean CommandLineRunner seed(InternshipRepository repo){return args->{
  if(repo.count()==0){
   repo.save(new Internship("Java Developer Intern","TechNova","Hyderabad","Java, OOP, SQL","On-site","Build backend features, write clean code and collaborate with engineers."));
   repo.save(new Internship("Frontend Developer Intern","PixelWorks","Remote","HTML, CSS, JavaScript, React","Remote","Create responsive user interfaces and improve user experience."));
   repo.save(new Internship("Data Analyst Intern","DataBridge","Bengaluru","Python, SQL, Excel","Hybrid","Analyze datasets and communicate useful business insights."));
  }
 };}
}
