package com.nicolas.vagas_bot.repository;

import com.nicolas.vagas_bot.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.nicolas.vagas_bot.model.AreaAtuacao;
import com.nicolas.vagas_bot.model.Modalidade;
import java.time.LocalDate;


public interface VagaRepository extends JpaRepository<Vaga,Long> {
    boolean existsByUrl(String url);

    List<Vaga> findByAreaAtuacaoAndModalidade(AreaAtuacao areaAtuacao, Modalidade modalidade);

    List<Vaga> findByDataPublicacaoGreaterThanEqual(LocalDate dataLimite);
}
