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

    //Calcula a distância em km entre dois pontos geográficos usando a fórmula de Haversine
    public double calcularDistanciaKM(double lat1, double lon1, double lat2, double lon2) {
        final double RAIO_TERRA_KM = 6371.0;

        //Converte a diferença de graus para radianos (Math.sin/cos trabalha em radianos)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        //Aplica a fórmula de Haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        //Converte o resultado para km
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA_KM * c;
    }

    //Coordenadas fixas do centro de São Paulo (capital)
    private static final double SP_LAT = -23.5505;
    private static final double SP_LON = -46.6333;
    private static final double RAIO_MAXIMO_KM = 45.0;

    //Verifica se uma cidade está dentro do raio de 45km de São Paulo
    public boolean estaNoRaio(String nomeCidade) {

    //Busca as coordenadas da cidade informada
    double[] coordenadas = buscarCoordenadas(nomeCidade);

        //Se não encontrou coordenadas, rejeita a vaga por segurança
        if (coordenadas == null) {
            System.out.println("Cidade não encontrada, rejeitando: " + nomeCidade);
            return false;
        }

        //Calcula a distância entre a cidade e o centro de SP
        double distancia = calcularDistanciaKM(SP_LAT, SP_LON, coordenadas[0], coordenadas[1]);

        System.out.println("Distância de " + nomeCidade + " até SP: " + distancia + " km");

        //Retorna true só se estiver dentro do raio permitido
        return distancia <= RAIO_MAXIMO_KM; 
    }
}

//A Haversine leva em conta a curvatura da Terra, ela pega a diferença de lat/lon entre dois pontos, 
//converte para radianos, e aplica trigonometria esférica para retornar a distância real em km.