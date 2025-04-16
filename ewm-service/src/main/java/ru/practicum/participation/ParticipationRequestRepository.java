package ru.practicum.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.participation.model.ParticipationRequest;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    @Query("SELECT p " +
            "FROM ParticipationRequest p " +
            "WHERE p.requester.id = :requesterId")
    List<ParticipationRequest> findAllByRequester(@Param("requesterId") Integer requesterId);

    @Query("SELECT p " +
            "FROM ParticipationRequest p " +
            "WHERE p.requester.id = :requesterId " +
            "AND p.event.id = :eventId")
    List<ParticipationRequest> findAllByRequesterAndEvent(@Param("requesterId") Integer requesterId,
                                                          @Param("eventId") Integer eventId);

    @Query("SELECT p " +
            "FROM ParticipationRequest p " +
            "WHERE p.event.id = :eventId")
    List<ParticipationRequest> findAllByEvent(@Param("eventId") Integer eventId);

    @Query("SELECT p " +
            "FROM ParticipationRequest p " +
            "WHERE p.id IN :list")
    List<ParticipationRequest> findParticipationsByIdList(@Param("list") List<Integer> list);
}
