package org.peter.teyatinybank.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreationResponse {
    Long customerId;
    Long accountId;
}
