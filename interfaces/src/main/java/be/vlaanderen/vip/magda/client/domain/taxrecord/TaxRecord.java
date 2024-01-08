package be.vlaanderen.vip.magda.client.domain.taxrecord;

import jakarta.validation.constraints.NotNull;

public record TaxRecord(
        @NotNull String code,
        double amount) { }
