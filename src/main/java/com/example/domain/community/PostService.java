package com.example.domain.community;

import com.example.domain.service.Service;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_services")
@NoArgsConstructor
@Getter
@Setter
public class PostService {

    @EmbeddedId
    private PostServiceId id;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private Service service;

    public PostService(Post post, Service service) {
        this.id = new PostServiceId(post.getId(), service.getId());
        this.post = post;
        this.service = service;
    }
}