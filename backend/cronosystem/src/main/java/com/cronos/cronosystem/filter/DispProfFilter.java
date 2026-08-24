package com.cronos.cronosystem.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DispProfFilter {

    private Long id;

    private String prof;

    private String horario;

}
