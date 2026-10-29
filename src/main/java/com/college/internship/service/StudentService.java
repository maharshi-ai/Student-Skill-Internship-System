package com.college.internship.service;

import com.college.internship.dao.SkillDao;
import com.college.internship.dao.SkillDaoImpl;
import com.college.internship.dao.StudentDao;
import com.college.internship.dao.StudentDaoImpl;
import com.college.internship.exception.DatabaseException;
import com.college.internship.exception.ValidationException;
import com.college.internship.model.Skill;
import com.college.internship.model.Student;
import com.college.internship.model.StudentSkill;

import java.util.List;

/**
 * Service class handling Student profile and skills management logic.
 */
public class StudentService {
    private final StudentDao studentDao;
    private final SkillDao skillDao;

    public StudentService() {
        this.studentDao = new StudentDaoImpl();
        this.skillDao = new SkillDaoImpl();
    }

    public StudentService(StudentDao studentDao, SkillDao skillDao) {
        this.studentDao = studentDao;
        this.skillDao = skillDao;
    }

    public Student getStudentProfile(int studentId) throws DatabaseException {
        Student student = studentDao.getStudentById(studentId)
                .orElseThrow(() -> new DatabaseException("Student profile not found for ID: " + studentId));
        
        List<StudentSkill> skills = skillDao.getStudentSkills(studentId);
        student.setSkills(skills);
        return student;
    }

    public void updateProfile(Student student) throws ValidationException, DatabaseException {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new ValidationException("Student name cannot be empty.");
        }
        if (student.getCgpa() < 0.0 || student.getCgpa() > 10.0) {
            throw new ValidationException("CGPA must be between 0.0 and 10.0.");
        }

        studentDao.updateProfile(student);
    }

    public List<Student> getAllStudents() throws DatabaseException {
        List<Student> students = studentDao.getAllStudents();
        for (Student s : students) {
            s.setSkills(skillDao.getStudentSkills(s.getId()));
        }
        return students;
    }

    public List<Skill> getAllMasterSkills() throws DatabaseException {
        return skillDao.getAllSkills();
    }

    public void addSkillToStudent(int studentId, int skillId, StudentSkill.ProficiencyLevel level) throws DatabaseException {
        skillDao.addStudentSkill(studentId, skillId, level);
    }

    public void removeSkillFromStudent(int studentSkillId) throws DatabaseException {
        skillDao.removeStudentSkill(studentSkillId);
    }
}
