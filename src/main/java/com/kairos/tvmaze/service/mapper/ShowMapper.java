package com.kairos.tvmaze.service.mapper;

import com.kairos.tvmaze.dto.response.ShowDetailResponse;
import com.kairos.tvmaze.dto.response.ShowSearchResponse;
import com.kairos.tvmaze.model.domain.TvMazeNetwork;
import com.kairos.tvmaze.model.domain.TvMazeShow;
import com.kairos.tvmaze.model.domain.TvMazeWebChannel;
import com.kairos.tvmaze.model.entity.ShowCache;

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

    /**
     * Convierte un TvMazeShow (respuesta de la API externa) a un
     * ShowDetailResponse (DTO para el endpoint de detalle).
     *
     * @param show modelo de dominio proveniente de TV Maze.
     * @return DTO de detalle.
     */
    public ShowDetailResponse toDetailResponse(TvMazeShow show) {
        if (show == null) {
            return null;
        }

        return ShowDetailResponse.builder()
                .id(show.getId())
                .name(show.getName())
                .url(show.getUrl())
                .summary(show.getSummary())
                .language(show.getLanguage())
                .genres(show.getGenres())
                .status(show.getStatus())
                .runtime(show.getRuntime())
                .premiered(show.getPremiered())
                .imageUrl(resolveImageUrl(show))
                .channelName(resolveChannelName(show.getNetwork(), show.getWebChannel()))
                .channelCountry(resolveChannelCountry(show.getNetwork(), show.getWebChannel()))
                .build();
    }

    /**
     * Resuelve el país del canal priorizando network sobre webChannel.
     *
     * @param network red de televisión tradicional.
     * @param webChannel canal web/plataforma.
     * @return nombre del país o null.
     */
    private String resolveChannelCountry(TvMazeNetwork network, TvMazeWebChannel webChannel) {
        if (network != null && network.getCountry() != null) {
            return network.getCountry().getName();
        }
        if (webChannel != null && webChannel.getCountry() != null) {
            return webChannel.getCountry().getName();
        }
        return null;
    }

    /**
     * Extrae la URL de la imagen original del show.
     * Si no hay imagen original, retorna la imagen media.
     *
     * @param show modelo de dominio con las imágenes.
     * @return URL de la imagen o null.
     */
    private String resolveImageUrl(TvMazeShow show) {
        if (show.getImage() == null) {
            return null;
        }
        if (show.getImage().getOriginal() != null) {
            return show.getImage().getOriginal().getUrl();
        }
        if (show.getImage().getMedium() != null) {
            return show.getImage().getMedium().getUrl();
        }
        return null;
    }

    /**
     * Convierte un TvMazeShow a una entidad ShowCache para persistirla en MongoDB.
     */
    public ShowCache toCacheEntity(TvMazeShow show) {
        if (show == null) return null;
        return ShowCache.builder()
                .showId(show.getId())
                .name(show.getName())
                .url(show.getUrl())
                .summary(show.getSummary())
                .language(show.getLanguage())
                .genres(show.getGenres())
                .status(show.getStatus())
                .runtime(show.getRuntime())
                .premiered(show.getPremiered())
                .imageUrl(resolveImageUrl(show))
                .channelName(resolveChannelName(show.getNetwork(), show.getWebChannel()))
                .channelCountry(resolveChannelCountry(show.getNetwork(), show.getWebChannel()))
                .cachedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Convierte una entidad ShowCache (de MongoDB) a un ShowDetailResponse (DTO de salida).
     */
    public ShowDetailResponse fromCacheToDetailResponse(ShowCache cache) {
        if (cache == null) return null;
        return ShowDetailResponse.builder()
                .id(cache.getShowId())
                .name(cache.getName())
                .url(cache.getUrl())
                .summary(cache.getSummary())
                .language(cache.getLanguage())
                .genres(cache.getGenres())
                .status(cache.getStatus())
                .runtime(cache.getRuntime())
                .premiered(cache.getPremiered())
                .imageUrl(cache.getImageUrl())
                .channelName(cache.getChannelName())
                .channelCountry(cache.getChannelCountry())
                .build();
    }

}

