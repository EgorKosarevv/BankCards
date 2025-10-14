package org.example.bankcards.dto.user.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.example.bankcards.entity.UserRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterRequestDTO {
    @Size(min = 2, max = 50, message = "The full name must contain from 2 to 50 characters")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЁё\\s'-]+$",
            message = "The full name must contain only letters, spaces, apostrophes, or hyphens")
    private String fullName;
    @Pattern(regexp = "^\\+?[0-9]$", message = "The phone number must be valid")
    private String phone;

    private UserRole role;
    private Boolean enabled;
}
