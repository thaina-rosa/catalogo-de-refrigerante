package br.com.sodacatalog.controller;

import br.com.sodacatalog.dto.SodaDTO;
import br.com.sodacatalog.exception.SodaAlreadyRegisteredException;
import br.com.sodacatalog.exception.SodaNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages soda stock")
public interface SodaControllerDocs {

    @ApiOperation(value = "Soda creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success soda creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    SodaDTO createSoda(SodaDTO sodaDTO) throws SodaAlreadyRegisteredException;

    @ApiOperation(value = "Returns soda found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success soda found in the system"),
            @ApiResponse(code = 404, message = "Soda with given name not found.")
    })
    SodaDTO findByName(@PathVariable String name) throws SodaNotFoundException;
    @ApiOperation(value = "Returns a list of all soda registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all soda registered in the system"),
    })
    List<SodaDTO> listSodas();

    @ApiOperation(value = "Returns soda found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success soda deleted in the system"),
            @ApiResponse(code = 404, message = "Soda with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws SodaNotFoundException ;

    }

