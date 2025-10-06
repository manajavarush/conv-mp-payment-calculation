package ru.bank.conv.mp_payment_calculation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_client_balance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // БЕЗОПАСНО
@Builder
public class Balance {

    @Id // Проблема по ТЗ: id без авто-инкремента
    @EqualsAndHashCode.Include // Только ID определяет равенство
    private Long id;

    @Column(name = "dt", nullable = false)
    private LocalDateTime balanceDateTime; // Дата и время получения данных

    @Column(name = "morning_balance")
    private Double morningBalance;  // !!! Проблема по ТЗ: баланс является опциональным (может быть null) !!!

    @Column(name = "current_balance", nullable = false)
    private Double currentBalance;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
