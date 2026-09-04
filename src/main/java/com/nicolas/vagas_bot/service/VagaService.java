package com.nicolas.vagas_bot.service;

import com.nicolas.vagas_bot.model.Vaga;
import com.nicolas.vagas_bot.repository.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service //Diz ao Spring: "essa classe contém lógica de negócio, gerencie ela pra mim"

public class VagaService {
    private final VagaRepository vagaRepository;

    //Injeção de dependência: o Spring entrega automaticamente uma instância
    //do VagaRepository pronta pra usar, sem você precisar fazer "new VagaRepository()"
    @Autowired 
    public VagaService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    //Salva a vaga só se a URL ainda não existir no banco
    //Retorna a vaga salva, ou null se era duplicata 
    public Vaga salvarSeNovaVaga(Vaga vaga) {
        boolean jaExiste = vagaRepository.existsByUrl(vaga.getUrl());

        if (jaExiste) {
            return null; //Ignora silenciosamente, sem erro
        }

        return vagaRepository.save(vaga);
    }

    public List<Vaga> listarTodas() {
        return vagaRepository.findAll();
    }

    public Optional<Vaga> buscarPorId(Long id) {
        return vagaRepository.findById(id);
    }
}
