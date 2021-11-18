package br.com.sodacatalog.mapper;

import br.com.sodacatalog.dto.SodaDTO;
import br.com.sodacatalog.entity.Soda;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SodaMapper {
    SodaMapper INSTANCE = Mappers.getMapper(SodaMapper.class);

    Soda toModel(SodaDTO sodaDTO);

    SodaDTO toDTO(Soda soda);

}
