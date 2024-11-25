package org.groupscope.schedule_nure.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.schedule_nure.dao.ScheduleRedisDAO;
import org.groupscope.schedule_nure.dao.ScheduleNureDAO;
import org.groupscope.schedule_nure.dto.*;
import org.groupscope.schedule_nure.entity.NureAuditory;
import org.groupscope.schedule_nure.entity.NureGroup;
import org.groupscope.schedule_nure.entity.NureTeacher;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
@Slf4j
public class ScheduleService {

    private final HashMap<EventTypes, String> hashesOfLastResponse;
    
    private final RestTemplate restTemplate;

    private final ScheduleNureDAO scheduleNureDAO;

    private final ScheduleRedisDAO scheduleRedisDAO;

    private final Gson gson = new Gson();

    public ScheduleService(RestTemplateBuilder restTemplateBuilder, ScheduleNureDAO scheduleNureDAO, ScheduleRedisDAO scheduleRedisDAO) {
        restTemplate = restTemplateBuilder.build();
        this.scheduleNureDAO = scheduleNureDAO;
        this.scheduleRedisDAO = scheduleRedisDAO;

        hashesOfLastResponse = new HashMap<>();
        
        hashesOfLastResponse.put(EventTypes.GROUP, "");
        hashesOfLastResponse.put(EventTypes.TEACHER, "");
        hashesOfLastResponse.put(EventTypes.AUDITORY, "");
    }

    private String getSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    /**
     * Retrieves groups information from the NURE API.
     *
     * @return A HashMap containing group IDs as keys and corresponding NureGroupDTO objects as values.
     */
    @Transactional
    public HashMap<Long, NureGroupDTO> getGroupsMap() {
        // Endpoint URL for fetching groups information from the NURE API
        String url = "https://cist.nure.ua/ias/app/tt/P_API_GROUP_JSON";

        try {
            // Sending a GET request to the API endpoint
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String jsonResponse = response.getBody();

            // Generating SHA-256 hash of the JSON response for comparison
            String responseHash = getSHA256Hash(requireNonNull(jsonResponse));

            // Checking if the retrieved data matches the last cached data
            if (hashesOfLastResponse.get(EventTypes.GROUP).equalsIgnoreCase(responseHash)) {
                // Returning cached groups map if the data matches
                return scheduleRedisDAO.getGroupsMap();
            }

            // Parsing the JSON response into a HashMap of groups
            HashMap<Long, NureGroupDTO> groups = NureGroupDTO.parse(jsonResponse);

            // Updating the cache with the new response hash and saving the groups map
            hashesOfLastResponse.put(EventTypes.GROUP, responseHash);
            scheduleRedisDAO.saveGroupsMap(groups);

            // Creating NureGroup entities from the fetched groups and saving them to the database
            List<NureGroup> nureGroups = groups.values().stream()
                    .map(NureGroupDTO::toEntity)
                    .toList();
            scheduleNureDAO.saveNureGroups(nureGroups);

            // Returning the fetched groups map
            return groups;

        } catch (HttpClientErrorException.NotFound ex) {
            // Retrieving groups information from the database if the API endpoint is not found
            List<NureGroup> nureGroups = scheduleNureDAO.getAllNureGroups();

            HashMap<Long, NureGroupDTO> groups = nureGroups.stream()
                    .map(g -> new NureGroupDTO(g.getId(), g.getName(), ""))
                    .collect(Collectors.toMap(NureGroupDTO::getId, g -> g, (g1, g2) -> g1, HashMap::new));

            scheduleRedisDAO.saveGroupsMap(groups);

            log.error("Groups not found in NURE API. Using data from the database.");
            ex.printStackTrace();
            return groups;
        }
    }

    /**
     * Retrieves teachers information from the NURE API.
     *
     * @return A HashMap containing teacher IDs as keys and corresponding NureTeacherDTO objects as values.
     */
    @Transactional
    public HashMap<Long, NureTeacherDTO> getTeachersMap() {
        // Endpoint URL for fetching teachers information from the NURE API
        String url = "https://cist.nure.ua/ias/app/tt/P_API_PODR_JSON";

        try {
            // Sending a GET request to the API endpoint
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String jsonResponse = response.getBody();

            // Fixing json structure
            StringBuilder sb = new StringBuilder(Objects.requireNonNull(jsonResponse));
            sb.delete(jsonResponse.length() - 2, jsonResponse.length());
            sb.append("]}}");
            jsonResponse = sb.toString();

            // Generating SHA-256 hash of the JSON response for comparison
            String responseHash = getSHA256Hash(requireNonNull(jsonResponse));

            // Checking if the retrieved data matches the last cached data
            if (hashesOfLastResponse.get(EventTypes.TEACHER).equalsIgnoreCase(responseHash)) {
                // Returning cached teachers map if the data matches
                return scheduleRedisDAO.getTeachersMap();
            }

            // Parsing the JSON response into a HashMap of teachers
            HashMap<Long, NureTeacherDTO> teachers = NureTeacherDTO.parse(jsonResponse);

            // Updating the cache with the new response hash and saving the teachers map
            hashesOfLastResponse.put(EventTypes.TEACHER, responseHash);
            scheduleRedisDAO.saveTeachersMap(teachers);

            // Creating NureTeacher entities from the fetched teachers and saving them to the database
            List<NureTeacher> nureTeachers = teachers.values().stream()
                    .map(NureTeacherDTO::toEntity)
                    .toList();

            scheduleNureDAO.saveNureTeachers(nureTeachers);

            // Returning the fetched teachers map
            return teachers;

        } catch (HttpClientErrorException.NotFound ex) {
            // Retrieving teachers information from the database if the API endpoint is not found
            List<NureTeacher> nureTeachers = scheduleNureDAO.getAllNureTeachers();

            HashMap<Long, NureTeacherDTO> teachers = nureTeachers.stream()
                    .map(t -> new NureTeacherDTO(t.getId(), t.getFullName(), t.getShortName()))
                    .collect(Collectors.toMap(NureTeacherDTO::getId, t -> t, (t1, t2) -> t1, HashMap::new));

            scheduleRedisDAO.saveTeachersMap(teachers);

            log.error("Teachers not found in NURE API. Using data from the database.");
            ex.printStackTrace();
            return teachers;
        }
    }

    /**
     * Retrieves auditoriums information from the NURE API.
     *
     * @return A HashMap containing auditorium IDs as keys and corresponding NureAuditoryDTO objects as values.
     */
    @Transactional
    public HashMap<Long, NureAuditoryDTO> getAuditoriesMap() {
        // Endpoint URL for fetching auditoriums information from the NURE API
        String url = "https://cist.nure.ua/ias/app/tt/P_API_AUDITORIES_JSON";

        try {
            // Sending a GET request to the API endpoint
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String jsonResponse = response.getBody();

            // Generating SHA-256 hash of the JSON response for comparison
            String responseHash = getSHA256Hash(requireNonNull(jsonResponse));

            // Checking if the retrieved data matches the last cached data
            if (hashesOfLastResponse.get(EventTypes.AUDITORY).equalsIgnoreCase(responseHash)) {
                // Returning cached auditoriums map if the data matches
                return scheduleRedisDAO.getAuditoriesMap();
            }

            // Parsing the JSON response into a HashMap of auditoriums
            HashMap<Long, NureAuditoryDTO> auditories = NureAuditoryDTO.parse(jsonResponse);

            // Updating the cache with the new response hash and saving the auditoriums map
            hashesOfLastResponse.put(EventTypes.AUDITORY, responseHash);
            scheduleRedisDAO.saveAuditoriesMap(auditories);

            // Creating NureAuditory entities from the fetched auditoriums and saving them to the database
            List<NureAuditory> nureAuditories = auditories.values().stream()
                    .map(NureAuditoryDTO::toEntity)
                    .toList();

            scheduleNureDAO.saveNureAuditories(nureAuditories);

            // Returning the fetched auditoriums map
            return auditories;

        } catch (HttpClientErrorException.NotFound ex) {
            // Retrieving auditoriums information from the database if the API endpoint is not found
            List<NureAuditory> nureAuditories = scheduleNureDAO.getAllNureAuditories();

            HashMap<Long, NureAuditoryDTO> auditories = nureAuditories.stream()
                    .map(a -> new NureAuditoryDTO(a.getId(), a.getName(), a.getCorps(), ""))
                    .collect(Collectors.toMap(NureAuditoryDTO::getId, a -> a, (a1, a2) -> a1, HashMap::new));

            scheduleRedisDAO.saveAuditoriesMap(auditories);

            log.error("Auditories not found in NURE API. Using data from the database.");
            ex.printStackTrace();
            return auditories;
        }
    }


    // TODO finalize this method
    public List<NureSubjectDTO> getSubjectsByNureGroupId(Long nureGroupId) {
        List<NureSubjectDTO> subjects = scheduleRedisDAO.getSubjectsByNureGroupId(nureGroupId);

        if (subjects == null) {
            String jsonResponse = download(nureGroupId, EventTypes.GROUP);

            HashMap<Long, NureSubjectDTO> subjectsMap = NureSubjectDTO.parse(jsonResponse);
            subjects = new ArrayList<>(subjectsMap.values());

            scheduleRedisDAO.addLinkBetweenNureGroupAndSubjects(nureGroupId, subjects);
        }

        return subjects;
    }


    /**
     * Downloads schedule data from the NURE API for a specific event type and timetable ID.
     *
     * @param id   The timetable ID.
     * @param type The type of the event (e.g., GROUP, TEACHER, AUDITORY).
     * @return A JSON string containing the downloaded schedule data.
     */
    public String download(Long id, EventTypes type) {
        // Constructing the URL to download schedule data based on the event type and timetable ID
        String url = "https://cist.nure.ua/ias/app/tt/P_API_EVEN_JSON?" +
                "type_id=" + type.getTypeId() +
                "&timetable_id=" + id +
                "&idClient=KNURESked";

        try {
            // Sending a GET request to the API endpoint to download schedule data
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();

        } catch (HttpClientErrorException.NotFound ex) {
            // Handling the case when the requested resource is not found
            ex.printStackTrace();
            return "{}"; // Returning an empty JSON object if download fails
        }
    }

    /**
     * Retrieves events from the NURE API for a specific entity (e.g., group, teacher, auditory).
     *
     * @param id        The ID of the entity (e.g., group ID, teacher ID, auditory ID).
     * @param type      The type of the entity (e.g., GROUP, TEACHER, AUDITORY).
     * @return A list of NureEventDTO objects representing the retrieved events.
     */
    public List<NureEventDTO> getEvents(long id, EventTypes type) {
        requireNonNull(type);

        // Retrieving the entity (e.g., group, teacher, auditory) from the database based on the type and ID
        Updatable entity = getEntityById(id, type);

        // Handling the case when the entity is not found
        if (entity == null) {
            log.warn("Entity:" + type.name() + " not found with id = " + id);
            return new ArrayList<>();
        }

        // Calculating the time elapsed since the last update of the entity's schedule
        double hoursFromUpdate = getHoursFromUpdate(entity);

        // Checking if the entity's schedule is empty, outdated, or needs updating
        if (entity.getSchedule() == null || entity.getSchedule().isEmpty() || entity.getSchedule().equals("[]") || hoursFromUpdate > 5.0) {
            try {
                // Downloading schedule data from the NURE API and parsing it into a list of NureEventDTO objects
                String json = download(entity.getId(), type);
                List<NureEventDTO> parsed = NureEventDTO.parse(json);
                entity.setSchedule(gson.toJson(parsed));
            } catch (Exception e) {
                // Handling exceptions that occur during download or parsing
                entity.setSchedule("[]");
            }
            // Updating the entity's last updated timestamp
            entity.setLastUpdated(Date.from(Instant.now()));
        }

        // Retrieving events within the specified time range from the entity's schedule
        return gson.fromJson(entity.getSchedule(), new TypeToken<List<NureEventDTO>>(){}.getType());
    }

    /**
     * Retrieves events from the NURE API for a specific entity (e.g., group, teacher, auditory) within a specified time range.
     *
     * @param id        The ID of the entity (e.g., group ID, teacher ID, auditory ID).
     * @param type      The type of the entity (e.g., GROUP, TEACHER, AUDITORY).
     * @param startTime The start time of the time range.
     * @param endTime   The end time of the time range.
     * @return A list of NureEventDTO objects representing the retrieved events within the specified time range.
     */
    public List<NureEventDTO> getEvents(long id, EventTypes type, long startTime, long endTime) {
        return getEvents(id, type).stream()
                .filter(e -> e.getStartTime() >= startTime && e.getStartTime() <= endTime)
                .sorted(Comparator.comparingLong(NureEventDTO::getStartTime))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the time elapsed (in hours) since the last update of an updatable entity.
     *
     * @param updatable The updatable entity.
     * @return The time elapsed (in hours) since the last update of the entity.
     */
    private double getHoursFromUpdate(Updatable updatable) {
        return updatable.getLastUpdated() == null ? Double.MAX_VALUE :
                (Date.from(Instant.now()).getTime() - updatable.getLastUpdated().getTime()) / (1000.0 * 60 * 60);
    }

    @Transactional
    private Updatable getEntityById(Long id, EventTypes type) {
        Updatable entity = switch (type) {
            case GROUP -> scheduleRedisDAO.getGroupById(id);
            case TEACHER -> scheduleRedisDAO.getTeacherById(id);
            case AUDITORY -> scheduleRedisDAO.getAuditoryById(id);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        if (entity == null) {
            String json = download(id, type);
            switch (type) {
                case GROUP -> entity = getGroupsMap().get(id);
                case TEACHER -> entity = getTeachersMap().get(id);
                case AUDITORY -> entity = getAuditoriesMap().get(id);
            }
        }

        return entity;
    }
}
