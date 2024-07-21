package com.example.domain.community;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PostServiceId implements Serializable {

    private Long postId;
    private Long serviceId;

    public PostServiceId() {}

    public PostServiceId(Long postId, Long serviceId) {
        this.postId = postId;
        this.serviceId = serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostServiceId that = (PostServiceId) o;
        return Objects.equals(postId, that.postId) &&
                Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, serviceId);
    }
}