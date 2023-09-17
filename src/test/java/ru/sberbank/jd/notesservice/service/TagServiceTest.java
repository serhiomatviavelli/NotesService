package ru.sberbank.jd.notesservice.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.sberbank.jd.notesservice.dao.entity.Tag;
import ru.sberbank.jd.notesservice.dao.repository.TagRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TagServiceTest {

    @Autowired
    private TagService tagService;

    @MockBean
    private TagRepository tagRepository;

    @Test
    void getTagById() {
        String id = "9da1f8e0-aecc-9d86-8bad-115129706a77";
        UUID id1 = UUID.fromString("9da1f8e0-aecc-9d86-8bad-115129706a77");
        Tag tag = tagService.getTagById(id);
        Mockito.verify(tagRepository,Mockito.times(1)).findById(id1);
        assertNull(tag);
    }


    @Test
    void getTagByValue() throws NoSuchAlgorithmException {
        String tagValue = "tag1";
        Tag tag = tagService.getTagByValue(tagValue);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        byte[] bytes = tagValue.toLowerCase().getBytes(StandardCharsets.UTF_8);
        UUID id = UUID.nameUUIDFromBytes(bytes);
        Mockito.verify(tagRepository,Mockito.times(1)).findById(id);
        assertNull(tag);
    }

    @Test
    void addTag() throws NoSuchAlgorithmException {
        String tagValue = "tag2";
        Tag tag = tagService.addTag(tagValue);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        byte[] bytes = tagValue.toLowerCase().getBytes(StandardCharsets.UTF_8);
        UUID id = UUID.nameUUIDFromBytes(bytes);
        assertEquals(id, tag.getId());
        assertEquals(tagValue, tag.getValue());
        Mockito.verify(tagRepository,Mockito.times(1)).save(tag);
        Mockito.verify(tagRepository,Mockito.times(1)).flush();
    }
}