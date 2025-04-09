package ru.practicum.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.participation.model.ParticipationRequest;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findAllByRequester(Integer requester);

    List<ParticipationRequest> findAllByRequesterAndEvent(Integer requester, Integer event);
}
