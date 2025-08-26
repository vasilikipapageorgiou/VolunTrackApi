package gr.voluntrack.controller.admin;

import gr.voluntrack.model.User;
import gr.voluntrack.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", adminUserService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/new")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", adminUserService.getAllRoles());
        return "admin/add-user";
    }

    @PostMapping
    public String saveUser(@ModelAttribute("user") User user, @RequestParam("roles") List<Long> roleIds) {
        adminUserService.saveUser(user, roleIds);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = adminUserService.getUserById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID:" + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", adminUserService.getAllRoles());
        return "admin/edit-user";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user, @RequestParam("roles") List<Long> roleIds) {
        user.setId(id);
        adminUserService.saveUser(user, roleIds);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
