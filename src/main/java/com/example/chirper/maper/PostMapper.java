package com.example.chirper.maper;

import com.example.chirper.dto.PostDto;
import com.example.chirper.dto.UserDto;
import com.example.chirper.persistence.entity.Post;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class PostMapper {
    protected UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public PostDto toPostDto(Post post) {
        if (post == null) {
            return null;
        }
        Long id = post.getId();
        String content = post.getContent();
        UserDto user = userMapper.toUserDto(post.getUser());
        Long timesTamp = toMillis(post.getCreatedAt());


        return new PostDto(id, content, timesTamp, user);
    }

    public abstract List<PostDto> toPostDtoList(List<Post> posts);

    private long toMillis(LocalDateTime createdAt) {
        if (createdAt == null) {
            return 0;
        }
        ZonedDateTime zdt = ZonedDateTime.of(createdAt, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

}
