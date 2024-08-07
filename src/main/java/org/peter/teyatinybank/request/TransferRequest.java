package org.peter.teyatinybank.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class TransferRequest {
    @NotNull
    private Long fromAccountId;
    private Long toAccountId;
    @NotNull
    private BigDecimal amount;

}