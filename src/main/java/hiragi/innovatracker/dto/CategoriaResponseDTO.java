package hiragi.innovatracker.dto;


import hiragi.innovatracker.model.TdCategoria;


public record CategoriaResponseDTO(
        Long idtCategoria,
        String sglCategoria,
        String nmeCategoria,
        String dscCategoria
) {
    public static CategoriaResponseDTO from(TdCategoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getIdtCategoria(),
                categoria.getSglCategoria(),
                categoria.getNmeCategoria(),
                categoria.getDscCategoria()
        );
    }
}
