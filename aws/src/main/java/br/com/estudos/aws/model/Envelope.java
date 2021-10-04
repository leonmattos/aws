package br.com.estudos.aws.model;

import br.com.estudos.aws.enums.EventType;
import lombok.Data;

@Data
public class Envelope {
    private EventType eventType;
    private String data;
}
