package com.example.chirper.repository;

import com.example.chirper.TestUtils;
import com.example.chirper.persistence.entity.FileAttachment;
import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.repository.FileAttachmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class FileAttachmentRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;

    @AfterEach
    public void cleanup() {
        testEntityManager.clear();
        fileAttachmentRepository.deleteAll();
    }

    @Test
    public void findByDateBeforeAndPostIsNull_shouldWork() {
        Post post1 = testEntityManager.persist(TestUtils.createValidPost());

        testEntityManager.persist(getOldFileAttachmentWithPost(post1));
        testEntityManager.persist(getOneHourOldFileAttachment());
        testEntityManager.persist(getFileAttachmentWithin1Hour());

        LocalDateTime oneHourAgo = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        List<FileAttachment> attachments = fileAttachmentRepository.findByDateBeforeAndPostIsNull(oneHourAgo);
        assertThat(attachments.size()).isEqualTo(1);
    }


    private FileAttachment getOneHourOldFileAttachment() {
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setDate(LocalDateTime.now().minus(1, ChronoUnit.HOURS));
        return fileAttachment;
    }
    private FileAttachment getFileAttachmentWithin1Hour() {
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setDate(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));
        return fileAttachment;
    }
    private FileAttachment getOldFileAttachmentWithPost(Post post) {
        FileAttachment fileAttachment = getOneHourOldFileAttachment();
        fileAttachment.setPost(post);
        return fileAttachment;
    }
}