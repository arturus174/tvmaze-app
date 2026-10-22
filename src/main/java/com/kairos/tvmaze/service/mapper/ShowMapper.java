package com.kairos.tvmaze.service.mapper;

import com.kairos.tvmaze.dto.response.ShowSearchResponse;
import com.kairos.tvmaze.model.domain.TvMazeNetwork;
import com.kairos.tvmaze.model.domain.TvMazeShow;
import com.kairos.tvmaze.model.domain.TvMazeWebChannel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Componente mapper encargado de convertir entre los modelos de dominio
 * (TvMazeShow) y los DTOs de respuesta (ShowSearchResponse).
 *
 */
@Component
public class ShowMapper {

    /**
     * Convierte un TvMazeShow (respuesta de la API externa) a un
     * ShowSearchResponse.
     */
    public ShowSearchResponse toSearchResponse(TvMazeShow show) {
        if (show == null) {
            return null;
        }

        return ShowSearchResponse.builder()
                .id(show.getId())
                .name(show.getName())
                .channel(resolveChannelName(show.getNetwork(), show.getWebChannel()))
                .summary(show.getSummary())
                .genres(show.getGenres())
                .build();
    }

    /**
     * Resuelve el nombre del canal priorizando network sobre webChannel.
     * Si ambos son null, retorna null.
     *
     * @param network red de televisión tradicional.
     * @param webChannel canal web/plataforma de streaming.
     * @return nombre del canal o null.
     */
    private String resolveChannelName(TvMazeNetwork network, TvMazeWebChannel webChannel) {
        if (network != null && network.getName() != null) {
            return network.getName();
        }
        if (webChannel != null && webChannel.getName() != null) {
            return webChannel.getName();
        }
        return null;
    }

}
