package tn.esprit.spring.event.demo.Controller;

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

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FeedBackController {

    private final FeedBackService feedbackService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @PostMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public FeedBack addFeedback(@PathVariable Long eventId,
                                @RequestBody FeedBack feedback,
                                Authentication authentication) {

        // ⚠ authentication.getName() retourne l'email du user connecté
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return feedbackService.addFeedback(eventId, user, feedback);
    }

    // ➤ Modifier un feedback (seulement par son propriétaire ou ADMIN)
    @PutMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public FeedBack updateFeedback(@PathVariable Long feedbackId,
                                   @RequestBody FeedBack newFeedbackData,
                                   Authentication authentication) {

        // 1️⃣ Récupérer l'email depuis le JWT
        String email = authentication.getName();

        // 2️⃣ Charger l’utilisateur connecté depuis la BD
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Récupérer le feedback existant
        FeedBack existingFeedback = feedbackService.getFeedbackById(feedbackId);


        // 5️⃣ Appliquer la modification
        return feedbackService.updateFeedback(feedbackId, newFeedbackData);
    }


    // ➤ Supprimer un feedback (CLIENT = supprime seulement les siens, ADMIN = tout)
    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public String deleteFeedback(@PathVariable Long feedbackId, Authentication authentication) {

        // 1️⃣ Récupérer l’email du user connecté
        String email = authentication.getName();

        // 2️⃣ Charger le user depuis la BD
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Charger le feedback
        FeedBack fb = feedbackService.getFeedback(feedbackId);
        if (fb == null) {
            throw new RuntimeException("Feedback not found");
        }

        // 4️⃣ Vérifier la propriété si le rôle est CLIENT
        boolean isAdmin = user.getRole().equals("ADMIN");
        boolean isOwner = fb.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Vous ne pouvez pas supprimer ce feedback !");
        }

        // 5️⃣ Supprimer
        feedbackService.deleteFeedback(feedbackId);

        return "Feedback supprimé avec succès";
    }


    // ➤ Récupérer un feedback
   /* @GetMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")

    public FeedBack getFeedback(@PathVariable Long feedbackId) {
        return feedbackService.getFeedback(feedbackId);
    }*/

    // ➤ Récupérer tous les feedbacks
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")

    public List<FeedBack> getAll() {
        return feedbackService.getAllFeedback();
    }



    // ➤ Récupérer tous les feedbacks user connecté
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public List<FeedBack> getMyFeedbacks(Authentication authentication) {

        // 1️⃣ Get email from JWT
        String email = authentication.getName();

        // 2️⃣ Load user from DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Return feedbacks of this user
        return feedbackService.getFeedbackByUser(user);
    }

    // ➤ Récupérer un feedback par ID
    @GetMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public FeedBack getFeedbackById(@PathVariable Long feedbackId,
                                    Authentication authentication) {

        // 1 Charger l’utilisateur connecté
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 2 Charger le feedback demandé
        FeedBack feedback = feedbackService.getFeedbackById(feedbackId);

        // 3 Règle de sécurité
        //    - ADMIN : peut voir tous les feedbacks
        //    - CLIENT : peut voir seulement ses feedbacks
        if (user.getRole().equals(Role.CLIENT) && !feedback.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Vous n’avez pas le droit d’accéder à ce feedback");
        }

        // 4️⃣ OK → retourner
        return feedback;
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public List<FeedBack> getFeedbacksByEvent(@PathVariable Long eventId,
                                              Authentication authentication) {

        // 1️⃣ Récupérer l'email depuis le token JWT
        String email = authentication.getName();
        // 2️⃣ Vérifier l'existence de l'événement
        eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // 3️⃣ Retourner TOUS les feedbacks du même événement
        return feedbackService.getFeedbackByEvent(eventId);
    }






}
