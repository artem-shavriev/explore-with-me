package ru.practicum.event;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventSpecification {
    public static Specification<Event> hasText(String text) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + text.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                                "%" + text.toLowerCase() + "%"));
    }

    public static Specification<Event> hasCategories(List<Integer> categories) {
        return (root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.join("category").get("id").in(categories);
        };
    }

    public static Specification<Event> hasStates(List<State> states) {
        return (root, query, criteriaBuilder) -> {
            if (states == null || states.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("state").in(states);
        };
    }

    public static Specification<Event> hasUsers(List<Integer> users) {
        return (root, query, criteriaBuilder) -> {
            if (users == null || users.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.join("initiator").get("id").in(users);
        };
    }

    public static Specification<Event> hasPaid(Boolean paid) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("paid"), paid);
    }

    public static Specification<Event> dateBetween(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("eventDate"), rangeStart, rangeEnd);
    }

    public static Specification<Event> dateAfterNow(LocalDateTime rangeStart) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
    }

    public static Specification<Event> hasState(State state) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), state);
    }

    public static Specification<Event> available(Boolean available) {
        return (root, query, criteriaBuilder) -> {
            if (!available) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("participantLimit"), 0),
                    criteriaBuilder.lessThanOrEqualTo(root.get("confirmedRequests"), root.get("participantLimit"))
            );
        };
    }

        /*public static Specification<Event> hasCategories(List<Integer> categories) {
            return (root, query, criteriaBuilder) -> {
                if (categories == null || categories.isEmpty()) {
                    return criteriaBuilder.conjunction();
                }
                return root.join("category").get("id").in(categories);
            };
        }*/


   /*
    (@RequestParam(defaultValue = "") String text,
    @RequestParam(required = false)
    List<Integer> categories,
    @RequestParam(required = false) Boolean paid,
    @RequestParam(required = false) String rangeStart,
    @RequestParam(required = false) String rangeEnd,
    @RequestParam(defaultValue = "false") Boolean onlyAvailable,
    @RequestParam(defaultValue = "EVENT_DATE") String sort,
    @RequestParam(defaultValue = "0") Integer from,
    @RequestParam(defaultValue = "10") Integer size,
    HttpServletRequest request) {*/
}
