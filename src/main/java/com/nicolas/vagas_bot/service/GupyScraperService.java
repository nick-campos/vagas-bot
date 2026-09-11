package com.nicolas.vagas_bot.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.nicolas.vagas_bot.model.AreaAtuacao;
import com.nicolas.vagas_bot.model.Modalidade;
import com.nicolas.vagas_bot.model.Nivel;
import com.nicolas.vagas_bot.model.Vaga;

@Service
public class GupyScraperService {

    private final VagaService vagaService;

    @Autowired
    public GupyScraperService(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    //1: acessa a página de lista e retorna só os links de cada vaga
    public List<String> buscarLinksDeVagas(String urlEmpresa) {
        List<String> links = new ArrayList<>();

        try {
            //O Jsoup baixa o HTML da página como se fosse um navegador
            Document pagina = Jsoup.connect(urlEmpresa)
                .userAgent("Mozzila/5.0") //Identifica como navegador comum, evita bloqueio simples
                .get();

            //Por enquanto, vai só extrair os títulos das vagas para testar a conexão
            Elements linksVagas = pagina.select("a[href*=/jobs/]");

            for(Element linkVaga : linksVagas) {
                links.add(linkVaga.absUrl("href"));
            }
        } catch (IOException e) {
            System.out.println("Erro ao acessar a página do Gupy: " + e.getMessage());
        }

        return links;
    }

    //2: acessa a página de UMA vaga específica e extrai os dados organizados
    public VagaGupyDTO extrairDetalhesDaVaga(String urlVaga) {
        VagaGupyDTO vaga = new VagaGupyDTO();
        vaga.setLink(urlVaga);

        try {
            Document pagina = Jsoup.connect(urlVaga)
                .userAgent("Mozilla/5.0")
                .get();

            //O título da vaga fica no <h1 id="h1">
            Element titulo = pagina.selectFirst("h1#h1");
            if (titulo != null) {
                vaga.setTitulo(titulo.text());
            }

            //Local, tipo e modalidade vêm em <span class="bzQMFp"> no formato "Rótulo: Valor"
            Elements infos = pagina.select("p.guFMyc span.bzQMFp");

            for (Element info : infos) {
                String textoCompleto = info.text(); //ex: "Local de trabalho: São Paulo - SP"

                if (textoCompleto.contains(":")) {
                    String[] partes = textoCompleto.split(":", 2);
                    String rotulo = partes[0].trim();
                    String valor = partes[1].trim();

                    switch ( (rotulo)) {
                        case "Local de trabalho" -> vaga.setLocalizacao(valor);
                        case "Tipo de vaga" -> vaga.setTipo(valor);
                        case "Modelo de trabalho" -> vaga.setModalidade(valor);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao acessar a vaga " + urlVaga + ":" + e.getMessage());
        }

        return vaga;
    }

    //Converte um VagaGupyDTO em Vaga e tenta salvar — retorna null se for duplicata ou fora do raio
    private Vaga converterESalvar(VagaGupyDTO dto) {
        Vaga vaga = new Vaga();

        //Campos diretos
        vaga.setUrl(dto.getLink());
        vaga.setEmpresa(extrairEmpresaDaUrl(dto.getLink()));
        vaga.setTitulo(dto.getTitulo());
        vaga.setLocalizacao(dto.getLocalizacao());
        vaga.setDataEncontrada(LocalDateTime.now());
        vaga.setNivel(Nivel.ESTAGIO);

        //Mapeia modalidade do texto da Gupy para o enum
        if (dto.getModalidade() != null) {
            String modalidadeLower = dto.getModalidade().toLowerCase();
            if (modalidadeLower.contains("remoto")) {
                vaga.setModalidade(Modalidade.REMOTO);
            } else if (modalidadeLower.contains("híbrido") || modalidadeLower.contains("hibrido")) {
                vaga.setModalidade((Modalidade.HIBRIDO));
            } else {
                vaga.setModalidade(Modalidade.PRESENCIAL);
            }
        }

        //Classifica a área de atuação pelo título da vaga
        if (dto.getTitulo() != null) {
            String tituloLower = dto.getTitulo().toLowerCase();
            if (tituloLower.contains("backend") || tituloLower.contains("back-end")) {
                vaga.setAreaAtuacao(AreaAtuacao.BACKEND);
            } else if (tituloLower.contains("frontend") || tituloLower.contains("front-end")) {
                vaga.setAreaAtuacao(AreaAtuacao.FRONTEND);
            } else if (tituloLower.contains("fullstack") || tituloLower.contains("full stack")) {
                vaga.setAreaAtuacao((AreaAtuacao.FULLSTACK));
            } else {
                vaga.setAreaAtuacao(AreaAtuacao.TI); //Categoria coringa
            }
        }

        //Tenta salvar, o VagaService já filtra duplicatas e raio
        return vagaService.salvarSeNovaVaga(vaga);
    }

    //Extrai o nome da empresa a partir do subdomínio da URL da Gupy
    private String extrairEmpresaDaUrl(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String host = uri.getHost(); //ex: "empresaestagio.gupy.io"
            String subdominio = host.split("\\.")[0]; //ex: "empresaestagio"

            //Deixa com a primeira letra maiúscula (capitalização simples)
            return subdominio.substring(0, 1).toUpperCase() + subdominio.substring(1);
        } catch (Exception e) {
            return "Empresa não identificada"; //fallback, nunca deixa null
        }
    }

    //Executa o scraping completo de uma empresa e salva as vagas válidas
    public List<Vaga> executarScraping(String urlEmpresa) {
        List<Vaga> vagasSalvas = new ArrayList<>();

        //Busca todos os links de vagas da página da empresa
        List<String> links = buscarLinksDeVagas(urlEmpresa);
        System.out.println("Links encontrados: " + links.size());

        //Para cada link, extrai os detalhes e tenta salvar
        for (String link : links) {
            VagaGupyDTO dto = extrairDetalhesDaVaga(link);
            Vaga salva = converterESalvar(dto);

            if (salva != null) {
                vagasSalvas.add(salva);
                System.out.println("Vaga salva: " + salva.getTitulo());
            } else {
                System.out.println("Vaga ignorada (duplicata ou fora do raio): " + dto.getTitulo());
            }
        }

        System.out.println("Total salvo: " + vagasSalvas.size() + " de " + links.size());
        return vagasSalvas;
    }
}
