package com.movieweb.movieweb.modules.movie.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.movieweb.movieweb.modules.server.entity.Server;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;
import lombok.*;

@Entity
@IdClass(StreamingSourceId.class)
@Table(name = "streaming_source")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreamingSource {

    @Id
    @Column(name = "movie_id")
    private String movieId;

    @Id
    @Column(name = "version_id")
    private String versionId;

    @Id
    @Column(name = "episode_name")
    private String episodeName;

    @Id
    @Column(name = "server_id")
    private String serverId;

    @Column(name = "order_index")
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "movie_id", referencedColumnName = "movie_id", insertable = false, updatable = false),
            @JoinColumn(name = "version_id", referencedColumnName = "version_id", insertable = false, updatable = false),
            @JoinColumn(name = "episode_name", referencedColumnName = "episode_name", insertable = false, updatable = false)
    })
    @JsonIgnore
    private Episode episode;

    @ManyToOne
    @JoinColumn(name = "server_id",insertable = false, updatable = false)
    private Server server;

    @Column(columnDefinition = "TEXT")
    private String url;

}

