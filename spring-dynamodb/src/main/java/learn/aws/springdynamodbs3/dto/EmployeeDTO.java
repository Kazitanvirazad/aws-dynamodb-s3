package learn.aws.springdynamodbs3.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import learn.aws.springdynamodbs3.dao.Employee;

import java.util.Set;

public final class EmployeeDTO {

    @NotNull(message = "Employee number cannot be null")
    @NotBlank(message = "Employee number cannot be empty")
    private String empno;
    @NotNull(message = "Employee name cannot be null")
    @NotBlank(message = "Employee name cannot be empty")
    private String ename;
    private String deptName;
    private String location;
    private Set<String> skills;

    public EmployeeDTO() {
        super();
    }

    public EmployeeDTO(Employee employee) {
        this.empno = employee.getEmpno();
        this.ename = employee.getEname();
        this.deptName = employee.getDeptName();
        this.location = employee.getLocation();
        this.skills = employee.getSkills();
    }

    public EmployeeDTO(String empno, String ename, String deptName, String location, Set<String> skills) {
        this.empno = empno;
        this.ename = ename;
        this.deptName = deptName;
        this.location = location;
        this.skills = skills;
    }

    public String getEmpno() {
        return empno;
    }

    public String getEname() {
        return ename;
    }

    public String getDeptName() {
        return deptName;
    }

    public String getLocation() {
        return location;
    }

    public Set<String> getSkills() {
        return skills;
    }
}
