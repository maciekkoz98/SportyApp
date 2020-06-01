package pl.edu.pw.sportyapp.game.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.edu.pw.sportyapp.facility.dao.Facility;
import pl.edu.pw.sportyapp.game.dao.Game;
import pl.edu.pw.sportyapp.user.dao.User;
import pl.edu.pw.sportyapp.user.security.AppUserRole;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@WebMvcTest
@ComponentScan("pl.edu.pw.sportyapp")
class GameControllerIntegrationTest {

    private MockMvc mockMvc;

    private MongoOperations mongoOperations;
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private User user;

    @BeforeEach
    void setUp() {
        mongoOperations = new MongoTemplate(MongoClients.create(), "test");
        mongoOperations.dropCollection("game");
        mongoOperations.dropCollection("user");
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        user = User.builder().username("bob").fullname("robert").email("bob@bob.pl").passwordHash("bobob").id(6L).role(AppUserRole.USER)
                .isEnabled(true).isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true)
                .gamesParticipatedIds(Lists.newArrayList()).friendsIds(Lists.newArrayList())
                .build();
    }

    @AfterEach
    void tearDown() {
        mongoOperations.dropCollection("game");
        mongoOperations.dropCollection("user");
        mongoOperations = null;
        objectMapper = null;
    }

    @Test
    void getAllNoGames() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game").with(user(user)).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String resultString = result.getResponse().getContentAsString();

        assertThat(((Collection) JsonPath.read(resultString, "$")).size()).isEqualTo(0);
    }

    @Test
    void getAllGamesExist() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).owner(6L).date(System.currentTimeMillis() * 2).duration(300).build();
        Game g2 = Game.builder().id(2L).facility(2L).owner(6L).date(System.currentTimeMillis() * 2).duration(300).build();

        mongoOperations.insert(g1, "game");
        mongoOperations.insert(g2, "game");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game").with(user(user)).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String resultString = result.getResponse().getContentAsString();

        assertThat(((Collection) JsonPath.read(resultString, "$")).size()).isEqualTo(2);
    }

    @Test
    void getOneGameExists() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).owner(6L).date(System.currentTimeMillis() * 2).duration(300).build();

        mongoOperations.insert(g1, "game");


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game/1").with(user(user)).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        String resultString = result.getResponse().getContentAsString();

        int resultId = JsonPath.parse(resultString).read("$.id");

        assertThat(resultId).isEqualTo(1);
    }

    @Test
    void getOneGameDoesNotExist() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/game/1").with(user(user)).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }

    @Test
    void createValidInput() throws Exception {
        Game g1 = Game.builder().facility(2L).sport(1L).owner(6L).name("gra").date(System.currentTimeMillis() * 2).duration(3600L).build();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(g1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }

        mongoOperations.insert(user, "user");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/game").with(user(user)).content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("1");
    }

    @Test
    void createInvalidInput() throws Exception {
        String json = "{\"name\":\"supergra\"}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/game").with(user(user)).content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    void updateValidInput() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).sport(1L).owner(6L).name("gra").isPublic(true).date(System.currentTimeMillis() * 2).duration(2600).build();
        mongoOperations.insert(g1, "game");
        mongoOperations.insert(user, "user");

        g1.setFacility(3L);

        String json = null;

        try {
            json = objectMapper.writeValueAsString(g1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/game/1").with(user(user)).content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        assertThat(mongoOperations.findById(1, Game.class, "game").getFacility()).isEqualTo(3L);
    }

    @Test
    void updateNotExistingGame() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).sport(1L).owner(6L).name("gra").isPublic(true).date(System.currentTimeMillis() * 2).duration(2600).build();

        String json = null;

        try {
            json = objectMapper.writeValueAsString(g1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("JSON parsing error");
        }

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/game/1").with(user(user)).content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/game/1").with(user(user)).content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
    }

    @Test
    void deleteExistingGame() throws Exception {
        Game g1 = Game.builder().id(1L).facility(2L).owner(6L).name("gra").isPublic(true).date(System.currentTimeMillis() * 2).duration(2600).build();

        mongoOperations.insert(g1, "game");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/game/1").with(user(user))).andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();
    }

    @Test
    void deleteNotExistingGame() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/game/1").with(user(user))).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn();
    }
}