package be.vlaanderen.vip.magda.client.domain.dto;

import be.vlaanderen.vip.magda.client.MagdaResponse;

public record WithMagdaResponse<T>(T object, MagdaResponse magdaResponse) {}