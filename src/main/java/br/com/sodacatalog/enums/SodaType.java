package br.com.sodacatalog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SodaType {

        COCACOLA("Coca-cola"),
        FANTA("Fanta"),
        GUARANAANTARTICA("Guarana - Antartica"),
        FANTAUVA("Fanta-Uva"),
        GUARANÁJESUS("Guarana-Jesus"),
        DOLI("Doli"),
        SPRITE("Sprite");

    private final String description;
}
