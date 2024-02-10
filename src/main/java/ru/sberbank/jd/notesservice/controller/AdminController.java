package ru.sberbank.jd.notesservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sberbank.jd.notesservice.service.AdminService;

import java.util.UUID;

/**
 * Контроллер работы администратора.
 */
@AllArgsConstructor
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private AdminService adminService;

    /**
     * Обрабатывает запрос на выдачу всех пользователей, не имеющих права администратора.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String all(Model model,
                      @RequestParam(value = "search_user", required = false) String searchUser) {
        model.addAttribute("users", adminService.getAllUsersWithoutAdmins(searchUser));
        return "users";
    }

    /**
     * Обрабатывает запрос на удаление пользователя из БД T_USER.
     */
    @GetMapping("/delete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String delete(@RequestParam("id") String userId) {
        adminService.deleteUserById(UUID.fromString(userId));
        return "redirect:/admin";
    }

    /**
     * Обрабатывает запрос на блокировку пользователя.
     *
     * @param userId - id пользователя
     * @return - редирект на панель администратора.
     */
    @GetMapping("/block")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String blockUser(@RequestParam("id") String userId) {
        adminService.blockUser(UUID.fromString(userId));
        return "redirect:/admin";
    }

    /**
     * Обрабатывает запрос на разблокировку пользователя.
     *
     * @param userId - id пользователя.
     * @return - редирект на панель администратора.
     */
    @GetMapping("/unblock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String unblockUser(@RequestParam("id") String userId) {
        adminService.unblockUser(UUID.fromString(userId));
        return "redirect:/admin";
    }
}
