package ru.vernalis.servicesspringsample.persistance.model;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ServiceUserRepository extends CrudRepository<ServiceUser, Long> {
    @Query("SELECT * FROM service_user WHERE service_user.name=:name")
    ServiceUser findUserByName(String name);
}
