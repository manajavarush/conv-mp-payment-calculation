package ru.bank.conv.mp_payment_calculation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balance_seq_gen")
    @SequenceGenerator(name = "balance_seq_gen", sequenceName = "balance_id_seq", allocationSize = 1)
    @EqualsAndHashCode.Include // Только ID определяет равенство
    private Long id;

    @Column(name = "dt", nullable = false)
    private LocalDateTime balanceDateTime; // Дата и время получения данных

    @Column(name = "morning_balance", precision = 18, scale = 2)
    private BigDecimal morningBalance;

    @Column(name = "current_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentBalance;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
