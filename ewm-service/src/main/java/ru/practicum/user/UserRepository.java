package ru.practicum.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.id IN :ids")
    List<User> findUsersByIdsList(Pageable usersPage, @Param("ids") List<Integer> ids);

    List<User> findAllByEmail(String email);
}
