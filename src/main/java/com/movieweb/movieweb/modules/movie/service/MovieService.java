package com.movieweb.movieweb.modules.movie.service;


import com.movieweb.movieweb.common.exception.BadRequestException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.common.utils.CsvFileParser;
import com.movieweb.movieweb.common.utils.Generator;
import com.movieweb.movieweb.modules.country.entity.Country;
import com.movieweb.movieweb.modules.country.service.CountryService;
import com.movieweb.movieweb.modules.genre.entity.Genre;
import com.movieweb.movieweb.modules.genre.service.GenreService;
import com.movieweb.movieweb.modules.movie.dto.MovieCsvRecord;
import com.movieweb.movieweb.modules.movie.dto.MovieDto;
import com.movieweb.movieweb.modules.movie.entity.*;
import com.movieweb.movieweb.modules.movie.repository.MovieRepository;
import com.movieweb.movieweb.modules.server.service.ServerService;
import com.movieweb.movieweb.modules.version.service.VersionService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    private final CountryService countryService;
    private final GenreService genreService;
    private final ServerService serverService;
    private final VersionService versionService;


    public int getMaxEpisodes(Movie movie) {
        if (movie.getEpisodes() == null || movie.getEpisodes().isEmpty()) {
            return 0;
        }

        Map<String, Long> versionCounts = movie.getEpisodes().stream()
                .collect(Collectors.groupingBy(
                        ep -> ep.getVersion().getId(),
                        Collectors.mapping(Episode::getEpisodeName, Collectors.toSet())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (long) e.getValue().size()
                ));

        return versionCounts.values().stream()
                .max(Long::compare)
                .orElse(0L)
                .intValue();
    }

    @Transactional
    @CacheEvict(value = {"moviesList"}, allEntries = true)
    public Movie createMovie(MovieDto dto) {

        List<Country> countries = countryService.getAllByIds(dto.getCountries().stream()
                .map(Country::getId)
                .toList());

        List<Genre> genres = genreService.getAllByIds(dto.getGenres().stream()
                .map(Genre::getId)
                .toList());

        if (genres.size() != dto.getGenres().size()) {
            throw new BadRequestException("Một số thể loại không tồn tại");
        }
        if (countries.size() != dto.getCountries().size()) {
            throw new BadRequestException("Một số quốc gia không tồn tại");
        }

        String id;
        do {
            id = Generator.generateId();
        } while (movieRepository.existsById(id));

        Movie movie = Movie.builder()
                .id(id)
                .title(dto.getTitle())
                .originalTitle(dto.getOriginalTitle())
                .posterPath(dto.getPosterPath())
                .backdropPath(dto.getBackdropPath())
                .mediaType(dto.getMediaType())
                .status(dto.getStatus())
                .runtime(dto.getRuntime())
                .numberOfEpisodes(dto.getNumberOfEpisodes())
                .currentEpisode(0)
                .releaseYear(dto.getReleaseYear())
                .trailerPath(dto.getTrailerPath())
                .overview(dto.getOverview())
                .actors(dto.getActors())
                .directors(dto.getDirectors())
                .countries(countries)
                .genres(genres)
                .episodes(new ArrayList<>())
                .build();
        movieRepository.save(movie);

        if (dto.getVersions() != null) {
            for (MovieDto.VersionDto versionDto : dto.getVersions()) {
                var version = versionService.getById(versionDto.getId());
                if (versionDto.getEpisodes() == null) continue;
                for (MovieDto.EpisodeDto episodeDto : versionDto.getEpisodes()) {
                    Episode episode = Episode.builder()
                            .movie(movie)
                            .version(version)
                            .movieId(movie.getId())
                            .versionId(version.getId())
                            .episodeName(episodeDto.getEpisodeName())
                            .streamingSources(new ArrayList<>())
                            .build();
                    movie.getEpisodes().add(episode);

                    if (episodeDto.getStreamingSources() != null) {
                        for (MovieDto.StreamingSourceDto ssDto : episodeDto.getStreamingSources()) {
                            StreamingSource ss = StreamingSource.builder()
                                    .server(serverService.getById(ssDto.getServerId()))
                                    .movieId(movie.getId())
                                    .versionId(version.getId())
                                    .episodeName(episodeDto.getEpisodeName())
                                    .serverId(ssDto.getServerId())
                                    .orderIndex(ssDto.getOrderIndex())
                                    .url(ssDto.getUrl())
                                    .build();
                            episode.getStreamingSources().add(ss);
                        }

                    }
                }
            }
        }

        movie.setCurrentEpisode(getMaxEpisodes(movie));
        movieRepository.save(movie);

        return movie;
    }

    @Transactional
    @CacheEvict(value = {"moviesById", "moviesList"}, allEntries = true, key = "#id")
    public Movie updateMovie(String id, MovieDto dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie không tồn tại"));

        List<Country> countries = countryService.getAllByIds(dto.getCountries().stream()
                .map(Country::getId)
                .toList());

        List<Genre> genres = genreService.getAllByIds(dto.getGenres().stream()
                .map(Genre::getId)
                .toList());

        if (genres.size() != dto.getGenres().size()) {
            throw new BadRequestException("Một số thể loại không tồn tại");
        }
        if (countries.size() != dto.getCountries().size()) {
            throw new BadRequestException("Một số quốc gia không tồn tại");
        }


        movie.setTitle(dto.getTitle());
        movie.setOriginalTitle(dto.getOriginalTitle());
        movie.setPosterPath(dto.getPosterPath());
        movie.setBackdropPath(dto.getBackdropPath());
        movie.setMediaType(dto.getMediaType());
        movie.setStatus(dto.getStatus());
        movie.setRuntime(dto.getRuntime());
        movie.setNumberOfEpisodes(dto.getNumberOfEpisodes());
        movie.setReleaseYear(dto.getReleaseYear());
        movie.setTrailerPath(dto.getTrailerPath());
        movie.setOverview(dto.getOverview());
        movie.setActors(dto.getActors());
        movie.setDirectors(dto.getDirectors());

        movie.getGenres().clear();
        movie.getCountries().clear();

        movie.setCountries(countries);
        movie.setGenres(genres);

        movie.getEpisodes().clear();

            if (dto.getVersions() != null) {
                for (MovieDto.VersionDto versionDto : dto.getVersions()) {
                    var version = versionService.getById(versionDto.getId());

                    if (versionDto.getEpisodes() == null) continue;

                    for (MovieDto.EpisodeDto episodeDto : versionDto.getEpisodes()) {
                        Episode episode = Episode.builder()
                                                    .movie(movie)
                                                    .movieId(movie.getId())
                                                    .versionId(version.getId())
                                                    .version(version)
                                                    .episodeName(episodeDto.getEpisodeName())
                                                    .streamingSources(new ArrayList<>())
                                                    .build();

                        // Thêm StreamingSource vào episode
                        if (episodeDto.getStreamingSources() != null) {
                            for (MovieDto.StreamingSourceDto ssDto : episodeDto.getStreamingSources()) {
                                StreamingSource ss = StreamingSource.builder()
                                                                    .server(serverService.getById(ssDto.getServerId()))
                                                                    .episode(episode)
                                                                    .versionId(version.getId())
                                                                    .movieId(movie.getId())
                                                                    .episodeName(episode.getEpisodeName())
                                                                    .serverId(ssDto.getServerId())
                                                                    .orderIndex(ssDto.getOrderIndex())
                                                                    .url(ssDto.getUrl())
                                                                    .build();
                                episode.getStreamingSources().add(ss);
                            }
                        }
                        movie.getEpisodes().add(episode);
                    }
                }
            }

        movie.setCurrentEpisode(getMaxEpisodes(movie));
        movieRepository.save(movie);

        return movieRepository.save(movie);
    }

    @Transactional
    @CacheEvict(value = {"moviesById", "moviesList"}, allEntries = true, key = "#id")
    public void deleteMovie(String id) {
        Movie movie = movieRepository.getReferenceById(id);
        movie.removeAllRelations();
        movieRepository.delete(movie);
    }

    @Cacheable(value = "moviesById", key = "#id")
    public Map<String, Object> getById(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie not found"));

        return this.formatMovie(movie, true);
    }
    public Map<String, Object> searchMovies(String genreId,
                                         String countryId,
                                         String mediaType,
                                         String title,
                                         Integer releaseYear,
                                         String status,
                                         int page,
                                         int limit) {

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        PageRequest pageable = PageRequest.of(page - 1, limit, sort);

        Specification<Movie> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.distinct(true);

            if(status != null && !status.equals("all")){
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (genreId != null && !genreId.equals("all")) {
                Join<Movie, Genre> genreJoin = root.join("genres", JoinType.INNER);
                predicates.add(cb.equal(genreJoin.get("id"), genreId));
            }

            if (countryId != null && !countryId.equals("all")) {
                Join<Movie, Country> countryJoin = root.join("countries", JoinType.INNER);
                predicates.add(cb.equal(countryJoin.get("id"), countryId));
            }

            if (mediaType != null && !mediaType.equals("all")) {
                predicates.add(cb.equal(root.get("mediaType"), mediaType));
            }

            if (title != null) {
                String pattern = "%" + title.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("originalTitle")), pattern)
                ));
            }

            if (releaseYear != null) {
                predicates.add(cb.equal(root.get("releaseYear"), releaseYear));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var moviePage = movieRepository.findAll(spec, pageable);

        List<Map<String, Object>> formattedResults = moviePage.getContent().stream()
                .map((Movie movie) -> formatMovie(movie, false))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("page", page);
        result.put("totalPages", moviePage.getTotalPages());
        result.put("totalResults", moviePage.getTotalElements());
        result.put("results", formattedResults);

        return result;
    }

    @Cacheable(value = "moviesList")
    public Map<String, Object> getMovieList(String type, String value, int limit) {
        // Sort mặc định theo updatedAt
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");

        if ("sort".equals(type) && ("createdAt".equals(value) || "updatedAt".equals(value))) {
            sort = Sort.by(Sort.Direction.DESC, value);
        }


        PageRequest pageable = PageRequest.of(0, limit, sort);

        // Specification để filter theo value
        Specification<Movie> spec = (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            if ("mediaType".equals(type) && (value.equals("movie") || value.equals("series"))) {
                predicates.add(cb.equal(root.get("mediaType"), value));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var moviePage = movieRepository.findAll(spec, pageable);

        List<Map<String, Object>> formattedResults = moviePage.getContent().stream()
                .map(movie -> formatMovie(movie, false))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("page", 1);
        result.put("totalPages", moviePage.getTotalPages());
        result.put("totalResults", moviePage.getTotalElements());
        result.put("results", formattedResults);

        return result;
    }


    private Map<String, Object> formatMovie(Movie movie, boolean includeEp) {

        Map<String, Object> movieMap = new LinkedHashMap<>();
        movieMap.put("id", movie.getId());
        movieMap.put("title", movie.getTitle());
        movieMap.put("originalTitle", movie.getOriginalTitle());
        movieMap.put("posterPath", movie.getPosterPath());
        movieMap.put("backdropPath", movie.getBackdropPath());
        movieMap.put("mediaType", movie.getMediaType());
        movieMap.put("status", movie.getStatus());
        movieMap.put("runtime", movie.getRuntime());
        movieMap.put("numberOfEpisodes", movie.getNumberOfEpisodes());
        movieMap.put("releaseYear", movie.getReleaseYear());
        movieMap.put("trailerPath", movie.getTrailerPath());
        movieMap.put("overview", movie.getOverview());
        movieMap.put("actors", movie.getActors());
        movieMap.put("directors", movie.getDirectors());
        movieMap.put("createdAt", movie.getCreatedAt());
        movieMap.put("updatedAt", movie.getUpdatedAt());
        movieMap.put("currentEpisode", movie.getCurrentEpisode());

        // Gọn genres
        if (movie.getGenres() != null) {
            List<Map<String, String>> genreList = movie.getGenres().stream()
                    .map(g -> Map.of(
                            "id", g.getId(),
                            "name", g.getName()
                    ))
                    .toList();
            movieMap.put("genres", genreList);
        }

        // Gọn country
        if (movie.getCountries() != null) {
            List<Map<String, String>> countryList = movie.getCountries().stream()
                    .map(c -> Map.of(
                            "id", c.getId(),
                            "name", c.getName()
                    ))
                    .toList();
            movieMap.put("countries", countryList);
        }
        if (includeEp) {
            if (movie.getEpisodes() != null && !movie.getEpisodes().isEmpty()) {
                Map<String, Map<String, Object>> versionMap = new LinkedHashMap<>();

                for (var ep : movie.getEpisodes()) {
                    var version = ep.getVersion();
                    if (version == null) continue;

                    Map<String, Object> v = versionMap.computeIfAbsent(version.getId(), id -> {
                        Map<String, Object> verMap = new LinkedHashMap<>();
                        verMap.put("id", version.getId());
                        verMap.put("name", version.getName());
                        verMap.put("episodes", new ArrayList<Map<String, Object>>());

                        return verMap;
                    });

                    // Tạo map cho từng episode
                    Map<String, Object> epMap = new LinkedHashMap<>();
                    epMap.put("episodeName", ep.getEpisodeName());

                    // --- Streaming sources ---
                    if (ep.getStreamingSources() != null && !ep.getStreamingSources().isEmpty()) {
                        List<Map<String, Object>> sourceList = ep.getStreamingSources().stream()
                                .map(src -> {
                                    Map<String, Object> s = new LinkedHashMap<>();
                                    s.put("serverId", src.getServer().getId());
                                    s.put("orderIndex", src.getOrderIndex());
                                    s.put("url", src.getUrl());
                                    return s;
                                })
                                .toList();
                        epMap.put("streamingSources", sourceList);
                    }

                    // Thêm episode vào version tương ứng
                    List<Map<String, Object>> episodes = (List<Map<String, Object>>) v.get("episodes");
                    episodes.add(epMap);
                }

                if (!versionMap.isEmpty()) {
                    movieMap.put("versions", new ArrayList<>(versionMap.values()));
                }
            }
        }

        return movieMap;
    }

    private Integer parseInteger(String value) {
        try {
            return (value == null || value.isEmpty()) ? null : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null; // bỏ qua giá trị lỗi thay vì dừng chương trình
        }
    }
    @Transactional
    @CacheEvict(value = {"moviesList"}, allEntries = true)
    public void importMoviesFromCsv(MultipartFile file) {
        List<MovieCsvRecord> records = CsvFileParser.parseCsv(file, MovieCsvRecord.class);

        List<Country> countriesList = countryService.getAll();
        List<Genre> genresList = genreService.getAll();

        List<Movie> moviesToSave = records.stream().map(r -> {
            List<Country> countries = Arrays.stream(r.getCountries().split(","))
                    .map(String::trim)
                    .map(name -> countriesList.stream()
                            .filter(c -> (c.getName()).equals((name)))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
            List<Genre> genres = Arrays.stream(r.getGenres().split(","))
                    .map(String::trim)
                    .map(name -> genresList.stream()
                            .filter(g -> (g.getName()).equals((name)))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            return  Movie.builder()
                    .id(Generator.generateId())
                    .title(r.getTitle())
                    .originalTitle(r.getOriginalTitle())
                    .posterPath(r.getPosterPath())
                    .backdropPath(r.getBackdropPath())
                    .mediaType(r.getMediaType())
                    .status(r.getStatus())
                    .runtime(parseInteger(r.getRuntime()))
                    .numberOfEpisodes(parseInteger(r.getNumberOfEpisodes()))
                    .currentEpisode(0)
                    .releaseYear(parseInteger(r.getReleaseYear()))
                    .trailerPath(r.getTrailerPath())
                    .overview(r.getOverview())
                    .actors(r.getActors())
                    .directors(r.getDirectors())
                    .countries(countries)
                    .genres(genres)
                    .episodes(new ArrayList<>())
                    .build();

        }).toList();
        movieRepository.saveAll(moviesToSave);
    }

}

