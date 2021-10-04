package br.com.estudos.aws.config.local;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.S3Event;
import com.amazonaws.services.s3.model.TopicConfiguration;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class S3ConfigLocal {
    private static final String BUCKET_NAME = "pcs-invoice";

    private AmazonS3 amazonS3;

    public S3ConfigLocal() {
        amazonS3 = getAmazonS3();

        createBucket();

        AmazonSNS amazonSNSClient = getAmazonSns();

        String s3InvoiceEventsTopicArn = createTopic(amazonSNSClient);

        AmazonSQS sqsClient = getAmazonSQS();

        createQueue(amazonSNSClient, s3InvoiceEventsTopicArn, sqsClient);

        configureBucket(s3InvoiceEventsTopicArn);
    }

    private void configureBucket(String s3InvoiceEventsTopicArn) {
        TopicConfiguration topicConfiguration = new TopicConfiguration();
        topicConfiguration.setTopicARN(s3InvoiceEventsTopicArn);
        topicConfiguration.addEvent(S3Event.ObjectCreatedByPut);

        amazonS3.setBucketNotificationConfiguration(BUCKET_NAME,
                new BucketNotificationConfiguration().addConfiguration("putObject", topicConfiguration));
    }

    private void createQueue(AmazonSNS amazonSNSClient, String s3InvoiceEventsTopicArn, AmazonSQS sqsClient) {
        String queueUrl = sqsClient.createQueue(new CreateQueueRequest("s3-invoice-events")).getQueueUrl();

        Topics.subscribeQueue(amazonSNSClient,sqsClient,s3InvoiceEventsTopicArn, queueUrl);

    }

    private String createTopic(AmazonSNS amazonSNSClient) {
        CreateTopicRequest createTopicRequest = new CreateTopicRequest("s3-invoice-events");
        return amazonSNSClient.createTopic(createTopicRequest).getTopicArn();

    }

    private AmazonSQS getAmazonSQS() {
        return  AmazonSQSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", Regions.US_EAST_2.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    private AmazonSNS getAmazonSns() {
        return  AmazonSNSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", Regions.US_EAST_2.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    private void createBucket() {
        this.amazonS3.createBucket(BUCKET_NAME);
    }

    @Bean
    public AmazonS3 amazonS3Client(){
        return this.amazonS3;
    }

    private AmazonS3 getAmazonS3() {
        AWSCredentials credentials = new BasicAWSCredentials("test", "test");

        this.amazonS3 =  AmazonS3Client.builder().standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566"
                        , Regions.US_EAST_2.getName()))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .enablePathStyleAccess()
                .build();

        return this.amazonS3;
    }


}
