package com.movieweb.movieweb.modules.movie.service;


import com.movieweb.movieweb.common.exception.BadRequestException;
import com.movieweb.movieweb.common.exception.NotFoundException;
import com.movieweb.movieweb.common.utils.Generator;
import com.movieweb.movieweb.modules.country.entity.Country;
import com.movieweb.movieweb.modules.country.service.CountryService;
import com.movieweb.movieweb.modules.genre.entity.Genre;
import com.movieweb.movieweb.modules.genre.service.GenreService;
import com.movieweb.movieweb.modules.movie.dto.MovieCsvRecord;
import com.movieweb.movieweb.modules.movie.dto.MovieDto;
import com.movieweb.movieweb.modules.movie.entity.*;
import com.movieweb.movieweb.modules.movie.repository.EpisodeRepository;
import com.movieweb.movieweb.modules.movie.repository.MovieRepository;
import com.movieweb.movieweb.modules.movie.repository.StreamingSourceRepository;
import com.movieweb.movieweb.modules.server.service.ServerService;
import com.movieweb.movieweb.modules.version.repository.VersionRepository;
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

import static com.movieweb.movieweb.common.utils.CsvFileParser.parseCsvFile;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    private final CountryService countryService;
    private final GenreService genreService;
    private final ServerService serverService;
    private final VersionService versionService;
    @Transactional
    @CacheEvict(value = {"moviesSearch"}, allEntries = true)
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
                            .episodeNumber(episodeDto.getEpisodeNumber())
                            .streamingSources(new ArrayList<>())
                            .build();
                    movie.getEpisodes().add(episode);

                    if (episodeDto.getStreamingSources() != null) {
                        for (MovieDto.StreamingSourceDto ssDto : episodeDto.getStreamingSources()) {
                            StreamingSource ss = StreamingSource.builder()
                                    .server(serverService.getById(ssDto.getServerId()))
                                    .movieId(movie.getId())
                                    .versionId(version.getId())
                                    .episodeNumber(episodeDto.getEpisodeNumber())
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
        return movie;
    }

    @Transactional
    @CacheEvict(value = {"moviesById", "moviesSearch"}, allEntries = true, key = "#id")
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
                                                    .episodeNumber(episodeDto.getEpisodeNumber())
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
                                                                    .episodeNumber(episode.getEpisodeNumber())
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

        return movieRepository.save(movie);
    }

    @Transactional
    @CacheEvict(value = {"moviesById", "moviesSearch"}, allEntries = true, key = "#id")
    public void deleteMovie(String id) {
        movieRepository.deleteById(id);
    }

    @Cacheable(value = "moviesById", key = "#id")
    public Map<String, Object> getById(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie not found"));

        return this.formatMovie(movie, true);
    }
    @Cacheable(value = "moviesSearch", key = "(#sortBy != null ? #sortBy : 'defaultSort') + '_' + (#mediaType != null ? #mediaType : 'all')")
    public Map<String, Object> searchMovies(String genreId,
                                         String countryId,
                                         String mediaType,
                                         String title,
                                         Integer releaseYear,
                                         String sortBy,
                                         String sortOrder,
                                         String status,
                                         int page,
                                         int limit) {

        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "updatedAt");
        PageRequest pageable = PageRequest.of(page - 1, limit, sort);

        Specification<Movie> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.distinct(true);
            root.fetch("episodes", JoinType.LEFT);

            if(!status.equals("all")){
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

        if (movie.getEpisodes() != null && !movie.getEpisodes().isEmpty()) {
            Map<String, Map<String, Object>> versionMap = new LinkedHashMap<>();

            for (var ep : movie.getEpisodes()) {
                var version = ep.getVersion();
                if (version == null) continue;


                Map<String, Object> v = versionMap.computeIfAbsent(version.getId(), id -> {
                    Map<String, Object> verMap = new LinkedHashMap<>();
                    verMap.put("id", version.getId());
                    verMap.put("name", version.getName());
                    verMap.put("currentEp", 0); // khởi tạo đếm tập
                    if (includeEp) {
                        verMap.put("episodes", new ArrayList<Map<String, Object>>());
                    }
                    return verMap;
                });

                // Mỗi lần gặp 1 episode thuộc version này → tăng currentEp lên 1
                int currentEp = (int) v.get("currentEp");
                v.put("currentEp", currentEp + 1);


                if(includeEp){
                    // Tạo map cho từng episode
                    Map<String, Object> epMap = new LinkedHashMap<>();
                    epMap.put("episodeNumber", ep.getEpisodeNumber());

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
            }

            if (!versionMap.isEmpty()) {
                movieMap.put("versions", new ArrayList<>(versionMap.values()));
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
    @CacheEvict(value = {"moviesSearch"}, allEntries = true)
    public void importMoviesFromCsv(MultipartFile file) {
        List<MovieCsvRecord> records = parseCsvFile(file, record -> {
            String title = record.get("title");
            String originalTitle = record.get("originalTitle");
            String posterPath = record.get("posterPath");
            String backdropPath = record.get("backdropPath");
            String mediaType = record.get("mediaType");
            String status = record.get("status");
            String runtime = record.get("runtime");
            String numberOfEpisodes = record.get("numberOfEpisodes");
            String releaseYear = record.get("releaseYear");
            String trailerPath = record.get("trailerPath");
            String overview = record.get("overview");
            String actors = record.get("actors");
            String directors = record.get("directors");
            String countries = record.get("countries");
            String genres = record.get("genres");

            return new MovieCsvRecord(
                    title, originalTitle, posterPath, backdropPath, mediaType, status,
                    runtime, numberOfEpisodes, releaseYear, trailerPath, overview, actors, directors,
                    countries, genres
            );
        });

        List<Country> countriesList = countryService.getAll();
        List<Genre> genresList = genreService.getAll();

        List<Movie> moviesToSave = records.stream().map(r -> {
            List<Country> countries = Arrays.stream(r.countries().split(","))
                    .map(String::trim)
                    .map(name -> countriesList.stream()
                            .filter(c -> Generator.generateSlug(c.getName()).equals(Generator.generateSlug(name)))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
            List<Genre> genres = Arrays.stream(r.genres().split(","))
                    .map(String::trim)
                    .map(name -> genresList.stream()
                            .filter(g -> Generator.generateSlug(g.getName()).equals(Generator.generateSlug(name)))
                            .findFirst()
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            return  Movie.builder()
                    .id(Generator.generateId())
                    .title(r.title())
                    .originalTitle(r.originalTitle())
                    .posterPath(r.posterPath())
                    .backdropPath(r.backdropPath())
                    .mediaType(r.mediaType())
                    .status(r.status())
                    .runtime(parseInteger(r.runtime()))
                    .numberOfEpisodes(parseInteger(r.numberOfEpisodes()))
                    .releaseYear(parseInteger(r.releaseYear()))
                    .trailerPath(r.trailerPath())
                    .overview(r.overview())
                    .actors(r.actors())
                    .directors(r.directors())
                    .countries(countries)
                    .genres(genres)
                    .episodes(new ArrayList<>())
                    .build();

        }).toList();
        movieRepository.saveAll(moviesToSave);
    }

}

