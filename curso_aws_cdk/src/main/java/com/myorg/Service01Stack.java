package com.myorg;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.sqs.Queue;

import java.util.HashMap;
import java.util.Map;

public class Service01Stack extends Stack {
    public Service01Stack(final Construct scope, final String id, Cluster cluster, SnsTopic productEventsTopic,
                          Bucket invoicBucket, Queue invoiceQueue) {
        this(scope, id, null, cluster, productEventsTopic, invoicBucket, invoiceQueue);
    }

    public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster, SnsTopic productEventsTopic, Bucket invoiceBucket, Queue invoiceQueue) {
        super(scope, id, props);

        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("SPRING_DATASOURCE_URL", "jdbc:mysql://" + Fn.importValue("rds-endpoint")
                + ":3306/aws_project01?createDatabaseIfNotExist=true");
        envVariables.put("SPRING_DATASOURCE_USERNAME", "admin");
        envVariables.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("rds-password"));
        envVariables.put("AWS_REGION", "us-east-2");
        envVariables.put("AWS_SNS_TOPIC_PRODUCT_EVENTS_ARN", productEventsTopic.getTopic().getTopicArn());
        envVariables.put("AWS_S3_BUCKET_INVOICE_NAME", invoiceBucket.getBucketName());
        envVariables.put("AWS_SQS_QUEUE_INVOICE_EVENTS_NAME", invoiceQueue.getQueueName());


        ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService.Builder.create(this, "ALB01")
                .serviceName("service-01")
                .cluster(cluster)
                .cpu(512)
                .memoryLimitMiB(1024)
                .desiredCount(2)
                .listenerPort(8080)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("aws_project01")
                                .image(ContainerImage.fromRegistry("leonmattos/curso_aws_project01:1.0.3"))
                                .containerPort(8080)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this, "Service01LogGroup")
                                                .logGroupName("Service01")
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .build())
                                        .streamPrefix("Service01")
                                        .build()))
                                .environment(envVariables)
                                .build())
                .publicLoadBalancer(true)
                .build();

        service01.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health")
                .port("8080")
                .healthyHttpCodes("200")
                .build());

        ScalableTaskCount scalableTaskCount = service01.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .minCapacity(2)
                .maxCapacity(4)
                .build());

        scalableTaskCount.scaleOnCpuUtilization("Service01AutoScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(50)
                .scaleInCooldown(Duration.seconds(60))
                .scaleOutCooldown(Duration.seconds(60))
                .build());

        productEventsTopic.getTopic().grantPublish(service01.getTaskDefinition().getTaskRole());
        invoiceQueue.grantConsumeMessages(service01.getTaskDefinition().getTaskRole());
        invoiceBucket.grantReadWrite(service01.getTaskDefinition().getTaskRole());

    }
}
