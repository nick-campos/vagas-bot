package com.nicolas.vagas_bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class TelegramService {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    public void enviarMensagem(String texto) {
        try {
            String textoCodificado = URLEncoder.encode(texto, StandardCharsets.UTF_8);
            String url = "https://api.telegram.org/bot" + botToken
                + "/sendMessage?chat_id=" + chatId
                + "&text=" + textoCodificado;

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Telegram respondeu: " + response.body());
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem pelo Telegram: " + e.getMessage());
        }
    }
}
