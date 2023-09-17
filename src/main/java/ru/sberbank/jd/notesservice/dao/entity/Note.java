package ru.sberbank.jd.notesservice.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Note - POJO-class для таблицы T_NOTE.
 */

@Entity
@Table(name = "T_NOTE")
@Getter
@Setter
@NoArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "note_id")
    private UUID id;

    @Column(name = "note_title", nullable = false)
    private String title;

    @Column(name = "note_text")
    private String text;

    @JoinColumn(name = "owner_id")
    @ManyToOne(targetEntity = User.class)
    private User owner;

    @Column(name = "public_flag", nullable = false)
    private Boolean publicFlag;

    @Column(name = "create_dtm", nullable = false)
    private LocalDateTime createDateTime;

    @Column(name = "edit_dtm", nullable = false)
    private LocalDateTime editDateTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "T_NOTE_TAG",
            joinColumns = {@JoinColumn(name = "note_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private Set<Tag> tags = new HashSet<Tag>();


    @Override
    public String toString() {
        return "Note{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", text='" + text + '\''
                + ", owner=" + owner
                + ", privateFlag=" + publicFlag
                + ", createDateTime=" + createDateTime
                + ", editDateTime=" + editDateTime
                + '}';
    }
}
