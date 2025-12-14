package com.udipsai.backend.citas.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCitaDTO {
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date fecha;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime hora;
    private String profesional;
    private String area;
}
