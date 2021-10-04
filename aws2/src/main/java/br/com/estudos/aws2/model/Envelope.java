package br.com.estudos.aws2.model;

import br.com.estudos.aws2.enums.EventType;
import lombok.Data;

@Data
public class Envelope {
    private EventType eventType;
    private String data;
}
