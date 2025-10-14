package org.example.bankcards.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequestDTO {
    @Pattern(regexp = "^\\+?[0-9]{11}$", message = "The phone number must be valid")
    private String phone;

    @NotBlank(message = "The full name must not be empty")
    @Size(min = 2, max = 50, message = "The full name must contain from 2 to 50 characters")
    @Pattern(
            regexp = "^[A-Za-zА-Яа-яЁё\\s'-]+$",
            message = "The full name must contain only letters, spaces, apostrophes, or hyphens"
    )
    private String fullName;

    @NotBlank(message = "The password must not be empty")
    @Size(min = 6, max = 50, message = "The name must contain from 2 to 50 characters")
    private String password;
}
