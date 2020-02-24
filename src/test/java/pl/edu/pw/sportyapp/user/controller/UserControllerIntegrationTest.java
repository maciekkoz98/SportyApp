package pl.edu.pw.sportyapp.user.controller;

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
import pl.edu.pw.sportyapp.user.dao.User;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@WebMvcTest
@ComponentScan("pl.edu.pw.sportyapp")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private MongoOperations mongoOperations;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mongoOperations = new MongoTemplate(MongoClients.create(), "test");
        mongoOperations.dropCollection("user");
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        mongoOperations.dropCollection("user");
        mongoOperations = null;
        objectMapper = null;
    }

    @Test
    void getAllNoUsers() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String resultString = result.getResponse().getContentAsString();

        assertThat(((Collection) JsonPath.read(resultString, "$")).size()).isEqualTo(0);
    }

    @Test
    void getAllUsersExists() throws Exception {
        User user1 = User.builder().id(1L).nickname("testowy1").name("Jan1").surname("Kowalski1").email("jkowal1@mail.com")
                .password("testowe1").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();
        User user2 = User.builder().id(2L).nickname("testowy2").name("Jan2").surname("Kowalski2").email("jkowal2@mail.com")
                .password("testowe2").averageGrade(4L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        mongoOperations.insert(user1, "user");
        mongoOperations.insert(user2, "user");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String resultString = result.getResponse().getContentAsString();

        assertThat(((Collection) JsonPath.read(resultString, "$")).size()).isEqualTo(2);
    }

    @Test
    void getOneUserExists() throws Exception {
        User user1 = User.builder().id(1L).nickname("testowy1").name("Jan1").surname("Kowalski1").email("jkowal1@mail.com")
                .password("testowe1").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        mongoOperations.insert(user1, "user");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String resultString = result.getResponse().getContentAsString();

        int resultId = JsonPath.parse(resultString).read("$.id");

        assertThat(resultId).isEqualTo(1);
    }

    @Test
    void getOneGameDoesNotExist() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    void createValidInput() throws Exception {
        User user1 = User.builder().id(1L).nickname("testowy1").name("Jan1").surname("Kowalski1").email("jkowal1@mail.com")
                .password("testowe1").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(user1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("1");
    }

    @Test
    void createInvalidInput() throws Exception {
        String json = "{\"nickname\":\"testowy\"}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    void updateValidInput() throws Exception {
        User user = User.builder().id(1L).nickname("testowy1").name("Jan1").surname("Kowalski1").email("jkowal1@mail.com")
                .password("testowe1").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        mongoOperations.insert(user, "user");
        user.setNickname("testowyNowy");

        String json = null;

        try {
            json = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/user/1").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        assertThat(mongoOperations.findById(1, User.class, "user").getNickname()).isEqualTo("testowyNowy");
    }

    @Test
    void updateInvalidInput() throws Exception {
        User user = User.builder().id(1L).nickname("testowy1").name("Jan1").surname("Kowalski1").email("jkowal1@mail.com")
                .password("testowe1").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        mongoOperations.insert(user, "user");

        user.setId(2L);

        String json = null;

        try {
            json = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/user/1").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    void updateNotExistingUser() throws Exception {
        User user = User.builder().id(1L).nickname("testowy1").name("Jan1").surname("Kowalski1").email("jkowal1@mail.com")
                .password("testowe1").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        String json = null;

        try {
            json = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/user/1").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    void deleteExistingUser() throws Exception {
        User user = User.builder().id(1L).nickname("testowy1").name("Jan1").surname("Kowalski1").email("jkowal1@mail.com")
                .password("testowe1").averageGrade(5L).gamesParticipatedIds(new ArrayList<>()).friendsIds(new ArrayList<>()).build();

        mongoOperations.insert(user, "user");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/user/1")).andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();
    }

    @Test
    void deleteNotExistingGame() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/user/1")).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

}
