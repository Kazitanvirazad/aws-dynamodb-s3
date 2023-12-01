package learn.aws.springdynamodbs3.config;

import learn.aws.springdynamodbs3.dao.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDBTableConfig {
    @Value("${employee_table.name}")
    private String employee_table_name;

    @Value("${region}")
    private String employee_table_region;

    @Bean
    public DynamoDbTable<Employee> getEmployeeTable() {
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();

        Region region = Region.of(employee_table_region);

        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        DynamoDbTable<Employee> table = enhancedClient.table(employee_table_name,
                TableSchema.fromBean(Employee.class));

        return table;
    }
}
