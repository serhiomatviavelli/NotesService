package ru.sberbank.jd.notesservice.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Tag - POJO-class для таблицы меток (T_TAG).
 */

@Entity
@Table(name = "T_TAG")
public class Tag {

    @Id
    @Column(name = "tag_id")
    private UUID id;

    @Column(name = "tag_value", unique = true, nullable = false)
    private String value;

    public Tag() {
    }

    public UUID getId() {
        return id;
    }

    public Tag setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Tag setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "Tag{"
                + "id=" + id
                + ", value='" + value + '\''
                + '}';
    }
}
