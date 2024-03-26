package com.bank.dtos;

import com.bank.domain.user.UserType;

import java.math.BigDecimal;

public record UserDTO(String name, String lastName, String email, String document, String password, BigDecimal balance, UserType userType) {
}
