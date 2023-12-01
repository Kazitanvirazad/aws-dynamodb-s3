package com.serverless.service;

import com.serverless.dao.AnimalDAO;
import com.serverless.dto.Animal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;


import java.lang.reflect.Field;
import java.util.*;

public class AnimalService {

    private static final Logger LOGGER = LogManager.getLogger(AnimalService.class);
    private static final Region region = Region.of(System.getenv("REGION"));
    private static final String practicetableName = System.getenv("DYNAMODB_PRACTICETABLE_NAME");
    private static final String practicetablePrimaryKey = System.getenv("DYNAMODB_PRACTICETABLE_PRIMARYKEY");

    public static boolean addAnimal(Animal animal) {
        boolean isAnimalAdded = false;
        if (animal != null) {
            AnimalDAO animalDAO = new AnimalDAO(animal);

            Map<String, AttributeValue> valueMap = animalDAO.getAttributeValueMap();

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(practicetableName)
                    .item(valueMap)
                    .build();
            try (DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                    .region(region)
                    .build()) {
                dynamoDbClient.putItem(putItemRequest);
                isAnimalAdded = true;
            } catch (ResourceNotFoundException e) {
                LOGGER.error(e.getMessage());
            } catch (DynamoDbException d) {
                LOGGER.error(d.getMessage());
            }
        }
        return isAnimalAdded;
    }

    public static List<Animal> getAnimalList() {
        List<Animal> animalList = new ArrayList<>();
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(practicetableName)
                .select(Select.ALL_ATTRIBUTES)
                .build();
        try (DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(region)
                .build()) {
            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
            if (scanResponse.hasItems()) {
                List<Map<String, AttributeValue>> scanItemsList = scanResponse.items();
                List<AnimalDAO> animalDAOList = AnimalDAO.getAnimalDAOListFromAttributeMapList(scanItemsList);

                for (AnimalDAO animalDAO : animalDAOList) {
                    animalList.add(new Animal(animalDAO));
                }
            }
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (DynamoDbException d) {
            LOGGER.error(d.getMessage());
        }
        return animalList;
    }

    public static List<Animal> getAnimalByName(String name) {
        List<Animal> animalList = new ArrayList<>();
        if (name != null && name.length() > 0) {
            Map<String, String> attributeNameMap = new HashMap<>() {{
                put("#nme", "name");
            }};
            Map<String, AttributeValue> attributeValueMap = new HashMap<>() {{
                put(":name", AttributeValue.builder().s(name).build());
            }};
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(practicetableName)
                    .filterExpression("#nme = :name")
                    .expressionAttributeNames(attributeNameMap)
                    .expressionAttributeValues(attributeValueMap)
                    .build();

            try (DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                    .region(region).build()) {
                ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
                if (scanResponse.hasItems()) {
                    List<Map<String, AttributeValue>> scanItemList = scanResponse.items();
                    List<AnimalDAO> animalDAOList = AnimalDAO.getAnimalDAOListFromAttributeMapList(scanItemList);
                    for (AnimalDAO animalDAO : animalDAOList) {
                        animalList.add(new Animal(animalDAO));
                    }
                }
            } catch (ResourceNotFoundException e) {
                LOGGER.error(e.getMessage());
            } catch (DynamoDbException d) {
                LOGGER.error(d.getMessage());
            }

            return animalList.size() > 0 ? animalList : null;
        } else {
            return null;
        }
    }

    /**
     * There is no way to update results in DynamoDB if you don't posses keys to the objects beforehand.
     * This means that while you can do that in SQL (UPDATE + WHERE) you can't do that in DynamoDB.
     * You will have to first fetch the objects (using the secondary indices) and then do a batch update on the primary keys.
     */


    public static boolean updateAnimalByAttribute(Map<String, Object> input) {
        boolean isItemUpdated = false;
        if (input != null && input.containsKey("key") && input.size() > 1) {
            Map<String, String> attributeNameMap = new HashMap<>();
            Map<String, AttributeValue> attributeValueMap = new HashMap<>();
            String updateExpression = "set ";
            Map<String, AttributeValue> keyMap = Map.of(practicetablePrimaryKey, AttributeValue.builder().s((String) input.get("key")).build());

//            attributeValueMap.put(":foodType", AttributeValue.builder().s((String) input.get("foodType")).build());
//            String conditionExpression = "food = :foodType";
            boolean inputHasClassFieldFlag = false;
            Field[] fields = AnimalDAO.class.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (input.containsKey(field.getName())) {
                    inputHasClassFieldFlag = true;
                    attributeNameMap.put("#" + field.getName(), field.getName());
                    updateExpression += "#" + attributeNameMap.get("#" + field.getName()) + " = ";
                    if (field.getType() == String.class) {
                        attributeValueMap.put(":" + field.getName(), AttributeValue.builder().s((String) input.get(field.getName())).build());
                    } else if (field.getType() == Boolean.class) {
                        attributeValueMap.put(":" + field.getName(), AttributeValue.builder().bool((Boolean) input.get(field.getName())).build());
                    }
                    updateExpression += ":" + field.getName() + ", ";
                }
            }

            if (!inputHasClassFieldFlag) {
                return false;
            } else {
                updateExpression = updateExpression.substring(0, updateExpression.length() - 2);
            }

            UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                    .tableName(practicetableName)
                    .key(keyMap)
                    .updateExpression(updateExpression)
                    .expressionAttributeNames(attributeNameMap)
                    .expressionAttributeValues(attributeValueMap)
                    .build();

            GetItemRequest getItemRequest = GetItemRequest.builder()
                    .tableName(practicetableName)
                    .key(keyMap)
                    .build();

            try (DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                    .region(region).build()) {
                GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
                if (getItemResponse.hasItem()) {
                    dynamoDbClient.updateItem(updateItemRequest);
                    isItemUpdated = true;
                }
            } catch (ResourceNotFoundException e) {
                LOGGER.error(e.getMessage());
            } catch (DynamoDbException d) {
                LOGGER.error(d.getMessage());
            }
        }
        return isItemUpdated;
    }

    public static boolean updateAnimalByFood(Map<String, Object> input) {
        boolean isItemUpdated = false;
        if (input != null && input.containsKey("foodType") && input.containsKey("key") && input.size() > 1) {
            String foodType = (String) input.get("foodType");
            String primary_key = (String) input.get("key");

            boolean inputHasClassFieldFlag = false;
            String partiQlStatement = "UPDATE \"" + practicetableName + "\" SET ";

            Iterator<String> iterator = input.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();

                try {
                    Field field = AnimalDAO.class.getDeclaredField(key);
                    inputHasClassFieldFlag = true;
                    partiQlStatement += "\"" + field.getName() + "\" = ";
                    if (field.getType() == String.class) {
                        partiQlStatement += "'" + input.get(field.getName()) + "' SET ";
                    } else {
                        partiQlStatement += input.get(field.getName()) + " SET ";
                    }
                } catch (NoSuchFieldException e) {

                }
            }

            if (!inputHasClassFieldFlag) {
                return false;
            } else {
                partiQlStatement = partiQlStatement.substring(0, partiQlStatement.length() - 4);
                partiQlStatement += "WHERE \"info_id\" = '" + primary_key + "' and \"food\" = '" + foodType + "'";
            }

            String finalPartiQlStatement = partiQlStatement;
            LOGGER.info(finalPartiQlStatement);

            List<BatchStatementRequest> statementList = new ArrayList<>() {{
                add(BatchStatementRequest.builder().statement(finalPartiQlStatement).build());
            }};
            BatchExecuteStatementRequest batchExecuteStatementRequest = BatchExecuteStatementRequest.builder()
                    .statements(statementList)
                    .build();

            try (DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                    .region(region).build()) {
                BatchExecuteStatementResponse response = dynamoDbClient.batchExecuteStatement(batchExecuteStatementRequest);
                if (response.hasResponses()) {
                    isItemUpdated = true;
                }
            } catch (ResourceNotFoundException e) {
                LOGGER.error(e.getMessage());
            } catch (DynamoDbException d) {
                LOGGER.error(d.getMessage());
            }
        }
        return isItemUpdated;
    }

    public static boolean deleteAnimal(String primaryKey) {
        boolean isAnimalDeleted = false;
        if (primaryKey != null && primaryKey.length() > 0) {

            Map<String, AttributeValue> keyMap = Map.of(practicetablePrimaryKey, AttributeValue.builder().s(primaryKey).build());

            GetItemRequest getItemRequest = GetItemRequest.builder()
                    .tableName(practicetableName)
                    .key(keyMap)
                    .build();

            DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                    .tableName(practicetableName)
                    .returnValues(ReturnValue.ALL_OLD)
                    .key(keyMap)
                    .build();

            try (DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                    .region(region).build()) {
                if (dynamoDbClient.getItem(getItemRequest).hasItem()) {
                    DeleteItemResponse response = dynamoDbClient.deleteItem(deleteItemRequest);
                    if (!response.attributes().isEmpty()) {
                        isAnimalDeleted = true;
                    }
                }
            } catch (DynamoDbException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return isAnimalDeleted;
    }
}