package learn.aws.springdynamodbs3.service;

import learn.aws.springdynamodbs3.dao.Employee;
import learn.aws.springdynamodbs3.dto.EmployeeDTO;
import learn.aws.springdynamodbs3.repository.EmployeeRepository;
import learn.aws.springdynamodbs3.validation.EmployeeNotFoundException;
import learn.aws.springdynamodbs3.validation.InvalidInputException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public boolean addEmployee(EmployeeDTO employeeDTO) {
        boolean isEmployeeAdded = false;
        if (employeeDTO != null && employeeDTO.getEmpno() != null && employeeDTO.getEname() != null) {
            Employee employee = new Employee(employeeDTO);
            isEmployeeAdded = employeeRepository.saveEmployee(employee);
        }
        return isEmployeeAdded;
    }

    public List<EmployeeDTO> getEmployeeList() {
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        List<Employee> employees = employeeRepository.getEmployeeList();
        for (Employee employee : employees) {
            employeeDTOList.add(new EmployeeDTO(employee));
        }
        return employeeDTOList;
    }

    public EmployeeDTO getEmployeeByEmpNo(String empno) {
        if (empno != null && empno.length() > 0) {
            Employee employee = employeeRepository.getEmployeeByEmpNo(empno);
            if (employee != null && employee.getEmpno() != null) {
                return new EmployeeDTO(employee);
            }
        }
        throw new EmployeeNotFoundException("Invalid Employee number!");
    }

    public boolean updateEmployee(EmployeeDTO employeeDTO) {
        boolean isEmployeeUpdated = false;
        if (employeeDTO != null && employeeDTO.getEmpno() != null && employeeDTO.getEname() != null) {
            Employee employee = new Employee(employeeDTO);
            isEmployeeUpdated = employeeRepository.updateEmployee(employee);
        }
        return isEmployeeUpdated;
    }

    public boolean deleteEmployee(String empno) {
        boolean isEmployeeDeleted = false;
        if (empno != null && empno.length() > 0) {
            isEmployeeDeleted = employeeRepository.deleteEmployee(empno);
        } else {
            throw new InvalidInputException("Invalid Employee number!");
        }
        return isEmployeeDeleted;
    }

    public List<EmployeeDTO> getEmployeeListBySkill(String skill) {
        if (skill == null || skill.length() < 1 || skill == "null") {
            throw new InvalidInputException("Entered skill is invalid!");
        }
        List<Employee> employeeList = employeeRepository.getEmployeeListBySkill(skill);
        if (employeeList.isEmpty()) {
            throw new EmployeeNotFoundException("Employee not found matching with the input skill");
        }
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        for (Employee employee : employeeList) {
            employeeDTOList.add(new EmployeeDTO(employee));
        }
        return employeeDTOList;
    }
}
