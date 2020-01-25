package pl.edu.pw.sportyapp.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.edu.pw.sportyapp.game.dao.Game;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@WebMvcTest
@ComponentScan("pl.edu.pw.sportyapp")
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private MongoOperations mongoOperations;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mongoOperations = new MongoTemplate(MongoClients.create(), "test");
        mongoOperations.dropCollection("game");
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        mongoOperations.dropCollection("game");
        mongoOperations = null;
        objectMapper = null;
    }

    @Test
    void getAllNoGames() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game").accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String resultString = result.getResponse().getContentAsString();

        assertThat(((Collection) JsonPath.read(resultString, "$")).size()).isEqualTo(0);
    }

    @Test
    void getAllGamesExist() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).build();
        Game g2 = Game.builder().id(2L).facility(2L).build();

        mongoOperations.insert(g1, "game");
        mongoOperations.insert(g2, "game");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game").accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String resultString = result.getResponse().getContentAsString();

        assertThat(((Collection) JsonPath.read(resultString, "$")).size()).isEqualTo(2);
    }

    @Test
    void getOneGameExists() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).build();
        mongoOperations.insert(g1, "game");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game/1").accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String resultString = result.getResponse().getContentAsString();

        int resultId = JsonPath.parse(resultString).read( "$.id");

        assertThat(resultId).isEqualTo(1);
    }

    @Test
    void getOneGameDoesNotExist() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game/1").accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    void createValidInput() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).build();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(g1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/game").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("1");
    }

    @Test
    void createInvalidInput() throws Exception {
        String json = "{\"name\":\"supergra\"}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/game").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    void updateValidInput() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).build();
        mongoOperations.insert(g1, "game");

        g1.setFacility(3L);

        String json = null;

        try {
            json = objectMapper.writeValueAsString(g1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/game/1").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        assertThat(mongoOperations.findById(1, Game.class, "game").getFacility()).isEqualTo(3L);
    }

    @Test
    void updateNotExistingGame() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).build();

        String json = null;

        try {
            json = objectMapper.writeValueAsString(g1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/game/1").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    void updateInvalidInput() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).build();

        mongoOperations.insert(g1, "game");

        g1.setId(2L);

        String json = null;

        try {
            json = objectMapper.writeValueAsString(g1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/game/1").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    void deleteExistingGame() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).build();

        mongoOperations.insert(g1, "game");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/game/1")).andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();
    }

    @Test
    void deleteNotExistingGame() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/game/1")).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }
}