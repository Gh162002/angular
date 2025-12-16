package tn.esprit.spring.event.demo.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.event.demo.Model.Event;
import tn.esprit.spring.event.demo.Model.FeedBack;

import java.util.List;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {
    // JpaRepository already provides CRUD: save, findAll, findById, deleteById, etc.
    List<FeedBack> findByEvent_Id(Long eventId);
    List<FeedBack> findByUser_Id(Long userId);
    List<FeedBack> findByUser_Email(String email);
    List<FeedBack> findByEventId(Long eventId);
    @Transactional
    void deleteByEventId(Long eventId);

}
