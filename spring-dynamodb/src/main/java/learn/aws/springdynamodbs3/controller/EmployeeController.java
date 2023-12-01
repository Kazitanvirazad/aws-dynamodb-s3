package learn.aws.springdynamodbs3.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import learn.aws.springdynamodbs3.dto.EmployeeDTO;
import learn.aws.springdynamodbs3.service.EmployeeService;
import learn.aws.springdynamodbs3.util.ResponseObject;
import learn.aws.springdynamodbs3.validation.InvalidInputException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(path = "/api")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(path = "/test")
    public ResponseEntity<Set> getTestResponse() {
        Set<String> set = new HashSet<>() {{
            add("Java");
            add("NodeJS");
            add("Python");
        }};
        MultiValueMap<String, String> headerMap = new HttpHeaders() {{
            add("developer", "Kazi Tanvir Azad");
        }};

        return new ResponseEntity<>(set, new HttpHeaders(headerMap), HttpStatus.OK);
    }


    @PostMapping(path = "/addemployee")
    public ResponseEntity<ResponseObject> addEmployee(@RequestBody @Valid EmployeeDTO employeeDTO) {
        boolean isEmployeeAdded = employeeService.addEmployee(employeeDTO);

        return isEmployeeAdded ? new ResponseEntity<>(ResponseObject.builder()
                .setMessage("Emplpoyee Added successfully!")
                .build(), new HttpHeaders(), HttpStatus.CREATED) :
                new ResponseEntity<>(ResponseObject.builder()
                        .setError(true)
                        .setMessage("Failed to add Employee!")
                        .build(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "/getemployeelist")
    public ResponseEntity<ResponseObject> getEmployeeList() {
        List<EmployeeDTO> employeeDTOList = employeeService.getEmployeeList();
        return employeeDTOList.size() > 0 ? new ResponseEntity<>(ResponseObject.builder()
                .setData(employeeDTOList)
                .setMessage("Count: " + employeeDTOList.size())
                .build(), new HttpHeaders(), HttpStatus.OK) : new ResponseEntity<>(ResponseObject
                .builder().setError(true)
                .setMessage("No Employee found!")
                .build(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/getemployeebyempno")
    public ResponseEntity<ResponseObject> getEmployeeByEmpNo(@RequestParam(name = "empno") @NotEmpty String empno) {
        EmployeeDTO employeeDTO = employeeService.getEmployeeByEmpNo(empno);
        return employeeDTO != null ? new ResponseEntity<>(ResponseObject.builder()
                .setData(employeeDTO)
                .build(), new HttpHeaders(), HttpStatus.OK) : new ResponseEntity<>(ResponseObject.builder()
                .setError(true)
                .setMessage("Employee not found!")
                .build(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = "/updateemployee")
    public ResponseEntity<ResponseObject> updateEmployee(@RequestBody @Valid EmployeeDTO employeeDTO) {
        boolean isEmployeeUpdated = employeeService.updateEmployee(employeeDTO);

        return isEmployeeUpdated ? new ResponseEntity<>(ResponseObject.builder()
                .setMessage("Employee updated successfully!")
                .build(), new HttpHeaders(), HttpStatus.ACCEPTED) :
                new ResponseEntity<>(ResponseObject.builder()
                        .setError(true)
                        .setMessage("Employee updated failed!").build(), new HttpHeaders(),
                        HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path = "/deleteemployee")
    public ResponseEntity<ResponseObject> deleteEmployee(@RequestBody Map<String, Object> input) {
        if (input == null || input.isEmpty() || !input.containsKey("empno") || input.get("empno") == null) {
            throw new InvalidInputException("Invalid request body!");
        }
        String empno = (String) input.get("empno");
        boolean isEmployeeDeleted = employeeService.deleteEmployee(empno);

        return isEmployeeDeleted ? new ResponseEntity<>(ResponseObject.builder()
                .setMessage("Employee deleted successfully!")
                .build(), new HttpHeaders(), HttpStatus.ACCEPTED) :
                new ResponseEntity<>(ResponseObject.builder()
                        .setError(true)
                        .setMessage("Employee deletion failed!").build(), new HttpHeaders(),
                        HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/getemployeelistbyskill")
    public ResponseEntity<ResponseObject> getEmployeeListBySkill(@RequestParam(name = "skill") @NotEmpty String skill) {

        List<EmployeeDTO> employeeDTOList = employeeService.getEmployeeListBySkill(skill);
        return new ResponseEntity<>(ResponseObject.builder()
                .setData(employeeDTOList)
                .build(), new HttpHeaders(), HttpStatus.OK);
    }
}
