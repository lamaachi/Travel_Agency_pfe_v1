package com.example.travel_agency_pfe.Controllers;

import com.example.travel_agency_pfe.Configurations.FileUplaodUtil;
import com.example.travel_agency_pfe.Models.AppUser;
import com.example.travel_agency_pfe.Models.Travel;
import com.example.travel_agency_pfe.Repositories.IAppUserRepository;
import com.example.travel_agency_pfe.Repositories.ITravelRepository;
import com.example.travel_agency_pfe.Services.ITravelServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;


@Controller
@AllArgsConstructor
@RequestMapping("/panel/admin")
public class TravelController {
    private IAppUserRepository appUserRepository;
    private ITravelServiceImpl travelService;


    @GetMapping("/travels")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String travelsList(Model model){
        List<Travel> travels = travelService.getAllTravels();

        model.addAttribute("travels",travels);

        return "pages/travels/travelsList";
    }

    @GetMapping("/travels/addnew")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String newTravelForm(Model model){
        Travel travel = Travel.builder().build();
        model.addAttribute("travel", travel);
        return "pages/travels/addNewTravel";
    }

    @PostMapping("/travels/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addTravel(Authentication authentication, @ModelAttribute("travel") @Valid Travel tr , @RequestParam("file") MultipartFile image, BindingResult result, Model model,@RequestParam(value="specialOffer", required=false) Boolean specialOffer) throws IOException {
        if (result.hasErrors()) {
            // Add error messages to the Thymeleaf model
            model.addAttribute("errors", result.getAllErrors());
            return "pages/travels/addNewTravel";
        }
        // Get the current user's username from the Authentication object
        String currentUsername = authentication.getName();
        // Find the AppUser object for the current user
        AppUser currentUser = appUserRepository.findByUserName(currentUsername);
        // Create a new Travel object and set its properties
        boolean special = true;
        if(specialOffer==null){
            special=false;
        }
        if(!image.isEmpty()){
            String originalFilename  = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            Travel travel = Travel.builder()
                    .id(tr.getId())
                    .title(tr.getTitle())
                    .travelDate(tr.getTravelDate())
                    .travelType(tr.getTravelType())
                    .exclus(tr.getExclus())
                    .inclus(tr.getInclus())
                    .specialOffer(special)
                    .Activities(tr.getActivities())
                    .image(filename)
                    .price(tr.getPrice())
                    .destiantion(tr.getDestiantion())
                    .nights(tr.getNights())
                    .days(tr.getDays())
                    .appUser(currentUser)
                    .build();
                    travelService.save(travel);

            String upload = "src/main/resources/static/travel-images";
            FileUplaodUtil.saveFile(upload,filename,image);
        }else{
            if(tr.getImage().isEmpty()){
                Travel travel = Travel.builder()
                        .id(tr.getId())
                        .title(tr.getTitle())
                        .travelDate(tr.getTravelDate())
                        .travelType(tr.getTravelType())
                        .exclus(tr.getExclus())
                        .inclus(tr.getInclus())
                        .Activities(tr.getActivities())
                        .image(null)
                        .price(tr.getPrice())
                        .destiantion(tr.getDestiantion())
                        .nights(tr.getNights())
                        .days(tr.getDays())
                        .appUser(currentUser)
                        .build();
                travelService.save(travel);
            }

        }

        // Redirect to the travel list page
        return "redirect:/panel/admin/travels?success";
    }

    @GetMapping("/travels/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteClient(Long id){
        travelService.deleteTravel(id);
        return "redirect:/panel/admin/travels?successdelete";
    }


//    travel details
   @GetMapping("/travels/{id}")
   @PreAuthorize("hasRole('ROLE_ADMIN')")
   public String detailsPage(@PathVariable("id") Long id, Model model){
        model.addAttribute("travel",travelService.getTravelById(id).get());
        return "pages/travels/travelDetails";
    }




}
