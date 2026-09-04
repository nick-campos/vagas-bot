package com.nicolas.vagas_bot.repository;

import com.nicolas.vagas_bot.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VagaRepository extends JpaRepository<Vaga,Long> {
    boolean existsByUrl(String url);
}
