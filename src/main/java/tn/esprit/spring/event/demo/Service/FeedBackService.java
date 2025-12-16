package tn.esprit.spring.event.demo.Service;

import tn.esprit.spring.event.demo.Model.Event;
import tn.esprit.spring.event.demo.Model.FeedBack;
import tn.esprit.spring.event.demo.Model.User;

import java.util.List;

public interface FeedBackService {

    FeedBack addFeedback(Long eventId, User user, FeedBack feedback);

    FeedBack updateFeedback(Long id, FeedBack feedback);

    void deleteFeedback(Long id);

    FeedBack getFeedback(Long id);

    List<FeedBack> getAllFeedback();

    List<FeedBack> getFeedbackByEvent(Long eventId);

    List<FeedBack> getFeedbackByUser(User user);
    FeedBack getFeedbackById(Long feedbackId);
    List<FeedBack> findByEventId(Long eventId);



}
