package ru.vernalis.servicesspringsample.persistance.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserMessageRepository extends CrudRepository<UserMessage, Long> {
    @Query(value = "SELECT * FROM user_message WHERE user_id=:userId ORDER BY user_message.id DESC LIMIT :limit", nativeQuery = true)
    List<UserMessage> getUserMessages(long userId, int limit);

}
