package br.com.estudos.aws.config.local;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.Topic;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@Slf4j
public class SqsCreateSubscribe {

    private final AmazonSQS sqsClient;

    public SqsCreateSubscribe(AmazonSNS snsClient
            , @Qualifier("productEventsTopic") Topic productEventsTopic) {
        this.sqsClient = AmazonSQSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", Regions.US_EAST_2.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        String queueUrl = sqsClient.createQueue(new CreateQueueRequest("product-events")).getQueueUrl();
        log.info("Sns Topic Arn {}", productEventsTopic);

        Topics.subscribeQueue(snsClient,sqsClient,productEventsTopic.getTopicArn(), queueUrl);
    }

}
