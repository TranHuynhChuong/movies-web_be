package com.movieweb.movieweb.modules.movie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreamingSourceId implements Serializable {
    @Column(name = "movie_id")
    private String movieId;

    @Column(name = "version_id")
    private String versionId;

    @Column(name = "episode_number")
    private Integer episodeNumber;

    @Column(name = "server_id")
    private String serverId;


}
