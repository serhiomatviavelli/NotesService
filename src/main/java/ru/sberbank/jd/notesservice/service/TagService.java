package ru.sberbank.jd.notesservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sberbank.jd.notesservice.dao.entity.Tag;
import ru.sberbank.jd.notesservice.dao.repository.TagRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;


/**
 * Класс, содержащий методы взаимодействия с базой данных для таблицы T_TAG.
 */
@Service
public class TagService {

    @Autowired
    TagRepository tagRepository;


    /**
     * Возвращает объект Tag по ID.
     *
     * @param tagId идентификатор объекта в формате UUID.
     * @return объект Tag.
     */
    public Tag getTagById(UUID tagId) {
        Optional<Tag> tag = tagRepository.findById(tagId);
        if (tag.isPresent()) {
            return tag.get();
        } else {
            return null;
        }
    }


    /**
     * Возвращает объект Tag по ID.
     *
     * @param tagId идентификатор объекта в формате String.
     * @return объект Tag.
     */
    public Tag getTagById(String tagId) {
        try {
            UUID uuid = UUID.fromString(tagId);
            return getTagById(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    /**
     * Ищет метку по ее значению.
     *
     * @param tagValue значение метки.
     * @return объект метка (Tag) если его значение есть в базе, null в противном случае.
     */
    public Tag getTagByValue(String tagValue) {

        UUID id = getIdByTagValue(tagValue);
        Optional<Tag> to = tagRepository.findById(id);
        if (to.isPresent()) {
            return to.get();
        } else {
            return null;
        }

    }

    /**
     * Создает объект Метка (Tag) по его значению и сохраняет в базу данных.
     *
     * @param tagValue значение метки
     * @return возвращает объект метка (Tag)
     */
    public Tag addTag(String tagValue) {
        Tag tag = new Tag();
        tag.setId(getIdByTagValue(tagValue));
        tag.setValue(tagValue);

        tagRepository.save(tag);
        tagRepository.flush();

        return tag;
    }


    /**
     * Метод возвращает идентификатор объекта по строке: строка -> MD5 -> UUID.
     *
     * @param tagValue строка по которой формируется UUID
     * @return идентификатор в формате UUID
     */
    private UUID getIdByTagValue(String tagValue) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            byte[] bytes = tagValue.toLowerCase().getBytes(StandardCharsets.UTF_8);
            UUID id = UUID.nameUUIDFromBytes(bytes);

            return id;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error in addTag() with " + tagValue, e);
        }
    }
}
