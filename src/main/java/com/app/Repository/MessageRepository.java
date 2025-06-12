package com.app.Repository;

import com.app.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByPostedBy(Integer postedBy);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.messageText = :newText WHERE m.messageId = :messageId")
    int updateMessageText(@Param("messageId") Integer messageId, @Param("newText") String newText);
}