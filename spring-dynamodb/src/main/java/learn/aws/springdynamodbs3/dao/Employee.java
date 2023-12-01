package learn.aws.springdynamodbs3.dao;

import learn.aws.springdynamodbs3.dto.EmployeeDTO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@DynamoDbBean
public class Employee {

    private String empno;
    private String ename;
    private String deptName;
    private String location;
    private Set<String> skills;

    public Employee() {
        super();
    }

    public Employee(EmployeeDTO employeeDTO) {
        this.empno = employeeDTO.getEmpno();
        this.ename = employeeDTO.getEname();
        this.deptName = employeeDTO.getDeptName();
        this.location = employeeDTO.getLocation();
        this.skills = employeeDTO.getSkills();
    }

    public Employee(String empno, String ename, String deptName, String location, Set<String> skills) {
        this.empno = empno;
        this.ename = ename;
        this.deptName = deptName;
        this.location = location;
        this.skills = skills;
    }

    @DynamoDbPartitionKey
    public String getEmpno() {
        return empno;
    }

    public void setEmpno(String empno) {
        this.empno = empno;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }
}
