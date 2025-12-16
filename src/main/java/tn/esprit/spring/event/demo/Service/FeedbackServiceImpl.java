package tn.esprit.spring.event.demo.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.event.demo.Model.Event;
import tn.esprit.spring.event.demo.Model.FeedBack;
import tn.esprit.spring.event.demo.Model.User;
import tn.esprit.spring.event.demo.Repository.EventRepository;
import tn.esprit.spring.event.demo.Repository.FeedBackRepository;
import tn.esprit.spring.event.demo.Repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedBackService {

    private final FeedBackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public FeedBack addFeedback(Long eventId, User user, FeedBack feedback) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        feedback.setUser(user);
        feedback.setEvent(event);

        return feedbackRepository.save(feedback);
    }

    @Override
    public FeedBack updateFeedback(Long id, FeedBack feedback) {
        FeedBack existing = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        existing.setMessage(feedback.getMessage());
        existing.setRate(feedback.getRate());
        existing.setDate(feedback.getDate());

        return feedbackRepository.save(existing);
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public FeedBack getFeedback(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
    }

    @Override
    public List<FeedBack> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    public List<FeedBack> getFeedbackByEvent(Long eventId) {
        return feedbackRepository.findByEvent_Id(eventId);
    }

    @Override
    public List<FeedBack> getFeedbackByUser(User user) {
        return feedbackRepository.findByUser_Id(user.getId());
    }

    @Override
    public FeedBack getFeedbackById(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback introuvable"));
    }



    @Override
    public List<FeedBack> findByEventId(Long eventId) {
        // Vérifier que l'événement existe
        eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        return feedbackRepository.findByEventId(eventId);    }


}
