package ru.bank.conv.mp_payment_calculation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // БЕЗОПАСНО
@Builder
public class Client {

    @Id // id без авто-инкремента -> получаем из внешней системы
    @EqualsAndHashCode.Include // Только ID определяет равенство
    private Long id;

    @Column(name = "inn", length = 12)
    private String inn;

    @Column(name = "name", nullable = false) // length = 255 по умолчанию, указание избыточно
    private String name;

    // Настройка columnDefinition для PostgresSQL -> устанавливает значение по умолчанию FALSE при вставке новой записи
    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default // устанавливает значение по умолчанию при использовании builder.build(), иначе был бы "null"
    private boolean isDeleted = false; // мягкое удаление

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
