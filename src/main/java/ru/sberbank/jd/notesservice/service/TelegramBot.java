package ru.sberbank.jd.notesservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sberbank.jd.notesservice.config.BotConfig;
import ru.sberbank.jd.notesservice.dao.entity.Note;
import ru.sberbank.jd.notesservice.dao.entity.User;
import ru.sberbank.jd.notesservice.dao.repository.NoteRepository;
import ru.sberbank.jd.notesservice.dao.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис класс Telegram-бота.
 */
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    final String helpText = """
            Список команд:

            /notes - просмотр пяти случайных публичных заметок,

            /my - просмотреть ваши заметки,

            /add - добавить заметку,

            /delete - удалить заметку""";

    final String noAccount = """
            К сожалению, у вас отсутствует учетная запись на данном сервисе.

            Чтобы пользоваться всеми возможностями NotesService зарегистрируйтесь по ссылке:

            http://localhost:8080/auth/register

            с указанием вашего Telegram-логина""";

    final String userBlocked = """
            К сожалению, ваша учетная запись заблокирована.

            Пожалуйста, обратитесь к администратору сервиса""";



    final String noNotes = """
            У вас нет заметок на NotesServive.
            Чтобы добавить заметку необходимо воспользоваться командой /add,
            либо создать ее по ссылке:

            http://localhost:8080/note/create""";

    @Autowired
    UserRepository userRepository;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    NoteService noteService;

    /**
     * Конструктор бота.
     */
    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> commandList = new ArrayList();
        commandList.add(new BotCommand("/start", "Приветственное сообщение"));
        commandList.add(new BotCommand("/notes", "Просмотреть случайные заметки"));
        commandList.add(new BotCommand("/my", "Просмотреть мои заметки"));
        commandList.add(new BotCommand("/delete", "Удалить заметку"));
        commandList.add(new BotCommand("/help", "Помощь"));
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ex) {
            log.info("Error setting bot's command list: " + ex.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    /**
     * Основной метод обработки сообщений.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (message) {
                case "/start" -> startCommandRecieved(chatId, update.getMessage().getChat().getUserName());
                case "/help" -> sendMessage(chatId, helpText);
                case "/notes" -> getNotes(chatId);
                case "/my" -> getMyNotes(chatId, update.getMessage().getChat().getUserName());
                case "/delete" -> deleteNote(chatId, update.getMessage().getChat().getUserName());
                default -> sendMessage(chatId, "К сожалению, я не знаю такой команды...");
            }
        } else if (update.hasCallbackQuery()
                && update.getCallbackQuery().getMessage().getText().equals("Какую заметку вы хотите удалить?")) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            EditMessageText message = new EditMessageText();
            Optional<User> user = userRepository.findByTelegramLogin(update.getCallbackQuery().getMessage()
                    .getChat().getUserName());

            if (user.isPresent()) {
                List<Note> notes = noteService.getAllUserNotes(user.get().getId());
                for (Note note : notes) {
                    if (callBackData.equals(note.getTitle())) {
                        String text = "Заметка с заголовком " + note.getTitle() + " была удалена!";
                        noteService.deleteNoteById(note.getId());
                        message.setChatId(String.valueOf(chatId));
                        message.setText(text);
                        message.setMessageId((int) messageId);
                    }
                }
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    log.error("Error: " + ex.toString());
                }
            }

        } else if (update.hasCallbackQuery()
                && update.getCallbackQuery().getMessage().getText().equals("Ваши заметки:")) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            EditMessageText message = new EditMessageText();
            Optional<User> user = userRepository.findByTelegramLogin(update.getCallbackQuery().getMessage()
                    .getChat().getUserName());

            if (user.isPresent()) {
                List<Note> notes = noteService.getAllUserNotes(user.get().getId());
                for (Note note : notes) {
                    if (callBackData.equals(note.getTitle())) {
                        String text = note.getText();
                        message.setChatId(String.valueOf(chatId));
                        message.setText(text);
                        message.setMessageId((int) messageId);
                    }
                }
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    log.error("Error: " + ex.toString());
                }
            }

        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            EditMessageText message = new EditMessageText();
            List<Note> notes = noteService.getNotes();
            for (Note note : notes) {
                if (callBackData.equals(note.getTitle())) {
                    String text = note.getText() + "\n\nАвтор: " + noteService.getAuthor(note.getTitle());
                    message.setChatId(String.valueOf(chatId));
                    message.setText(text);
                    message.setMessageId((int) messageId);
                }
            }
            try {
                execute(message);
            } catch (TelegramApiException ex) {
                log.error("Error: " + ex.toString());
            }
        }
    }

    /**
     * Получить приветственное сообщение.
     *
     * @param chatId - id чата.
     * @param name - telegram-логин пользователя.
     */
    private void startCommandRecieved(long chatId, String name) {
        String answer = "Привет, " + name + "! Я - telegram-бот сервиса NotesService."
                + " В моем меню указаны команды, которые я знаю. Начнем?";
        log.info("Replied to user " + name + ": " + answer);
        sendMessage(chatId, answer);
    }

    /**
     * Отправить сообщение пользователю.
     *
     * @param chatId - id чата.
     * @param textToSend - текст сообщения для отправки.
     */
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }

    /**
     * Получить пять случайных заметок.
     *
     * @param chatId - id чата.
     */
    private void getNotes(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Случайные заметки:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        List<Note> notes = noteService.getFiveRandomNotes();

        for (Note note : notes) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(note.getTitle());
            button.setCallbackData(note.getTitle());
            rowInLine.add(button);
        }

        rowsLine.add(rowInLine);
        markup.setKeyboard(rowsLine);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }

    /**
     * Получить заметки пользователя.
     *
     * @param chatId - id чата.
     * @param login - telegram-логин пользователя.
     */
    private void getMyNotes(long chatId, String login) {
        Optional<User> user = userRepository.findByTelegramLogin(login);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        if (user.isPresent()) {
            //Authentication auth = new UsernamePasswordAuthenticationToken
            //(new NotesServiceUserDetails(user.get()), null);
            //SecurityContextHolder.getContext().setAuthentication(auth);
            List<Note> notes = noteService.getAllUserNotes(user.get().getId());

            if (user.get().isBlocked()) {
                message.setChatId(String.valueOf(chatId));
                message.setText(userBlocked);
            } else if (notes.isEmpty()) {
                message.setText(noNotes);
            } else {
                message.setText("Ваши заметки:");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsLine = new ArrayList<>();
                List<InlineKeyboardButton> rowInLine = new ArrayList<>();

                for (Note note : notes) {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(note.getTitle());
                    button.setCallbackData(note.getTitle());
                    rowInLine.add(button);
                }

                rowsLine.add(rowInLine);
                markup.setKeyboard(rowsLine);
                message.setReplyMarkup(markup);
            }
        } else {
            message.setText(noAccount);
        }

        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }

    /**
     * Удаляет заметку.
     *
     * @param chatId - id чата.
     * @param login - telegram-логин пользователя.
     */
    private void deleteNote(long chatId, String login) {
        Optional<User> user = userRepository.findByTelegramLogin(login);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        if (user.isPresent()) {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    new NotesServiceUserDetails(user.get()), null);
            SecurityContextHolder.getContext().setAuthentication(auth);
            List<Note> notes = noteService.getAllUserNotes(user.get().getId());
            if (user.get().isBlocked()) {
                message.setChatId(String.valueOf(chatId));
                message.setText(userBlocked);
            } else if (notes.isEmpty()) {
                message.setText(noNotes);
            } else {
                message.setText("Какую заметку вы хотите удалить?");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsLine = new ArrayList<>();
                List<InlineKeyboardButton> rowInLine = new ArrayList<>();

                for (Note note : notes) {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(note.getTitle());
                    button.setCallbackData(note.getTitle());
                    rowInLine.add(button);
                }

                rowsLine.add(rowInLine);
                markup.setKeyboard(rowsLine);
                message.setReplyMarkup(markup);
            }
        } else {
            message.setText(noAccount);
        }

        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }
}
