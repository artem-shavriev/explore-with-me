package ru.practicum.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.participation.model.ParticipationRequest;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {

   List<ParticipationRequest> findByRequesterId(Integer requesterId);

    List<ParticipationRequest> findByRequesterIdAndEventId(Integer requesterId, Integer eventId);

    List<ParticipationRequest> findByEventId(Integer eventId);

    List<ParticipationRequest> findParticipationByIdIn(List<Integer> list);
}
