package ru.practicum.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.id IN :ids")
    List<User> findUsersByIdsList(Pageable usersPage, @Param("uris") List<Integer> ids);

    List<User> findAllByEmail(String email);
}
