package com.nicolas.vagas_bot.controller;

import java.util.List;
import com.nicolas.vagas_bot.model.AreaAtuacao;
import com.nicolas.vagas_bot.model.Modalidade;
import com.nicolas.vagas_bot.model.Vaga;
import com.nicolas.vagas_bot.service.GupyScraperService;
import com.nicolas.vagas_bot.service.VagaGupyDTO;
import com.nicolas.vagas_bot.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;


@RestController //Diz ao Spring: essa classe responde requisições HTTP e devolve dados (não uma página HTML)
@RequestMapping("/vagas")

public class VagaController {
    
    private final VagaService vagaService;

    @Autowired 
    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    //Responde POST em /vagas
    //Recebe um JSON no corpo da requisição e transforma automaticamente em um objeto Vaga
    @PostMapping
    public ResponseEntity<Vaga> criar(@RequestBody Vaga vaga) {
        Vaga salva = vagaService.salvarSeNovaVaga(vaga);

        if (salva == null) {
            return ResponseEntity.status(409).build(); //409 = Conflict (já existe essa URL)
        }

        return ResponseEntity.ok(salva);
    }

    //Responde GET em /vagas - Retorna a lista de todas as vagas cadastradas no banco
    @GetMapping
        public ResponseEntity<List<Vaga>> listarTodas() {
            List<Vaga> vagas = vagaService.listarTodas();
            return ResponseEntity.ok(vagas);
        }

    //Responde GET em /vagas/{id} - Busca uma vaga específica pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Vaga> buscarPorId(@PathVariable Long id) {
        return vagaService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<Vaga>> filtar(
        @RequestParam AreaAtuacao area,
        @RequestParam Modalidade modalidade) {
    
        List<Vaga> vagas = vagaService.filtrar(area, modalidade);
        return ResponseEntity.ok(vagas);
            }

    @GetMapping("/validas")
    public ResponseEntity<List<Vaga>> listarVagasValidas() {
        List<Vaga> vagas = vagaService.listarVagasValidas();
        return ResponseEntity.ok(vagas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVaga(@PathVariable Long id) {
        vagaService.deletarVaga(id);
        return ResponseEntity.noContent().build(); //Retorna 204, padrão para DELETE bem-sucedido
    }

    @Autowired 
    private GupyScraperService gupyScraperService; //Adicione essa injeção junto das outras que você já tem

    @GetMapping("/testar-scraper")
    public List<VagaGupyDTO> testarScraper() {
        List<String> links = gupyScraperService.buscarLinksDeVagas("https://wegestagio.gupy.io/");

        List<VagaGupyDTO> vagasDetalhadas = new ArrayList<>();

        for (String link : links) {
            VagaGupyDTO vaga = gupyScraperService.extrairDetalhesDaVaga(link);
            vagasDetalhadas.add(vaga);
        }

        return vagasDetalhadas;
    }
    
}