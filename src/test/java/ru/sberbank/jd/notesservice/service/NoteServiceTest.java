package ru.sberbank.jd.notesservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.sberbank.jd.notesservice.dao.entity.Note;
import ru.sberbank.jd.notesservice.dao.entity.Tag;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.dao.repository.NoteRepository;
import ru.sberbank.jd.notesservice.dao.repository.TagRepository;
import ru.sberbank.jd.notesservice.dao.repository.UserRepository;
import ru.sberbank.jd.notesservice.dto.NoteDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class NoteServiceTest {
    NoteDto noteDto = new NoteDto();
    User user = new User();
    String userId = "55d35870-bd93-11ed-afa1-0242ac120002";

    UUID noteId = UUID.fromString("b762844e-bdb6-11ed-afa1-0242ac120002");

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TagRepository tagRepository;


    @MockBean
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteDto.setTagString("Tag1 Tag2");
        NotesServiceUserDetails mockPrincipal = Mockito.mock(NotesServiceUserDetails.class);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockPrincipal);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAllNotes() {
        noteService.getAllNotes();
        Mockito.verify(noteRepository,Mockito.times(1)).findAll();
    }

    @Test
    void getNotes() {
        noteService.getNotes();
        Mockito.verify(noteRepository,Mockito.times(1)).findNotes();
    }

    @Test
    void getNotesByTag() {
        UUID id = UUID.fromString("55d35870-bd93-11ed-afa1-0242ac120002");
        noteService.getNotesByTag(id);
        Mockito.verify(noteRepository,Mockito.times(1)).findNotesByTag(id);
    }

    @Test
    void getAllNotesByOwner() {
        UUID id = UUID.fromString("55d35870-bd93-11ed-afa1-0242ac120002");
        noteService.getAllNotesByOwner(id);
        Mockito.verify(noteRepository,Mockito.times(1)).findAllNotesByOwner(id);
    }

    @Test
    void getAllNotesByOwnerAndTag() {
        UUID id = UUID.fromString("55d35870-bd93-11ed-afa1-0242ac120002");
        UUID id1 = UUID.fromString("55d35870-bd93-11ed-afa1-0242ac120003");
        noteService.getAllNotesByOwnerAndTag(id, id1);
        Mockito.verify(noteRepository,Mockito.times(1)).findAllNotesByOwnerAndTag(id, id1);
    }

    @Test
    void getUserNotes() {
        List<Note> noteList = noteService.getUserNotes(userId);
        List<Note> testNoteList = new ArrayList<>();
        assertEquals(testNoteList, noteList);
    }

    @Test
    void saveEmptyNote() {
        Note note = noteService.saveNote(noteDto, user);
        LocalDateTime time = LocalDateTime.now();
        assertFalse(note.getPublicFlag());
        assertEquals(user, note.getOwner());
        assertTrue(time.isAfter(note.getCreateDateTime()) || time.isEqual(note.getCreateDateTime()));
        assertTrue(time.isAfter(note.getEditDateTime()) || time.isEqual(note.getEditDateTime()));
        assertEquals(2, note.getTags().size());
        Mockito.verify(noteRepository,Mockito.times(1)).save(note);
        Mockito.verify(noteRepository,Mockito.times(1)).flush();
    }

    @Test
    void saveFilledNote() {
        noteDto.setPublicFlag(true);
        noteDto.setText("NoteText");
        noteDto.setTitle("NoteTitle");
        Note note = noteService.saveNote(noteDto, user);
        assertTrue(note.getPublicFlag());
        assertEquals("NoteText", note.getText());
        assertEquals("NoteTitle", note.getTitle());
        Mockito.verify(noteRepository,Mockito.times(1)).save(note);
        Mockito.verify(noteRepository,Mockito.times(1)).flush();
    }

    @Test
    void deleteNoteById() {
        noteService.deleteNoteById(noteId);
        Mockito.verify(noteRepository,Mockito.times(1)).deleteById(noteId);
    }

    @Test
    void getNoteById() {
        Note note = noteService.getNoteById(noteId);
        Mockito.verify(noteRepository,Mockito.times(1)).findById(noteId);
        assertNull(note);
    }

    @Test
    void GetNoteByIdString() {
        Note note = noteService.getNoteById("b762844e-bdb6-11ed-afa1-0242ac120002");
        Mockito.verify(noteRepository,Mockito.times(1)).findById(noteId);
        assertNull(note);
    }

    @Test
    void convertToDto() {
        Note note = new Note();
        note.setOwner(user);
        note.setCreateDateTime(LocalDateTime.now());
        note.setText("**Bold**");
        note.setTitle("NoteTitle");
        Tag tag1 = new Tag();
        tag1.setValue("Tag1");
        Tag tag2 = new Tag();
        tag2.setValue("Tag2");
        Set<Tag> setTags = new HashSet<>();
        setTags.add(tag1);
        setTags.add(tag2);
        note.setTags(setTags);
        NoteDto noteDto1 = noteService.convertToDto(note);
        assertEquals(user, noteDto1.getOwner());
        assertEquals("**Bold**", noteDto1.getText());
        assertEquals("<p><strong>Bold</strong></p>" + "\n", noteDto1.getFormattedText());
        assertEquals(note.getTitle(), noteDto1.getTitle());
        assertEquals("Tag1 Tag2", noteDto1.getTagString());
    }
}