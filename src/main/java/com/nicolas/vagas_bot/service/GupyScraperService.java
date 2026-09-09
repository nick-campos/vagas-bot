package com.nicolas.vagas_bot.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GupyScraperService {

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
}
