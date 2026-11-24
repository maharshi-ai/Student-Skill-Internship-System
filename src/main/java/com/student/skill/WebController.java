package com.student.skill;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class WebController {
 private final InternshipRepository internships; private final ApplicationRepository applications;
 public WebController(InternshipRepository i,ApplicationRepository a){internships=i;applications=a;}
 @GetMapping("/") public String home(Model m){
  m.addAttribute("internships",internships.findAll());m.addAttribute("applications",applications.findAll());
  m.addAttribute("total",internships.count());m.addAttribute("applied",applications.count());return "index";
 }
 @PostMapping("/apply") public String apply(@RequestParam String studentName,@RequestParam String email,
  @RequestParam String course,@RequestParam Long internshipId,RedirectAttributes ra){
  Internship i=internships.findById(internshipId).orElse(null);
  if(i==null){ra.addFlashAttribute("error","Internship not found.");return "redirect:/";}
  applications.save(new StudentApplication(studentName,email,course,i));
  ra.addFlashAttribute("success","Application submitted successfully.");return "redirect:/";
 }
 @PostMapping("/admin/internships") public String add(@RequestParam String title,@RequestParam String company,
  @RequestParam String location,@RequestParam String skills,@RequestParam String type,@RequestParam String description,
  RedirectAttributes ra){
  internships.save(new Internship(title,company,location,skills,type,description));
  ra.addFlashAttribute("success","Internship published.");return "redirect:/";
 }
}
