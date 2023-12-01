package learn.aws.springdynamodbs3.repository;

import learn.aws.springdynamodbs3.dao.Employee;

import java.util.List;

public interface EmployeeRepository {
    boolean saveEmployee(Employee employee);

    List<Employee> getEmployeeList();

    Employee getEmployeeByEmpNo(String empno);

    boolean updateEmployee(Employee employee);

    boolean deleteEmployee(String empno);

    List<Employee> getEmployeeListBySkill(String skill);
}
