package ru.practicum.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.participation.model.ParticipationRequest;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findAllByRequester(Integer requester);

    List<ParticipationRequest> findAllByRequesterAndEvent(Integer requester, Integer event);

    List<ParticipationRequest> findAllByEvent(Integer event);

    @Query("SELECT p " +
            "FROM ParticipationRequest p " +
            "WHERE p.id IN :List")
    List<ParticipationRequest> findParticipationsByIdList(@Param("List") List<Integer> List);
}
