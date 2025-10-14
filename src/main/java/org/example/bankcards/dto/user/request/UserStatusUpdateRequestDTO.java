package org.example.bankcards.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusUpdateRequestDTO {

    @Builder.Default
    private boolean enable = true;
}
