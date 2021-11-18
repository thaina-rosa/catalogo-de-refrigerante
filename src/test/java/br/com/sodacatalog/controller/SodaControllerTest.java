package br.com.sodacatalog.controller;

import br.com.sodacatalog.builder.SodaDTOBuilder;
import br.com.sodacatalog.dto.SodaDTO;
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

import static br.com.sodacatalog.utils.JsonConvertionUtils.asJsonString;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
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

        mockMvc.perform(MockMvcRequestBuilders.post(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sodaDTO)))
                .andExpect(status().isCreated())
                .andExpect( (ResultMatcher)jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect((ResultMatcher) jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect((ResultMatcher) jsonPath("$.type", is(sodaDTO.getType().toString())));

    }
}
/*
    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setBrand(null);
        mockMvc.perform(post(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content (asJsonString(sodaDTO)))
                .andExpect(status().isBadRequest());

    }
}
        /*
        // then
        mockMvc.perform(post(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sodaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(sodaDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setBrand(null);

        // then
        mockMvc.perform(post(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(sodaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        when(sodaService.findByName(sodaDTO.getName())).thenReturn(sodaDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH + "/" + sodaDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect((ResultMatcher) jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect((ResultMatcher) jsonPath("$.type", is(sodaDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        when(sodaService.findByName(sodaDTO.getName())).thenThrow(SodaNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH + "/" + sodaDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void whenGETListWithSodasIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        when(sodaService.listAll()).thenReturn(Collections.singletonList(sodaDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$[0].name", is(sodaDTO.getName())))
                .andExpect((ResultMatcher) jsonPath("$[0].brand", is(sodaDTO.getBrand())))
                .andExpect((ResultMatcher) jsonPath("$[0].type", is(sodaDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutSodasIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        when(sodaService.listAll()).thenReturn(Collections.singletonList(sodaDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        doNothing().when(sodaService).deleteById(sodaDTO.getId());

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(SODA_API_URL_PATH + "/" + sodaDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(SodaNotFoundException.class).when(sodaService).deleteById(INVALID_SODA_ID);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(SODA_API_URL_PATH + "/" + INVALID_SODA_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setQuantity(sodaDTO.getQuantity() + quantityDTO.getQuantity());

        when(sodaService.increment(VALID_SODA_ID, quantityDTO.getQuantity())).thenReturn(sodaDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/" + VALID_SODA_ID + SODA_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect((ResultMatcher) jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect((ResultMatcher) jsonPath("$.type", is(sodaDTO.getType().toString())))
                .andExpect((ResultMatcher) jsonPath("$.quantity", is(sodaDTO.getQuantity())));
    }

}
/*
    @Test
      void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setQuantity(sodaDTO.getQuantity() + quantityDTO.getQuantity());
    when(sodaService.increment(VALID_SODA_ID, quantityDTO.getQuantity())).thenThrow(SodaStockExceededException.class);

        mockMvc.perform(patch(SODA_API_URL_PATH + "/" + VALID_SODA_ID + SODA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .con(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
   }

}
      /*


    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        SodaDTO beerDTO = SodaDTOBuilder.builder().build().toBeerDTO();

        //when
        when(sodaService.findByName(sodaDTO.getName())).thenReturn(beerDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        doNothing().when(beerService).deleteById(beerDTO.getId());

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + beerDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(BeerNotFoundException.class).when(beerService).deleteById(INVALID_BEER_ID);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
    }

//    @Test
//    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);
//
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .con(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }

//    @Test
//    void whenPATCHIsCalledWithInvalidBeerIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        when(beerService.increment(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(beerService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);
//
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
//                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
//                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
//                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
//    }
//
//    @Test
//    void whenPATCHIsCalledToDEcrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(60)
//                .build();
//
//        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(beerService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);
//
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void whenPATCHIsCalledWithInvalidBeerIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        when(beerService.decrement(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
}
     */



