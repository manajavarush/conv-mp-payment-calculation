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

    @Id // Проблема(?) по ТЗ: id без авто-инкремента
    @EqualsAndHashCode.Include // Только ID определяет равенство
    private Long id;

    @Column(name = "inn")
    private Long inn; // !!! Проблема по ТЗ: ИНН как Long (потеря ведущих нулей, может начинаться с "0") !!!

    @Column(name = "name", nullable = false) // length = 255 по умолчанию, указание избыточно
    private String name;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false; // мягкое удаление

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
