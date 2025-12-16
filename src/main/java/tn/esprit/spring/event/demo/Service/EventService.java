package tn.esprit.spring.event.demo.Service;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import tn.esprit.spring.event.demo.Model.Event;
import tn.esprit.spring.event.demo.Repository.EventRepository;

import java.util.List;

@Service
public class EventService {

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public List<Event> findAll() {
        return repo.findAll();
    }

    public Event findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    public Event create(Event event) {
        if (event.getNblikes() == null) event.setNblikes(0); // initialisation
        return repo.save(event);
    }

    public Event update(Long id, Event updated) {
        Event existing = findById(id);

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setDate(updated.getDate());
        existing.setLocation(updated.getLocation());
        existing.setPrice(updated.getPrice());
        existing.setOrganizerid(updated.getOrganizerid());
        existing.setImageUrl(updated.getImageUrl());
        existing.setNbplaces(updated.getNbplaces());
        existing.setNblikes(updated.getNblikes() != null ? updated.getNblikes() : 0);

        return repo.save(existing);
    }

    // delete
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // like 👍
    public Event likeEvent(Long id) {
        Event event = findById(id);
        if (event.getNblikes() == null) event.setNblikes(0); // sécurité null
        event.setNblikes(event.getNblikes() + 1);
        return repo.save(event);
    }

    // dislike 👎
    public Event dislikeEvent(Long id) {
        Event event = findById(id);
        if (event.getNblikes() == null) event.setNblikes(0);
        if (event.getNblikes() > 0) {
            event.setNblikes(event.getNblikes() - 1);
        }
        return repo.save(event);
    }
    public List<Event> getEventsByLocation(String location) {
        return repo.findByLocation(location);
    }

}