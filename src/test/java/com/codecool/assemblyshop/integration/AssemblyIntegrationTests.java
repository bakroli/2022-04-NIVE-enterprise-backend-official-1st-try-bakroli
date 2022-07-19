package com.codecool.assemblyshop.integration;

import com.codecool.assemblyshop.testmodels.Assembly;
import com.codecool.assemblyshop.testmodels.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.codecool.assemblyshop.data.TestAssembly.*;
import static com.codecool.assemblyshop.data.TestParts.METAL_SPRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class AssemblyIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;
    private String entityUrl;
    private String baseUrl;

    private Assembly postAssembly(Assembly assembly) {

        List<Part> postedParts = assembly.getParts().stream()
                .map(p -> restTemplate.postForObject(baseUrl + "/part", p, Part.class))
                .toList();
        Assembly assemblyToPost = new Assembly(assembly.getId(), assembly.getName(), postedParts);
        return restTemplate.postForObject(entityUrl, assemblyToPost, Assembly.class);
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        entityUrl = baseUrl + "/assembly";
    }

    @RepeatedTest(2)
    void emptyDatabase_getAll_shouldReturnEmptyList() {
        assertEquals(Collections.emptyList(), List.of(restTemplate.getForObject(entityUrl, Assembly[].class)));
    }

    @Test
    void emptyDatabase_addOne_shouldReturnAdded() {
        CAR.getParts().forEach(ing -> System.out.println(ing.getId()));
        Assembly result = postAssembly(CAR);
        assertEquals(CAR.getName(), result.getName());
    }

    @Test
    void someStored_getAll_shouldReturnAll() {
        List<Assembly> testData = List.of(CAR, TOY);
        Set<String> expectedNames = testData.stream().map(Assembly::getName).collect(Collectors.toSet());
        testData.forEach(this::postAssembly);

        Assembly[] response = restTemplate.getForObject(entityUrl, Assembly[].class);

        assertEquals(testData.size(), response.length);
        for (Assembly a : response) {
            assertTrue(expectedNames.contains(a.getName()));
        }
    }

    @Test
    void oneStored_getOneById_shouldReturnCorrect() {
        Long id = postAssembly(CAR).getId();
        Assembly response = restTemplate.getForObject(entityUrl + "/" + id, Assembly.class);
        assertEquals(CAR.getName(), response.getName());
        assertEquals(CAR.getParts().size(), response.getParts().size());

        List<String> responsePartNames = response.getParts().stream().map(Part::getName).toList();
        List<String> expectedPartNames = CAR.getParts().stream().map(Part::getName).toList();
        assertThat(responsePartNames).hasSameElementsAs(expectedPartNames);
    }

    @Test
    void getOneByWrongId_shouldRespond404() {
        ResponseEntity<String> response = restTemplate.getForEntity(entityUrl + "/12345", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void someAssemblysStored_deleteOne_getAllShouldReturnRemaining() {
        List<Assembly> testData = new ArrayList<>(List.of(CAR, TOY));
        testData.replaceAll(this::postAssembly);

        String url = entityUrl + "/" + testData.get(0).getId();
        restTemplate.delete(url);
        testData.remove(testData.get(0));

        Assembly[] result = restTemplate.getForObject(entityUrl, Assembly[].class);

        assertEquals(testData.size(), result.length);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i).getName(), result[i].getName());
        }
    }

    @Test
    void oneAssemblyStored_deleteById_getAllShouldReturnEmptyList() {
        Assembly testData = postAssembly(CAR);

        restTemplate.delete(entityUrl + "/" + testData.getId());

        Assembly[] result = restTemplate.getForObject(entityUrl, Assembly[].class);

        assertEquals(0, result.length);
    }

    @Test
    void postInvalidPartWithNull_shouldRespond400() {
        var data = new Assembly(null, null, List.of(METAL_SPRING));
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, data, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postInvalidPartWithBlankString_shouldRespond400() {
        var data = new Assembly(null, "", List.of(METAL_SPRING));
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, data, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postInvalidPartWithBlankCollection_shouldRespond400() {
        var data = new Assembly(null, "ABC", null);
        ResponseEntity<String> response = restTemplate.postForEntity(entityUrl, data, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void postAssemblies_whenRequestingSimple_ReturnOnlyTheSimpleOnes() {
        postAssembly(CAR);
        postAssembly(TOY);
        postAssembly(JUMP_ROPE);

        Assembly[] response = restTemplate.getForObject(entityUrl + "/simple", Assembly[].class);
        Set<String> responseNames = Arrays.stream(response).map(Assembly::getName).collect(Collectors.toSet());

        var expectedNames = Stream.of(TOY, JUMP_ROPE).map(Assembly::getName).collect(Collectors.toSet());

        assertThat(response).hasSize(2);
        assertThat(responseNames).hasSameElementsAs(expectedNames);
    }
}
