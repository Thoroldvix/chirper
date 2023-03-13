package com.example.chirper.maper;

import com.example.chirper.dto.FileAttachmentDto;
import com.example.chirper.persistence.entity.FileAttachment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileAttachmentMapper {

    FileAttachmentDto toFileAttachmentDto(FileAttachment attachment);


}
