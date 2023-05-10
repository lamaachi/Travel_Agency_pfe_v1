package com.example.travel_agency_pfe.Repositories;

import com.example.travel_agency_pfe.Models.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface ITravelRepository extends JpaRepository<Travel,Long> {
    // Find the last travel record with true special offer sorted by createdAt in descending order
    Travel findFirstBySpecialOfferTrueOrderByCreatedAtDesc();
}
