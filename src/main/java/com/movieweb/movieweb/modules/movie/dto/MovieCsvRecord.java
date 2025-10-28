package com.movieweb.movieweb.modules.movie.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  MovieCsvRecord{
        @CsvBindByName(column = "title")
        private String title;

        @CsvBindByName(column = "originalTitle")
        private String originalTitle;

        @CsvBindByName(column = "posterPath")
        private String posterPath;

        @CsvBindByName(column = "backdropPath")
        private String backdropPath;

        @CsvBindByName(column = "mediaType")
        private String mediaType;

        @CsvBindByName(column = "status")
        private String status;

        @CsvBindByName(column = "runtime")
        private String runtime;

        @CsvBindByName(column = "numberOfEpisodes")
        private String numberOfEpisodes;

        @CsvBindByName(column = "releaseYear")
        private String releaseYear;

        @CsvBindByName(column = "trailerPath")
        private String trailerPath;

        @CsvBindByName(column = "overview")
        private String overview;

        @CsvBindByName(column = "actors")
        private String actors;

        @CsvBindByName(column = "directors")
        private String directors;

        @CsvBindByName(column = "countries")
        private String countries;

        @CsvBindByName(column = "genres")
        private String genres;
}