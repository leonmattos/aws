package br.com.estudos.aws.model;

import lombok.Data;

import javax.persistence.*;

@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"invoiceNumber"})
        }
)
@Entity
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String invoiceNumber;
    private String customerName;
    private float totalValue;
    private long productId;
    private int quantity;
}
