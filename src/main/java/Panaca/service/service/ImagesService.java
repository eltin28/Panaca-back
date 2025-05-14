package Panaca.service.service;

import org.springframework.web.multipart.MultipartFile;
public interface ImagesService {

    String subirImagen(MultipartFile imagen) throws Exception;
    void eliminarImagen(String nombreImagen) throws Exception;

}
