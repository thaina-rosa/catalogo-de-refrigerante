package br.com.sodacatalog.service;

import br.com.sodacatalog.builder.SodaDTOBuilder;
import br.com.sodacatalog.dto.SodaDTO;
import br.com.sodacatalog.entity.Soda;
import br.com.sodacatalog.exception.SodaAlreadyRegisteredException;
import br.com.sodacatalog.exception.SodaNotFoundException;
import br.com.sodacatalog.exception.SodaStockExceededException;
import br.com.sodacatalog.mapper.SodaMapper;
import br.com.sodacatalog.repository.SodaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SodaServiceTest {

    private static final long INVALID_SODA_ID = 1L;

    @Mock
    private SodaRepository sodaRepository;

    private SodaMapper sodaMapper = SodaMapper.INSTANCE;

    @InjectMocks
    private SodaService sodaService;

    @Test
    void whenSodaInformedThenItShouldBeCreated() throws SodaAlreadyRegisteredException {
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSavedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findByName(expectedSodaDTO.getName())).thenReturn(Optional.empty());
        when(sodaRepository.save(expectedSavedSoda)).thenReturn(expectedSavedSoda);

        SodaDTO createdSodaDTO = sodaService.createSoda(expectedSodaDTO);

        assertThat(createdSodaDTO.getId(), is(equalTo(expectedSodaDTO.getId())));
        assertThat(createdSodaDTO.getName(), is(equalTo(expectedSodaDTO.getName())));
        assertThat(createdSodaDTO.getQuantity(), is(equalTo(expectedSodaDTO.getQuantity())));



    }
    @Test
    void whenAlreadyRegisteredSodaInformedThenAnExceptionShouldBeThrown() {
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda duplicatedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findByName(expectedSodaDTO.getName())).thenReturn(Optional.of(duplicatedSoda));

       assertThrows(SodaAlreadyRegisteredException.class, () -> sodaService.createSoda(expectedSodaDTO));
    }
        @Test
    void whenValidSodaNameIsGivenThenReturnASoda() throws SodaNotFoundException {
            SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
            Soda expectedFoundSoda = sodaMapper.toModel(expectedFoundSodaDTO);

            when(sodaRepository.findByName(expectedFoundSoda.getName())).thenReturn(Optional.of(expectedFoundSoda));

            SodaDTO foundSodaDTO = sodaService.findByName(expectedFoundSoda.getName());

            assertThat(foundSodaDTO, is(equalTo(expectedFoundSodaDTO)));

        }
    @Test
    void whenNoRegisterSodaNameISGivenThenThrowAnException() {
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();


        when(sodaRepository.findByName(expectedFoundSodaDTO.getName())).thenReturn(Optional.empty());

        assertThrows(SodaNotFoundException.class, () -> sodaService.findByName(expectedFoundSodaDTO.getName()));
    }
    @Test
    void whenListSodaIsCalledThenReturnAListOfSoda(){
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedFoundSoda = sodaMapper.toModel(expectedFoundSodaDTO);

        //when
        when(sodaRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundSoda));

        List<SodaDTO> foundListSodasDTO = sodaService.listAll();

        assertThat(foundListSodasDTO, is(not(empty())));
        assertThat(foundListSodasDTO.get(0), is(equalTo(expectedFoundSodaDTO)));
    }
    @Test
    void whenListSodaIsCalledThenReturnAnEmptyListOfSodas() {


        //when
        when(sodaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<SodaDTO> foundListSodasDTO = sodaService.listAll();

        assertThat(foundListSodasDTO, is(empty()));
    }
    @Test
    void wheExclusionIsCalledWithValidIdThenASodaShouldBeDeleted() throws SodaNotFoundException {
        SodaDTO expectedDeleteSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedDeleteSoda = sodaMapper.toModel(expectedDeleteSodaDTO);

        when(sodaRepository.findById(expectedDeleteSodaDTO.getId())).thenReturn(Optional.of(expectedDeleteSoda));
        doNothing().when(sodaRepository).deleteById(expectedDeleteSodaDTO.getId());

        sodaService.deleteById(expectedDeleteSodaDTO.getId());

        verify(sodaRepository, times(1)).findById(expectedDeleteSodaDTO.getId());
        verify(sodaRepository, times(1)).deleteById(expectedDeleteSodaDTO.getId());
    }
    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws SodaNotFoundException, SodaStockExceededException {
        //given
        SodaDTO expectedBeerDTO =SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedBeer = sodaMapper.toModel(expectedBeerDTO);

        //when
        when(sodaRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        when(sodaRepository.save(expectedBeer)).thenReturn(expectedBeer);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedBeerDTO.getQuantity() + quantityToIncrement;

        // then
        SodaDTO incrementedBeerDTO = sodaService.increment(expectedBeerDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedBeerDTO.getMax()));
    }
    @Test
    void whenIncrementIsCalledThenIncrementSodaStock(){

        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));

        int quantityToIncrement = 80;

        assertThrows(SodaStockExceededException.class,() -> sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement));

    }
    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException(){
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findById(expectedSoda.getId())).thenReturn(Optional.of(expectedSoda));

        int quantityToIncrement = 45;
        assertThrows(SodaStockExceededException.class, () -> sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement));
    }
    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(sodaRepository.findById(INVALID_SODA_ID)).thenReturn(Optional.empty());

        assertThrows(SodaNotFoundException.class, () ->sodaService.increment(INVALID_SODA_ID, quantityToIncrement));
    }
}
