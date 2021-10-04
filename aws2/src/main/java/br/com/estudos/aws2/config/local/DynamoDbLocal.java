package br.com.estudos.aws2.config.local;

import br.com.estudos.aws2.repository.ProductEventLogRepository;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import lombok.extern.slf4j.Slf4j;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableDynamoDBRepositories(basePackageClasses = ProductEventLogRepository.class)
@Profile("local")
@Slf4j
public class DynamoDbLocal {
    private final AmazonDynamoDB amazonDynamoDB;

    public DynamoDbLocal() {
        this.amazonDynamoDB = AmazonDynamoDBClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566",
                        Regions.US_EAST_2.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

        List<AttributeDefinition> attributeDefinitionList =
                new ArrayList<>();
        attributeDefinitionList.add(new AttributeDefinition().withAttributeName("pk").withAttributeType(ScalarAttributeType.S));
        attributeDefinitionList.add(new AttributeDefinition().withAttributeName("sk").withAttributeType(ScalarAttributeType.S));

        List<KeySchemaElement> keySchemaElements =
                new ArrayList<>();
        keySchemaElements.add(new KeySchemaElement().withAttributeName("pk").withKeyType(KeyType.HASH));
        keySchemaElements.add(new KeySchemaElement().withAttributeName("sk").withKeyType(KeyType.RANGE));

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName("product-events")
                .withKeySchema(keySchemaElements)
                .withAttributeDefinitions(attributeDefinitionList)
                .withBillingMode(BillingMode.PAY_PER_REQUEST);

        //Table table = dynamoDB.createTable(createTableRequest);
        //try {
          //  table.waitForActive();
        //} catch (InterruptedException e) {
          //  log.error(e.getMessage());
        //}

    }

    @Bean
    @Primary
    public DynamoDBMapperConfig dynamoDBMapperConfig() {
        return DynamoDBMapperConfig.DEFAULT;
    }

    @Bean
    @Primary
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB,
                                         DynamoDBMapperConfig config) {
        return new DynamoDBMapper(amazonDynamoDB, config);
    }

    @Bean
    @Primary
    public AmazonDynamoDB amazonDynamoDB() {
       return this.amazonDynamoDB;
    }
}
