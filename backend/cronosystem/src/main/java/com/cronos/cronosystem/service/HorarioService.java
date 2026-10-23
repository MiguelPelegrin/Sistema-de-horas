package com.cronos.cronosystem.service;


import com.cronos.cronosystem.exception.RegraNegocioException;
import com.cronos.cronosystem.model.Horario;
import com.cronos.cronosystem.repository.DispProfRepository;
import com.cronos.cronosystem.repository.GradeRepository;
import com.cronos.cronosystem.repository.HorarioRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;


@Service
public class HorarioService {
    @Autowired
    private HorarioRepository repository;

    @Autowired
    private DispProfRepository dispProfRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EscolaService escolaService;

    public List<Horario> listar() {
        return repository.findByEscola_Id(escolaService.escolaLogadaId());
    }

    public Horario salvar(Horario x) {
        validarIntervalo(x.getHoraInicio(), x.getHoraFim());
        x.setEscola(escolaService.escolaLogada());
        return repository.save(x);
    }

    public Horario buscaroufalhar(Long yId) {
        return repository.findByIdAndEscola_Id(yId, escolaService.escolaLogadaId())
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com esse ID."));
    }

    public static void validarIntervalo(LocalTime horaInicio, LocalTime horaFim) {
        if (horaInicio != null && horaFim != null && !horaFim.isAfter(horaInicio)) {
            throw new RegraNegocioException("O horário de término precisa ser depois do horário de início.");
        }
    }

    @Transactional
    public void excluir(Long yId) {
        Horario horario = buscaroufalhar(yId);
        gradeRepository.deleteByHorario_Id(horario.getId());
        dispProfRepository.deleteByHorario_Id(horario.getId());
        repository.delete(horario);
    }
}
