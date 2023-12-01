package learn.aws.springdynamodbs3.repository.impl;

import learn.aws.springdynamodbs3.dao.Employee;
import learn.aws.springdynamodbs3.repository.EmployeeRepository;
import learn.aws.springdynamodbs3.validation.EmployeeNotFoundException;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;

@Component
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private DynamoDbTable<Employee> employeeDynamoDbTable;

    public EmployeeRepositoryImpl(DynamoDbTable<Employee> employeeDynamoDbTable) {
        this.employeeDynamoDbTable = employeeDynamoDbTable;
    }

    @Override
    public boolean saveEmployee(Employee employee) {
        boolean isItemAdded = false;
        if (employee != null && employee.getEmpno() != null && employee.getEname() != null) {
            String empno = employee.getEmpno();

            Employee existEmployee = employeeDynamoDbTable.getItem(Key.builder()
                    .partitionValue(empno)
                    .build());
            if (existEmployee == null) {
                employeeDynamoDbTable.putItem(employee);
                isItemAdded = true;
            }
        }
        return isItemAdded;
    }

    @Override
    public List<Employee> getEmployeeList() {
        try {
            return employeeDynamoDbTable.scan().items().stream().toList();
        } catch (EmployeeNotFoundException e) {
            throw new EmployeeNotFoundException("Employee not found");
        }
    }

    @Override
    public Employee getEmployeeByEmpNo(String empno) {
        Employee employee = employeeDynamoDbTable.getItem(GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(empno)
                        .build())
                .build());
        return employee;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        boolean isEmployeeUpdated = false;
        Employee existingEmployee = getEmployeeByEmpNo(employee.getEmpno());
        if (existingEmployee != null && existingEmployee.getEmpno() != null) {
            employeeDynamoDbTable.updateItem(UpdateItemEnhancedResponse.builder(Employee.class)
                    .attributes(employee)
                    .build().attributes());
            isEmployeeUpdated = true;
        } else {
            throw new EmployeeNotFoundException("Employee not found");
        }
        return isEmployeeUpdated;
    }

    @Override
    public boolean deleteEmployee(String empno) {
        boolean isEmployeeDeleted = false;
        Employee existingEmployee = getEmployeeByEmpNo(empno);
        if (existingEmployee != null && existingEmployee.getEmpno() != null) {
            employeeDynamoDbTable.deleteItem(DeleteItemEnhancedRequest.builder()
                    .key(Key.builder().partitionValue(empno).build())
                    .build());
            isEmployeeDeleted = true;
        } else {
            throw new EmployeeNotFoundException("Employee not found");
        }
        return isEmployeeDeleted;
    }

//    @Override
//    public List<Employee> getEmployeeListBySkill(String skill) {
//        String finalPartiQlStatement = "SELECT * FROM \"employee\" WHERE contains(\"skills\",'Java')";
//        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
//
//        Region region = Region.of(employee_table_region);
//        List<BatchStatementRequest> statementList = new ArrayList<>() {{
//            add(BatchStatementRequest.builder().statement(finalPartiQlStatement).build());
//        }};
//        BatchExecuteStatementRequest batchExecuteStatementRequest = BatchExecuteStatementRequest.builder()
//                .statements(statementList)
//                .build();
//
//        try (DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
//                .region(region)
//                .credentialsProvider(credentialsProvider)
//                .build()) {
//            BatchExecuteStatementResponse response = dynamoDbClient.batchExecuteStatement(batchExecuteStatementRequest);
//            if (response.hasResponses()) {
//                for (BatchStatementResponse batchStatementResponse : response.responses()) {
//                    resList.add(batchStatementResponse.item());
//                }
//            }
//        } catch (ResourceNotFoundException e) {
//            System.out.println(e.getMessage());
//        }
//        if (!resList.isEmpty()) {
//            return resList;
//        } else {
//            throw new EmployeeNotFoundException("Employee not found matching with the input skill");
//        }
//    }

    @Override
    public List<Employee> getEmployeeListBySkill(String skill) {
//        List<Employee> employeeList = employeeDynamoDbTable.scan()
//                .items().stream().filter(emp -> emp.getSkills().contains(skill)).toList();

        List<Employee> employeeList = employeeDynamoDbTable.scan(ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                        .putExpressionName("#skills", "skills")
                        .putExpressionValue(":skill", AttributeValue.builder().s(skill).build())
                        .expression("contains(#skills, :skill)")
                        .build()).build()).items().stream().toList();
        return employeeList;
    }
}
