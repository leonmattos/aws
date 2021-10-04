package br.com.estudos.aws.model;

import lombok.Data;

@Data
public class UrlResponse {
    private String url;
    private long expirationTime;
}
