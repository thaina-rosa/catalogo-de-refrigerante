package br.com.sodacatalog.controller;

import br.com.sodacatalog.builder.SodaDTOBuilder;
import br.com.sodacatalog.dto.QuantityDTO;
import br.com.sodacatalog.dto.SodaDTO;
import br.com.sodacatalog.exception.SodaNotFoundException;
import br.com.sodacatalog.service.SodaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static br.com.sodacatalog.utils.JsonConvertionUtils.asJsonString;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith (MockitoExtension.class )
public class SodaControllerTest {

    private static final String SODA_API_URL_PATH = "/api/v1/sodas";
    private static final long VALID_SODA_ID = 1L;
    private static final long INVALID_SODA_ID = 2l;
    private static final String SODA_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String SODA_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private SodaService sodaService;

    @InjectMocks
    private SodaController sodaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sodaController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenASodaIsCreated() throws Exception {

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();


        when(sodaService.createSoda(sodaDTO)).thenReturn(sodaDTO);


        mockMvc.perform(post(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sodaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(sodaDTO.getName()))
                .andExpect( jsonPath("$.brand").value(sodaDTO.getBrand()))
                .andExpect( jsonPath("$.type").value(sodaDTO.getType().toString()));
    }

    @Test
    void   whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setBrand(null);

        mockMvc.perform(post(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sodaDTO)))
                        .andExpect(status().isBadRequest());

    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaService.findByName(sodaDTO.getName())).thenReturn(sodaDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH + "/" + sodaDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$.name").value(sodaDTO.getName()))
                .andExpect( jsonPath("$.brand").value(sodaDTO.getBrand()))
                .andExpect( jsonPath("$.type").value(sodaDTO.getType().toString()));

    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaService.findByName(sodaDTO.getName())).thenThrow(SodaNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH + "/" + sodaDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void whenGETListWithSodaIsCalledThenOkStatusIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaService.listAll()).thenReturn(Collections.singletonList(sodaDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect( jsonPath("$[0].name").value(sodaDTO.getName()))
                .andExpect( jsonPath("$[0].brand").value(sodaDTO.getBrand()))
                .andExpect( jsonPath("$[0].type").value(sodaDTO.getType().toString()));
    }

    @Test
    void whenGETListWithoutSodaIsCalledThenOkStatusIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaService.listAll()).thenReturn(Collections.singletonList(sodaDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        doNothing().when(sodaService).deleteById(sodaDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete(SODA_API_URL_PATH + "/" + sodaDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNotFoundStatusIsReturned() throws Exception {


        doThrow(SodaNotFoundException.class).when(sodaService).deleteById(INVALID_SODA_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete(SODA_API_URL_PATH + "/" + INVALID_SODA_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }
        @Test
        void whenPATCHIsCalledToIncrementDiscountThenOKStatusIsReturned() throws Exception {
            QuantityDTO quantityDTO = QuantityDTO.builder()
                    .quantity(10)
                    .build();

            SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
            sodaDTO.setQuantity(sodaDTO.getQuantity() + quantityDTO.getQuantity());

            when(sodaService.increment(VALID_SODA_ID, quantityDTO.getQuantity())).thenReturn(sodaDTO);

            mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/" + VALID_SODA_ID + SODA_API_SUBPATH_INCREMENT_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                    .andExpect( jsonPath("$.name").value(sodaDTO.getName()))
                    .andExpect( jsonPath("$.brand").value(sodaDTO.getBrand()))
                    .andExpect( jsonPath("$.type").value(sodaDTO.getType().toString()))
                    .andExpect( jsonPath("$.quantity").value(sodaDTO.getQuantity()));

        }
}