package org.peter.teyatinybank.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class DepositRequest {
    @NotNull
    private Long accountId;
    @NotNull
    private BigDecimal amount;


}