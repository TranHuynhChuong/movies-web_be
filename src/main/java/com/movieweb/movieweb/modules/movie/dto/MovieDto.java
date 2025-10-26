package com.movieweb.movieweb.modules.movie.dto;

import com.movieweb.movieweb.modules.country.entity.Country;
import com.movieweb.movieweb.modules.genre.entity.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {

    @NotBlank(message = "Tiêu đề phim là bắt buộc")
    private String title;

    @NotBlank(message = "Tên gốc của phim là bắt buộc")
    private String originalTitle;

    @NotBlank(message = "Đường dẫn poster là bắt buộc")
    private String posterPath;

    @NotBlank(message = "Đường dẫn backdrop là bắt buộc")
    private String backdropPath;

    @NotBlank(message = "Loại phim (movie hoặc series) là bắt buộc")
    private String mediaType;

    @NotBlank(message = "Trạng thái phim là bắt buộc")
    private String status;

    @NotNull(message = "Thời lượng phim là bắt buộc")
    private Integer runtime;

    @NotNull(message = "Số tập phim là bắt buộc (ít nhất 1)")
    private Integer numberOfEpisodes;

    @NotNull(message = "Năm phát hành là bắt buộc")
    private Integer releaseYear;

    @NotBlank(message = "Đường dẫn trailer là bắt buộc")
    private String trailerPath;

    @NotEmpty(message = "Phim phải có ít nhất một thể loại")
    private List<Genre> genres;

    @NotBlank(message = "Mô tả phim là bắt buộc")
    private String overview;

    @NotBlank(message = "Danh sách diễn viên là bắt buộc")
    private String actors;

    @NotBlank(message = "Đạo diễn là bắt buộc")
    private String directors;

    @NotBlank(message = "Quốc gia sản xuất là bắt buộc")
    private Country country;
    private List<VersionDto> versions; // Danh sách phiên bản (thuyết minh, vietsub, v.v.)

    // ------------------- INNER CLASSES -------------------
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VersionDto {
        @NotBlank(message = "Mã phiên bản không được để trống")
        private String id;
        private List<EpisodeDto> episodes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EpisodeDto {
        @NotNull(message = "Số tập không được để trống")
        private Integer episodeNumber;
        private List<StreamingSourceDto> streamingSources;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StreamingSourceDto {
        @NotBlank(message = "ID máy chủ không được để trống")
        private String serverId;
        private Integer orderIndex;
        @NotBlank(message = "url streaming không được để trống")
        private String url;
    }
}