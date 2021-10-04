package br.com.estudos.aws2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SnsMessage {
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("TopicArn")
    private String topicArn;
    @JsonProperty("Timestamp")
    private String timestamp;
    @JsonProperty("MessageId")
    private String messageId;
}
