package br.com.sodacatalog.builder;

import br.com.sodacatalog.dto.SodaDTO;
import br.com.sodacatalog.enums.SodaType;
import lombok.Builder;

@Builder
public class SodaDTOBuilder {
    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "COCACOLA";

    @Builder.Default
    private String brand = "Ambev";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private SodaType type = SodaType.FANTA;

    public SodaDTO toSodaDTO() {
        return new SodaDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }


}
