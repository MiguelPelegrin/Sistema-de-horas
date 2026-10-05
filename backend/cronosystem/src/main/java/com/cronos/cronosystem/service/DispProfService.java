package com.cronos.cronosystem.service;

import com.cronos.cronosystem.dto.DisponibilidadeProfDto;
import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.model.Horario;
import com.cronos.cronosystem.model.Prof;
import com.cronos.cronosystem.model.enums.DiaSemana;
import com.cronos.cronosystem.repository.DispProfRepository;
import com.cronos.cronosystem.repository.HorarioRepository;
import com.cronos.cronosystem.repository.ProfRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DispProfService {
    @Autowired
    private DispProfRepository repository;

    @Autowired
    private ProfRepository profRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    public DispProf salvar(DispProf x) {
        return repository.save(x);
    }

    public DispProf buscaroufalhar(Long yId){
        return repository.findById(yId)
                .orElseThrow(() -> new EntityNotFoundException("Disponibilidade não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){
        repository.deleteById(yId);
    }

    @Transactional
    public List<DispProf> salvarDisponibilidade(Long profId, List<DisponibilidadeProfDto> disponibilidades) {
        Prof prof = profRepository.findById(profId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com esse ID."));

        List<DispProf> salvas = new ArrayList<>();

        for (DisponibilidadeProfDto item : disponibilidades) {
            if (item == null || item.getDia() == null || item.getHoraInicio() == null || item.getHoraFim() == null) {
                continue;
            }

            DiaSemana diaSemana = DiaSemana.valueOf(item.getDia());
            LocalTime horaInicio = LocalTime.parse(item.getHoraInicio());
            LocalTime horaFim = LocalTime.parse(item.getHoraFim());

            Horario horario = horarioRepository
                    .findFirstByDiaSemanaAndHoraInicioAndHoraFim(diaSemana, horaInicio, horaFim)
                    .orElseGet(() -> {
                        Horario novoHorario = new Horario();
                        novoHorario.setDiaSemana(diaSemana);
                        novoHorario.setHoraInicio(horaInicio);
                        novoHorario.setHoraFim(horaFim);
                        novoHorario.setTempo_aula(item.getTempoAula() == null ? 50 : item.getTempoAula());
                        return horarioRepository.save(novoHorario);
                    });

            if (!repository.existsByProf_IdAndHorario_Id(prof.getId(), horario.getId())) {
                DispProf disponibilidade = new DispProf();
                disponibilidade.setProf(prof);
                disponibilidade.setHorario(horario);
                salvas.add(repository.save(disponibilidade));
            }
        }

        return salvas;
    }
}
