package com.movieweb.movieweb.modules.movie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.movieweb.movieweb.modules.version.entity.Version;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@IdClass(EpisodeId.class)
@Table(name = "episodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episode {

    @Id
    @Column(name = "movie_id")
    private String movieId;

    @Id
    @Column(name = "version_id")
    private String versionId;

    @Id
    @Column(name = "episode_name")
    private String episodeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @MapsId("versionId")
    @JoinColumn(name = "version_id")
    private Version version;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StreamingSource> streamingSources;

}

