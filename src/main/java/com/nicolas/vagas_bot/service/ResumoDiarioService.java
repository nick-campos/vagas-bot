package com.nicolas.vagas_bot.service;

import com.nicolas.vagas_bot.model.Vaga;
import com.nicolas.vagas_bot.repository.VagaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumoDiarioService {

    // O Spring entrega as duas dependências sozinho (um único construtor não precisa de @Autowired)
    private final VagaRepository vagaRepository;
    private final TelegramService telegramService;

    public ResumoDiarioService(VagaRepository vagaRepository, TelegramService telegramService) {
        this.vagaRepository = vagaRepository;
        this.telegramService = telegramService;
    }

    public void enviarResumoDiario() {
        // Busca as vagas com vagaRepository.findByEnviadaFalse()
        List<Vaga> vagasParaEnviar = vagaRepository.findByEnviadaFalse();

        // Se a lista estiver vazia, não faz nada e sai do método
        if (vagasParaEnviar.isEmpty()) {
            return;
        }

        // Divide a lista em blocos de no máximo 10 vagas
        int size = vagasParaEnviar.size();
        int numberOfBatches = (size + 9) / 10;

        for (int i = 0; i < numberOfBatches; i++) {
            int fromIndex = i * 10;
            int toIndex = Math.min(fromIndex + 10, size);

            List<Vaga> batch = vagasParaEnviar.subList(fromIndex, toIndex);

            // Monta uma mensagem de texto com o título "Novas vagas:" e, para cada vaga, as linhas: titulo - empresa, modalidade - localizacao, e a url
            StringBuilder texto = new StringBuilder();
            texto.append("Novas vagas:\n");
            for (Vaga vaga : batch) {
                texto.append(vaga.getTitulo()).append(" - ").append(vaga.getEmpresa()).append("\n");
                texto.append(vaga.getModalidade()).append(" - ").append(vaga.getLocalizacao()).append("\n");
                texto.append(vaga.getUrl()).append("\n\n");
            }

            // Envia a mensagem com telegramService.enviarMensagem(texto)
            telegramService.enviarMensagem(texto.toString());

            // Marca cada vaga do bloco com setEnviada(true) e salva com vagaRepository.saveAll(bloco)
            for (Vaga vaga : batch) {
                vaga.setEnviada(true);
            }
            vagaRepository.saveAll(batch);
        }
    }
}

