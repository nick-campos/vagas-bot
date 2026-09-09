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

    //Método que busca vagas de uma página pública de carreiras do Gupy
    //Recebe a URL da página da empresa (ex: https://empresa.gupy.io/)
    public List<String> buscarVagas(String urlEmpresa) {
        List<String> titulosEncontrados = new ArrayList<>();

        try {
            //O Jsoup baixa o HTML da página como se fosse um navegador
            Document pagina = Jsoup.connect(urlEmpresa)
                .userAgent("Mozzila/5.0") //Identifica como navegador comum, evita bloqueio simples
                .get();

            //Por enquanto, vai só extrair os títulos das vagas para testar a conexão
            Elements vagas = pagina.select("a[href*=/jobs/]");

            for(Element vaga : vagas) {
                titulosEncontrados.add(vaga.text());
            }
        } catch (IOException e) {
            System.out.println("Erro ao acessar a página do Gupy: " + e.getMessage());
        }

        return titulosEncontrados;
    }
}
