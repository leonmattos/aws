package br.com.estudos.aws.service;

import br.com.estudos.aws.enums.EventType;
import br.com.estudos.aws.model.Envelope;
import br.com.estudos.aws.model.Product;
import br.com.estudos.aws.model.ProductEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductPublisher {
    private AmazonSNS snsClient;
    private Topic productEventsTopic;
    private ObjectMapper objectMapper;

    public ProductPublisher(AmazonSNS snsClient
            , @Qualifier("productEventsTopic") Topic productEventsTopic
            , ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.productEventsTopic = productEventsTopic;
        this.objectMapper = objectMapper;
    }

    public void publishProductEvent(Product product, EventType eventType
            , String username) {
        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductId(product.getId());
        productEvent.setCode(product.getCode());
        productEvent.setUsername(username);

        Envelope envelope = new Envelope();
        envelope.setEventType(eventType);
        try {
            envelope.setData(objectMapper.writeValueAsString(product));
            snsClient.publish(productEventsTopic.getTopicArn(),
                    objectMapper.writeValueAsString(envelope));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
