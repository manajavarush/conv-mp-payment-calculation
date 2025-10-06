package ru.bank.conv.mp_payment_calculation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_client_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // БЕЗОПАСНО
@Builder
public class Payment {

    @Id // Проблема(?) по ТЗ: id без авто-инкремента
    @EqualsAndHashCode.Include // Только ID определяет равенство
    private Long id;

    @Column(name = "dt", nullable = false)
    private LocalDateTime paymentDateTime;  // Дата и время получения данных о платежах

    @Column(name = "amount")
    private Double amount;  // !!! Проблема по ТЗ: double precision !!!

    @Column(name = "status")
    private Boolean isExecuted; // Статус платежа: true - исполнен / false - НЕ исполнен

    @Column(name = "direction")
    private Boolean isIncoming; // Направление платежа: true - входящий / false - исходящий

    @Column(name = "out_bank")
    private Boolean isExternal; // Тип платежа: true - внешний / false - внутренний

    @Column(name = "corr_bank_name")
    private String recipientBankName; // Название банка получателя платежа

    @Column(name = "description")
    private String description; // Описание назначения платежа

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
