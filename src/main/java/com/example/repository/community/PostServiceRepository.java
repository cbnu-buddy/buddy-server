package com.example.repository.community;

import com.example.domain.community.PostService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostServiceRepository extends JpaRepository<PostService, Long> {
}