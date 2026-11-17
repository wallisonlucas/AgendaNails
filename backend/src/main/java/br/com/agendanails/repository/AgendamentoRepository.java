package br.com.agendanails.repository;

import br.com.agendanails.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("""
            SELECT a FROM Agendamento a
            JOIN FETCH a.cliente
            JOIN FETCH a.servico
            JOIN FETCH a.profissional
            WHERE a.inicio >= :de AND a.inicio < :ate
            ORDER BY a.inicio
            """)
    List<Agendamento> buscarDoPeriodo(@Param("de") LocalDateTime de,
                                      @Param("ate") LocalDateTime ate);

    @Query("""
            SELECT a FROM Agendamento a
            WHERE a.profissional.id = :profissionalId
              AND a.status <> br.com.agendanails.model.StatusAgendamento.CANCELADO
              AND a.inicio < :fim
              AND a.fim > :inicio
            """)
    List<Agendamento> buscarConflitos(@Param("profissionalId") Long profissionalId,
                                      @Param("inicio") LocalDateTime inicio,
                                      @Param("fim") LocalDateTime fim);

    List<Agendamento> findByClienteIdOrderByInicioDesc(Long clienteId);

    boolean existsByClienteId(Long clienteId);
}