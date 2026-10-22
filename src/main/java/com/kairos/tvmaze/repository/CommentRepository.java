package com.kairos.tvmaze.repository;

import com.kairos.tvmaze.model.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para gestionar los comentarios en MongoDB.
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    /**
     * Obtiene todos los comentarios de un show específico.
     */
    List<Comment> findByShowIdOrderByCreatedAtDesc(Integer showId);
}
