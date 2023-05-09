package com.example.travel_agency_pfe.Controllers;

import com.example.travel_agency_pfe.Models.AppUser;
import com.example.travel_agency_pfe.Services.IClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClientController {
    @Autowired
    private IClientServiceImpl iClientService;

    @GetMapping("/panel/admin/clients/delete")
    public String deleteClient(String id){
        iClientService.deleteClient(id);
        return "redirect:/panel/admin/clients";
    }

    @GetMapping("/panel/admin/clients/{id}")
    public String updateForn(@PathVariable("id") String id, Model model){
        model.addAttribute("user",iClientService.getById(id));
        return "pages/clients/editClient";
    }

    @PostMapping("/panel/admin/clients/update")
    public String updateUser(AppUser user, RedirectAttributes ra){
        iClientService.save(user);
        ra.addAttribute("updateMessage","The user has been updated successfully.");
        return "redirect:/panel/admin/clients";
    }




}