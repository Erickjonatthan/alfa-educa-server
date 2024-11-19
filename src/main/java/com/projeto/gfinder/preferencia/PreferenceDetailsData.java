package com.projeto.gfinder.preferencia;

import java.util.List;


public record PreferenceDetailsData(Long id, Long idUsuario, List<String> generos, List<String> plataformas, List<String> modosDeJogo) {
    public PreferenceDetailsData(Preference preference) {
        this(preference.getId(), preference.getUsuario().getId(), preference.getGeneros(), preference.getPlataformas(), preference.getModosDeJogo());
    }
    
}
