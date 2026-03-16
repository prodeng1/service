package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(

        @NotBlank
        String name,

        @Email
        String email,

        @NotBlank
        String phone
) {}