package com.movieweb.movieweb.modules.genre.init;

import com.movieweb.movieweb.modules.genre.dto.GenreDto;
import com.movieweb.movieweb.modules.genre.service.GenreService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenreInitRunner implements CommandLineRunner {

    private final GenreService genreService;

    public GenreInitRunner(GenreService genreService) {
        this.genreService = genreService;
    }

    List<String> genres = List.of(
            "Bí Ẩn",
            "Chiến Tranh",
            "Chiếu Rạp",
            "Chuyển Thể",
            "Chính Kịch",
            "Chính Luận",
            "Chính Trị",
            "Chương Trình Truyền Hình",
            "Cung Đấu",
            "Cuối Tuần",
            "Cách Mạng",
            "Cổ Trang",
            "Cổ Tích",
            "Cổ Điển",
            "DC",
            "Disney",
            "Gây Cấn",
            "Gia Đình",
            "Giáng Sinh",
            "Giả Tưởng",
            "Hoàng Cung",
            "Hoạt Hình",
            "Hài",
            "Hành Động",
            "Hình Sự",
            "Học Đường",
            "Khoa Học",
            "Kinh Dị",
            "Kinh Điển",
            "Kịch Nói",
            "Kỳ Ảo",
            "LGBT+",
            "Live Action",
            "Lãng Mạn",
            "Lịch Sử",
            "Marvel",
            "Miền Viễn Tây",
            "Nghề Nghiệp",
            "Người Mẫu",
            "Nhạc Kịch",
            "Phiêu Lưu",
            "Phép Thuật",
            "Siêu Anh Hùng",
            "Thiếu Nhi",
            "Thần Thoại",
            "Thể Thao",
            "Truyền Hình Thực Tế",
            "Tuổi Trẻ",
            "Tài Liệu",
            "Tâm Lý",
            "Tình Cảm",
            "Tập Luyện",
            "Viễn Tưởng",
            "Võ Thuật",
            "Xuyên Không",
            "Đau Thương",
            "Đời Thường",
            "Ẩm Thực"
    );

    @Override
    public void run(String... args) throws Exception {
//        if (genreService.count() == 0) {
//            List<GenreDto> dtos = genres.stream()
//                    .map(name -> {
//                        GenreDto dto = new GenreDto();
//                        dto.setName(name);
//                        return dto;
//                    })
//                    .toList();
//
//            genreService.createAll(dtos);
//        }
    }
}
