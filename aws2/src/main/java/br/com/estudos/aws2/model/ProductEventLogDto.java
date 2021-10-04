package br.com.estudos.aws2.model;

import br.com.estudos.aws2.enums.EventType;
import lombok.Data;

@Data
public class ProductEventLogDto {
    private String code;

    private EventType eventType;

    private long productId;

    private String username;

    private long timestamp;

    public ProductEventLogDto(ProductEventLog productEventLog) {
        this.code = productEventLog.getPk();
        this.eventType = productEventLog.getEventType();
        this.productId = productEventLog.getProductId();
        this.username = productEventLog.getUsername();
        this.timestamp = productEventLog.getTimestamp();
    }
}
