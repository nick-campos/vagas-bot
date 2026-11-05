package com.nicolas.vagas_bot.service;

import com.nicolas.vagas_bot.model.Vaga;
import com.nicolas.vagas_bot.repository.VagaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.nicolas.vagas_bot.model.AreaAtuacao;
import com.nicolas.vagas_bot.model.Modalidade;
import java.time.LocalDate;

@Service //Diz ao Spring: "essa classe contém lógica de negócio, gerencie ela pra mim"
public class VagaService {
    private final VagaRepository vagaRepository;
    private final GeocodingService geocodingService;

    //Injeção de dependência: o Spring entrega automaticamente uma instância
    //do VagaRepository pronta pra usar, sem você precisar fazer "new VagaRepository()"
    public VagaService(VagaRepository vagaRepository, GeocodingService geocodingService) {
        this.vagaRepository = vagaRepository;
        this.geocodingService = geocodingService;
    }

    //Salva a vaga só se a URL ainda não existir no banco e a cidade estiver dentro do raio de SP
    //Retorna a vaga salva, ou null se era duplicata ou fora do raio
    public Vaga salvarSeNovaVaga(Vaga vaga) {

        //Verifica duplicata por URL
        if (vagaRepository.existsByUrl(vaga.getUrl())) {
            return null; //Ignora silenciosamente, sem erro
        }

        if (vaga.getLocalizacao() != null && !vaga.getLocalizacao().equalsIgnoreCase("remoto")) {
            boolean noRaio = geocodingService.estaNoRaio(vaga.getLocalizacao());
            if (!noRaio) {
                return null;
            }
        }

        return vagaRepository.save(vaga);
    }

    public List<Vaga> listarTodas() {
        return vagaRepository.findAll();
    }

    public Optional<Vaga> buscarPorId(Long id) {
        return vagaRepository.findById(id);
    }

    public List<Vaga> filtrar(AreaAtuacao area, Modalidade modalidade) {
        return vagaRepository.findByAreaAtuacaoAndModalidade(area, modalidade);
    }

    public List<Vaga> listarVagasValidas() {
        LocalDate dataLimite = LocalDate.now().minusMonths(1);
        return vagaRepository.findByDataPublicacaoGreaterThanEqual(dataLimite);
    }

    public void deletarVaga(Long id) {
        // Verifica se a vaga existe antes de tentar deletar, senão o Spring lançaria uma exceção genérica difícil de entender
        if (!vagaRepository.existsById(id)) {
            throw new RuntimeException("Vaga não encontrada com id: " + id);
        }
        vagaRepository.deleteById(id);
    }
}