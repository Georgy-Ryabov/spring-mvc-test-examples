package net.petrikainulainen.spring.testmvc.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Petri Kainulainen
 */
public class ITStandaloneHomeControllerTest {

    @Test
    public void showHomePage() throws Exception {
        MockMvcBuilders.standaloneSetup(new HomeController()).build()
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name(HomeController.VIEW_HOME_PAGE));
    }
}
