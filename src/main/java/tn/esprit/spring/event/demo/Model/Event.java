package tn.esprit.spring.event.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
@Table(name = "events")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;


    @Column(nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;


    private String location;

    private Double price;

    private Long organizerid;

    @Lob
    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private String imageUrl;

    private Integer nbplaces;

    private Integer nblikes;

    // constructors
    public Event() {}

    public Event(String title, String description, LocalDate date, String location,
                 Double price, Long organizerid, String imageUrl,
                 Integer nbplaces, Integer nblikes) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.price = price;
        this.organizerid = organizerid;
        this.imageUrl = imageUrl;
        this.nbplaces = nbplaces;
        this.nblikes = nblikes;
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Long getOrganizerid() { return organizerid; }
    public void setOrganizerid(Long organizerid) { this.organizerid = organizerid; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getNbplaces() { return nbplaces; }
    public void setNbplaces(Integer nbplaces) { this.nbplaces = nbplaces; }

    public Integer getNblikes() { return nblikes; }
    public void setNblikes(Integer nblikes) { this.nblikes = nblikes; }
}