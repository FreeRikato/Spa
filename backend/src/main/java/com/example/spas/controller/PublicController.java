package com.example.spas.controller;

import com.example.spas.dto.SpaDetailView;
import com.example.spas.dto.SpaView;
import com.example.spas.service.SpaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final SpaService spaService;

    public PublicController(SpaService spaService) {
        this.spaService = spaService;
    }

    /**
     * Feature 3: Find all approved spas
     */
    @GetMapping("/spas")
    public ResponseEntity<List<SpaView>> getAllSpas() {
        return ResponseEntity.ok(spaService.findAllApprovedSpas());
    }

    /**
     * Feature 3: Find spa by name
     */
    @GetMapping("/spas/search")
    public ResponseEntity<List<SpaView>> searchSpas(@RequestParam String name) {
        return ResponseEntity.ok(spaService.findSpasByName(name));
    }

    /**
     * Feature 5: View a single spa's details
     * Edge Case: Service logic throws 404 if spaId is not found.
     */
    @GetMapping("/spas/{spaId}")
    public ResponseEntity<SpaDetailView> getSpaDetails(@PathVariable Long spaId) {
        return ResponseEntity.ok(spaService.getSpaDetails(spaId));
    }
}