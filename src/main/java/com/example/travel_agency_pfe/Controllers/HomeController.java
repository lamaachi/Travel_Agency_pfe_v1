package com.example.travel_agency_pfe.Controllers;

import com.example.travel_agency_pfe.Configurations.ImageUtil;
import com.example.travel_agency_pfe.Models.*;
import com.example.travel_agency_pfe.Repositories.IAgencyRepositry;
import com.example.travel_agency_pfe.Repositories.ITravelRepository;
import com.example.travel_agency_pfe.Services.IReviewServiceImpl;
import com.example.travel_agency_pfe.Services.ITravelService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Controller
public class HomeController{
    private ITravelService travelService;
    private IReviewServiceImpl reviewService;
    private ITravelRepository travelRepository;
    private IAgencyRepositry iAgencyRepositry;

    @GetMapping(value = {"/","/index","/pages/"})
    public String index(Model model, @RequestParam(value = "page",defaultValue = "0") int page,@RequestParam(value = "size",defaultValue = "3") int size){
        Page<Travel> travelList = travelRepository.findAll(PageRequest.of(page, size));
        if (!travelList.isEmpty()) {
            Map<Long, String> travelImageMap = new HashMap<>();
            for (Travel travel : travelList) {
                Image image = travel.getImage();
                if (image != null) {
                    byte[] imageData = ImageUtil.decompressImage(image.getImageData());
                    String base64Image = Base64.getEncoder().encodeToString(imageData);
                    String imageUrl = "data:" + image.getType() + ";base64," + base64Image;
                    travelImageMap.put(travel.getId(), imageUrl);
                }
            }
            model.addAttribute("travelImageMap", travelImageMap);
        }

        List<Review> reviews = reviewService.getAtiveReviews();
        Subscriber subscriber = new Subscriber();

        Travel specialOffer = travelRepository.findFirstBySpecialOfferTrueOrderByCreatedAtDesc();
        if (specialOffer != null) {
            Image image = specialOffer.getImage();
            if (image != null) {
                byte[] imageData = ImageUtil.decompressImage(image.getImageData());
                String base64Image = Base64.getEncoder().encodeToString(imageData);
                String imageUrl = "data:" + image.getType() + ";base64," + base64Image;
                model.addAttribute("imageUrl", imageUrl);
            }
            model.addAttribute("specialOffer", specialOffer);
        }
        Agency agency = iAgencyRepositry.findById(1L).get();
        model.addAttribute("agency",agency);
        model.addAttribute("travels",travelList.getContent());
        model.addAttribute("pages",new int[travelList.getTotalPages()]);
        model.addAttribute("curruntpage",page);
        model.addAttribute("reviews",reviews);
        model.addAttribute("subscriber",subscriber);

        return "index";
    }
    @GetMapping("/booknow/travel/{id}")
    public String booknow(@PathVariable("id") Long id,Model model){
        Subscriber subscriber = new Subscriber();
        model.addAttribute("subscriber",subscriber);
        Image image = travelService.getTravelById(id).get().getImage();
        byte[] imageData = ImageUtil.decompressImage(image.getImageData());
        String base64Image = Base64.getEncoder().encodeToString(imageData);
        String imageUrl = "data:" + image.getType() + ";base64," + base64Image;
        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("travel",travelService.getTravelById(id).get());
        Agency agency = iAgencyRepositry.findById(1L).get();
        model.addAttribute("agency",agency);
        return "pages/travels/booknow";
    }
}




