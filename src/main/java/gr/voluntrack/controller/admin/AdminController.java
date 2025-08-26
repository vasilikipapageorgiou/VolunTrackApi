package gr.voluntrack.controller.admin;

import gr.voluntrack.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/volunteers")
    public String listVolunteers(Model model) {
        model.addAttribute("volunteers", adminService.getAllVolunteersPendingApproval());
        return "admin/volunteers"; // thymeleaf template admin/volunteers.html
    }

    @PostMapping("/volunteers/{id}/approve")
    public String approveVolunteer(@PathVariable Long id) {
        adminService.approveVolunteer(id);
        return "redirect:/admin/volunteers";
    }

    @GetMapping("/events")
    public String listEvents(Model model) {
        model.addAttribute("events", adminService.getAllEventsPendingApproval());
        return "admin/events"; // thymeleaf template admin/events.html
    }

    @PostMapping("/events/{id}/approve")
    public String approveEvent(@PathVariable Long id) {
        adminService.approveEvent(id);
        return "redirect:/admin/events";
    }

    // Προσθέτουμε ανάλογες μεθόδους για διαχείριση χρηστών κτλ
}
