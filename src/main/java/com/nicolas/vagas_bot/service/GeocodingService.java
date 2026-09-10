package com.nicolas.vagas_bot.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final JsonMapper mapper = new JsonMapper();

    //Recebe o nome de uma cidade (ex: "São Paulo - SP") e devolve as coordenadas, ou null se não conseguir encontrar
    public double[] buscarCoordenadas(String nomeCidade) {
        try {
            String cidadeCodificada = URLEncoder.encode(nomeCidade + ", Brasil", StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + cidadeCodificada + "&format=json&limit=1";

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                //Nominatim exige um User-Agent identificando a aplicação, senão bloqueia a requisição
                .header("User-Agent", "vagas-bot-nicolas/1.0")
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode resultados = mapper.readTree(response.body());

            if(resultados.isArray() && resultados.size() > 0) {
                JsonNode primeiroResultado = resultados.get(0);
                double latitude = primeiroResultado.get("lat").asDouble();
                double longitude = primeiroResultado.get("lon").asDouble();
                return new double[] { latitude, longitude };
            }
        } catch (Exception e) {
            System.out.println("Erro ao geocodificar a cidade " + nomeCidade + ":" + e.getMessage());
        }

        return null; //Não encontrou coordenadas
    }
}