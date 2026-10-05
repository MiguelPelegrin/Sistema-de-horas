package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.repository.GradeRepository;
import com.cronos.cronosystem.repository.MateriaRepository;
import com.cronos.cronosystem.repository.ProfRepository;
import com.cronos.cronosystem.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProfRepository profRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @GetMapping("/estatisticas")
    public Map<String, Long> estatisticas() {
        return Map.of(
                "totalProfessores", profRepository.count(),
                "totalDisciplinas", materiaRepository.count(),
                "totalTurmas", turmaRepository.count(),
                "totalAulas", gradeRepository.count()
        );
    }
}
