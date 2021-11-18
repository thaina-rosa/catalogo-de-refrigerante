package br.com.sodacatalog.controller;

import br.com.sodacatalog.dto.QuantityDTO;
import br.com.sodacatalog.dto.SodaDTO;
import br.com.sodacatalog.exception.SodaAlreadyRegisteredException;
import br.com.sodacatalog.exception.SodaNotFoundException;
import br.com.sodacatalog.exception.SodaStockExceededException;
import br.com.sodacatalog.service.SodaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sodas")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SodaController implements SodaControllerDocs {

    private final SodaService sodaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SodaDTO createSoda(@RequestBody @Valid SodaDTO sodaDTO)throws SodaAlreadyRegisteredException {
        return sodaService.createSoda(sodaDTO);

    }
    @GetMapping("/{name}")
    public SodaDTO findByName(@PathVariable String name) throws SodaNotFoundException {
        return sodaService.findByName(name);
    }


    @GetMapping
    public List<SodaDTO> listSodas() {
        return sodaService.listAll();
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id)throws SodaNotFoundException {
        sodaService.deleteById(id);
    }

}
