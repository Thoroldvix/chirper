package com.example.chirper.maper;

import com.example.chirper.dto.PostDto;
import com.example.chirper.persistence.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, FileAttachmentMapper.class})
public interface PostMapper {
    @Mapping(source = "createdAt", target = "timestamp", qualifiedByName = "toMillis")
    PostDto toPostDto(Post post);

    List<PostDto> toPostDtoList(List<Post> posts);

    @Named("toMillis")
    static long toMillis(LocalDateTime createdAt) {
        if (createdAt == null) {
            return 0;
        }
        ZonedDateTime zdt = ZonedDateTime.of(createdAt, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }
}
