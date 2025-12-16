package tn.esprit.spring.event.demo.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.event.demo.Model.Event;
import tn.esprit.spring.event.demo.Model.User;
import tn.esprit.spring.event.demo.Repository.FeedBackRepository;
import tn.esprit.spring.event.demo.Service.EventService;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.event.demo.Model.Event;
import tn.esprit.spring.event.demo.Model.FeedBack;
import tn.esprit.spring.event.demo.Model.Role;
import tn.esprit.spring.event.demo.Model.User;
import tn.esprit.spring.event.demo.Repository.EventRepository;
import tn.esprit.spring.event.demo.Repository.UserRepository;
import tn.esprit.spring.event.demo.Service.FeedBackService;

@RestController
@RequestMapping("/api/v1/events")
@CrossOrigin(origins = "http://localhost:4200")
public class EventController {

    private final EventService service;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final FeedBackRepository feedbackRepository;



    public EventController(EventService service,  UserRepository userRepositoryy,EventRepository eventRepository, FeedBackRepository feedbackRepository ) {
        this.service = service;
        this.userRepository = userRepositoryy;
        this.eventRepository = eventRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public List<Event> all() {
        return service.findAll();
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")

    @GetMapping("/{id}")
    public Event get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Event event) {
        try {
            Event saved = service.create(event);
            return ResponseEntity.created(URI.create("/api/v1/events/" + saved.getId())).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    // ✅ Update event (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Event update(@PathVariable Long id,
                             @Valid @RequestBody Event event,
                             Authentication authentication) {

        // 1️⃣ Optional: verify user exists (ADMIN)
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 2️⃣ Only ADMIN can update, already enforced by @PreAuthorize
        return service.update(id, event);
    }

    // ✅ Delete event (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, Authentication authentication) {

        // 1️⃣ Récupérer l’utilisateur connecté (optionnel, mais utile si tu veux logger ou vérifier)
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 2️⃣ Supprimer tous les feedbacks liés à cet événement
        feedbackRepository.deleteByEventId(id);

        // 3️⃣ Supprimer l'événement
        eventRepository.deleteById(id);

        // 4️⃣ Retourner 204 No Content
        return ResponseEntity.noContent().build();
    }

    // ✅ Like an event (CLIENT only)
    @PutMapping("/{id}/like")
    @PreAuthorize("hasRole('CLIENT')")
    public Event likeEvent(@PathVariable Long id,
                           Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return service.likeEvent(id); // service handles logic
    }

    // ✅ Dislike an event (CLIENT only)
    @PutMapping("/{id}/dislike")
    @PreAuthorize("hasRole('CLIENT')")
    public Event dislikeEvent(@PathVariable Long id,
                              Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return service.dislikeEvent(id);
    }

    @GetMapping("/Same/{location}")  // ✅ Plus clair
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public List<Event> getEventsByLocation(@PathVariable String location,
                                           Authentication authentication) {
        String email = authentication.getName();
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return service.getEventsByLocation(location);
    }
}