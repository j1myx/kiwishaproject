package com.kiwisha.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoPreferenceDTO {
    private String preferenceId;
    private String initPoint;
    private String sandboxInitPoint;
}
