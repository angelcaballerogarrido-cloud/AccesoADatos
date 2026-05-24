package com.persistencia.practica1.dtos;

import java.util.List;

public class MiembrosUpdateDTO {
    
    // Exactamente como exige el profesor: mapeamos el JSON entrante a los IDs de los héroes
    private List<Long> heroeIds;

    public List<Long> getHeroeIds() { return heroeIds; }
    public void setHeroeIds(List<Long> heroeIds) { this.heroeIds = heroeIds; }
}
