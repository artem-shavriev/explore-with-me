package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {

    List<Event> findByCategoryIdOrderByEventDateDesc(Integer categoryId);

    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id = :initiatorId ")
    Page<Event> findAllByInitiatorId(@Param("initiatorId") Integer initiatorId, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.id IN :idList " +
            "ORDER BY e.eventDate DESC")
    List<Event> findByIdList(@Param("idList") List<Integer> idList);
}
