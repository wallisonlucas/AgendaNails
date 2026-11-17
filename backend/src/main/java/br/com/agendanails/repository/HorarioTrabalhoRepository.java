package br.com.agendanails.repository;

import br.com.agendanails.model.HorarioTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.DayOfWeek;
import java.util.List;

public interface HorarioTrabalhoRepository extends JpaRepository<HorarioTrabalho, Long> {

    List<HorarioTrabalho> findByProfissionalIdAndDiaSemana(Long profissionalId, DayOfWeek diaSemana);

    List<HorarioTrabalho> findByProfissionalId(Long profissionalId);
}