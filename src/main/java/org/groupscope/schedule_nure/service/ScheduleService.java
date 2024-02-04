package org.groupscope.schedule_nure.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.groupscope.schedule_nure.dao.ScheduleDAO;
import org.groupscope.schedule_nure.dto.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    private final ScheduleDAO scheduleDAO;
    private final Gson gson = new Gson();

    public ScheduleService(RestTemplateBuilder restTemplateBuilder, ScheduleDAO scheduleDAO) {
        restTemplate = restTemplateBuilder.build();
        this.scheduleDAO = scheduleDAO;
        
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
                return scheduleDAO.getGroupsMap();
            }

            // Parsing the JSON response into a HashMap of groups
            HashMap<Long, NureGroupDTO> groups = NureGroupDTO.parse(jsonResponse);

            // Updating the cache with the new response hash and saving the groups map
            hashesOfLastResponse.put(EventTypes.GROUP, responseHash);
            scheduleDAO.saveGroupsMap(groups);

            // Returning the fetched groups map
            return groups;

        } catch (HttpClientErrorException.NotFound ex) {
            // Handling the case when the requested resource is not found
            ex.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Retrieves teachers information from the NURE API.
     *
     * @return A HashMap containing teacher IDs as keys and corresponding NureTeacherDTO objects as values.
     */
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
                return scheduleDAO.getTeachersMap();
            }

            // Parsing the JSON response into a HashMap of teachers
            HashMap<Long, NureTeacherDTO> teachers = NureTeacherDTO.parse(jsonResponse);

            // Updating the cache with the new response hash and saving the teachers map
            hashesOfLastResponse.put(EventTypes.TEACHER, responseHash);
            scheduleDAO.saveTeachersMap(teachers);

            // Returning the fetched teachers map
            return teachers;

        } catch (HttpClientErrorException.NotFound ex) {
            // Handling the case when the requested resource is not found
            ex.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Retrieves auditoriums information from the NURE API.
     *
     * @return A HashMap containing auditorium IDs as keys and corresponding NureAuditoryDTO objects as values.
     */
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
                return scheduleDAO.getAuditoriesMap();
            }

            // Parsing the JSON response into a HashMap of auditoriums
            HashMap<Long, NureAuditoryDTO> auditories = NureAuditoryDTO.parse(jsonResponse);

            // Updating the cache with the new response hash and saving the auditoriums map
            hashesOfLastResponse.put(EventTypes.AUDITORY, responseHash);
            scheduleDAO.saveAuditoriesMap(auditories);

            // Returning the fetched auditoriums map
            return auditories;

        } catch (HttpClientErrorException.NotFound ex) {
            // Handling the case when the requested resource is not found
            ex.printStackTrace();
            return new HashMap<>();
        }
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
     * Retrieves events from the NURE API for a specific entity (e.g., group, teacher, auditory) within a specified time range.
     *
     * @param id        The ID of the entity (e.g., group ID, teacher ID, auditory ID).
     * @param type      The type of the entity (e.g., GROUP, TEACHER, AUDITORY).
     * @param startTime The start time of the time range.
     * @param endTime   The end time of the time range.
     * @return A list of NureEventDTO objects representing the retrieved events within the specified time range.
     */
    public List<NureEventDTO> getEvents(long id, EventTypes type, long startTime, long endTime) {
        requireNonNull(type);

        // Retrieving the entity (e.g., group, teacher, auditory) from the database based on the type and ID
        Updatable entity = null;
        switch (type) {
            case GROUP -> entity = scheduleDAO.getGroupById(id);
            case TEACHER -> entity = scheduleDAO.getTeacherById(id);
            case AUDITORY -> entity = scheduleDAO.getAuditoryById(id);
        }

        // Handling the case when the entity is not found
        if (entity == null) {
            log.warn("Entity:" + type.name() + " not found with id = " + id);
            return new ArrayList<>();
        }

        // Calculating the time elapsed since the last update of the entity's schedule
        double hoursFromUpdate = getHoursFromUpdate(entity);

        // Checking if the entity's schedule is empty, outdated, or needs updating
        if (entity.getSchedule().isEmpty() || entity.getSchedule().equals("[]") || hoursFromUpdate > 5.0) {
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
        List<NureEventDTO> schedule = gson.fromJson(entity.getSchedule(), new TypeToken<List<NureEventDTO>>(){}.getType());
        return schedule.stream()
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


}
