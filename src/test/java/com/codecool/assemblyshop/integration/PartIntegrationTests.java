package com.codecool.assemblyshop.integration;

import com.codecool.assemblyshop.testmodels.Assembly;
import com.codecool.assemblyshop.testmodels.Part;
import com.codecool.assemblyshop.testmodels.PartMaterial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static com.codecool.assemblyshop.data.TestParts.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class PartIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String entityUrl;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port;
        entityUrl = baseUrl + "/part";
    }

    @Test
    void emptyDatabase_getAll_shouldReturnEmptyList() {
        assertEquals(Collections.emptyList(), List.of(restTemplate.getForObject(entityUrl, Part[].class)));
    }

    @Test
    void emptyDatabase_addOne_shouldReturnAddedPart() {
        Part result = restTemplate.postForObject(entityUrl, METAL_WHEEL, Part.class);
        assertEquals(METAL_WHEEL.getName(), result.getName());
    }

    @Test
    void somePartsStored_getAll_shouldReturnAll() {
        List<Part> testData = List.of(METAL_WHEEL, ROPE, METAL_SPRING);
        testData.forEach(ingredient -> restTemplate.postForObject(entityUrl, ingredient, Part.class));

        Part[] result = restTemplate.getForObject(entityUrl, Part[].class);
        assertEquals(testData.size(), result.length);

        Set<String> partNames = testData.stream().map(Part::getName).collect(Collectors.toSet());
        assertTrue(Arrays.stream(result).map(Part::getName).allMatch(partNames::contains));
    }

    @Test
    void onePartStored_getOneById_shouldReturnCorrectPart() {
        Long id = restTemplate.postForObject(entityUrl, METAL_WHEEL, Part.class).getId();
        Part result = restTemplate.getForObject(entityUrl + "/" + id, Part.class);
        assertEquals(METAL_WHEEL.getName(), result.getName());
        assertEquals(id, result.getId());
    }

    @Test
    void somePartsStored_deleteOne_getAllShouldReturnRemaining() {
        List<Part> testData = new ArrayList<>(List.of(METAL_WHEEL, PLASTIC_TUBE, METAL_SPRING));
        testData = new ArrayList<>(testData.stream()
                .map(p -> restTemplate.postForObject(entityUrl, p, Part.class))
                .toList());

        restTemplate.delete(entityUrl + "/" + testData.get(0).getId());
        Set<String> expectedPartNames = testData.stream().skip(1L).map(Part::getName).collect(Collectors.toSet());

        Part[] response = restTemplate.getForObject(entityUrl, Part[].class);

        assertEquals(expectedPartNames.size(), response.length);
        for (Part p : response) {
            assertTrue(expectedPartNames.contains(p.getName()));
        }
    }

    @Test
    void onePartStored_deleteById_getAllShouldReturnEmptyList() {
        Part testPart = restTemplate.postForObject(entityUrl, METAL_SPRING, Part.class);
        assertNotNull(testPart.getId());
        restTemplate.delete(entityUrl + "/" + testPart.getId());
        Part[] result = restTemplate.getForObject(entityUrl, Part[].class);
        assertEquals(0, result.length);
    }

    @Test
    void onePartStoredUsedInAssembly_deleteById_PartShouldNotBeDeleted() {
        Part testPart = restTemplate.postForObject(entityUrl, WHEEL, Part.class);
        Assembly testAssembly = restTemplate.postForObject(
                "http://localhost:" + port + "/assembly",
                new Assembly(null, "toy car", List.of(testPart)),
                Assembly.class
        );
        restTemplate.delete(entityUrl + "/" + testPart.getId());
        Part result = restTemplate.getForObject(entityUrl + "/" + testPart.getId(), Part.class);
        assertEquals(testPart.getName(), result.getName());
    }

    @Test
    void onePartStored_updateIt_PartShouldBeUpdated() {
        Part testPart = restTemplate.postForObject(entityUrl, METAL_WHEEL, Part.class);

        testPart.setName(testPart.getName() + "update");
        String url = entityUrl + "/" + testPart.getId();
        restTemplate.put(url, testPart);

        Part result = restTemplate.getForObject(url, Part.class);
        assertEquals(testPart.getName(), result.getName());
    }

    @Test
    void onePartStored_updateWithWrongId_PartShouldBeUnchanged() {
        Part testPart = restTemplate.postForObject(entityUrl, PLASTIC_TUBE, Part.class);

        String originalName = testPart.getName();
        assertNotNull(originalName);
        Long originalId = testPart.getId();

        testPart.setName(originalName + "update");
        testPart.setId(42L);
        String url = entityUrl + "/" + originalId;
//         restTemplate.put(url, testPart, Object.class);
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(testPart, null), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        Part result = restTemplate.getForObject(url, Part.class);
        assertEquals(originalName, result.getName());
    }

    @Test
    void getOneByWrongId_shouldRespond404() {
        ResponseEntity<String> response = restTemplate.getForEntity(entityUrl + "/12345", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void postInvalidPartWithNull_shouldRespond400() {
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, new Part(null, null, PartMaterial.METAL), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postInvalidPartWithBlankString_shouldRespond400() {
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, new Part(null, "", PartMaterial.METAL), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void putInvalidPartWithBlankString_shouldRespond400() {
        Part testPart = restTemplate.postForObject(entityUrl, METAL_SPRING, Part.class);
        String url = entityUrl + "/" + testPart.getId();

        testPart.setName("");
        ResponseEntity<?> resp = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(testPart, null), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
}
